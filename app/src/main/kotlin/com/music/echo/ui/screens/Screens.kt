package echo.music.iad1tya.ui.screens

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import echo.music.iad1tya.R

@Immutable
sealed class Screens(
  @StringRes val titleId: Int,
  @DrawableRes val iconIdInactive: Int,
  @DrawableRes val iconIdActive: Int,
  val route: String,
) {
  object Home :
    Screens(
      titleId = R.string.home,
      iconIdInactive = R.drawable.ic_tuneify_nav_home,
      iconIdActive = R.drawable.ic_tuneify_nav_home,
      route = "home"
    )

  object Explore :
    Screens(
      titleId = R.string.explore,
      iconIdInactive = R.drawable.ic_tuneify_nav_explore,
      iconIdActive = R.drawable.ic_tuneify_nav_explore,
      route = "explore"
    )

  object Library :
    Screens(
      titleId = R.string.filter_library,
      iconIdInactive = R.drawable.ic_tuneify_nav_library,
      iconIdActive = R.drawable.ic_tuneify_nav_library,
      route = "library"
    )

  object Search :
    Screens(
      titleId = R.string.search,
      iconIdInactive = R.drawable.ic_tuneify_nav_search,
      iconIdActive = R.drawable.ic_tuneify_nav_search,
      route = "search_input"
    )

  object ListenTogether :
    Screens(
      titleId = R.string.together,
      iconIdInactive = R.drawable.group_outlined,
      iconIdActive = R.drawable.group_filled,
      route = "listen_together"
    )

  object Profile :
    Screens(
      titleId = R.string.account,
      iconIdInactive = R.drawable.account,
      iconIdActive = R.drawable.account,
      route = "profile"
    )

  object Generate :
    Screens(
      titleId = R.string.playlists,
      iconIdInactive = R.drawable.playlist_play,
      iconIdActive = R.drawable.playlist_play,
      route = "generate"
    )

  companion object {
    val MainScreens = listOf(Home, Search, Explore, Library)
  }
}
