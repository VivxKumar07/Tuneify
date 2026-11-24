package echo.music.iad1tya.ui.theme

import android.graphics.Bitmap
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import echo.music.iad1tya.constants.AppFont
import echo.music.iad1tya.constants.SelectedFontKey
import echo.music.iad1tya.utils.rememberPreference
import com.materialkolor.score.Score

/**
 * The one accent the app is allowed to be built around. If the user picks a different accent color
 * in settings we honour it for `primary` only — surfaces, borders and text stay on the fixed
 * deep-forest palette, which is what keeps screens from turning into wallpaper soup.
 */
val DefaultThemeColor = TuneifyTokens.Lime

/** Fixed surface palette. These never change with the user's accent choice. */
private fun tuneifyLightScheme(): ColorScheme =
  lightColorScheme(
    // Lime is a light colour, so on light surfaces the *role* primary is a deep forest-lime that
    // stays readable as text and as a button fill. Lime itself lives in primaryContainer.
    primary = Color(0xFF3F6D12),
    onPrimary = Color.White,
    primaryContainer = TuneifyTokens.LimeSoft,
    onPrimaryContainer = Color(0xFF16230A),
    inversePrimary = TuneifyTokens.Lime,
    secondary = TuneifyTokens.LightTextSecondary,
    onSecondary = Color.White,
    // `secondary` is a *text/neutral* role in Tuneify. Coral lives in the palette as an explicit
    // token (likes, live, errors) rather than as a scheme role, because ~50 call sites across the
    // app used `secondary` for metadata text and would otherwise have turned red.
    secondaryContainer = TuneifyTokens.LightElevated,
    onSecondaryContainer = TuneifyTokens.LightTextPrimary,
    tertiary = Color(0xFF0F6B55),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFB7F5DA),
    onTertiaryContainer = Color(0xFF00291C),
    background = TuneifyTokens.LightBackground,
    onBackground = TuneifyTokens.LightTextPrimary,
    surface = TuneifyTokens.LightSurface,
    onSurface = TuneifyTokens.LightTextPrimary,
    surfaceVariant = TuneifyTokens.LightElevated,
    onSurfaceVariant = TuneifyTokens.LightTextSecondary,
    surfaceTint = TuneifyTokens.Lime,
    surfaceBright = Color(0xFFFFFFFF),
    surfaceDim = Color(0xFFDCE7D6),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF8FBF5),
    surfaceContainer = Color(0xFFF1F6EC),
    surfaceContainerHigh = TuneifyTokens.LightElevated,
    surfaceContainerHighest = Color(0xFFDAE6D3),
    outline = Color(0xFF6E7F73),
    outlineVariant = TuneifyTokens.Light.BorderSubtle,
    inverseSurface = Color(0xFF0C1A12),
    inverseOnSurface = Color(0xFFE9F3E6),
    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
    scrim = Color.Black,
  )

private fun tuneifyDarkScheme(pureBlack: Boolean): ColorScheme {
  val bg = if (pureBlack) Color.Black else TuneifyTokens.DarkBackground
  val surface = if (pureBlack) Color(0xFF050D09) else TuneifyTokens.DarkSurface
  return darkColorScheme(
    primary = TuneifyTokens.Lime,
    onPrimary = TuneifyTokens.OnLime,
    primaryContainer = Color(0xFF33440F),
    onPrimaryContainer = Color(0xFFDDFF9A),
    inversePrimary = Color(0xFF5A7A18),
    secondary = TuneifyTokens.DarkTextSecondary,
    onSecondary = Color(0xFF0C1A12),
    secondaryContainer = TuneifyTokens.Dark.Elevated,
    onSecondaryContainer = TuneifyTokens.DarkTextPrimary,
    tertiary = TuneifyTokens.Mint,
    onTertiary = Color(0xFF04281A),
    tertiaryContainer = Color(0xFF12402C),
    onTertiaryContainer = Color(0xFFB7F5DA),
    background = bg,
    onBackground = TuneifyTokens.DarkTextPrimary,
    surface = surface,
    onSurface = TuneifyTokens.DarkTextPrimary,
    surfaceVariant = TuneifyTokens.Dark.SurfaceHigh,
    onSurfaceVariant = TuneifyTokens.DarkTextSecondary,
    surfaceTint = TuneifyTokens.Lime,
    surfaceBright = Color(0xFF22392A),
    surfaceDim = bg,
    surfaceContainerLowest = if (pureBlack) Color.Black else Color(0xFF050D09),
    surfaceContainerLow = if (pureBlack) Color(0xFF070F0B) else Color(0xFF0A1610),
    surfaceContainer = surface,
    surfaceContainerHigh = TuneifyTokens.Dark.Elevated,
    surfaceContainerHighest = TuneifyTokens.Dark.SurfaceHigh,
    // Deliberately NOT near-white: a bright outline on every card is what made dark mode
    // unreadable before. Cards get tonal separation; only interactive elements get this outline.
    outline = TuneifyTokens.Dark.Border,
    outlineVariant = TuneifyTokens.Dark.BorderSubtle,
    inverseSurface = Color(0xFFE9F3E6),
    inverseOnSurface = Color(0xFF0C1A12),
    error = Color(0xFFFF6B62),
    onError = Color(0xFF3A0A07),
    errorContainer = Color(0xFF4A1512),
    onErrorContainer = Color(0xFFFFB4AD),
    scrim = Color.Black,
  )
}


/** Re-tones only the accent trio so a user-chosen seed never disturbs the surface palette. */
private fun ColorScheme.withAccent(accent: Color, isDark: Boolean): ColorScheme {
  if (accent == DefaultThemeColor) return this
  val container =
    if (isDark) lerp(background, accent, 0.30f) else lerp(Color.White, accent, 0.22f)
  val onContainer =
    if (isDark) accent else lerp(accent, Color.Black, 0.45f)
  return copy(
    primary = accent,
    onPrimary = TuneifyTokens.onAccent(accent),
    primaryContainer = container,
    onPrimaryContainer = onContainer,
    surfaceTint = accent,
    inversePrimary = lerp(accent, Color.Black, 0.3f),
  )
}

@Composable
fun echomusicTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  pureBlack: Boolean = false,
  themeColor: Color = DefaultThemeColor,
  content: @Composable () -> Unit,
) {
  val selectedFontValue by rememberPreference(SelectedFontKey, AppFont.SYSTEM.value)
  val customFontPathValue by rememberPreference(echo.music.iad1tya.constants.CustomFontPathKey, "")

  // The brand face is Outfit by default — a geometric sans in the Bauhaus lineage. Picking a
  // different font in settings overrides both display and body so the user's choice still wins.
  val selectedFont =
    remember(selectedFontValue, customFontPathValue) {
      when (AppFont.fromValue(selectedFontValue)) {
        AppFont.SYSTEM -> null
        AppFont.GOOGLE_SANS -> GoogleSansFontFamily
        AppFont.SANS_FLEX -> SansFlexFontFamily
        AppFont.OUTFIT -> OutfitFontFamily
        AppFont.PLUS_JAKARTA_SANS -> PlusJakartaSansFontFamily
        AppFont.CUSTOM -> {
          try {
            if (customFontPathValue.isNotEmpty() && java.io.File(customFontPathValue).exists()) {
              val typeface = android.graphics.Typeface.createFromFile(customFontPathValue)
              FontFamily(androidx.compose.ui.text.font.Typeface(typeface))
            } else {
              null
            }
          } catch (e: Exception) {
            null
          }
        }
      }
    }

  val brandFont = selectedFont ?: OutfitFontFamily
  val plainFont = selectedFont ?: FontFamily.Default

  val colorScheme =
    remember(darkTheme, pureBlack, themeColor) {
      if (darkTheme) {
        tuneifyDarkScheme(pureBlack).withAccent(themeColor, isDark = true)
      } else {
        tuneifyLightScheme().withAccent(themeColor, isDark = false)
      }
    }

  val tuneifyColors = if (darkTheme) DarkTuneifyColorScheme else LightTuneifyColorScheme

  CompositionLocalProvider(LocalTuneifyColors provides tuneifyColors) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = getTypography(brandFont, plainFont),
      shapes =
        MaterialTheme.shapes.copy(
          extraSmall = RoundedCornerShape(TuneifyTokens.RadiusSmall),
          small = RoundedCornerShape(TuneifyTokens.RadiusSmall),
          medium = RoundedCornerShape(TuneifyTokens.RadiusMedium),
          large = RoundedCornerShape(TuneifyTokens.RadiusSheet),
          extraLarge = RoundedCornerShape(TuneifyTokens.RadiusXLarge),
        ),
      content = content,
    )
  }
}

fun Bitmap.extractThemeColor(): Color {
  val colorsToPopulation =
    Palette.from(this).maximumColorCount(8).generate().swatches.associate {
      it.rgb to it.population
    }
  val rankedColors = Score.score(colorsToPopulation)
  return Color(rankedColors.first())
}

fun Bitmap.extractGradientColors(): List<Color> {
  val extractedColors =
    Palette.from(this).maximumColorCount(64).generate().swatches.associate {
      it.rgb to it.population
    }

  val orderedColors =
    Score.score(extractedColors, 2, 0xff4285f4.toInt(), true).sortedByDescending {
      Color(it).luminance()
    }

  return if (orderedColors.size >= 2) listOf(Color(orderedColors[0]), Color(orderedColors[1]))
  else listOf(Color(0xFF595959), Color(0xFF0D0D0D))
}

fun ColorScheme.pureBlack(apply: Boolean) =
  if (apply) copy(surface = Color.Black, background = Color.Black) else this

val ColorSaver =
  object : Saver<Color, Int> {
    override fun restore(value: Int): Color = Color(value)

    override fun SaverScope.save(value: Color): Int = value.toArgb()
  }
