package echo.music.iad1tya.utils

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager

enum class AppIconType(val value: Int) {
  DEFAULT(0),
  LEGACY(1),
  STATIC(2),
  CAT(3),
  CRAZY_BLUE(4),
  POOKIE(5),
  SKY(6),
  ECHO_CAT(7),
  EKO(8),
  WIERD_CAT(9),
  BILLIE_EILISH(10),
  SABRINA_CARPENTER(11),
  SABRINA_CARPENTER_2(12)
}

object IconUtils {
  fun setIcon(context: Context, iconType: AppIconType) {
    val pkg = context.packageName
    val pm = context.packageManager
    val dynamic = ComponentName(context, "$pkg.MainActivityAlias")
    val static = ComponentName(context, "$pkg.MainActivityStatic")
    val legacy = ComponentName(context, "$pkg.MainActivityLegacy")
    val cat = ComponentName(context, "$pkg.MainActivityCat")
    val crazyBlue = ComponentName(context, "$pkg.MainActivityCrazyBlue")
    val pookie = ComponentName(context, "$pkg.MainActivityPookie")
    val sky = ComponentName(context, "$pkg.MainActivitySky")
    val echoCat = ComponentName(context, "$pkg.MainActivityEchoCat")
    val eko = ComponentName(context, "$pkg.MainActivityEko")
    val wierdCat = ComponentName(context, "$pkg.MainActivityWierdCat")
    val billie = ComponentName(context, "$pkg.MainActivityBillieEilish")
    val sabrina = ComponentName(context, "$pkg.MainActivitySabrina")
    val sabrina2 = ComponentName(context, "$pkg.MainActivitySabrina2")

    fun setComponent(name: ComponentName, enabled: Boolean) {
      try {
        pm.setComponentEnabledSetting(
          name,
          if (enabled) PackageManager.COMPONENT_ENABLED_STATE_ENABLED
          else PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
          PackageManager.DONT_KILL_APP
        )
      } catch (_: Exception) {}
    }

    setComponent(dynamic, iconType == AppIconType.DEFAULT)
    setComponent(legacy, iconType == AppIconType.LEGACY)
    setComponent(static, iconType == AppIconType.STATIC)
    setComponent(cat, iconType == AppIconType.CAT)
    setComponent(crazyBlue, iconType == AppIconType.CRAZY_BLUE)
    setComponent(pookie, iconType == AppIconType.POOKIE)
    setComponent(sky, iconType == AppIconType.SKY)
    setComponent(echoCat, iconType == AppIconType.ECHO_CAT)
    setComponent(eko, iconType == AppIconType.EKO)
    setComponent(wierdCat, iconType == AppIconType.WIERD_CAT)
    setComponent(billie, iconType == AppIconType.BILLIE_EILISH)
    setComponent(sabrina, iconType == AppIconType.SABRINA_CARPENTER)
    setComponent(sabrina2, iconType == AppIconType.SABRINA_CARPENTER_2)
  }
}
