package echo.music.iad1tya.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.music.innertube.models.SongItem
import com.music.innertube.models.WatchEndpoint
import echo.music.iad1tya.LocalPlayerAwareWindowInsets
import echo.music.iad1tya.LocalPlayerConnection
import echo.music.iad1tya.R
import echo.music.iad1tya.constants.ListItemHeight
import echo.music.iad1tya.models.toMediaMetadata
import echo.music.iad1tya.playback.queues.YouTubeQueue
import echo.music.iad1tya.ui.component.LocalMenuState
import echo.music.iad1tya.ui.component.NavigationTitle
import echo.music.iad1tya.ui.theme.TuneifyTokens
import echo.music.iad1tya.ui.component.YouTubeGridItem
import echo.music.iad1tya.ui.component.YouTubeListItem
import echo.music.iad1tya.ui.component.shimmer.GridItemPlaceHolder
import echo.music.iad1tya.ui.component.shimmer.ShimmerHost
import echo.music.iad1tya.ui.component.shimmer.TextPlaceholder
import echo.music.iad1tya.ui.menu.YouTubeAlbumMenu
import echo.music.iad1tya.ui.menu.YouTubeSongMenu
import echo.music.iad1tya.ui.utils.SnapLayoutInfoProvider
import echo.music.iad1tya.utils.listItemShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import echo.music.iad1tya.ui.component.ChipsRow
import echo.music.iad1tya.ui.component.glass.DynamicArtworkBackground
import echo.music.iad1tya.ui.component.glass.LocalDynamicAccentColor
import echo.music.iad1tya.ui.component.glass.ExploreFeaturedBanner
import echo.music.iad1tya.ui.component.glass.ExpressiveSectionHeader
import echo.music.iad1tya.ui.component.glass.GlassButton
import echo.music.iad1tya.ui.component.glass.GlassPill
import echo.music.iad1tya.ui.component.glass.GlassSongRow
import echo.music.iad1tya.ui.component.glass.TuneifyGlassTokens
import echo.music.iad1tya.viewmodels.ChartsViewModel
import echo.music.iad1tya.viewmodels.ExploreViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ExploreScreen(
  navController: NavController,
  exploreViewModel: ExploreViewModel = hiltViewModel(),
  chartsViewModel: ChartsViewModel = hiltViewModel(),
) {
  val menuState = LocalMenuState.current
  val haptic = LocalHapticFeedback.current
  val playerConnection = LocalPlayerConnection.current ?: return
  val isPlaying by playerConnection.isEffectivelyPlaying.collectAsState()
  val mediaMetadata by playerConnection.mediaMetadata.collectAsState()

  val explorePage by exploreViewModel.explorePage.collectAsState()
  val chartsPage by chartsViewModel.chartsPage.collectAsState()
  val isChartsLoading by chartsViewModel.isLoading.collectAsState()

  val coroutineScope = rememberCoroutineScope()
  val scrollState = rememberScrollState()

  val backStackEntry by navController.currentBackStackEntryAsState()
  val scrollToTop by
    backStackEntry?.savedStateHandle?.getStateFlow("scrollToTop", false)?.collectAsState() ?: return

  LaunchedEffect(Unit) {
    if (chartsPage == null) {
      chartsViewModel.loadCharts()
    }
  }

  LaunchedEffect(scrollToTop) {
    if (scrollToTop) {
      scrollState.animateScrollTo(0)
      backStackEntry?.savedStateHandle?.set("scrollToTop", false)
    }
  }

  DynamicArtworkBackground(
    accentColor = LocalDynamicAccentColor.current
  ) {
    Box(
      modifier = Modifier.fillMaxSize(),
    ) {
      Column(
        modifier = Modifier.verticalScroll(scrollState),
      ) {
        Spacer(
          Modifier.height(
            LocalPlayerAwareWindowInsets.current.asPaddingValues().calculateTopPadding(),
          ),
        )

        // 1. Explore Header with Search Glass Button
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Explore",
            style = MaterialTheme.typography.headlineLarge.copy(
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          )

          GlassButton(
            onClick = { navController.navigate("search_input") },
            size = 42.dp,
            backgroundColor = TuneifyGlassTokens.GlassBgElevated
          ) {
            Icon(
              painter = painterResource(R.drawable.search),
              contentDescription = "Search",
              tint = Color.White,
              modifier = Modifier.size(20.dp)
            )
          }
        }

        // 2. Filter Pills: For You, Moods, Genres, Decades (smoothly horizontally scrollable)
        var selectedFilter by rememberSaveable { mutableStateOf("For You") }
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          GlassPill(
            text = "For You",
            selected = selectedFilter == "For You",
            onClick = { selectedFilter = "For You" },
            icon = {
              Icon(
                painter = painterResource(R.drawable.sparks),
                contentDescription = null,
                modifier = Modifier.size(15.dp)
              )
            }
          )
          GlassPill(
            text = "Moods",
            selected = selectedFilter == "Moods",
            onClick = {
              selectedFilter = "Moods"
              navController.navigate("mood_and_genres")
            },
            icon = {
              Icon(
                painter = painterResource(R.drawable.tune),
                contentDescription = null,
                modifier = Modifier.size(15.dp)
              )
            }
          )
          GlassPill(
            text = "Genres",
            selected = selectedFilter == "Genres",
            onClick = {
              selectedFilter = "Genres"
              navController.navigate("mood_and_genres")
            },
            icon = {
              Icon(
                painter = painterResource(R.drawable.grid_view),
                contentDescription = null,
                modifier = Modifier.size(15.dp)
              )
            }
          )
          GlassPill(
            text = "Decades",
            selected = selectedFilter == "Decades",
            onClick = {
              selectedFilter = "Decades"
            },
            icon = {
              Icon(
                painter = painterResource(R.drawable.ic_calendar_month),
                contentDescription = null,
                modifier = Modifier.size(15.dp)
              )
            }
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 3. Featured Banner: Bollywood Top Hits (matching reference Screen 3)
        val featuredSong = chartsPage?.sections?.firstOrNull()?.items?.filterIsInstance<SongItem>()?.firstOrNull()
        ExploreFeaturedBanner(
          title = "Bollywood\nTop Hits",
          thumbnailUrl = featuredSong?.thumbnail ?: "https://i.ytimg.com/vi/NX5yDs_TLqA/hqdefault.jpg",
          onPlayClick = {
            if (featuredSong != null) {
              if (featuredSong.id == mediaMetadata?.id) {
                playerConnection.togglePlayPause()
              } else {
                playerConnection.playQueue(
                  YouTubeQueue(
                    endpoint = WatchEndpoint(videoId = featuredSong.id),
                    preloadItem = featuredSong.toMediaMetadata(),
                  ),
                )
              }
            } else {
              playerConnection.playQueue(
                YouTubeQueue(
                  endpoint = WatchEndpoint(videoId = "sAzlWScHTc4"),
                )
              )
            }
          },
          onClick = {
            if (featuredSong != null) {
              playerConnection.playQueue(
                YouTubeQueue(
                  endpoint = WatchEndpoint(videoId = featuredSong.id),
                  preloadItem = featuredSong.toMediaMetadata(),
                ),
              )
            } else {
              playerConnection.playQueue(
                YouTubeQueue(
                  endpoint = WatchEndpoint(videoId = "sAzlWScHTc4"),
                )
              )
            }
          }
        )

        Spacer(Modifier.height(12.dp))

        // 4. Trending Now section with Glass Song Rows matching reference Screen 3
        ExpressiveSectionHeader(
          title = "Trending Now",
          onChevronClick = { navController.navigate("charts_screen") }
        )

        val trendingSongs = chartsPage
          ?.sections
          ?.find { it.title == "Trending" }
          ?.items
          ?.filterIsInstance<SongItem>()
          ?.distinctBy { it.id }
          ?: chartsPage?.sections?.firstOrNull()?.items?.filterIsInstance<SongItem>()?.distinctBy { it.id }
          ?: emptyList()

        if (trendingSongs.isNotEmpty()) {
          trendingSongs.take(8).forEach { song ->
            GlassSongRow(
              title = song.title,
              subtitle = song.artists.joinToString { it.name },
              thumbnailUrl = song.thumbnail ?: "https://i.ytimg.com/vi/${song.id}/hqdefault.jpg",
              isActive = song.id == mediaMetadata?.id,
              isPlaying = isPlaying,
              onPlayClick = {
                if (song.id == mediaMetadata?.id) {
                  playerConnection.togglePlayPause()
                } else {
                  playerConnection.playQueue(
                    YouTubeQueue(
                      endpoint = WatchEndpoint(videoId = song.id),
                      preloadItem = song.toMediaMetadata(),
                    ),
                  )
                }
              },
              onClick = {
                if (song.id == mediaMetadata?.id) {
                  playerConnection.togglePlayPause()
                } else {
                  playerConnection.playQueue(
                    YouTubeQueue(
                      endpoint = WatchEndpoint(videoId = song.id),
                      preloadItem = song.toMediaMetadata(),
                    ),
                  )
                }
              }
            )
          }
        } else {
          // Canonical trending reference tracks matching design specification Screen 3 with real artwork
          val canonicalTrending = listOf(
            Triple("Agar Tum Saath Ho", "Tamasha", "https://i.ytimg.com/vi/xRb8hxwN5zc/hqdefault.jpg" to "xRb8hxwN5zc"),
            Triple("Satranga", "Animal", "https://i.ytimg.com/vi/HrnrqYxYrbk/hqdefault.jpg" to "HrnrqYxYrbk"),
            Triple("Heeriye", "Jasleen Royal, Arijit Singh", "https://i.ytimg.com/vi/RLzC55ai0eo/hqdefault.jpg" to "RLzC55ai0eo")
          )
          canonicalTrending.forEach { (trackTitle, trackArtist, artAndId) ->
            GlassSongRow(
              title = trackTitle,
              subtitle = trackArtist,
              thumbnailUrl = artAndId.first,
              isActive = mediaMetadata?.title == trackTitle,
              isPlaying = isPlaying && mediaMetadata?.title == trackTitle,
              onPlayClick = {
                playerConnection.playQueue(
                  YouTubeQueue(
                    endpoint = WatchEndpoint(videoId = artAndId.second),
                  )
                )
              },
              onClick = {
                playerConnection.playQueue(
                  YouTubeQueue(
                    endpoint = WatchEndpoint(videoId = artAndId.second),
                  )
                )
              }
            )
          }
        }

        Spacer(Modifier.height(16.dp))

        Spacer(Modifier.height(14.dp))

        chartsPage
          ?.sections
          ?.filter { it.title != "Top music videos" && it.title != "Trending" }
          ?.forEach { section ->
            NavigationTitle(
              title =
                when (section.title) {
                  "Trending" -> stringResource(R.string.trending)
                  else -> section.title.ifEmpty { stringResource(R.string.charts) }
                },
            )
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
              val horizontalLazyGridItemWidthFactor =
                if (maxWidth * 0.475f >= 320.dp) 0.475f else 0.9f
              val horizontalLazyGridItemWidth = maxWidth * horizontalLazyGridItemWidthFactor

              val lazyGridState = rememberLazyGridState()
              val snapLayoutInfoProvider =
                remember(lazyGridState) {
                  SnapLayoutInfoProvider(
                    lazyGridState = lazyGridState,
                    positionInLayout = { layoutSize, itemSize ->
                      (layoutSize * horizontalLazyGridItemWidthFactor / 2f - itemSize / 2f)
                    },
                  )
                }

              LazyHorizontalGrid(
                state = lazyGridState,
                rows = GridCells.Fixed(4),
                flingBehavior = rememberSnapFlingBehavior(snapLayoutInfoProvider),
                contentPadding =
                  WindowInsets.systemBars.only(WindowInsetsSides.Horizontal).asPaddingValues(),
                modifier = Modifier.fillMaxWidth().height(ListItemHeight * 4),
              ) {
                itemsIndexed(
                  items = section.items.filterIsInstance<SongItem>().distinctBy { it.id },
                  key = { _, it -> it.id },
                ) { index, song ->
                  YouTubeListItem(
                    item = song,
                    isActive = song.id == mediaMetadata?.id,
                    isPlaying = isPlaying,
                    isSwipeable = false,
                    shape = listItemShape(index % 4, 4),
                    trailingContent = {
                      IconButton(
                        onClick = {
                          menuState.show {
                            YouTubeSongMenu(
                              song = song,
                              navController = navController,
                              onDismiss = menuState::dismiss,
                            )
                          }
                        },
                      ) {
                        Icon(
                          painter = painterResource(R.drawable.more_vert),
                          contentDescription = null,
                        )
                      }
                    },
                    modifier =
                      Modifier.width(horizontalLazyGridItemWidth)
                        .combinedClickable(
                          onClick = {
                            if (song.id == mediaMetadata?.id) {
                              playerConnection.togglePlayPause()
                            } else {
                              playerConnection.playQueue(
                                YouTubeQueue(
                                  endpoint = WatchEndpoint(videoId = song.id),
                                  preloadItem = song.toMediaMetadata(),
                                ),
                              )
                            }
                          },
                          onLongClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            menuState.show {
                              YouTubeSongMenu(
                                song = song,
                                navController = navController,
                                onDismiss = menuState::dismiss,
                              )
                            }
                          },
                        ),
                  )
                }
              }
            }
          }

        chartsPage
          ?.sections
          ?.find { it.title == "Top music videos" }
          ?.let { topVideosSection ->
            NavigationTitle(
              title = stringResource(R.string.top_music_videos),
            )
            LazyRow(
              contentPadding =
                WindowInsets.systemBars.only(WindowInsetsSides.Horizontal).asPaddingValues(),
            ) {
              items(
                items = topVideosSection.items.filterIsInstance<SongItem>().distinctBy { it.id },
                key = { it.id },
              ) { video ->
                YouTubeGridItem(
                  item = video,
                  isActive = video.id == mediaMetadata?.id,
                  isPlaying = isPlaying,
                  coroutineScope = coroutineScope,
                  modifier =
                    Modifier.combinedClickable(
                        onClick = {
                          if (video.id == mediaMetadata?.id) {
                            playerConnection.togglePlayPause()
                          } else {
                            playerConnection.playQueue(
                              YouTubeQueue(
                                endpoint = WatchEndpoint(videoId = video.id),
                                preloadItem = video.toMediaMetadata(),
                              ),
                            )
                          }
                        },
                        onLongClick = {
                          haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                          menuState.show {
                            YouTubeSongMenu(
                              song = video,
                              navController = navController,
                              onDismiss = menuState::dismiss,
                            )
                          }
                        },
                      )
                      .animateItem(),
                )
              }
            }
          }

        Spacer(
          Modifier.height(
            LocalPlayerAwareWindowInsets.current.asPaddingValues().calculateBottomPadding() + 80.dp
          )
        )
      }
    }
  }
}

@Composable
private fun ExploreCategoryTile(
  title: String,
  subtitle: String,
  icon: Int,
  accentColor: Color,
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
) {
  Surface(
    shape = TuneifyTokens.ShapeMedium,
    color = MaterialTheme.colorScheme.surfaceContainerHigh,
    modifier = modifier.clickable(onClick = onClick)
  ) {
    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp)) {
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
      Spacer(modifier = Modifier.height(10.dp))
      Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
    }
  }
}
