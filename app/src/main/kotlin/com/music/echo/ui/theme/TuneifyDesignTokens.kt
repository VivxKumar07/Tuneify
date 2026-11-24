package echo.music.iad1tya.ui.theme

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Tuneify Design System — **Material 3 Expressive, "Deep Forest / Volt Lime"**.
 *
 * The identity in one line: near-black green canvas, deep forest-green tonal surfaces, and a single
 * vibrant lime accent that behaves like a light source. Everything else is quiet.
 *
 * Rules that matter more than the values:
 * 1. **One accent per screen.** Lime is the primary action and the live state. It is never used as
 *    background decoration behind text.
 * 2. **Elevation is soft.** Shadows are wide, low-opacity and tinted by the surface beneath them —
 *    never hard offset blocks. Most separation still comes from tonal steps
 *    (`surfaceContainerLow` → `surfaceContainerHigh`), not from shadows.
 * 3. **Radii are generous and consistent.** Artwork, cards and sheets each own one radius. Avoid
 *    pills except for the navigation indicator and the primary play control.
 * 4. **Gradients are controlled.** At most one subtle radial glow per screen, always low alpha.
 */
object TuneifyTokens {

  // ---------------------------------------------------------------------------------------------
  // Brand core — the fixed identity. Never remapped by the user's theme-colour preference.
  // ---------------------------------------------------------------------------------------------
  object Brand {
    /** The one accent. "Volt" lime: vivid, slightly green-shifted, legible with near-black text. */
    val Lime = Color(0xFFC6F53D)

    /** Pressed / pressed-container lime. */
    val LimeDeep = Color(0xFFA9D62C)

    /** Tonal lime for containers and highlight fills. */
    val LimeSoft = Color(0xFFE9FFB4)

    /** Text and icons placed directly on lime. */
    val OnLime = Color(0xFF0B1408)

    /** Deep forest green — secondary brand surface. */
    val Forest = Color(0xFF1E5B3A)
    val ForestDeep = Color(0xFF0E3324)

    // Harmonised support accents. Same in both themes so colour-blocking stays predictable.
    val Mint = Color(0xFF4FE3A6)
    val Teal = Color(0xFF3ED6D0)
    val Amber = Color(0xFFF5C24B)
    val Coral = Color(0xFFFF6B5A)
    val Sky = Color(0xFF5EA8FF)
    val Violet = Color(0xFFB48CFF)

    // Deep variants, used where the reference blocks colour behind artwork.
    val VioletDeep = Color(0xFF6A4AA0)
    val Magenta = Color(0xFFA2367E)
    val Pink = Color(0xFFFF7FB2)

    /** Dim olive-lime. Only for gradients that fade a lime block toward the canvas. */
    val Olive = Color(0xFF608820)
  }

  object Light {
    val Background = Color(0xFFF4F8F0)
    val Surface = Color(0xFFFFFFFF)
    val Elevated = Color(0xFFE6EFE0)
    val TextPrimary = Color(0xFF0A140E)
    val TextSecondary = Color(0xFF54655A)

    /** Strong outline. Interactive elements only — never on content cards. */
    val Border = Color(0xFF0A140E)

    /** Hairline separator. Card edges, dividers, list rules. */
    val BorderSubtle = Color(0xFFD3E0CC)

    /** Same hairline expressed as alpha, for overlaying arbitrary surfaces. */
    val Hairline = Color(0x1A0A140E)

    val Yellow = Brand.Lime
    val Red = Brand.Coral
    val Blue = Brand.Sky
    val Green = Brand.Mint
    val Purple = Brand.Violet
    val Pink = Color(0xFFFF7FB2)
    val Cyan = Brand.Teal
    val Amber = Brand.Amber
    val OnYellow = Brand.OnLime
  }

  object Dark {
    /** Near-black with a green tint — never pure #000 except in the explicit pure-black mode. */
    val Background = Color(0xFF07120C)
    val Surface = Color(0xFF0C1A12)
    val Elevated = Color(0xFF14251A)
    val SurfaceHigh = Color(0xFF1C3223)
    val TextPrimary = Color(0xFFE9F3E6)
    val TextSecondary = Color(0xFF9DB4A3)

    /** Strong outline. Interactive elements only. */
    val Border = Color(0xFF4A6B55)

    /** Hairline separator. Card edges, dividers, list rules. */
    val BorderSubtle = Color(0xFF1E3326)

    /** The only "loud" border allowed in dark mode: hero artwork frames and focused controls. */
    val BorderHigh = Color(0xFF6E9078)

    val Hairline = Color(0xFF1E3326)

    val Yellow = Brand.Lime
    val Red = Brand.Coral
    val Blue = Brand.Sky
    val Green = Brand.Mint
    val Purple = Brand.Violet
    val Pink = Color(0xFFFF7FB2)
    val Cyan = Brand.Teal
    val Amber = Brand.Amber
    val OnYellow = Brand.OnLime
  }

  // Flat aliases for backwards compatibility.
  val LightBackground = Light.Background
  val LightSurface = Light.Surface
  val LightElevated = Light.Elevated
  val LightTextPrimary = Light.TextPrimary
  val LightTextSecondary = Light.TextSecondary
  val LightBorder = Light.Border
  val LightBorderSubtle = Light.BorderSubtle

  val DarkBackground = Dark.Background
  val DarkSurface = Dark.Surface
  val DarkElevated = Dark.Elevated
  val DarkSurfaceHigh = Dark.SurfaceHigh
  val DarkTextPrimary = Dark.TextPrimary
  val DarkTextSecondary = Dark.TextSecondary
  val DarkBorder = Dark.Border
  val DarkBorderHigh = Dark.BorderHigh

  // The accent. `Lime` is the real name; the legacy aliases below keep older call sites working.
  val Lime = Brand.Lime
  val LimeDeep = Brand.LimeDeep
  val LimeSoft = Brand.LimeSoft
  val OnLime = Brand.OnLime
  val Forest = Brand.Forest
  val ForestDeep = Brand.ForestDeep
  val Mint = Brand.Mint
  val Teal = Brand.Teal
  val Amber = Brand.Amber
  val Coral = Brand.Coral
  val Sky = Brand.Sky
  val Violet = Brand.Violet
  val VioletDeep = Brand.VioletDeep
  val Magenta = Brand.Magenta
  val Olive = Brand.Olive

  val Yellow = Brand.Lime
  val Red = Brand.Coral
  val Blue = Brand.Sky
  val Green = Brand.Mint
  val Purple = Brand.Violet
  val Pink = Color(0xFFFF7FB2)
  val Cyan = Brand.Teal

  // Legacy accent names. Kept so the ~90 existing call sites rebrand without a mechanical rewrite.
  val PrimaryYellow = Lime
  val SecondaryRed = Coral
  val SupportingBlue = Sky
  val SignatureYellow = Lime
  val SignatureRed = Coral
  val SignatureBlue = Sky
  val SignaturePurple = Violet
  val SignatureGreen = Mint
  val SignaturePink = Pink
  val SignatureCyan = Teal

  // On-accent text.
  val OnYellow = OnLime
  val OnRed = Color(0xFF2A0A06)
  val OnBlue = Color(0xFF04182E)
  val OnLimeText = OnLime

  /** Contrast-correct foreground for an arbitrary accent color. */
  fun onAccent(color: Color): Color =
    if (color.luminance() > 0.5f) Color(0xFF0B1408) else Color(0xFFFFFFFF)

  // ---------------------------------------------------------------------------------------------
  // Geometry
  // ---------------------------------------------------------------------------------------------

  /** 0dp — full-bleed dividers and the bottom edge of docking bars. */
  val RadiusSharp = 0.dp

  /** 8dp — thumbnails, badges, chips, small indicators. */
  val RadiusSmall = 8.dp

  /** 14dp — buttons, inputs, list containers, dialogs. */
  val RadiusMedium = 14.dp

  /** 18dp — artwork and content cards. */
  val RadiusCard = 18.dp

  /** 26dp — bottom sheets, the navigation bar and floating player chrome. */
  val RadiusSheet = 26.dp

  /** 26dp — alias for prominent sections (matches [RadiusSheet]). */
  val RadiusLarge = 26.dp

  /** 34dp — hero surfaces and full-width feature blocks. */
  val RadiusXLarge = 34.dp

  /** Effectively circular. Reserved for the play control and the nav indicator. */
  val RadiusPill = 100.dp

  val ShapeSharp = RoundedCornerShape(RadiusSharp)
  val ShapeSmall = RoundedCornerShape(RadiusSmall)
  val ShapeMedium = RoundedCornerShape(RadiusMedium)
  val ShapeCard = RoundedCornerShape(RadiusCard)
  val ShapeSheet = RoundedCornerShape(RadiusSheet)
  val ShapeLarge = RoundedCornerShape(RadiusLarge)
  val ShapeXLarge = RoundedCornerShape(RadiusXLarge)
  val ShapePill = RoundedCornerShape(RadiusPill)

  // Border widths.
  val BorderHairline = 1.dp
  val BorderThin = 1.dp
  val BorderStandard = 1.5.dp
  val BorderThick = 2.dp

  // ---------------------------------------------------------------------------------------------
  // Spacing — everything sits on an 8dp grid so layouts line up by accident.
  // ---------------------------------------------------------------------------------------------
  object Space {
    val Xxs = 2.dp
    val Xs = 4.dp
    val Sm = 8.dp
    val Md = 12.dp
    val Lg = 16.dp
    val Xl = 24.dp
    val Xxl = 32.dp
    val Xxxl = 48.dp

    /** Screen side gutter. */
    val Gutter = 16.dp

    /** Gap between two top-level sections on a screen. */
    val SectionGap = 28.dp

    /** Gap between items inside a grid. */
    val GridGap = 8.dp
  }

  // ---------------------------------------------------------------------------------------------
  // Elevation — soft, wide, low-opacity. Used for the hero surfaces only.
  // ---------------------------------------------------------------------------------------------
  val ShadowOffsetSmall = 2.dp
  val ShadowOffsetStandard = 3.dp
  val ShadowOffsetLarge = 4.dp

  /** Default soft elevation for raised surfaces (navigation bar, mini-player, dialogs). */
  val ElevationLow = 2.dp
  val ElevationMedium = 6.dp
  val ElevationHigh = 14.dp
}

/**
 * Motion tokens. Function over decoration: the app should feel smooth because of how it moves, not
 * because of how many effects it stacks.
 */
object TuneifyMotion {
  /** 140ms — press states, ripples, toggles. */
  const val Fast = 140

  /** 220ms — fades, small position changes, lyric line swaps. */
  const val Base = 220

  /** 320ms — screen and artwork transitions. */
  const val Slow = 320

  /** 480ms — emphasised navigation and shared-element transitions. */
  const val Emphasized = 480

  /** Press feedback: slight overshoot on release, no wobble. */
  fun <T> snappy() = spring<T>(dampingRatio = 0.9f, stiffness = Spring.StiffnessHigh)

  /** The signature expansion used by artwork → player. Soft, confident settle. */
  fun <T> signature() = spring<T>(dampingRatio = 0.82f, stiffness = Spring.StiffnessMediumLow)

  /** Default for layout changes and shared-element transitions. */
  fun <T> standard() = spring<T>(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow)

  /** Expressive settle with a touch of overshoot, for nav indicators and hero reveals. */
  fun <T> expressive() = spring<T>(dampingRatio = 0.65f, stiffness = Spring.StiffnessMedium)

  /** Pressed scale applied by [echo.music.iad1tya.ui.component.tuneifyPressScale]. */
  const val PressScale = 0.96f
}

@Immutable
data class TuneifyColorScheme(
  val background: Color,
  val surface: Color,
  val elevatedSurface: Color,
  val textPrimary: Color,
  val textSecondary: Color,
  val border: Color,
  val borderSubtle: Color,
  val primaryAccent: Color,
  val secondaryAccent: Color,
  val supportingAccent: Color,
  val onPrimary: Color,
  val isDark: Boolean,
)

val LightTuneifyColorScheme =
  TuneifyColorScheme(
    background = TuneifyTokens.LightBackground,
    surface = TuneifyTokens.LightSurface,
    elevatedSurface = TuneifyTokens.LightElevated,
    textPrimary = TuneifyTokens.LightTextPrimary,
    textSecondary = TuneifyTokens.LightTextSecondary,
    border = TuneifyTokens.LightBorder,
    borderSubtle = TuneifyTokens.LightBorderSubtle,
    primaryAccent = TuneifyTokens.Lime,
    secondaryAccent = TuneifyTokens.Coral,
    supportingAccent = TuneifyTokens.Sky,
    onPrimary = TuneifyTokens.OnLime,
    isDark = false,
  )

val DarkTuneifyColorScheme =
  TuneifyColorScheme(
    background = TuneifyTokens.DarkBackground,
    surface = TuneifyTokens.DarkSurface,
    elevatedSurface = TuneifyTokens.DarkElevated,
    textPrimary = TuneifyTokens.DarkTextPrimary,
    textSecondary = TuneifyTokens.DarkTextSecondary,
    border = TuneifyTokens.DarkBorder,
    borderSubtle = TuneifyTokens.Dark.BorderSubtle,
    primaryAccent = TuneifyTokens.Lime,
    secondaryAccent = TuneifyTokens.Coral,
    supportingAccent = TuneifyTokens.Sky,
    onPrimary = TuneifyTokens.OnLime,
    isDark = true,
  )

val LocalTuneifyColors = staticCompositionLocalOf { LightTuneifyColorScheme }

/**
 * Soft, wide elevation behind the element.
 *
 * This replaces the old hard-offset Bauhaus shadow. The offset parameters are kept for call-site
 * compatibility and are now used only to derive how far the surface lifts, so a call site that asked
 * for "2.5dp down-right" now gets a subtle lift instead of a black block.
 */
fun Modifier.neoShadow(
  offsetX: Dp = TuneifyTokens.ShadowOffsetStandard,
  offsetY: Dp = TuneifyTokens.ShadowOffsetStandard,
  color: Color = Color(0xFF000000),
  shape: Shape = TuneifyTokens.ShapeCard,
): Modifier =
  this.shadow(
    elevation = (offsetX + offsetY) * 1.6f,
    shape = shape,
    clip = false,
    ambientColor = color,
    spotColor = color,
  )

fun neoShadow(
  modifier: Modifier = Modifier,
  color: Color = Color(0xFF000000),
  offsetX: Dp = TuneifyTokens.ShadowOffsetStandard,
  offsetY: Dp = TuneifyTokens.ShadowOffsetStandard,
  shape: Shape = TuneifyTokens.ShapeCard,
): Modifier = modifier.neoShadow(offsetX, offsetY, color, shape)

/**
 * The default Tuneify surface: a tonal fill with an optional soft lift and **no outline**.
 * Hierarchy comes from the tonal steps, not from borders.
 */
fun Modifier.neoCard(
  isDark: Boolean,
  prominent: Boolean = false,
  shape: Shape = TuneifyTokens.ShapeCard,
  backgroundColor: Color? = null,
  borderColor: Color? = null,
  withShadow: Boolean = false,
): Modifier {
  val bg =
    backgroundColor
      ?: if (isDark) {
        if (prominent) TuneifyTokens.Dark.SurfaceHigh else TuneifyTokens.Dark.Surface
      } else {
        if (prominent) TuneifyTokens.Light.Elevated else TuneifyTokens.Light.Surface
      }

  return this.then(
      if (withShadow) {
        Modifier.shadow(
          elevation = if (prominent) TuneifyTokens.ElevationHigh else TuneifyTokens.ElevationLow,
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
}

/**
 * A single, low-alpha radial glow. This is the only gradient the system allows behind content — it
 * gives a screen a light source without turning into wallpaper.
 */
fun Modifier.tuneifyGlow(
  color: Color,
  alpha: Float = 0.22f,
  radiusFraction: Float = 0.9f,
): Modifier =
  this.drawBehind {
    drawCircle(
      brush =
        androidx.compose.ui.graphics.Brush.radialGradient(
          colors = listOf(color.copy(alpha = alpha), Color.Transparent),
          center = Offset(size.width * 0.5f, size.height * 0.06f),
          radius = size.maxDimension * radiusFraction,
        ),
      radius = size.maxDimension * radiusFraction,
      center = Offset(size.width * 0.5f, size.height * 0.06f),
    )
  }

/**
 * Halftone dot grid. Retained as an optional texture for empty states and hero surfaces only —
 * never behind text-heavy content.
 */
fun Modifier.halftoneTexture(
  dotColor: Color = Color.Black.copy(alpha = 0.08f),
  dotRadius: Dp = 1.dp,
  spacing: Dp = 5.dp,
): Modifier = this.drawBehind {
  val step = spacing.toPx()
  val radius = dotRadius.toPx()
  var x = step / 2
  while (x < size.width) {
    var y = step / 2
    while (y < size.height) {
      drawCircle(
        color = dotColor,
        radius = radius,
        center = Offset(x, y),
      )
      y += step
    }
    x += step
  }
}
