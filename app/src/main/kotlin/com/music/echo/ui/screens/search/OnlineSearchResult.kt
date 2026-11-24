package echo.music.iad1tya.ui.screens.search

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import echo.music.iad1tya.ui.component.glass.DynamicArtworkBackground
import echo.music.iad1tya.ui.component.glass.LocalDynamicAccentColor
import echo.music.iad1tya.ui.component.glass.TuneifyGlassTokens
import echo.music.iad1tya.ui.theme.TuneifyTokens
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.Alignment
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.music.innertube.YouTube.SearchFilter.Companion.FILTER_ALBUM
import com.music.innertube.YouTube.SearchFilter.Companion.FILTER_ARTIST
import com.music.innertube.YouTube.SearchFilter.Companion.FILTER_COMMUNITY_PLAYLIST
import com.music.innertube.YouTube.SearchFilter.Companion.FILTER_FEATURED_PLAYLIST
import com.music.innertube.YouTube.SearchFilter.Companion.FILTER_SONG
import com.music.innertube.YouTube.SearchFilter.Companion.FILTER_VIDEO
import com.music.innertube.models.AlbumItem
import com.music.innertube.models.ArtistItem
import com.music.innertube.models.PlaylistItem
import com.music.innertube.models.SongItem
import com.music.innertube.models.WatchEndpoint
import com.music.innertube.models.YTItem
import echo.music.iad1tya.LocalDatabase
import echo.music.iad1tya.LocalPlayerConnection
import echo.music.iad1tya.R
import echo.music.iad1tya.constants.MiniPlayerBottomSpacing
import echo.music.iad1tya.constants.MiniPlayerHeight
import echo.music.iad1tya.constants.NavigationBarHeight
import echo.music.iad1tya.constants.PauseSearchHistoryKey
import echo.music.iad1tya.db.entities.SearchHistory
import echo.music.iad1tya.models.toMediaMetadata
import echo.music.iad1tya.playback.queues.YouTubeQueue
import echo.music.iad1tya.ui.component.ChipsRow
import echo.music.iad1tya.ui.component.EmptyPlaceholder
import echo.music.iad1tya.ui.component.LocalMenuState
import echo.music.iad1tya.ui.component.NavigationTitle
import echo.music.iad1tya.ui.component.YouTubeListItem
import echo.music.iad1tya.ui.component.shimmer.ListItemPlaceHolder
import echo.music.iad1tya.ui.component.shimmer.ShimmerHost
import echo.music.iad1tya.ui.menu.YouTubeAlbumMenu
import echo.music.iad1tya.ui.menu.YouTubeArtistMenu
import echo.music.iad1tya.ui.menu.YouTubePlaylistMenu
import echo.music.iad1tya.ui.menu.YouTubeSongMenu
import echo.music.iad1tya.utils.listItemShape
import echo.music.iad1tya.utils.rememberPreference
import echo.music.iad1tya.viewmodels.OnlineSearchViewModel
import java.net.URLDecoder
import java.net.URLEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OnlineSearchResult(
  navController: NavController,
  viewModel: OnlineSearchViewModel = hiltViewModel(),
  pureBlack: Boolean = false
) {
  val database = LocalDatabase.current
  val menuState = LocalMenuState.current
  val playerConnection = LocalPlayerConnection.current ?: return
  val haptic = LocalHapticFeedback.current
  val isPlaying by playerConnection.isEffectivelyPlaying.collectAsState()
  val mediaMetadata by playerConnection.mediaMetadata.collectAsState()

  val coroutineScope = rememberCoroutineScope()
  val lazyListState = rememberLazyListState()
  val focusManager = LocalFocusManager.current
  val focusRequester = remember { FocusRequester() }

  var isSearchFocused by remember { mutableStateOf(false) }

  val pauseSearchHistory by rememberPreference(PauseSearchHistoryKey, defaultValue = false)

  BackHandler(enabled = isSearchFocused) {
    isSearchFocused = false
    focusManager.clearFocus()
  }

  val encodedQuery = navController.currentBackStackEntry?.arguments?.getString("query") ?: ""
  val decodedQuery =
    remember(encodedQuery) {
      try {
        URLDecoder.decode(encodedQuery, "UTF-8")
      } catch (e: Exception) {
        encodedQuery
      }
    }

  var query by
    rememberSaveable(stateSaver = TextFieldValue.Saver) {
      mutableStateOf(TextFieldValue(decodedQuery, TextRange(decodedQuery.length)))
    }

  val keyboardController = LocalSoftwareKeyboardController.current

  val onSearch: (String) -> Unit = remember {
    { searchQuery ->
      if (searchQuery.isNotEmpty()) {
        isSearchFocused = false
        focusManager.clearFocus()
        keyboardController?.hide()

        navController.navigate("search/${URLEncoder.encode(searchQuery.trim(), "UTF-8")}") {
          popUpTo("search/${URLEncoder.encode(decodedQuery, "UTF-8")}") { inclusive = true }

          if (!pauseSearchHistory) {
            coroutineScope.launch(Dispatchers.IO) {
              database.query { insert(SearchHistory(query = searchQuery)) }
            }
          }
        }
      }
    }
  }

  LaunchedEffect(decodedQuery) {
    query = TextFieldValue(decodedQuery, TextRange(decodedQuery.length))
  }

  val searchFilter by viewModel.filter.collectAsState()
  val searchSummary = viewModel.summaryPage
  val itemsPage by
    remember(searchFilter) {
      derivedStateOf { searchFilter?.value?.let { viewModel.viewStateMap[it] } }
    }

  LaunchedEffect(lazyListState) {
    snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo.any { it.key == "loading" } }
      .collect { shouldLoadMore ->
        if (!shouldLoadMore) return@collect
        viewModel.loadMore()
      }
  }

  val ytItemContent: @Composable LazyItemScope.(YTItem, Int, Int) -> Unit =
    { item: YTItem, index: Int, size: Int ->
      val longClick = {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        menuState.show {
          when (item) {
            is SongItem ->
              YouTubeSongMenu(
                song = item,
                navController = navController,
                onDismiss = menuState::dismiss,
              )
            is AlbumItem ->
              YouTubeAlbumMenu(
                albumItem = item,
                navController = navController,
                onDismiss = menuState::dismiss,
              )
            is ArtistItem ->
              YouTubeArtistMenu(
                artist = item,
                onDismiss = menuState::dismiss,
              )
            is PlaylistItem ->
              YouTubePlaylistMenu(
                playlist = item,
                coroutineScope = coroutineScope,
                onDismiss = menuState::dismiss,
              )
          }
        }
      }
      val isItemActive = when (item) {
        is SongItem -> mediaMetadata?.id == item.id
        is AlbumItem -> mediaMetadata?.album?.id == item.id
        else -> false
      }
      val cardShape = RoundedCornerShape(16.dp)

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp)
          .shadow(elevation = 6.dp, shape = cardShape, ambientColor = Color(0x60000000), spotColor = Color(0x40000000))
          .clip(cardShape)
          .background(
            if (isItemActive) Brush.linearGradient(listOf(Color(0x55285438), Color(0x35163020)))
            else Brush.linearGradient(listOf(Color(0x38183022), Color(0x220D1A12))),
            cardShape
          )
          .border(
            width = 1.dp,
            brush = Brush.linearGradient(
              if (isItemActive) listOf(LocalDynamicAccentColor.current.copy(0.7f), Color(0x20FFFFFF))
              else listOf(Color(0x50FFFFFF), Color(0x10FFFFFF))
            ),
            shape = cardShape
          )
          .combinedClickable(
            onClick = {
              when (item) {
                is SongItem -> {
                  if (item.id == mediaMetadata?.id) {
                    playerConnection.togglePlayPause()
                  } else {
                    playerConnection.playQueue(
                      YouTubeQueue(WatchEndpoint(videoId = item.id), item.toMediaMetadata())
                    )
                  }
                }
                is AlbumItem -> navController.navigate("album/${item.id}")
                is ArtistItem -> navController.navigate("artist/${item.id}")
                is PlaylistItem -> navController.navigate("online_playlist/${item.id}")
              }
            },
            onLongClick = longClick,
          )
          .padding(horizontal = 12.dp, vertical = 10.dp)
          .animateItem()
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth()
        ) {
          // Thumbnail
          Box(
            modifier = Modifier
              .size(52.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFF102418))
          ) {
            if (!item.thumbnail.isNullOrBlank()) {
              AsyncImage(
                model = item.thumbnail,
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
              )
            }
          }

          Spacer(Modifier.width(14.dp))

          // Title and Subtitle
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = item.title,
              color = if (isItemActive) TuneifyGlassTokens.VoltLime else Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(3.dp))
            val subtitle = when (item) {
              is SongItem -> item.artists.joinToString { it.name }
              is AlbumItem -> item.artists?.joinToString { it.name } ?: "Album"
              is ArtistItem -> "Artist"
              is PlaylistItem -> item.author?.name ?: "Playlist"
              else -> ""
            }
            Text(
              text = subtitle,
              color = TuneifyGlassTokens.TextSecondary,
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }

          Spacer(Modifier.width(8.dp))

          // Trailing options button
          IconButton(onClick = longClick) {
            Icon(
              painter = painterResource(R.drawable.more_vert),
              contentDescription = null,
              tint = Color.White.copy(alpha = 0.7f),
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }
    }

  val dynamicAccent = LocalDynamicAccentColor.current

  DynamicArtworkBackground(accentColor = dynamicAccent) {
    Column(
      modifier =
        Modifier.fillMaxSize()
          .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top))
    ) {
      val isDark = isSystemInDarkTheme()
      OutlinedTextField(
        value = query,
        onValueChange = { newQuery -> query = newQuery },
        placeholder = {
          Text(
            text = stringResource(R.string.search_yt_music),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.6f)
          )
        },
        leadingIcon = {
          IconButton(onClick = { navController.navigateUp() }) {
            Icon(
              painter = painterResource(R.drawable.arrow_back),
              contentDescription = stringResource(R.string.dismiss),
              tint = dynamicAccent
            )
          }
        },
        trailingIcon = {
          if (query.text.isNotEmpty()) {
            IconButton(onClick = { query = TextFieldValue("") }) {
              Icon(
                painter = painterResource(R.drawable.close),
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.7f)
              )
            }
          }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch(query.text) }),
        singleLine = true,
        shape = RoundedCornerShape(24.dp),
        colors =
          OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0x40163022),
            unfocusedContainerColor = Color(0x3012241A),
            focusedBorderColor = TuneifyGlassTokens.VoltLime.copy(alpha = 0.8f),
            unfocusedBorderColor = Color(0x35FFFFFF),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          ),
        modifier =
          Modifier.fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .focusRequester(focusRequester)
            .onFocusChanged { focusState ->
              if (focusState.isFocused) {
                isSearchFocused = true
              }
            }
      )

    Box(modifier = Modifier.weight(1f)) {
      Column(modifier = Modifier.fillMaxWidth()) {
        ChipsRow(
          chips =
            listOf(
              null to stringResource(R.string.filter_all),
              FILTER_SONG to stringResource(R.string.filter_songs),
              FILTER_VIDEO to stringResource(R.string.filter_videos),
              FILTER_ALBUM to stringResource(R.string.filter_albums),
              FILTER_ARTIST to stringResource(R.string.filter_artists),
              FILTER_COMMUNITY_PLAYLIST to stringResource(R.string.filter_community_playlists),
              FILTER_FEATURED_PLAYLIST to stringResource(R.string.filter_featured_playlists),
            ),
          currentValue = searchFilter,
          onValueUpdate = {
            if (viewModel.filter.value != it) {
              viewModel.filter.value = it
            }
            coroutineScope.launch { lazyListState.animateScrollToItem(0) }
          },
          modifier = Modifier.fillMaxWidth()
        )

        LazyColumn(
          state = lazyListState,
          contentPadding = WindowInsets.systemBars.only(WindowInsetsSides.Bottom).asPaddingValues(),
          modifier = Modifier.fillMaxWidth()
        ) {
          if (searchFilter == null) {
            searchSummary?.summaries?.forEach { summary ->
              item { NavigationTitle(summary.title) }

              itemsIndexed(
                items = summary.items,
                key = { index, item -> "${summary.title}/${item.id}/$index" },
              ) { index, item ->
                ytItemContent(item, index, summary.items.size)
              }
            }

            if (searchSummary?.summaries?.isEmpty() == true) {
              item {
                EmptyPlaceholder(
                  icon = R.drawable.search,
                  text = stringResource(R.string.no_results_found),
                )
              }
            }
          } else {
            itemsIndexed(
              items = itemsPage?.items.orEmpty().distinctBy { it.id },
              key = { _, it -> "filtered_${it.id}" },
            ) { index, item ->
              ytItemContent(item, index, itemsPage?.items.orEmpty().distinctBy { it.id }.size)
            }

            if (itemsPage?.continuation != null) {
              item(key = "loading") { ShimmerHost { repeat(3) { ListItemPlaceHolder() } } }
            }

            if (itemsPage?.items?.isEmpty() == true) {
              item {
                EmptyPlaceholder(
                  icon = R.drawable.search,
                  text = stringResource(R.string.no_results_found),
                )
              }
            }
          }

          if (
            searchFilter == null && searchSummary == null ||
              searchFilter != null && itemsPage == null
          ) {
            item { ShimmerHost { repeat(8) { ListItemPlaceHolder() } } }
          }

          item(key = "bottom_spacer") {
            Spacer(
              modifier =
                Modifier.height(MiniPlayerHeight + MiniPlayerBottomSpacing + NavigationBarHeight)
            )
          }
        }
      }
      if (isSearchFocused) {
        OnlineSearchScreen(
          query = query.text,
          onQueryChange = { query = it },
          navController = navController,
          onSearch = onSearch,
          onDismiss = {
            isSearchFocused = false
            focusManager.clearFocus()
          },
          pureBlack = pureBlack
        )
      }
    }
  }
}
}
