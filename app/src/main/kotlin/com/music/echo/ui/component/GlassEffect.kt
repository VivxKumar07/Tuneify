/** vivimusic Project (C) 2026 Licensed under GPL-3.0 | See git history for contributors */
package echo.music.iad1tya.ui.component

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp

/**
 * Tuneify (Swiss-Bauhaus): **backdrop blur is retired.**
 *
 * This file used to vendor a full `RenderEffect` glass pipeline (31 files under
 * `ui/component/backdrop/`) that re-recorded and downsampled the app subtree every frame. It was the
 * single biggest source of dropped frames on mid-range GPUs, and it muddied the fixed yellow/red/blue
 * triad into grey.
 *
 * What remains is a deliberately small compatibility surface: the config object the settings
 * preferences still feed, and a [liquidGlass] modifier that paints a flat theme-aware surface. Call
 * sites can be migrated to [echo.music.iad1tya.ui.theme.TuneifyTokens] surfaces screen by screen
 * without breaking the build at any point.
 */
@Stable
data class GlassEffectConfig(
  val globalEnabled: Boolean = false,
  val vibrancy: Float = 1f,
  /** Retained only so existing preferences deserialize; unused by the flat surface. */
  val blurRadius: Float = 8f,
  val lensHeight: Float = 0.5f,
  val lensAmount: Float = 0.5f,
  val chromaticAberration: Boolean = true,
  val depthEffect: Boolean = true,
  /** [Color.Unspecified] means adaptive: light surface on light theme, dark on dark. */
  val surfaceTintColor: Color = Color.Unspecified,
  val surfaceOpacity: Float = 0.4f,
  val textColor: Color = Color.Unspecified,
  val playerEnabled: Boolean = true,
  val miniPlayerEnabled: Boolean = true,
  val navBarEnabled: Boolean = true,
) {
  /**
   * Whether the legacy glass treatment should be requested for [component], taking the master
   * switch and the per-component switch into account. Always false while the shim is in place —
   * callers that branch on this fall through to their flat surface path.
   */
  fun isEnabledFor(component: GlassComponent): Boolean = false
}

/** UI surfaces that used to opt in or out of the glass effect. */
enum class GlassComponent {
  PLAYER,
  MINI_PLAYER,
  NAV_BAR,
}

val LocalGlassEffectConfig = staticCompositionLocalOf { GlassEffectConfig() }

/**
 * Always true now: the flat surface path needs no `RenderEffect` support check. Kept so existing
 * call sites that gate on it keep compiling.
 */
fun isGlassSupported(sdkInt: Int = Build.VERSION.SDK_INT): Boolean = sdkInt >= 0

/**
 * Paints a flat, theme-aware surface in place of the old liquid glass. Same shape, no per-frame
 * backdrop cost.
 */
@Composable
fun Modifier.liquidGlass(
  config: GlassEffectConfig,
  shape: CornerBasedShape = RoundedCornerShape(0.dp),
  applyEdgeEffects: Boolean = true,
  blurRadiusDp: Float = config.blurRadius,
): Modifier {
  val surface = MaterialTheme.colorScheme.surfaceContainerHigh
  val tint =
    when {
      config.surfaceTintColor.isSpecified -> config.surfaceTintColor
      MaterialTheme.colorScheme.surface.luminance() > 0.5f -> Color(0xFFFAF8F3)
      else -> Color(0xFF1B1B1B)
    }
  val opacity = config.surfaceOpacity.coerceIn(0f, 1f).takeIf { it > 0f } ?: 1f
  return this.clip(shape).background(lerp(surface, tint, opacity), shape)
}
