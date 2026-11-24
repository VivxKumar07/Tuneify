package echo.music.iad1tya.utils

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager

/**
 * The launcher-icon variants Tuneify ships. Each one is the same Swiss-Bauhaus "T" mark on a
 * different background from the fixed colour triad, so the app always looks like itself.
 */
enum class AppIconType(val value: Int) {
  DEFAULT(0),
  LIGHT(1),
  YELLOW(2),
  BLUE(3),
  RED(4),
  MONO(5)
}

object IconUtils {
  fun setIcon(context: Context, iconType: AppIconType) {
    val pm = context.packageManager

    val components =
      listOf(
        AppIconType.DEFAULT to ComponentName(context, "echo.music.iad1tya.MainActivityAlias"),
        AppIconType.LIGHT to ComponentName(context, "echo.music.iad1tya.MainActivityLight"),
        AppIconType.YELLOW to ComponentName(context, "echo.music.iad1tya.MainActivityYellow"),
        AppIconType.BLUE to ComponentName(context, "echo.music.iad1tya.MainActivityBlue"),
        AppIconType.RED to ComponentName(context, "echo.music.iad1tya.MainActivityRed"),
        AppIconType.MONO to ComponentName(context, "echo.music.iad1tya.MainActivityMono"),
      )

    components.forEach { (type, component) ->
      pm.setComponentEnabledSetting(
        component,
        if (iconType == type) PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        else PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
        PackageManager.DONT_KILL_APP
      )
    }
  }
}
