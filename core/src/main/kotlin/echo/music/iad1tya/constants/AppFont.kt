package echo.music.iad1tya.constants

enum class AppFont(val value: String) {
  GOOGLE_SANS("google_sans"),
  CHARGER_PRO("charger_pro"),
  MONUMENT_EXTENDED("monument_extended"),
  MONTSERRAT("montserrat"),
  SYSTEM("system"),
  SANS_FLEX("sans_flex"),
  OUTFIT("outfit"),
  PLUS_JAKARTA_SANS("plus_jakarta_sans"),
  CUSTOM("custom");

  companion object {
    fun fromValue(value: String): AppFont = entries.firstOrNull { it.value == value } ?: GOOGLE_SANS
  }
}
