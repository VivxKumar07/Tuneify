package echo.music.iad1tya.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

fun createAppTypography(
  displayFontFamily: FontFamily = GoogleSansFontFamily,
  bodyFontFamily: FontFamily = GoogleSansFontFamily
): Typography =
  Typography(
    displayLarge =
      TextStyle(
        fontFamily = displayFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 50.sp,
        lineHeight = 56.sp,
        letterSpacing = (-0.5).sp
      ),
    displayMedium =
      TextStyle(
        fontFamily = displayFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 38.sp,
        lineHeight = 44.sp,
        letterSpacing = (-0.25).sp
      ),
    displaySmall =
      TextStyle(
        fontFamily = displayFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
      ),
    headlineLarge =
      TextStyle(
        fontFamily = displayFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
      ),
    headlineMedium =
      TextStyle(
        fontFamily = displayFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
      ),
    headlineSmall =
      TextStyle(
        fontFamily = displayFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
      ),
    titleLarge =
      TextStyle(
        fontFamily = displayFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        lineHeight = 23.sp,
        letterSpacing = 0.sp
      ),
    titleMedium =
      TextStyle(
        fontFamily = displayFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        lineHeight = 21.sp,
        letterSpacing = 0.1.sp
      ),
    titleSmall =
      TextStyle(
        fontFamily = bodyFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 19.sp,
        letterSpacing = 0.1.sp
      ),
    bodyLarge =
      TextStyle(
        fontFamily = bodyFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.25.sp
      ),
    bodyMedium =
      TextStyle(
        fontFamily = bodyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.5.sp,
        lineHeight = 19.sp,
        letterSpacing = 0.25.sp
      ),
    bodySmall =
      TextStyle(
        fontFamily = bodyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.3.sp
      ),
    labelLarge =
      TextStyle(
        fontFamily = displayFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.1.sp
      ),
    labelMedium =
      TextStyle(
        fontFamily = bodyFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
      ),
    labelSmall =
      TextStyle(
        fontFamily = bodyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 15.sp,
        letterSpacing = 0.4.sp
      )
  )

val AppTypography = createAppTypography(MonumentExtendedFontFamily, GoogleSansFontFamily)

