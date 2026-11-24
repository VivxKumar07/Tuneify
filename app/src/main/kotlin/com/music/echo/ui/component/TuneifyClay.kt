package echo.music.iad1tya.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import echo.music.iad1tya.ui.theme.TuneifyTokens as CoreTuneifyTokens

/**
 * Compatibility token shim.
 *
 * The names below predate the "Deep Forest / Volt Lime" identity, so they are kept only so older
 * call sites keep compiling. New code should use [CoreTuneifyTokens] directly.
 */
object TuneifyTokens {
  // Light Palette
  val LightBg = CoreTuneifyTokens.LightBackground
  val LightSurface = CoreTuneifyTokens.LightSurface
  val LightSurfaceContainer = CoreTuneifyTokens.LightElevated
  val LightElevated = CoreTuneifyTokens.LightElevated
  val LightSurfaceContainerHigh = CoreTuneifyTokens.Light.Elevated
  val LightCard = CoreTuneifyTokens.LightSurface
  val LightTextPrimary = CoreTuneifyTokens.LightTextPrimary
  val LightTextSecondary = CoreTuneifyTokens.LightTextSecondary
  val LightTextTertiary = Color(0xFF6E7F73)
  val LightBorder = CoreTuneifyTokens.LightBorder

  // Dark Palette
  val DarkBg = CoreTuneifyTokens.DarkBackground
  val DarkSurface = CoreTuneifyTokens.DarkSurface
  val DarkSurfaceContainer = CoreTuneifyTokens.DarkElevated
  val DarkSurfaceContainerHigh = CoreTuneifyTokens.Dark.SurfaceHigh
  val DarkCard = CoreTuneifyTokens.DarkSurface
  val DarkTextPrimary = CoreTuneifyTokens.DarkTextPrimary
  val DarkTextSecondary = CoreTuneifyTokens.DarkTextSecondary
  val DarkTextTertiary = Color(0xFF7E9486)
  val DarkBorder = CoreTuneifyTokens.DarkBorder

  // Brand accents
  val PrimaryYellow = CoreTuneifyTokens.Lime
  val SecondaryRed = CoreTuneifyTokens.Coral
  val SupportingBlue = CoreTuneifyTokens.Sky

  // Aliases for compatibility
  val Coral = CoreTuneifyTokens.Coral
  val LightCoral = CoreTuneifyTokens.Coral
  val DarkCoral = CoreTuneifyTokens.Coral
  val Peach = CoreTuneifyTokens.Lime
  val LightPeach = CoreTuneifyTokens.Lime
  val DarkPeach = CoreTuneifyTokens.Lime
  val Salmon = CoreTuneifyTokens.Coral
  val LightSalmon = CoreTuneifyTokens.Coral
  val DarkSalmon = CoreTuneifyTokens.Coral
  val Yellow = CoreTuneifyTokens.Lime
  val Mint = CoreTuneifyTokens.Mint
  val PaleBlue = CoreTuneifyTokens.Sky
  val Lilac = CoreTuneifyTokens.Violet
  val DarkSurfaceElevated = DarkSurfaceContainer
  val LightSurfaceCard = LightSurface

  // Category and chips (harmonised with the forest palette)
  val ChipFeelGoodLight = Color(0xFFE9FFB4)
  val TextFeelGoodLight = Color(0xFF111111)
  val ChipRomanceLight = Color(0xFFFFDAD4)
  val TextRomanceLight = Color(0xFF111111)
  val ChipRelaxLight = Color(0xFFD6E4FF)
  val TextRelaxLight = Color(0xFF111111)
  val ChipPartyLight = Color(0xFFE9FFB4)
  val TextPartyLight = Color(0xFF111111)
  val ChipEnergyLight = Color(0xFFFFDAD4)
  val TextEnergyLight = Color(0xFF111111)

  val ChipFeelGoodDark = Color(0xFF2A3A12)
  val TextFeelGoodDark = Color(0xFFC6F53D)
  val ChipRomanceDark = Color(0xFF3A1E1A)
  val TextRomanceDark = Color(0xFFFF8A80)
  val ChipRelaxDark = Color(0xFF17263A)
  val TextRelaxDark = Color(0xFF93B2FF)
  val ChipPartyDark = Color(0xFF2A3A12)
  val TextPartyDark = Color(0xFFC6F53D)
  val ChipEnergyDark = Color(0xFF3A1E1A)
  val TextEnergyDark = Color(0xFFFF8A80)
}

/** Soft tonal surface with an optional lift. No hard offset shadows. */
fun Modifier.claySurface(
  color: Color,
  cornerRadius: Dp = 14.dp,
  elevation: Dp = 2.dp,
  isDark: Boolean = false,
): Modifier = neoSurface(
  shape = RoundedCornerShape(cornerRadius),
  elevation = elevation,
  isDark = isDark,
  backgroundColor = color,
)

fun Modifier.claySurface(
  shape: Shape = RoundedCornerShape(14.dp),
  elevation: Dp = 2.dp,
  isDark: Boolean = false,
  backgroundColor: Color? = null,
): Modifier = neoSurface(
  shape = shape,
  elevation = elevation,
  isDark = isDark,
  backgroundColor = backgroundColor,
)

fun Modifier.neoSurface(
  shape: Shape = RoundedCornerShape(14.dp),
  elevation: Dp = 2.dp,
  isDark: Boolean = false,
  backgroundColor: Color? = null,
  borderColor: Color? = null,
  borderWidth: Dp = 1.dp,
): Modifier {
  val bg =
    backgroundColor
      ?: if (isDark) CoreTuneifyTokens.Dark.SurfaceHigh else CoreTuneifyTokens.Light.Elevated

  return this
    .then(
      if (elevation > 0.dp) {
        Modifier.shadow(
          elevation = elevation * 2,
          shape = shape,
          clip = false,
          ambientColor = Color.Black,
          spotColor = Color.Black,
        )
      } else {
        Modifier
      }
    )
    .clip(shape)
    .background(bg, shape)
    .then(
      if (borderColor != null) Modifier.border(borderWidth, borderColor, shape) else Modifier
    )
}

/**
 * The Tuneify brand mark. Retained under the old name so existing call sites rebrand automatically.
 */
@Composable
fun TuneifyEqualizerMark(
  modifier: Modifier = Modifier,
  barWidth: Dp = 4.dp,
  spacing: Dp = 3.dp,
  isDark: Boolean = isSystemInDarkTheme(),
) {
  TuneifyMark(
    modifier = modifier,
    size = 30.dp,
    tint = MaterialTheme.colorScheme.primary,
  )
}

/**
 * Tuneify lockup: brand mark + wordmark.
 */
@Composable
fun TuneifyLogo(
  modifier: Modifier = Modifier,
  isDark: Boolean = isSystemInDarkTheme(),
  fontSize: Dp = 22.dp,
) {
  TuneifyWordmark(
    modifier = modifier,
    markSize = fontSize + 4.dp,
    fontSize = fontSize,
    markTint = MaterialTheme.colorScheme.primary,
    textColor = MaterialTheme.colorScheme.onSurface,
  )
}

/**
 * Segmented control for Player: "Song", "Lyrics", "Queue".
 *
 * A tonal container with a single lime indicator that slides between segments. The indicator is the
 * only place a pill is allowed outside the navigation bar, and it carries the brand accent so the
 * active section is unmissable.
 */
@Composable
fun PlayerSegmentedTabs(
  selectedTab: Int,
  onTabSelected: (Int) -> Unit,
  modifier: Modifier = Modifier,
  isDark: Boolean = isSystemInDarkTheme(),
) {
  val containerBg = MaterialTheme.colorScheme.surfaceContainerHigh
  val containerShape = RoundedCornerShape(16.dp)
  val segmentShape = RoundedCornerShape(12.dp)
  val inactiveTextColor = MaterialTheme.colorScheme.onSurfaceVariant

  Row(
    modifier = modifier
      .clip(containerShape)
      .background(containerBg, containerShape)
      .padding(4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    listOf("Song", "Lyrics", "Queue").forEachIndexed { index, title ->
      val isSelected = selectedTab == index

      val bg by
        animateColorAsState(
          targetValue = if (isSelected) CoreTuneifyTokens.Lime else Color.Transparent,
          animationSpec = tween(durationMillis = 180),
          label = "segmentBg"
        )
      val fg by
        animateColorAsState(
          targetValue = if (isSelected) CoreTuneifyTokens.OnLime else inactiveTextColor,
          animationSpec = tween(durationMillis = 180),
          label = "segmentFg"
        )

      Box(
        modifier =
          Modifier
            .clip(segmentShape)
            .background(bg, segmentShape)
            .clickable { onTabSelected(index) }
            .padding(horizontal = 18.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = title,
          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
          fontSize = 13.sp,
          color = fg,
          letterSpacing = 0.1.sp
        )
      }
    }
  }
}

/** Small helper used by the player chrome: an animated lime dot that marks the live track. */
@Composable
fun TuneifyLiveDot(modifier: Modifier = Modifier, size: Dp = 8.dp) {
  val animatedSize by
    animateDpAsState(targetValue = size, animationSpec = tween(200), label = "liveDot")
  Box(
    modifier
      .size(animatedSize)
      .clip(RoundedCornerShape(50))
      .background(CoreTuneifyTokens.Lime)
  )
}
