package echo.music.iad1tya.echomusic.updater.downloadmanager

import android.content.Context
import android.os.Environment
import echo.music.iad1tya.R
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request

class CustomDownloadManager {
  private var downloadJob: Job? = null
  private var isPaused = false

  fun downloadApk(
    context: Context,
    apkUrl: String,
    onProgress: (Float) -> Unit,
    onDownloadComplete: (File) -> Unit,
    onError: (String) -> Unit,
  ) {
    downloadJob?.cancel()
    isPaused = false

    downloadJob =
      CoroutineScope(Dispatchers.IO).launch {
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
            withContext(Dispatchers.Main) {
              onError("Server returned HTTP $code")
            }
            return@launch
          }

          val body = response.body ?: throw IllegalStateException("Empty response body")
          val fileLength = body.contentLength()
          val inputStream = BufferedInputStream(body.byteStream(), 256 * 1024)

          val downloadDir =
            File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "echo_updates")
          if (!downloadDir.exists()) {
            downloadDir.mkdirs()
          }

          val outputFile = File(downloadDir, "echomusic.apk")
          val outputStream = BufferedOutputStream(FileOutputStream(outputFile), 256 * 1024)

          val buffer = ByteArray(131072)
          var bytesRead: Int
          var totalBytesRead: Long = 0
          var lastProgressTime = 0L

          while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            if (isPaused) {
              outputStream.close()
              inputStream.close()
              response.close()
              return@launch
            }

            outputStream.write(buffer, 0, bytesRead)
            totalBytesRead += bytesRead

            if (fileLength > 0) {
              val now = System.currentTimeMillis()
              if (now - lastProgressTime >= 100L || totalBytesRead == fileLength) {
                lastProgressTime = now
                val progress = totalBytesRead.toFloat() / fileLength.toFloat()
                withContext(Dispatchers.Main) { onProgress(progress) }
              }
            }
          }

          outputStream.flush()
          outputStream.close()
          inputStream.close()
          response.close()

          withContext(Dispatchers.Main) { onDownloadComplete(outputFile) }
        } catch (e: Exception) {
          withContext(Dispatchers.Main) {
            onError(e.message ?: context.getString(R.string.download_failed))
          }
        }
      }
  }

  fun pauseDownload() {
    isPaused = true
    downloadJob?.cancel()
  }

  fun cancelDownload() {
    downloadJob?.cancel()
    downloadJob = null
  }
}
