package echo.music.iad1tya.ui.theme

import android.graphics.Bitmap
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.rememberDynamicColorScheme
import com.materialkolor.score.Score

val TuneifyObsidian = Color(0xFF7B2CBF)
val DefaultThemeColor = TuneifyObsidian

val ObsidianPurpleBackgroundColor = Color(0xFF09070F)
val ObsidianPurpleSurfaceColor = Color(0xFF100D1A)
val ObsidianPurpleSurfaceContainer = Color(0xFF171324)
val ObsidianPurpleSurfaceVariant = Color(0xFF1F1A30)
val ObsidianPurpleSurfaceHighest = Color(0xFF2B2342)

@Composable
fun echomusicTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  pureBlack: Boolean = false,
  themeColor: Color = DefaultThemeColor,
  content: @Composable () -> Unit,
) {
  val context = LocalContext.current
  val (selectedFontValue) =
    echo.music.iad1tya.utils.rememberPreference(
      echo.music.iad1tya.constants.SelectedFontKey,
      defaultValue = "monument_extended"
    )
  val (customFontPath) =
    echo.music.iad1tya.utils.rememberPreference(
      echo.music.iad1tya.constants.CustomFontPathKey,
      defaultValue = ""
    )

  val activeFontFamily = remember(selectedFontValue, customFontPath) {
    getAppFontFamily(selectedFontValue, customFontPath)
  }

  val typography = remember(activeFontFamily) {
    createAppTypography(displayFontFamily = activeFontFamily, bodyFontFamily = GoogleSansFontFamily)
  }

  val effectiveSeedColor = if (themeColor == Color(0xFF1B1B2F) || themeColor == Color(0xFF00E5FF) || themeColor == Color(0xFF141722)) TuneifyObsidian else themeColor

  val baseColorScheme =
    rememberDynamicColorScheme(
      seedColor = effectiveSeedColor,
      isDark = darkTheme,
      specVersion = ColorSpec.SpecVersion.SPEC_2025,
      style = PaletteStyle.Fidelity
    )

  val colorScheme =
    remember(baseColorScheme, pureBlack, darkTheme) {
      if (darkTheme) {
        val darkBase = if (pureBlack) baseColorScheme.pureBlack(true) else baseColorScheme
        darkBase.copy(
          primary = Color(0xFFE0AAFF),
          onPrimary = Color(0xFF0F0620),
          primaryContainer = Color(0xFF381F66),
          onPrimaryContainer = Color(0xFFF3EEFA),
          background = if (pureBlack) Color.Black else ObsidianPurpleBackgroundColor,
          surface = if (pureBlack) Color.Black else ObsidianPurpleSurfaceColor,
          surfaceVariant = ObsidianPurpleSurfaceVariant,
          surfaceContainer = ObsidianPurpleSurfaceContainer,
          surfaceContainerHigh = ObsidianPurpleSurfaceVariant,
          surfaceContainerHighest = ObsidianPurpleSurfaceHighest,
          onSurface = Color(0xFFF3EEFA),
          onSurfaceVariant = Color(0xFFAFA5C2),
          outline = Color(0xFF453566),
          outlineVariant = Color(0xFF281E3D)
        )
      } else {
        baseColorScheme.copy(
          primary = Color(0xFF7B2CBF),
          onPrimary = Color.White
        )
      }
    }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = typography,
    shapes =
      androidx.compose.material3.MaterialTheme.shapes.copy(
        extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
      ),
    content = content
  )
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
