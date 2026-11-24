package echo.music.iad1tya.ui.screens.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import echo.music.iad1tya.ui.theme.TuneifyTokens
import androidx.compose.material3.Surface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.music.innertube.models.AlbumItem
import com.music.innertube.models.ArtistItem
import com.music.innertube.models.PlaylistItem
import com.music.innertube.models.SongItem
import com.music.innertube.utils.YouTubeUrlParser
import echo.music.iad1tya.LocalDatabase
import echo.music.iad1tya.LocalPlayerAwareWindowInsets
import echo.music.iad1tya.LocalPlayerConnection
import echo.music.iad1tya.R
import echo.music.iad1tya.constants.SuggestionItemHeight
import echo.music.iad1tya.models.toMediaMetadata
import echo.music.iad1tya.playback.queues.YouTubeQueue
import echo.music.iad1tya.ui.component.LocalMenuState
import echo.music.iad1tya.ui.component.YouTubeListItem
import echo.music.iad1tya.ui.menu.YouTubeAlbumMenu
import echo.music.iad1tya.ui.menu.YouTubeArtistMenu
import echo.music.iad1tya.ui.menu.YouTubePlaylistMenu
import echo.music.iad1tya.ui.menu.YouTubeSongMenu
import echo.music.iad1tya.utils.getGroupedShape
import echo.music.iad1tya.utils.listItemShape
import echo.music.iad1tya.viewmodels.OnlineSearchSuggestionViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.drop

@OptIn(
  ExperimentalFoundationApi::class,
  ExperimentalComposeUiApi::class,
  ExperimentalMaterial3Api::class,
  FlowPreview::class
)
@Composable
fun OnlineSearchScreen(
  query: String,
  onQueryChange: (TextFieldValue) -> Unit,
  navController: NavController,
  onSearch: (String) -> Unit,
  onDismiss: () -> Unit,
  pureBlack: Boolean,
  viewModel: OnlineSearchSuggestionViewModel = hiltViewModel(),
) {
  val database = LocalDatabase.current
  val keyboardController = LocalSoftwareKeyboardController.current
  val menuState = LocalMenuState.current
  val playerConnection = LocalPlayerConnection.current ?: return

  val scope = rememberCoroutineScope()

  val haptic = LocalHapticFeedback.current
  val isPlaying by playerConnection.isEffectivelyPlaying.collectAsState()
  val mediaMetadata by playerConnection.mediaMetadata.collectAsState()

  val coroutineScope = rememberCoroutineScope()
  val viewState by viewModel.viewState.collectAsState()

  val lazyListState = rememberLazyListState()

  LaunchedEffect(Unit) {
    snapshotFlow { lazyListState.firstVisibleItemScrollOffset }
      .drop(1)
      .collect { keyboardController?.hide() }
  }

  LaunchedEffect(query) {
    if (YouTubeUrlParser.isYouTubeUrl(query)) {
      viewModel.query.value = query
    } else {
      kotlinx.coroutines.delay(300L)
      viewModel.query.value = query
    }
  }

  LazyColumn(
    state = lazyListState,
    contentPadding =
      LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Bottom).asPaddingValues(),
    modifier =
      Modifier.fillMaxSize()
        .background(Color.Transparent)
  ) {
    if (viewState.history.isNotEmpty()) {
      item(key = "history_header") {
        Row(
          modifier =
            Modifier.fillMaxWidth()
              .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
              .animateItem(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = "Recent Searches",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
          )
          Text(
            text = "Clear all",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier =
              Modifier.clickable {
                coroutineScope.launch(Dispatchers.IO) {
                  database.query { viewState.history.forEach { delete(it) } }
                }
              },
          )
        }
      }
    }

    itemsIndexed(viewState.history, key = { _, it -> "history_${it.query}" }) { index, history ->
      SuggestionItem(
        query = history.query,
        online = false,
        shape = getGroupedShape(index, viewState.history.size),
        onClick = {
          onSearch(history.query)
          onDismiss()
        },
        onDelete = { database.query { delete(history) } },
        onFillTextField = {
          onQueryChange(TextFieldValue(history.query, TextRange(history.query.length)))
        },
        modifier = Modifier.animateItem(),
        pureBlack = pureBlack
      )
    }

    if (viewState.history.isNotEmpty() && viewState.suggestions.isNotEmpty()) {
      item(key = "history_suggestion_spacer") {
        Spacer(modifier = Modifier.height(16.dp).animateItem())
      }
    }

    if (viewState.suggestions.isNotEmpty()) {
      item(key = "suggestions_header") {
        Text(
          text = stringResource(R.string.suggestions),
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp).animateItem()
        )
      }
    }

    itemsIndexed(viewState.suggestions, key = { _, it -> "suggestion_$it" }) { index, query ->
      SuggestionItem(
        query = query,
        online = true,
        shape = getGroupedShape(index, viewState.suggestions.size),
        onClick = {
          onSearch(query)
          onDismiss()
        },
        onFillTextField = { onQueryChange(TextFieldValue(query, TextRange(query.length))) },
        modifier = Modifier.animateItem(),
        pureBlack = pureBlack
      )
    }

    if (viewState.suggestions.isNotEmpty()) {
      item(key = "suggestions_bottom_spacer") {
        Spacer(modifier = Modifier.height(16.dp).animateItem())
      }
    }

    if (viewState.items.isNotEmpty()) {
      item(key = "search_divider") {
        Text(
          text =
            stringResource(
              if (viewState.isFromLink) R.string.parsed_from_link else R.string.top_result
            ),
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier.padding(start = 16.dp, top = 8.dp).animateItem()
        )
      }
      item(key = "search_divider_spacer") { Spacer(modifier = Modifier.height(8.dp)) }
    }

    itemsIndexed(viewState.items, key = { _, it -> "item_${it.id}" }) { index, item ->
      YouTubeListItem(
        item = item,
        isActive =
          when (item) {
            is SongItem -> mediaMetadata?.id == item.id
            is AlbumItem -> mediaMetadata?.album?.id == item.id
            else -> false
          },
        isPlaying = isPlaying,
        shape = listItemShape(index, viewState.items.size),
        trailingContent = {
          IconButton(
            onClick = {
              menuState.show {
                when (item) {
                  is SongItem ->
                    YouTubeSongMenu(
                      song = item,
                      navController = navController,
                      onDismiss = {
                        menuState.dismiss()
                        onDismiss()
                      }
                    )
                  is AlbumItem ->
                    YouTubeAlbumMenu(
                      albumItem = item,
                      navController = navController,
                      onDismiss = {
                        menuState.dismiss()
                        onDismiss()
                      }
                    )
                  is ArtistItem ->
                    YouTubeArtistMenu(
                      artist = item,
                      onDismiss = {
                        menuState.dismiss()
                        onDismiss()
                      }
                    )
                  is PlaylistItem ->
                    YouTubePlaylistMenu(
                      playlist = item,
                      coroutineScope = scope,
                      onDismiss = {
                        menuState.dismiss()
                        onDismiss()
                      }
                    )
                }
              }
            }
          ) {
            Icon(painter = painterResource(R.drawable.more_vert), contentDescription = null)
          }
        },
        modifier =
          Modifier.combinedClickable(
              onClick = {
                when (item) {
                  is SongItem -> {
                    if (item.id == mediaMetadata?.id) {
                      playerConnection.togglePlayPause()
                    } else {
                      playerConnection.playQueue(YouTubeQueue.radio(item.toMediaMetadata()))
                      onDismiss()
                    }
                  }
                  is AlbumItem -> {
                    navController.navigate("album/${item.id}")
                    onDismiss()
                  }
                  is ArtistItem -> {
                    navController.navigate("artist/${item.id}")
                    onDismiss()
                  }
                  is PlaylistItem -> {
                    navController.navigate("online_playlist/${item.id}")
                    onDismiss()
                  }
                }
              },
              onLongClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                menuState.show {
                  when (item) {
                    is SongItem ->
                      YouTubeSongMenu(
                        song = item,
                        navController = navController,
                        onDismiss = {
                          menuState.dismiss()
                          onDismiss()
                        }
                      )
                    is AlbumItem ->
                      YouTubeAlbumMenu(
                        albumItem = item,
                        navController = navController,
                        onDismiss = {
                          menuState.dismiss()
                          onDismiss()
                        }
                      )
                    is ArtistItem ->
                      YouTubeArtistMenu(
                        artist = item,
                        onDismiss = {
                          menuState.dismiss()
                          onDismiss()
                        }
                      )
                    is PlaylistItem ->
                      YouTubePlaylistMenu(
                        playlist = item,
                        coroutineScope = coroutineScope,
                        onDismiss = {
                          menuState.dismiss()
                          onDismiss()
                        }
                      )
                  }
                }
              }
            )
            .background(if (pureBlack) Color.Black else MaterialTheme.colorScheme.surface)
            .animateItem()
      )
    }

    if (query.isEmpty()) {
      item(key = "browse_all_header") {
        Text(
          text = "Browse all",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
          modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 12.dp).animateItem(),
        )
      }
      item(key = "browse_all_grid") {
        val isDark = isSystemInDarkTheme()
        Column(
          modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).animateItem(),
          verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            BrowseCategoryTile(
              title = "Songs",
              icon = R.drawable.music_note,
              accentColor = MaterialTheme.colorScheme.primary,
              modifier = Modifier.weight(1f),
              onClick = {
                onSearch("Top Songs")
                onDismiss()
              },
            )
            BrowseCategoryTile(
              title = "Albums",
              icon = R.drawable.album,
              accentColor = MaterialTheme.colorScheme.secondary,
              modifier = Modifier.weight(1f),
              onClick = {
                navController.navigate("new_release")
                onDismiss()
              },
            )
          }
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            BrowseCategoryTile(
              title = "Artists",
              icon = R.drawable.artist,
              accentColor = MaterialTheme.colorScheme.tertiary,
              modifier = Modifier.weight(1f),
              onClick = {
                onSearch("Popular Artists")
                onDismiss()
              },
            )
            BrowseCategoryTile(
              title = "Playlists",
              icon = R.drawable.queue_music,
              accentColor = MaterialTheme.colorScheme.primary,
              modifier = Modifier.weight(1f),
              onClick = {
                navController.navigate("mood_and_genres")
                onDismiss()
              },
            )
          }
        }
      }
    }
  }
}

@Composable
private fun BrowseCategoryTile(
  title: String,
  icon: Int,
  accentColor: Color,
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
) {
  val shape = RoundedCornerShape(16.dp)
  Surface(
    shape = shape,
    color = Color(0x3514261B),
    modifier = modifier
      .clip(shape)
      .background(Color(0x3514261B), shape)
      .clickable(onClick = onClick)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      modifier = Modifier.padding(horizontal = 14.dp, vertical = 16.dp),
    ) {
      Box(
        modifier =
          Modifier.size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(accentColor.copy(alpha = 0.16f)),
        contentAlignment = Alignment.Center,
      ) {
        Icon(
          painter = painterResource(icon),
          contentDescription = null,
          tint = accentColor,
          modifier = Modifier.size(20.dp),
        )
      }
      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
    }
  }
}

@Composable
fun SuggestionItem(
  modifier: Modifier = Modifier,
  query: String,
  online: Boolean,
  shape: Shape,
  onClick: () -> Unit,
  onDelete: () -> Unit = {},
  onFillTextField: () -> Unit,
  pureBlack: Boolean
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier =
      modifier
        .padding(horizontal = 16.dp, vertical = 1.dp)
        .fillMaxWidth()
        .height(SuggestionItemHeight)
        .clip(shape)
        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
        .clickable(onClick = onClick)
        .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)),
  ) {
    Icon(
      painterResource(if (online) R.drawable.search else R.drawable.history),
      contentDescription = null,
      modifier = Modifier.padding(horizontal = 16.dp).alpha(0.5f)
    )

    Text(
      text = query,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      modifier = Modifier.weight(1f),
    )

    if (!online) {
      IconButton(
        onClick = onDelete,
        modifier = Modifier.alpha(0.5f),
      ) {
        Icon(
          painter = painterResource(R.drawable.close),
          contentDescription = null,
        )
      }
    }

    IconButton(
      onClick = onFillTextField,
      modifier = Modifier.alpha(0.5f),
    ) {
      Icon(
        painter = painterResource(R.drawable.arrow_top_left),
        contentDescription = null,
      )
    }
  }
}
