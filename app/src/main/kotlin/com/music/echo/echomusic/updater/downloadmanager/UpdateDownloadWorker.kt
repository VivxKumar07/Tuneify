package echo.music.iad1tya.echomusic.updater.downloadmanager

import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Environment
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import echo.music.iad1tya.R
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.zip.ZipInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class UpdateDownloadWorker(private val context: Context, workerParams: WorkerParameters) :
  CoroutineWorker(context, workerParams) {

  override suspend fun doWork(): Result =
    withContext(Dispatchers.IO) {
      val apkUrl = inputData.getString("apk_url") ?: return@withContext Result.failure()
      val version = inputData.getString("version") ?: "unknown"
      val fileSize = inputData.getString("file_size") ?: ""

      try {
        val startingNotification =
          DownloadNotificationManager.getDownloadStartingNotification(version, fileSize)
        val foregroundInfo =
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
              DownloadNotificationManager.NOTIFICATION_ID,
              startingNotification,
              ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
          } else {
            ForegroundInfo(DownloadNotificationManager.NOTIFICATION_ID, startingNotification)
          }
        setForeground(foregroundInfo)
      } catch (e: Exception) {}

      try {
        val client = OkHttpClient.Builder()
          .followRedirects(true)
          .followSslRedirects(true)
          .connectTimeout(20, TimeUnit.SECONDS)
          .readTimeout(60, TimeUnit.SECONDS)
          .build()

        val request = Request.Builder()
          .url(apkUrl)
          .header("User-Agent", "Tuneify-App")
          .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
          val code = response.code
          response.close()
          DownloadNotificationManager.showDownloadFailed(
            version,
            context.getString(R.string.server_error, code)
          )
          if (code >= 500) {
            return@withContext Result.retry()
          }
          return@withContext Result.failure()
        }

        val body = response.body ?: return@withContext Result.failure()
        val fileLength = body.contentLength()
        val inputStream = BufferedInputStream(body.byteStream(), 256 * 1024)

        val downloadDir =
          File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "echo_updates")
        if (!downloadDir.exists()) {
          downloadDir.mkdirs()
        }

        val isZip = apkUrl.contains("nightly.link") || apkUrl.endsWith(".zip")
        val downloadFile =
          if (isZip) File(downloadDir, "echo_temp.zip") else File(downloadDir, "echomusic.apk")
        val outputStream = BufferedOutputStream(FileOutputStream(downloadFile), 256 * 1024)

        val buffer = ByteArray(131072)
        var bytesRead: Int
        var totalBytesRead: Long = 0
        var lastProgress = -1
        var lastNotificationTime = 0L

        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
          if (isStopped) {
            outputStream.close()
            inputStream.close()
            response.close()
            if (downloadFile.exists()) {
              downloadFile.delete()
            }
            return@withContext Result.retry()
          }

          outputStream.write(buffer, 0, bytesRead)
          totalBytesRead += bytesRead

          if (fileLength > 0) {
            val progress = (totalBytesRead.toFloat() / fileLength.toFloat() * 100).toInt()
            val currentTime = System.currentTimeMillis()

            if (progress > lastProgress && currentTime - lastNotificationTime >= 500) {
              lastProgress = progress
              lastNotificationTime = currentTime

              val progressNotification =
                DownloadNotificationManager.getDownloadProgressNotification(progress, version)
              val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE)
                  as android.app.NotificationManager
              notificationManager.notify(
                DownloadNotificationManager.NOTIFICATION_ID,
                progressNotification
              )

              setProgress(workDataOf("progress" to progress.toFloat() / 100f))
            }
          }
        }

        outputStream.flush()
        outputStream.close()
        inputStream.close()
        response.close()

        val finalFile =
          if (isZip) {
            val targetApkFile = File(downloadDir, "echomusic.apk")
            var extracted = false
            try {
              ZipInputStream(downloadFile.inputStream()).use { zis ->
                var entry = zis.nextEntry
                while (entry != null) {
                  if (!entry.isDirectory && entry.name.endsWith(".apk")) {
                    FileOutputStream(targetApkFile).use { fos -> zis.copyTo(fos) }
                    extracted = true
                    break
                  }
                  entry = zis.nextEntry
                }
              }
            } catch (e: Exception) {
              if (downloadFile.exists()) downloadFile.delete()
              DownloadNotificationManager.showDownloadFailed(
                version,
                e.message ?: "Failed to extract zip file"
              )
              return@withContext Result.failure()
            } finally {
              if (downloadFile.exists()) {
                downloadFile.delete()
              }
            }
            if (!extracted) {
              DownloadNotificationManager.showDownloadFailed(version, "Could not find APK in zip")
              return@withContext Result.failure()
            }
            targetApkFile
          } else {
            downloadFile
          }

        if (version.startsWith("nightly-r")) {
          val runNumberString = version.removePrefix("nightly-r")
          val runNumber = runNumberString.toIntOrNull()
          if (runNumber != null) {
            val sharedPreferences =
              context.getSharedPreferences("update_settings", Context.MODE_PRIVATE)
            sharedPreferences.edit().putInt("last_installed_nightly_run", runNumber).apply()
          }
        }

        DownloadNotificationManager.showDownloadComplete(version, finalFile.absolutePath)

        Result.success(workDataOf("file_path" to finalFile.absolutePath))
      } catch (e: IOException) {
        DownloadNotificationManager.showDownloadFailed(
          version,
          e.message ?: context.getString(R.string.download_failed)
        )
        return@withContext Result.retry()
      } catch (e: Exception) {
        DownloadNotificationManager.showDownloadFailed(
          version,
          e.message ?: context.getString(R.string.download_failed)
        )
        Result.failure()
      }
    }
}
