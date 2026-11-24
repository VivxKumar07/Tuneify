package echo.music.iad1tya.ui.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import echo.music.iad1tya.LocalPlayerAwareWindowInsets
import echo.music.iad1tya.R
import echo.music.iad1tya.ui.component.IconButton
import echo.music.iad1tya.ui.component.TuneifyEqualizerMark
import echo.music.iad1tya.ui.component.TuneifyTokens
import echo.music.iad1tya.ui.component.claySurface
import echo.music.iad1tya.ui.utils.backToMain

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
  navController: NavController,
  scrollBehavior: TopAppBarScrollBehavior,
  onBack: (() -> Unit)? = null,
  highlightKey: String? = null
) {
  val uriHandler = LocalUriHandler.current
  val isDark = isSystemInDarkTheme()

  val bgColor = if (isDark) TuneifyTokens.DarkBg else TuneifyTokens.LightBg
  val cardBg = if (isDark) TuneifyTokens.DarkSurface else TuneifyTokens.LightCard
  val textPrimary = if (isDark) TuneifyTokens.DarkTextPrimary else TuneifyTokens.LightTextPrimary
  val textSecondary = if (isDark) TuneifyTokens.DarkTextSecondary else TuneifyTokens.LightTextSecondary

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = bgColor,
    
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = stringResource(R.string.about),
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = textPrimary
          )
        },
        navigationIcon = {
          IconButton(
            onClick = { onBack?.invoke() ?: navController.navigateUp() },
            onLongClick = navController::backToMain,
          ) {
            Icon(
              painter = painterResource(R.drawable.arrow_back),
              contentDescription = null,
              tint = textPrimary
            )
          }
        },
        windowInsets = TopAppBarDefaults.windowInsets,
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = bgColor,
          scrolledContainerColor = bgColor,
        ),
        scrollBehavior = scrollBehavior,
      )
    },
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .windowInsetsPadding(
          LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Horizontal)
        ),
      contentPadding = PaddingValues(
        start = 20.dp,
        top = innerPadding.calculateTopPadding() + 8.dp,
        end = 20.dp,
        bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() + 80.dp,
      ),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      // 1. Centered Header Branding
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Tuneify Logo Mark
          Box(
            modifier = Modifier
              .size(76.dp)
              .claySurface(shape = RoundedCornerShape(16.dp), elevation = 0.dp, isDark = isDark),
            contentAlignment = Alignment.Center
          ) {
            TuneifyEqualizerMark(
              modifier = Modifier.size(36.dp),
              barWidth = 5.dp,
              spacing = 3.dp,
              isDark = isDark
            )
          }

          Spacer(Modifier.height(14.dp))

          Text(
            text = "Tuneify",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = textPrimary,
            letterSpacing = (-0.5).sp
          )

          Spacer(Modifier.height(4.dp))

          Surface(
            shape = CircleShape,
            color = if (isDark) Color(0xFF33201D) else Color(0xFFFFECE5),
          ) {
            Text(
              text = "v1.0.0",
              style = MaterialTheme.typography.labelMedium,
              color = if (isDark) TuneifyTokens.DarkCoral else TuneifyTokens.LightCoral,
              fontWeight = FontWeight.SemiBold,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            )
          }

          Spacer(Modifier.height(10.dp))

          Text(
            text = "A modern music player for everyone.\nDiscover, stream and enjoy your favorite music with a clean and beautiful experience.",
            style = MaterialTheme.typography.bodyMedium,
            color = textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp),
            lineHeight = 20.sp
          )
        }
      }

      // 2. Developer & Social Link Rows in Clay Card
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .claySurface(shape = RoundedCornerShape(16.dp), elevation = 0.dp, isDark = isDark)
            .padding(vertical = 8.dp)
        ) {
          AboutLinkRow(
            icon = painterResource(R.drawable.person),
            iconTint = if (isDark) Color(0xFFC7B2FA) else Color(0xFF7E57C2),
            iconBg = if (isDark) Color(0xFF2A2236) else Color(0xFFEDE4FF),
            title = "Developer",
            subtitle = "@VivxKumar07",
            onClick = { uriHandler.openUri("https://github.com/VivxKumar07") },
            isDark = isDark
          )

          AboutDivider(isDark)

          AboutLinkRow(
            icon = painterResource(R.drawable.github),
            iconTint = textPrimary,
            iconBg = if (isDark) Color(0xFF2B2C33) else Color(0xFFF0EBE4),
            title = "GitHub",
            subtitle = "github.com/VivxKumar07",
            onClick = { uriHandler.openUri("https://github.com/VivxKumar07") },
            isDark = isDark
          )

          AboutDivider(isDark)

          AboutLinkRow(
            icon = painterResource(R.drawable.github),
            iconTint = if (isDark) TuneifyTokens.DarkCoral else TuneifyTokens.LightCoral,
            iconBg = if (isDark) Color(0xFF33201D) else Color(0xFFFFECE5),
            title = "Tuneify Repository",
            subtitle = "github.com/VivxKumar07/Tuneify",
            onClick = { uriHandler.openUri("https://github.com/VivxKumar07/Tuneify") },
            isDark = isDark
          )

          AboutDivider(isDark)

          AboutLinkRow(
            icon = painterResource(R.drawable.ic_discord_new),
            iconTint = Color(0xFF5865F2),
            iconBg = if (isDark) Color(0xFF20233D) else Color(0xFFE8EBFF),
            title = "Discord",
            subtitle = "discord.gg/BXddfCWe",
            onClick = { uriHandler.openUri("https://discord.gg/BXddfCWe") },
            isDark = isDark
          )

          AboutDivider(isDark)

          AboutLinkRow(
            icon = painterResource(R.drawable.ic_instagram_new),
            iconTint = Color(0xFFE1306C),
            iconBg = if (isDark) Color(0xFF331E2A) else Color(0xFFFDE8F3),
            title = "Instagram",
            subtitle = "instagram.com/vivxk.kumar/",
            onClick = { uriHandler.openUri("https://www.instagram.com/vivxk.kumar/") },
            isDark = isDark
          )

          AboutDivider(isDark)

          AboutLinkRow(
            icon = painterResource(R.drawable.ic_x_new),
            iconTint = textPrimary,
            iconBg = if (isDark) Color(0xFF2B2C33) else Color(0xFFF0EBE4),
            title = "X (Twitter)",
            subtitle = "x.com/vivxkkumar",
            onClick = { uriHandler.openUri("https://x.com/vivxkkumar") },
            isDark = isDark
          )

          AboutDivider(isDark)

          AboutLinkRow(
            icon = painterResource(R.drawable.ic_youtube_new),
            iconTint = Color(0xFFFF0000),
            iconBg = if (isDark) Color(0xFF331C1C) else Color(0xFFFFE8E8),
            title = "YouTube",
            subtitle = "youtube.com/@OnePeakOG",
            onClick = { uriHandler.openUri("https://www.youtube.com/@OnePeakOG") },
            isDark = isDark
          )
        }
      }

      // 3. License & Legal Attribution
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .claySurface(shape = RoundedCornerShape(16.dp), elevation = 0.dp, isDark = isDark)
            .padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "Tuneify is licensed under GNU GPL v3.0",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = textPrimary,
          )
          Spacer(Modifier.height(4.dp))
          Text(
            text = "Based on Echo-Music / InnerTune open-source project.\nAll copyrighted music and artwork remain property of their respective owners.",
            style = MaterialTheme.typography.bodySmall,
            color = textSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 16.sp
          )
        }
      }
    }
  }
}

@Composable
private fun AboutLinkRow(
  icon: Painter,
  iconTint: Color,
  iconBg: Color,
  title: String,
  subtitle: String,
  onClick: () -> Unit,
  isDark: Boolean,
) {
  val textPrimary = if (isDark) TuneifyTokens.DarkTextPrimary else TuneifyTokens.LightTextPrimary
  val textSecondary = if (isDark) TuneifyTokens.DarkTextSecondary else TuneifyTokens.LightTextSecondary

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(40.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(iconBg),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        painter = icon,
        contentDescription = null,
        tint = iconTint,
        modifier = Modifier.size(20.dp)
      )
    }

    Spacer(Modifier.width(14.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.SemiBold,
        color = textPrimary
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = textSecondary,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }

    Icon(
      painter = painterResource(R.drawable.navigate_next),
      contentDescription = null,
      tint = textSecondary.copy(alpha = 0.6f),
      modifier = Modifier.size(20.dp)
    )
  }
}

@Composable
private fun AboutDivider(isDark: Boolean) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp)
      .height(0.5.dp)
      .background(if (isDark) Color(0x1AFFFFFF) else Color(0x12000000))
  )
}
