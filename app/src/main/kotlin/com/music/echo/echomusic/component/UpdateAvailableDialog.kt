package echo.music.iad1tya.echomusic.component

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import echo.music.iad1tya.R
import echo.music.iad1tya.echomusic.updater.ChangelogSection
import echo.music.iad1tya.ui.component.glass.TuneifyGlassTokens
import echo.music.iad1tya.ui.utils.parseMarkdownToSections
import echo.music.iad1tya.ui.utils.parseSimpleMarkdown
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Tuneify In-App Auto-Update Dialog with embedded download manager and direct APK installer.
 */
@Composable
fun UpdateAvailableDialog(
  version: String,
  changelog: List<ChangelogSection>,
  description: String?,
  apkUrl: String? = null,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()

  var isDownloading by remember { mutableStateOf(false) }
  var downloadProgress by remember { mutableFloatStateOf(0f) }
  var downloadError by remember { mutableStateOf<String?>(null) }
  var downloadedFile by remember { mutableStateOf<File?>(null) }
  var isDownloaded by remember { mutableStateOf(false) }

  fun triggerInstall(file: File) {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        if (!context.packageManager.canRequestPackageInstalls()) {
          val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
            data = Uri.parse("package:${context.packageName}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          }
          context.startActivity(intent)
          return
        }
      }

      val contentUri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.FileProvider",
        file
      )

      val installIntent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(contentUri, "application/vnd.android.package-archive")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(installIntent)
    } catch (e: Exception) {
      downloadError = "Installation error: ${e.message}"
    }
  }

  fun startDownload() {
    val downloadEndpoint = apkUrl?.takeIf { it.isNotBlank() }
      ?: "https://github.com/VivxKumar07/Tuneify/releases/download/$version/app-universal-gms-debug.apk"

    isDownloading = true
    downloadError = null
    downloadProgress = 0f

    coroutineScope.launch(Dispatchers.IO) {
      try {
        var currentUrl = downloadEndpoint
        var connection: HttpURLConnection
        var redirects = 0
        while (true) {
          val url = URL(currentUrl)
          connection = url.openConnection() as HttpURLConnection
          connection.instanceFollowRedirects = false
          connection.connectTimeout = 15000
          connection.readTimeout = 15000
          connection.connect()

          val responseCode = connection.responseCode
          if (responseCode in 300..399) {
            val redirectLocation = connection.getHeaderField("Location")
            connection.disconnect()
            if (redirectLocation != null && redirects < 5) {
              currentUrl = redirectLocation
              redirects++
              continue
            }
          }
          break
        }

        val totalLength = connection.contentLengthLong
        val inputStream = connection.inputStream
        val downloadDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "tuneify_updates")
        if (!downloadDir.exists()) {
          downloadDir.mkdirs()
        }

        val outputFile = File(downloadDir, "Tuneify-$version.apk")
        val outputStream = FileOutputStream(outputFile)

        val buffer = ByteArray(8192)
        var bytesRead: Int
        var totalBytesRead: Long = 0

        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
          outputStream.write(buffer, 0, bytesRead)
          totalBytesRead += bytesRead
          if (totalLength > 0) {
            val progress = (totalBytesRead.toFloat() / totalLength.toFloat()).coerceIn(0f, 1f)
            withContext(Dispatchers.Main) {
              downloadProgress = progress
            }
          }
        }

        outputStream.flush()
        outputStream.close()
        inputStream.close()
        connection.disconnect()

        withContext(Dispatchers.Main) {
          isDownloading = false
          isDownloaded = true
          downloadedFile = outputFile
          // Automatically prompt installation
          triggerInstall(outputFile)
        }
      } catch (e: Exception) {
        withContext(Dispatchers.Main) {
          isDownloading = false
          downloadError = e.message ?: "Failed to download update"
        }
      }
    }
  }

  Dialog(
    onDismissRequest = { if (!isDownloading) onDismiss() },
    properties = DialogProperties(dismissOnBackPress = !isDownloading, dismissOnClickOutside = !isDownloading)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp),
      shape = RoundedCornerShape(26.dp),
      color = Color(0xFA111A13),
      border = androidx.compose.foundation.BorderStroke(
        1.dp,
        TuneifyGlassTokens.VoltLime.copy(alpha = 0.35f)
      ),
      tonalElevation = 8.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(TuneifyGlassTokens.VoltLime.copy(alpha = 0.15f))
                .border(0.75.dp, TuneifyGlassTokens.VoltLime.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                painter = painterResource(R.drawable.update),
                contentDescription = null,
                tint = TuneifyGlassTokens.VoltLime,
                modifier = Modifier.size(20.dp)
              )
            }

            Column {
              Text(
                text = "Update Available",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
              Text(
                text = "Tuneify $version",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = TuneifyGlassTokens.VoltLime
              )
            }
          }
        }

        // Changelog Content
        val (effectiveDescription, effectiveSections) = remember(changelog, description) {
          if (changelog.isNotEmpty()) {
            Pair(description?.takeIf { it.isNotBlank() }, changelog)
          } else if (!description.isNullOrBlank()) {
            parseMarkdownToSections(description)
          } else {
            Pair("New performance improvements, UI enhancements, and stability fixes.", emptyList())
          }
        }

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 240.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x25FFFFFF))
            .border(0.5.dp, Color(0x1FFFFFFF), RoundedCornerShape(16.dp))
            .padding(14.dp)
            .verticalScroll(rememberScrollState())
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
              text = "What's New in this Release:",
              style = MaterialTheme.typography.labelLarge,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )

            if (!effectiveDescription.isNullOrBlank()) {
              Text(
                text = parseSimpleMarkdown(effectiveDescription, TuneifyGlassTokens.VoltLime),
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFDDDDDD),
                lineHeight = 18.sp
              )
            }

            if (effectiveSections.isNotEmpty()) {
              effectiveSections.forEach { section ->
                if (section.title.isNotBlank()) {
                  Text(
                    text = section.title,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = TuneifyGlassTokens.VoltLime,
                    modifier = Modifier.padding(top = 4.dp)
                  )
                }
                section.items.forEach { item ->
                  Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                  ) {
                    Text(
                      text = "•",
                      style = MaterialTheme.typography.bodyMedium,
                      color = TuneifyGlassTokens.VoltLime,
                      fontWeight = FontWeight.Bold
                    )
                    Text(
                      text = parseSimpleMarkdown(item.trim(), TuneifyGlassTokens.VoltLime),
                      style = MaterialTheme.typography.bodySmall,
                      color = Color(0xFFCCCCCC),
                      modifier = Modifier.weight(1f)
                    )
                  }
                }
              }
            }
          }
        }

        // Download progress or error
        if (isDownloading) {
          Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Downloading Tuneify update...",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f)
              )
              Text(
                text = "${(downloadProgress * 100).toInt()}%",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = TuneifyGlassTokens.VoltLime
              )
            }

            LinearProgressIndicator(
              progress = { downloadProgress },
              modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
              color = TuneifyGlassTokens.VoltLime,
              trackColor = Color(0x33FFFFFF)
            )
          }
        } else if (downloadError != null) {
          Text(
            text = downloadError ?: "",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error
          )
        }

        // Actions
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.End
        ) {
          if (!isDownloading) {
            TextButton(
              onClick = onDismiss,
              shape = RoundedCornerShape(12.dp)
            ) {
              Text(
                text = "Next time",
                color = Color.White.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium
              )
            }

            Spacer(modifier = Modifier.width(8.dp))
          }

          Button(
            onClick = {
              if (isDownloaded && downloadedFile != null) {
                triggerInstall(downloadedFile!!)
              } else if (!isDownloading) {
                startDownload()
              }
            },
            enabled = !isDownloading,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = TuneifyGlassTokens.VoltLime,
              contentColor = Color.Black
            )
          ) {
            Text(
              text = when {
                isDownloading -> "Downloading..."
                isDownloaded -> "Install Now"
                else -> "Update"
              },
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}
