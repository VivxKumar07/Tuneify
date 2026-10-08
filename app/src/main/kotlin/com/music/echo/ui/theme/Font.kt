package echo.music.iad1tya.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import echo.music.iad1tya.R

val ChargerProFontFamily = FontFamily(
  Font(R.font.charger_pro_nar, FontWeight.Normal),
  Font(R.font.charger_pro_bd_nar, FontWeight.Medium),
  Font(R.font.charger_pro_bd_nar, FontWeight.SemiBold),
  Font(R.font.charger_pro_exbd_nar, FontWeight.Bold),
  Font(R.font.charger_pro_exbd_nar, FontWeight.ExtraBold),
  Font(R.font.charger_pro_exbd_nar, FontWeight.Black),
)

val MonumentExtendedFontFamily = FontFamily(
  Font(R.font.monument_extended_regular, FontWeight.Normal),
  Font(R.font.monument_extended_regular, FontWeight.Medium),
  Font(R.font.monument_extended_bold, FontWeight.SemiBold),
  Font(R.font.monument_extended_bold, FontWeight.Bold),
  Font(R.font.monument_extended_bold, FontWeight.ExtraBold),
  Font(R.font.monument_extended_bold, FontWeight.Black),
)

val GoogleSansFontFamily = FontFamily(
  Font(R.font.google_sans_flex, FontWeight.Normal),
  Font(R.font.google_sans_flex, FontWeight.Medium),
  Font(R.font.google_sans_flex, FontWeight.SemiBold),
  Font(R.font.google_sans_flex, FontWeight.Bold),
)

val bbhBartle = FontFamily(Font(R.font.bbh_bartle_regular, FontWeight.Normal))
val SansFlexFontFamily = FontFamily(Font(R.font.sans_flex))
val OutfitFontFamily = FontFamily(Font(R.font.outfit))
val PlusJakartaSansFontFamily = FontFamily(Font(R.font.plus_jakarta_sans))
val MontserratFontFamily = FontFamily(
  Font(R.font.montserrat, FontWeight.Normal),
  Font(R.font.montserrat, FontWeight.Medium),
  Font(R.font.montserrat, FontWeight.SemiBold),
  Font(R.font.montserrat, FontWeight.Bold),
  Font(R.font.montserrat, FontWeight.ExtraBold),
  Font(R.font.montserrat, FontWeight.Black),
)
val AeonikFontFamily = GoogleSansFontFamily
val TuneifyModernFontFamily = GoogleSansFontFamily

fun getAppFontFamily(selectedFontValue: String?, customFontPath: String? = null): FontFamily {
  return when (selectedFontValue) {
    "google_sans" -> GoogleSansFontFamily
    "charger_pro" -> ChargerProFontFamily
    "monument_extended" -> MonumentExtendedFontFamily
    "montserrat" -> MontserratFontFamily
    "sans_flex" -> SansFlexFontFamily
    "outfit" -> OutfitFontFamily
    "plus_jakarta_sans" -> PlusJakartaSansFontFamily
    "custom" -> {
      try {
        if (!customFontPath.isNullOrEmpty() && java.io.File(customFontPath).exists()) {
          val typeface = android.graphics.Typeface.createFromFile(customFontPath)
          FontFamily(typeface)
        } else {
          GoogleSansFontFamily
        }
      } catch (e: Exception) {
        GoogleSansFontFamily
      }
    }
    else -> GoogleSansFontFamily
  }
}
