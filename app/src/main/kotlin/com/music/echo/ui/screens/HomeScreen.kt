package echo.music.iad1tya.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.music.innertube.YouTube
import com.music.innertube.models.AlbumItem
import com.music.innertube.models.ArtistItem
import com.music.innertube.models.BrowseEndpoint
import com.music.innertube.models.PlaylistItem
import com.music.innertube.models.SongItem
import com.music.innertube.models.WatchEndpoint
import com.music.innertube.models.YTItem
import com.music.innertube.pages.MoodAndGenres
import com.music.innertube.utils.completed
import com.music.innertube.utils.parseCookieString
import echo.music.iad1tya.LocalDatabase
import echo.music.iad1tya.LocalPlayerAwareWindowInsets
import echo.music.iad1tya.LocalPlayerConnection
import echo.music.iad1tya.R
import echo.music.iad1tya.constants.GridItemSize
import echo.music.iad1tya.constants.GridItemsSizeKey
import echo.music.iad1tya.constants.GridThumbnailHeight
import echo.music.iad1tya.constants.InnerTubeCookieKey
import echo.music.iad1tya.constants.ListItemHeight
import echo.music.iad1tya.constants.ListThumbnailSize
import echo.music.iad1tya.constants.RandomizeHomeOrderKey
import echo.music.iad1tya.constants.ShowSpeedDialKey
import echo.music.iad1tya.constants.SmallGridThumbnailHeight
import echo.music.iad1tya.constants.ThumbnailCornerRadius
import echo.music.iad1tya.db.entities.Album
import echo.music.iad1tya.db.entities.Artist
import echo.music.iad1tya.db.entities.LocalItem
import echo.music.iad1tya.db.entities.Playlist
import echo.music.iad1tya.db.entities.PlaylistEntity
import echo.music.iad1tya.db.entities.PlaylistSongMap
import echo.music.iad1tya.db.entities.Song
import echo.music.iad1tya.extensions.toMediaItem
import echo.music.iad1tya.models.toMediaMetadata
import echo.music.iad1tya.playback.queues.ListQueue
import echo.music.iad1tya.playback.queues.YouTubeQueue
import echo.music.iad1tya.ui.component.AlbumGridItem
import echo.music.iad1tya.ui.component.ArtistGridItem
import echo.music.iad1tya.ui.component.ChipsRow
import echo.music.iad1tya.ui.component.LocalBottomSheetPageState
import echo.music.iad1tya.ui.component.LocalMenuState
import androidx.compose.foundation.clickable
import echo.music.iad1tya.ui.component.NavigationTitle
import echo.music.iad1tya.ui.component.AtmosphericFeatherLightHeader
import echo.music.iad1tya.ui.component.TuneifyHomeTopBar
import echo.music.iad1tya.ui.component.TuneifyAlbumCard
import echo.music.iad1tya.ui.component.TuneifyDailyMixCard
import echo.music.iad1tya.ui.component.TuneifyTrendingCarousel
import echo.music.iad1tya.ui.component.TuneifyHeroCard
import echo.music.iad1tya.ui.component.TuneifyHomeScreenSkeleton
import echo.music.iad1tya.ui.component.TuneifySpotlightHeroCarousel
import echo.music.iad1tya.ui.component.TuneifyCookieShape
import echo.music.iad1tya.ui.component.TuneifyCompactTrackRow
import echo.music.iad1tya.ui.component.TuneifyArtistExpressiveCard
import echo.music.iad1tya.ui.component.TuneifyCascadingGenreCard
import echo.music.iad1tya.ui.component.TuneifyYTMusicColumnCarousel
import echo.music.iad1tya.ui.component.MaterialExpressiveShapes
import echo.music.iad1tya.ui.component.CuratedGenrePosters
import echo.music.iad1tya.ui.component.CuratedGenreGradients
import echo.music.iad1tya.ui.component.PremiumHeader
import echo.music.iad1tya.ui.component.PremiumGlow
import echo.music.iad1tya.ui.component.RandomizeGridItem
import echo.music.iad1tya.ui.component.SongGridItem
import echo.music.iad1tya.ui.component.SongListItem
import echo.music.iad1tya.ui.component.SpeedDialGridItem
import echo.music.iad1tya.ui.component.YouTubeGridItem
import echo.music.iad1tya.ui.component.YouTubeListItem
import echo.music.iad1tya.ui.component.shimmer.GridItemPlaceHolder
import echo.music.iad1tya.ui.component.shimmer.ShimmerHost
import echo.music.iad1tya.ui.component.shimmer.TextPlaceholder
import echo.music.iad1tya.ui.menu.AlbumMenu
import echo.music.iad1tya.ui.menu.ArtistMenu
import echo.music.iad1tya.ui.menu.SongMenu
import echo.music.iad1tya.ui.menu.YouTubeAlbumMenu
import echo.music.iad1tya.ui.menu.YouTubeArtistMenu
import echo.music.iad1tya.ui.menu.YouTubePlaylistMenu
import echo.music.iad1tya.ui.menu.YouTubeSongMenu
import echo.music.iad1tya.ui.utils.SnapLayoutInfoProvider
import echo.music.iad1tya.ui.utils.resize
import echo.music.iad1tya.utils.listItemShape
import echo.music.iad1tya.utils.makeTimeString
import echo.music.iad1tya.utils.rememberEnumPreference
import echo.music.iad1tya.utils.rememberPreference
import echo.music.iad1tya.viewmodels.CommunityPlaylistItem
import echo.music.iad1tya.viewmodels.DailyDiscoverItem
import echo.music.iad1tya.viewmodels.HomeViewModel
import kotlin.math.min
import kotlin.random.Random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private fun NavController.navigateToPlaylistItem(playlist: PlaylistItem) {
  when (val playlistId = playlist.id.removePrefix("VL")) {
    "LM" -> navigate("auto_playlist/liked")
    "SE" -> navigate("auto_playlist/downloaded")
    else -> navigate("online_playlist/$playlistId")
  }
}

sealed class HomeSection(val id: String, val baseWeight: Int) {
  data object SpeedDial : HomeSection("speed_dial", 100)

  data object AiRecommendations : HomeSection("ai_recommendations", 95)

  data object QuickPicks : HomeSection("quick_picks", 90)

  data object TopChartPicks : HomeSection("top_chart_picks", 88)

  data object FeaturedArtists : HomeSection("featured_artists", 85)

  data object DailyDiscover : HomeSection("daily_discover", 80)

  data object KeepListening : HomeSection("keep_listening", 50)

  data object AccountPlaylists : HomeSection("account_playlists", 40)

  data object ForgottenFavorites : HomeSection("forgotten_favorites", 30)

  data object TrendingStations : HomeSection("trending_stations", 25)

  data object LateNightChill : HomeSection("late_night_chill", 23)

  data object EnergyBoosters : HomeSection("energy_boosters", 22)

  data object FromTheCommunity : HomeSection("from_the_community", 20)

  data class SimilarRecommendation(val index: Int) :
    HomeSection("similar_recommendation_$index", 10)

  data class HomePageSection(val index: Int) : HomeSection("home_page_section_$index", 10)

  data object MoodAndGenres : HomeSection("mood_and_genres", 5)
}

@Composable
fun CommunityPlaylistCard(
  item: CommunityPlaylistItem,
  onClick: () -> Unit,
  onSongClick: (SongItem) -> Unit,
  modifier: Modifier = Modifier
) {
  val database = LocalDatabase.current
  val playerConnection = LocalPlayerConnection.current
  val scope = rememberCoroutineScope()
  val isDark = isSystemInDarkTheme()

  val containerColor =
    if (isDark) {
      MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
    } else {
      MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }

  val dbPlaylist by database.playlistByBrowseId(item.playlist.id).collectAsState(initial = null)
  val isBookmarked = dbPlaylist?.playlist?.bookmarkedAt != null

  Card(
    modifier = modifier.width(320.dp).height(420.dp),
    colors = CardDefaults.cardColors(containerColor = containerColor),
    shape = RoundedCornerShape(28.dp),
    onClick = onClick
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Box(modifier = Modifier.size(100.dp).clip(RoundedCornerShape(12.dp))) {
          Column(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.weight(1f)) {
              AsyncImage(
                model = item.songs.getOrNull(0)?.thumbnail?.resize(544, 544),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.weight(1f).fillMaxSize()
              )
              AsyncImage(
                model = item.songs.getOrNull(1)?.thumbnail?.resize(544, 544),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.weight(1f).fillMaxSize()
              )
            }
            Row(modifier = Modifier.weight(1f)) {
              AsyncImage(
                model = item.songs.getOrNull(2)?.thumbnail?.resize(544, 544),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.weight(1f).fillMaxSize()
              )
              AsyncImage(
                model = item.songs.getOrNull(3)?.thumbnail?.resize(544, 544),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.weight(1f).fillMaxSize()
              )
            }
          }
        }

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
          Text(
            text = item.playlist.title,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 2,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = item.playlist.author?.name ?: "",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            maxLines = 1
          )
        }
      }

      Column(modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 16.dp)) {
        item.songs.take(3).forEach { song ->
          Row(
            modifier =
              Modifier.fillMaxWidth()
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(12.dp))
                .combinedClickable(onClick = { onSongClick(song) }),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            AsyncImage(
              model = song.thumbnail.resize(544, 544),
              contentDescription = null,
              modifier = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)),
              contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = song.title,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
              )
              Text(
                text = song.artists.joinToString(", ") { it.name },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
              )
            }
          }
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
      ) {
        IconButton(
          onClick = {
            item.playlist.playEndpoint?.let { playerConnection?.playQueue(YouTubeQueue(it)) }
          },
          modifier =
            Modifier.size(48.dp).background(MaterialTheme.colorScheme.onSurface, CircleShape)
        ) {
          Icon(
            painter = painterResource(R.drawable.ic_widget_play),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.surface,
            modifier = Modifier.size(24.dp)
          )
        }

        IconButton(
          onClick = {
            item.playlist.radioEndpoint?.let { playerConnection?.playQueue(YouTubeQueue(it)) }
          },
          modifier =
            Modifier.size(48.dp)
              .background(
                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                CircleShape
              )
        ) {
          Icon(
            painter = painterResource(R.drawable.radio),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.size(24.dp)
          )
        }

        IconButton(
          onClick = {
            scope.launch(Dispatchers.IO) {
              if (dbPlaylist?.playlist == null) {
                database.transaction {
                  val playlistEntity =
                    PlaylistEntity(
                        name = item.playlist.title,
                        browseId = item.playlist.id,
                        thumbnailUrl = item.playlist.thumbnail,
                        remoteSongCount =
                          item.playlist.songCountText?.split(" ")?.firstOrNull()?.toIntOrNull(),
                        playEndpointParams = item.playlist.playEndpoint?.params,
                        shuffleEndpointParams = item.playlist.shuffleEndpoint?.params,
                        radioEndpointParams = item.playlist.radioEndpoint?.params
                      )
                      .toggleLike()
                  insert(playlistEntity)
                  scope.launch(Dispatchers.IO) {
                    item.songs
                      .ifEmpty {
                        YouTube.playlist(item.playlist.id).completed().getOrNull()?.songs.orEmpty()
                      }
                      .map { it.toMediaMetadata() }
                      .onEach(::insert)
                      .mapIndexed { index, song ->
                        PlaylistSongMap(
                          songId = song.id,
                          playlistId = playlistEntity.id,
                          position = index,
                          setVideoId = song.setVideoId
                        )
                      }
                      .forEach(::insert)
                  }
                }
              } else {
                database.transaction {
                  val currentPlaylist = dbPlaylist!!.playlist
                  update(currentPlaylist.toggleLike())
                }
              }
            }
          },
          modifier =
            Modifier.size(48.dp)
              .background(
                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                CircleShape
              )
        ) {
          Icon(
            painter =
              painterResource(
                if (isBookmarked) R.drawable.library_add_check else R.drawable.library_add
              ),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.size(24.dp)
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DailyDiscoverCard(
  dailyDiscover: echo.music.iad1tya.viewmodels.DailyDiscoverItem,
  onClick: () -> Unit,
  navController: NavController,
  modifier: Modifier = Modifier
) {
  val database = LocalDatabase.current
  val playCount by
    database.getLifetimePlayCount(dailyDiscover.recommendation.id).collectAsState(initial = 0)
  val menuState = LocalMenuState.current
  val haptic = LocalHapticFeedback.current

  val song = dailyDiscover.recommendation as? SongItem
  val playsString = stringResource(R.string.plays)

  Card(
    modifier =
      modifier
        .fillMaxSize()
        .clip(RoundedCornerShape(28.dp))
        .combinedClickable(
          onClick = onClick,
          onLongClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            if (song != null) {
              menuState.show {
                YouTubeSongMenu(
                  song = song,
                  navController = navController,
                  onDismiss = { menuState.dismiss() }
                )
              }
            }
          }
        ),
    colors =
      CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
      ),
    shape = RoundedCornerShape(28.dp)
  ) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
      AsyncImage(
        model =
          ImageRequest.Builder(LocalContext.current)
            .data(dailyDiscover.recommendation.thumbnail?.resize(1200, 1200))
            .crossfade(true)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
      )

      if (maxWidth > 200.dp) {
        Box(
          modifier =
            Modifier.fillMaxSize()
              .background(
                brush =
                  Brush.verticalGradient(
                    colors =
                      listOf(
                        Color.Black.copy(alpha = 0.3f),
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.6f),
                        Color.Black.copy(alpha = 0.9f)
                      )
                  )
              )
        )

        Column(
          modifier = Modifier.fillMaxSize().padding(24.dp),
          verticalArrangement = Arrangement.SpaceBetween
        ) {
          Column {
            Text(
              text = dailyDiscover.recommendation.title,
              style = MaterialTheme.typography.titleMedium,
              color = Color.White
            )
            Text(
              text =
                buildString {
                  append(
                    (dailyDiscover.recommendation as? SongItem)?.artists?.joinToString(", ") {
                      it.name
                    } ?: ""
                  )
                  if (playCount > 0) {
                    append(" • $playCount $playsString")
                  }
                },
              style = MaterialTheme.typography.bodyMedium,
              color = Color.White.copy(alpha = 0.7f)
            )
          }

          val messages =
            listOf(
              R.string.daily_discover_sounds_like,
              R.string.daily_discover_because_you_listen_to,
              R.string.daily_discover_similar_to,
              R.string.daily_discover_based_on,
              R.string.daily_discover_for_fans_of
            )
          val messageRes =
            remember(dailyDiscover.seed.id) {
              messages[kotlin.math.abs(dailyDiscover.seed.id.hashCode()) % messages.size]
            }

          Text(
            text =
              stringResource(
                messageRes,
                "${dailyDiscover.seed.title} • ${dailyDiscover.seed.artists.joinToString(", ") { it.name }}"
              ),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
            color = Color.White.copy(alpha = 0.6f),
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
          )
        }
      }
    }
  }
}

@OptIn(
  ExperimentalFoundationApi::class,
  ExperimentalMaterial3Api::class,
  ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun HomeScreen(
  navController: NavController,
  snackbarHostState: SnackbarHostState,
  viewModel: HomeViewModel = hiltViewModel(),
) {
  val menuState = LocalMenuState.current
  val bottomSheetPageState = LocalBottomSheetPageState.current
  val database = LocalDatabase.current
  val playerConnection = LocalPlayerConnection.current ?: return
  val haptic = LocalHapticFeedback.current

  val isPlaying by playerConnection.isEffectivelyPlaying.collectAsState()
  val mediaMetadata by playerConnection.mediaMetadata.collectAsState()

  val quickPicks by viewModel.quickPicks.collectAsState()
  val aiRecommendedPlaylist by viewModel.aiRecommendedPlaylist.collectAsState()
  val forgottenFavorites by viewModel.forgottenFavorites.collectAsState()
  val keepListening by viewModel.keepListening.collectAsState()
  val similarRecommendations by viewModel.similarRecommendations.collectAsState()
  val accountPlaylists by viewModel.accountPlaylists.collectAsState()
  val homePage by viewModel.homePage.collectAsState()
  val explorePage by viewModel.explorePage.collectAsState()
  val dailyDiscover by viewModel.dailyDiscover.collectAsState()
  val communityPlaylists by viewModel.communityPlaylists.collectAsState()

  val allLocalItems by viewModel.allLocalItems.collectAsState()
  val allYtItems by viewModel.allYtItems.collectAsState()
  val speedDialItems by viewModel.speedDialItems.collectAsState()
  val selectedChip by viewModel.selectedChip.collectAsState()

  val isLoading: Boolean by viewModel.isLoading.collectAsState()
  val isMoodAndGenresLoading = isLoading && explorePage?.moodAndGenres == null
  val isRefreshing by viewModel.isRefreshing.collectAsState()
  val isRandomizing by viewModel.isRandomizing.collectAsState()
  val pullRefreshState = rememberPullToRefreshState()

  val quickPicksLazyGridState = rememberLazyGridState()
  val forgottenFavoritesLazyGridState = rememberLazyGridState()

  val accountName by viewModel.accountName.collectAsState()
  val accountImageUrl by viewModel.accountImageUrl.collectAsState()
  val innerTubeCookie by rememberPreference(InnerTubeCookieKey, "")
  val (randomizeHomeOrder) = rememberPreference(RandomizeHomeOrderKey, true)
  val (showSpeedDial) = rememberPreference(ShowSpeedDialKey, true)

  val isLoggedIn = remember(innerTubeCookie) { "SAPISID" in parseCookieString(innerTubeCookie) }
  val url = if (isLoggedIn) accountImageUrl else null

  val scope = rememberCoroutineScope()

  var randomizeJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }

  val lazylistState = rememberLazyListState()
  val gridItemSize by rememberEnumPreference(GridItemsSizeKey, GridItemSize.BIG)
  val currentGridHeight =
    if (gridItemSize == GridItemSize.BIG) GridThumbnailHeight else SmallGridThumbnailHeight
  val backStackEntry by navController.currentBackStackEntryAsState()
  val scrollToTop =
    backStackEntry?.savedStateHandle?.getStateFlow("scrollToTop", false)?.collectAsState()

  var randomSeed by rememberSaveable { mutableLongStateOf(System.currentTimeMillis()) }

  LaunchedEffect(isRefreshing) {
    if (isRefreshing) {
      randomSeed = System.currentTimeMillis()
    }
  }

  val foundInSettings = stringResource(R.string.found_in_settings_content)

  LaunchedEffect(scrollToTop?.value) {
    if (scrollToTop?.value == true) {
      lazylistState.animateScrollToItem(0)
      backStackEntry?.savedStateHandle?.set("scrollToTop", false)
    }
  }

  fun isBhojpuriText(text: String): Boolean {
    val lower = text.lowercase()
    return lower.contains("bhojpuri") || lower.contains("bhojp") || lower.contains("khesari") ||
           lower.contains("pawan singh") || lower.contains("silpi raj") || lower.contains("shilpi raj") ||
           lower.contains("chhotu") || lower.contains("bhojpuriya") || lower.contains("pramod premi")
  }

  fun Song.isBhojpuriSong(): Boolean {
    return isBhojpuriText(title + " " + artists.joinToString(" ") { it.name } + " " + (song.albumName ?: ""))
  }

  fun SongItem.isBhojpuriSongItem(): Boolean {
    return isBhojpuriText(title + " " + artists.joinToString(" ") { it.name } + " " + (album?.name ?: ""))
  }

  val homeContinuation = homePage?.continuation
  val isLoadingMore by viewModel.isLoadingMore.collectAsState()
  var isFetchingContinuation by remember { mutableStateOf(false) }

  val initialSectionsCount = rememberSaveable { mutableIntStateOf(0) }
  LaunchedEffect(homePage?.sections) {
    val secSize = homePage?.sections?.size ?: 0
    if (initialSectionsCount.intValue == 0 && secSize > 0) {
      initialSectionsCount.intValue = secSize
    }
  }

  LaunchedEffect(isLoadingMore) {
    if (!isLoadingMore) {
      isFetchingContinuation = false
    }
  }

  LaunchedEffect(Unit) {
    snapshotFlow { lazylistState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
      .collect { lastVisibleIndex ->
        val len = lazylistState.layoutInfo.totalItemsCount
        val cont = homePage?.continuation
        if (lastVisibleIndex != null && len > 0 && lastVisibleIndex >= len - 4 && !isFetchingContinuation && !isLoadingMore) {
          if (cont != null) {
            isFetchingContinuation = true
            viewModel.loadMoreYouTubeItems(cont)
          }
        }
      }
  }

  NetworkReload(onReload = viewModel::refresh)

  if (selectedChip != null) {
    BackHandler { viewModel.toggleChip(selectedChip) }
  }

  val localGridItem: @Composable (LocalItem) -> Unit = {
    when (it) {
      is Song ->
        SongGridItem(
          song = it,
          modifier =
            Modifier.fillMaxWidth()
              .combinedClickable(
                onClick = {
                  if (it.id == mediaMetadata?.id) {
                    playerConnection.togglePlayPause()
                  } else {
                    playerConnection.playQueue(
                      YouTubeQueue.radio(it.toMediaMetadata()),
                    )
                  }
                },
                onLongClick = {
                  haptic.performHapticFeedback(
                    HapticFeedbackType.LongPress,
                  )
                  menuState.show {
                    SongMenu(
                      originalSong = it,
                      navController = navController,
                      onDismiss = menuState::dismiss,
                    )
                  }
                },
              ),
          isActive = it.id == mediaMetadata?.id,
          isPlaying = isPlaying,
        )
      is Album ->
        AlbumGridItem(
          album = it,
          isActive = it.id == mediaMetadata?.album?.id,
          isPlaying = isPlaying,
          coroutineScope = scope,
          modifier =
            Modifier.fillMaxWidth()
              .combinedClickable(
                onClick = { navController.navigate("album/${it.id}") },
                onLongClick = {
                  haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                  menuState.show {
                    AlbumMenu(
                      originalAlbum = it,
                      navController = navController,
                      onDismiss = menuState::dismiss
                    )
                  }
                }
              )
        )
      is Artist ->
        ArtistGridItem(
          artist = it,
          modifier =
            Modifier.fillMaxWidth()
              .combinedClickable(
                onClick = { navController.navigate("artist/${it.id}") },
                onLongClick = {
                  haptic.performHapticFeedback(
                    HapticFeedbackType.LongPress,
                  )
                  menuState.show {
                    ArtistMenu(
                      originalArtist = it,
                      coroutineScope = scope,
                      onDismiss = menuState::dismiss,
                    )
                  }
                },
              ),
        )
      is Playlist -> {}
    }
  }

  val ytGridItem: @Composable (YTItem) -> Unit = { item ->
    if (item is ArtistItem) {
      TuneifyArtistExpressiveCard(
        name = item.title,
        thumbnailUrl = item.thumbnail,
        onClick = { navController.navigate("artist/${item.id}") },
        onLongClick = {
          haptic.performHapticFeedback(HapticFeedbackType.LongPress)
          menuState.show { YouTubeArtistMenu(artist = item, onDismiss = menuState::dismiss) }
        }
      )
    } else {
      val subtitleText = when (item) {
        is SongItem -> item.artists.joinToString(", ") { it.name }
        is AlbumItem -> item.artists?.joinToString(", ") { it.name } ?: "Album"
        is PlaylistItem -> item.author?.name ?: "Playlist"
        else -> null
      }
      TuneifyAlbumCard(
        title = item.title,
        subtitle = subtitleText,
        thumbnailUrl = item.thumbnail,
        onClick = {
          when (item) {
            is SongItem ->
              playerConnection.playQueue(
                YouTubeQueue(
                  item.endpoint ?: WatchEndpoint(videoId = item.id),
                  item.toMediaMetadata()
                )
              )
            is AlbumItem -> navController.navigate("album/${item.id}")
            is ArtistItem -> navController.navigate("artist/${item.id}")
            is PlaylistItem -> navController.navigateToPlaylistItem(item)
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
                  onDismiss = menuState::dismiss
                )
              is AlbumItem ->
                YouTubeAlbumMenu(
                  albumItem = item,
                  navController = navController,
                  onDismiss = menuState::dismiss
                )
              is ArtistItem -> YouTubeArtistMenu(artist = item, onDismiss = menuState::dismiss)
              is PlaylistItem ->
                YouTubePlaylistMenu(
                  playlist = item,
                  coroutineScope = scope,
                  onDismiss = menuState::dismiss
                )
            }
          }
        }
      )
    }
  }

  val homeSections =
    remember(
      randomizeHomeOrder,
      randomSeed,
      speedDialItems,
      quickPicks,
      dailyDiscover,
      keepListening,
      accountPlaylists,
      forgottenFavorites,
      communityPlaylists,
      similarRecommendations,
      homePage?.sections,
      explorePage?.moodAndGenres,
      aiRecommendedPlaylist
    ) {
      val list = mutableListOf<HomeSection>()

      // 1. Quick picks (YouTube Music 4-track column layout)
      list.add(HomeSection.QuickPicks)

      // 2. Top Chart Picks (YouTube Music 4-track column layout)
      list.add(HomeSection.TopChartPicks)

      // 3. Round square posters from home sections
      list.add(HomeSection.HomePageSection(0))

      // 4. Trending Stations (YouTube Music 4-track column layout)
      list.add(HomeSection.TrendingStations)

      // 5. The 2x8 genres card
      list.add(HomeSection.MoodAndGenres)

      // 6. Featured Artists (7-sided cookie shape with real singer images)
      list.add(HomeSection.FeaturedArtists)

      // 7. Late Night Chill (YouTube Music 4-track column layout)
      list.add(HomeSection.LateNightChill)

      // 8. Forgotten Favorites (YouTube Music 4-track column layout)
      list.add(HomeSection.ForgottenFavorites)

      // 9. Energy Boosters (YouTube Music 4-track column layout)
      list.add(HomeSection.EnergyBoosters)

      // Account playlists (when logged in)
      if (accountPlaylists?.isNotEmpty() == true) {
        list.add(HomeSection.AccountPlaylists)
      }

      // Community playlists
      if (communityPlaylists?.isNotEmpty() == true) {
        list.add(HomeSection.FromTheCommunity)
      }

      // AI Recommendations
      if (aiRecommendedPlaylist != null && aiRecommendedPlaylist!!.second.isNotEmpty()) {
        list.add(HomeSection.AiRecommendations)
      }

      // Speed Dial
      if (showSpeedDial && speedDialItems.isNotEmpty()) {
        list.add(HomeSection.SpeedDial)
      }

      // Similar recommendations
      similarRecommendations?.indices?.forEach { i ->
        list.add(HomeSection.SimilarRecommendation(i))
      }

      // All remaining Home Page sections from YouTube Music
      val totalSections = homePage?.sections?.size ?: 0
      for (i in 1 until totalSections) {
        val sec = HomeSection.HomePageSection(i)
        if (!list.contains(sec)) {
          list.add(sec)
        }
      }

      list
    }

  LaunchedEffect(quickPicks) { quickPicksLazyGridState.scrollToItem(0) }

  LaunchedEffect(forgottenFavorites) { forgottenFavoritesLazyGridState.scrollToItem(0) }

  PullToRefreshBox(
    state = pullRefreshState,
    isRefreshing = isRefreshing,
    onRefresh = viewModel::refresh,
    indicator = {
      PullToRefreshDefaults.LoadingIndicator(
        state = pullRefreshState,
        isRefreshing = isRefreshing,
        modifier =
          Modifier.align(Alignment.TopCenter)
            .padding(top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding() + 8.dp),
      )
    }
  ) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopStart) {
      val horizontalLazyGridItemWidthFactor = if (maxWidth * 0.475f >= 320.dp) 0.475f else 0.9f
      val horizontalLazyGridItemWidth = maxWidth * horizontalLazyGridItemWidthFactor
      val quickPicksSnapLayoutInfoProvider =
        remember(quickPicksLazyGridState) {
          SnapLayoutInfoProvider(
            lazyGridState = quickPicksLazyGridState,
            positionInLayout = { layoutSize, itemSize ->
              (layoutSize * horizontalLazyGridItemWidthFactor / 2f - itemSize / 2f)
            }
          )
        }
      val forgottenFavoritesSnapLayoutInfoProvider =
        remember(forgottenFavoritesLazyGridState) {
          SnapLayoutInfoProvider(
            lazyGridState = forgottenFavoritesLazyGridState,
            positionInLayout = { layoutSize, itemSize ->
              (layoutSize * horizontalLazyGridItemWidthFactor / 2f - itemSize / 2f)
            }
          )
        }

      val trendingSongs = remember(quickPicks, homePage?.sections) {
        val fromQP = quickPicks?.filterNot { it.isBhojpuriSong() }?.map { song ->
          SongItem(
            id = song.id,
            title = song.title,
            artists = song.artists.map { com.music.innertube.models.Artist(name = it.name, id = it.id) },
            album = song.song.albumName?.let { com.music.innertube.models.Album(name = it, id = song.song.albumId ?: "") },
            duration = song.song.duration.toInt(),
            thumbnail = song.thumbnailUrl ?: "",
            endpoint = null
          )
        } ?: emptyList()
        val fromHP = homePage?.sections?.flatMap { it.items.filterIsInstance<SongItem>() }?.filterNot { it.isBhojpuriSongItem() } ?: emptyList()
        (fromQP + fromHP).distinctBy { it.id }.filter { it.thumbnail.isNotBlank() }.take(8)
      }

      val defaultPopularTracks = remember {
        listOf(
          SongItem(
            id = "4NRXx6U8ABQ",
            title = "Saanson Ki Mala",
            artists = listOf(com.music.innertube.models.Artist(name = "Sahil Nautiyal", id = "")),
            album = null,
            duration = 240,
            thumbnail = "https://i.ytimg.com/vi/4NRXx6U8ABQ/hqdefault.jpg",
            endpoint = null
          ),
          SongItem(
            id = "njA3vYpXk5M",
            title = "Khalasi",
            artists = listOf(com.music.innertube.models.Artist(name = "Achint, Aditya Gadhvi", id = "")),
            album = null,
            duration = 250,
            thumbnail = "https://i.ytimg.com/vi/njA3vYpXk5M/hqdefault.jpg",
            endpoint = null
          ),
          SongItem(
            id = "bpOSxM0rNPM",
            title = "Husn",
            artists = listOf(com.music.innertube.models.Artist(name = "Anuv Jain", id = "")),
            album = null,
            duration = 220,
            thumbnail = "https://i.ytimg.com/vi/bpOSxM0rNPM/hqdefault.jpg",
            endpoint = null
          ),
          SongItem(
            id = "u9n7Cw-4_PE",
            title = "Heeriye",
            artists = listOf(com.music.innertube.models.Artist(name = "Jasleen Royal, Arijit Singh", id = "")),
            album = null,
            duration = 210,
            thumbnail = "https://i.ytimg.com/vi/u9n7Cw-4_PE/hqdefault.jpg",
            endpoint = null
          )
        )
      }

      val spotlightSongs = remember(trendingSongs, homePage?.sections, explorePage?.newReleaseAlbums) {
        val fromTrending = trendingSongs.filter { it.thumbnail.isNotBlank() }
        val fromHP = homePage?.sections?.flatMap { it.items.filterIsInstance<SongItem>() }?.filter { it.thumbnail.isNotBlank() }.orEmpty()
        val fromReleases = explorePage?.newReleaseAlbums?.map { album ->
          SongItem(
            id = album.id,
            title = album.title,
            artists = album.artists?.map { com.music.innertube.models.Artist(name = it.name, id = it.id) } ?: emptyList(),
            album = null,
            duration = 200,
            thumbnail = album.thumbnail,
            endpoint = null
          )
        }.orEmpty()
        val combined = (fromTrending + fromHP + fromReleases).distinctBy { it.id }.filter { it.thumbnail.isNotBlank() }
        if (combined.isNotEmpty()) combined.take(8) else defaultPopularTracks
      }

      val topChartSongs = remember(homePage?.sections, trendingSongs, spotlightSongs) {
        val fromHp = homePage?.sections?.getOrNull(0)?.items?.filterIsInstance<SongItem>()?.filterNot { it.isBhojpuriSongItem() }.orEmpty()
        (fromHp + trendingSongs + spotlightSongs).distinctBy { it.id }.take(16)
      }

      val stationSongs = remember(homePage?.sections, trendingSongs, spotlightSongs) {
        val fromHp = homePage?.sections?.drop(1)?.flatMap { it.items.filterIsInstance<SongItem>() }?.filterNot { it.isBhojpuriSongItem() }.orEmpty()
        (fromHp + trendingSongs + spotlightSongs).distinctBy { it.id }.take(16)
      }

      val lateNightSongs = remember(homePage?.sections, trendingSongs, spotlightSongs) {
        val fromHp = homePage?.sections?.find { it.title.contains("chill", true) || it.title.contains("relax", true) || it.title.contains("night", true) }?.items?.filterIsInstance<SongItem>()?.filterNot { it.isBhojpuriSongItem() }.orEmpty()
        (fromHp + trendingSongs.reversed() + spotlightSongs).distinctBy { it.id }.take(16)
      }

      val energySongs = remember(homePage?.sections, spotlightSongs, trendingSongs) {
        val fromHp = homePage?.sections?.find { it.title.contains("energy", true) || it.title.contains("party", true) || it.title.contains("workout", true) }?.items?.filterIsInstance<SongItem>()?.filterNot { it.isBhojpuriSongItem() }.orEmpty()
        (fromHp + spotlightSongs.shuffled() + trendingSongs).distinctBy { it.id }.take(16)
      }

      val dbArtists by database.artistsByNameAsc().collectAsState(initial = emptyList())

      val defaultRegionalArtists = remember {
        listOf(
          ArtistItem(id = "UC0C-w0YjGpqDXGB8Uqc614A", title = "Arijit Singh", thumbnail = "https://lh3.googleusercontent.com/v8t_kG6jG9QO6nBqY7u7K7k=w544-h544-l90-rj", shuffleEndpoint = null, radioEndpoint = null),
          ArtistItem(id = "UC7kI8Wcl49qu8_xhnXkSE7w", title = "Shreya Ghoshal", thumbnail = "https://lh3.googleusercontent.com/q1jG9QO6nBqY7u7K7k=w544-h544-l90-rj", shuffleEndpoint = null, radioEndpoint = null),
          ArtistItem(id = "UCpl0-3mYxNlA2oG43-r327A", title = "Pritam", thumbnail = "https://lh3.googleusercontent.com/AIdro_n4H9=w544-h544-l90-rj", shuffleEndpoint = null, radioEndpoint = null),
          ArtistItem(id = "UC2_5fWc2F7-X-H1Ld8J77GA", title = "Anirudh Ravichander", thumbnail = "https://lh3.googleusercontent.com/AIdro_l2K8=w544-h544-l90-rj", shuffleEndpoint = null, radioEndpoint = null),
          ArtistItem(id = "UC8b_f2J0-Y0-W1-T", title = "A.R. Rahman", thumbnail = "https://lh3.googleusercontent.com/AIdro_m5N3=w544-h544-l90-rj", shuffleEndpoint = null, radioEndpoint = null),
          ArtistItem(id = "UC9-f32J0-Y1-W2", title = "Diljit Dosanjh", thumbnail = "https://lh3.googleusercontent.com/AIdro_p8Q1=w544-h544-l90-rj", shuffleEndpoint = null, radioEndpoint = null),
          ArtistItem(id = "UC_karan_aujla_yt", title = "Karan Aujla", thumbnail = "https://lh3.googleusercontent.com/AIdro_aujla=w544-h544-l90-rj", shuffleEndpoint = null, radioEndpoint = null),
          ArtistItem(id = "UC_sidhu_mw_yt", title = "Sidhu Moose Wala", thumbnail = "https://lh3.googleusercontent.com/AIdro_sidhu=w544-h544-l90-rj", shuffleEndpoint = null, radioEndpoint = null)
        )
      }

      val featuredArtistItems = remember(isLoggedIn, dbArtists, homePage?.sections) {
        if (isLoggedIn && dbArtists.isNotEmpty()) {
          val list = dbArtists.filter { it.artist.thumbnailUrl?.isNotBlank() == true }.map {
            ArtistItem(
              id = it.id,
              title = it.artist.name,
              thumbnail = it.artist.thumbnailUrl,
              shuffleEndpoint = null,
              radioEndpoint = null
            )
          }
          if (list.isNotEmpty()) list.take(12) else defaultRegionalArtists
        } else {
          val fromSections = homePage?.sections?.flatMap { it.items }?.filterIsInstance<ArtistItem>()?.filter { it.thumbnail?.isNotBlank() == true } ?: emptyList()
          if (fromSections.isNotEmpty()) {
            fromSections.distinctBy { it.id }.take(12)
          } else {
            defaultRegionalArtists
          }
        }
      }

      val topStatusBarPadding = WindowInsets.systemBars.only(WindowInsetsSides.Top).asPaddingValues().calculateTopPadding()
      val bottomPlayerPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues().calculateBottomPadding()

      LazyColumn(
        state = lazylistState,
        contentPadding = PaddingValues(top = topStatusBarPadding, bottom = bottomPlayerPadding)
      ) {
        item(key = "tuneify_home_top_bar") {
          AtmosphericFeatherLightHeader(
            userName = if (isLoggedIn) accountName else "Guest",
            modifier = Modifier.padding(top = 0.dp)
          )
        }

        item(key = "tuneify_filter_chips") {
          val ytChips = homePage?.chips.orEmpty()
          val exploreCategories: List<com.music.innertube.pages.MoodAndGenres.Item> = explorePage?.moodAndGenres.orEmpty()
          val allChipTitles: List<String> = remember(ytChips, exploreCategories) {
            val list = mutableListOf("All")
            ytChips.forEach { chip ->
              if (!list.contains(chip.title)) {
                list.add(chip.title)
              }
            }
            listOf("Relax", "Workout", "Focus", "Energize", "Commute", "Party", "Romance").forEach { opt ->
              if (list.none { it.equals(opt, ignoreCase = true) }) {
                list.add(opt)
              }
            }
            if (!list.contains("Mood & Genres")) {
              list.add("Mood & Genres")
            }
            list
          }

          ChipsRow(
            chips = allChipTitles.map { it to it },
            currentValue = selectedChip?.title ?: "All",
            onValueUpdate = { chipName ->
              when (chipName) {
                "All" -> {
                  if (selectedChip != null) {
                    viewModel.toggleChip(null)
                  }
                }
                "Mood & Genres" -> {
                  navController.navigate("mood_and_genres")
                }
                else -> {
                  val matched = ytChips.firstOrNull { it.title.equals(chipName, ignoreCase = true) || it.title.contains(chipName, ignoreCase = true) }
                  if (matched != null) {
                    viewModel.toggleChip(matched)
                  } else {
                    val matchedCategory = exploreCategories.firstOrNull { it.title.contains(chipName, ignoreCase = true) }
                    if (matchedCategory != null) {
                      val paramsStr = matchedCategory.endpoint.params?.let { "?params=$it" }.orEmpty()
                      navController.navigate("youtube_browse/${matchedCategory.endpoint.browseId}$paramsStr")
                    } else {
                      navController.navigate("mood_and_genres")
                    }
                  }
                }
              }
            }
          )
        }

        item(key = "trending_hero_top") {
          TuneifySpotlightHeroCarousel(
            items = spotlightSongs,
            onItemClick = { song ->
              playerConnection.playQueue(
                YouTubeQueue(
                  song.endpoint ?: WatchEndpoint(videoId = song.id),
                  song.toMediaMetadata()
                )
              )
            },
            modifier = Modifier.padding(vertical = 4.dp)
          )
        }

        if (isLoading && homePage?.sections.isNullOrEmpty()) {
          item(key = "home_skeleton_loader") {
            TuneifyHomeScreenSkeleton()
          }
        }

        homeSections.forEach { section ->
          when (section) {
            HomeSection.DailyDiscover -> {}
            HomeSection.SpeedDial -> {
              speedDialItems
                .takeIf { it.isNotEmpty() }
                ?.let { items ->
                  item(key = "speed_dial_title") {
                    NavigationTitle(
                      title = stringResource(R.string.speed_dial),
                      modifier = Modifier.animateItem()
                    )
                  }

                  item(key = "speed_dial_list") {
                    val targetItemSize = 160.dp
                    val availableWidth = maxWidth - 32.dp
                    val columns = (availableWidth / targetItemSize).toInt().coerceAtLeast(3)
                    val rows = if (columns >= 6) 1 else if (columns >= 4) 2 else 3
                    val itemsPerPage = columns * rows
                    val itemWidth = availableWidth / columns

                    val pagerState =
                      rememberPagerState(
                        pageCount = { (items.size + itemsPerPage - 1) / itemsPerPage }
                      )

                    Column(
                      modifier = Modifier.fillMaxWidth().animateItem(),
                    ) {
                      HorizontalPager(
                        state = pagerState,
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        pageSpacing = 16.dp,
                        modifier = Modifier.fillMaxWidth().height(itemWidth * rows),
                      ) { page ->
                        val pageStartIndex = page * itemsPerPage
                        val pageItems = items.drop(pageStartIndex).take(itemsPerPage)

                        Column(modifier = Modifier.fillMaxSize()) {
                          for (row in 0 until rows) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                              for (col in 0 until columns) {
                                val itemIndex = row * columns + col

                                val isRandomizeSlot = (page == 0 && itemIndex == itemsPerPage - 1)

                                if (isRandomizeSlot) {
                                  Box(
                                    modifier =
                                      Modifier.width(itemWidth).height(itemWidth).padding(4.dp)
                                  ) {
                                    RandomizeGridItem(
                                      isLoading = isRandomizing,
                                      onClick = {
                                        if (isRandomizing) {
                                          randomizeJob?.cancel()
                                        } else {
                                          randomizeJob =
                                            scope.launch {
                                              val randomItem = viewModel.getRandomItem()
                                              if (randomItem != null) {
                                                when (randomItem) {
                                                  is SongItem ->
                                                    playerConnection.playQueue(
                                                      YouTubeQueue(
                                                        randomItem.endpoint
                                                          ?: WatchEndpoint(videoId = randomItem.id),
                                                        randomItem.toMediaMetadata()
                                                      )
                                                    )
                                                  is AlbumItem ->
                                                    navController.navigate("album/${randomItem.id}")
                                                  is ArtistItem ->
                                                    navController.navigate(
                                                      "artist/${randomItem.id}"
                                                    )
                                                  is PlaylistItem ->
                                                    navController.navigateToPlaylistItem(randomItem)
                                                }
                                              }
                                            }
                                        }
                                      }
                                    )
                                  }
                                } else if (itemIndex < pageItems.size) {
                                  val item = pageItems[itemIndex]
                                  val isPinned by
                                    database.speedDialDao
                                      .isPinned(item.id)
                                      .collectAsState(initial = false)

                                  Box(
                                    modifier =
                                      Modifier.width(itemWidth).height(itemWidth).padding(4.dp)
                                  ) {
                                    SpeedDialGridItem(
                                      item = item,
                                      isPinned = isPinned,
                                      isActive =
                                        item.id in
                                          listOf(mediaMetadata?.album?.id, mediaMetadata?.id),
                                      isPlaying = isPlaying,
                                      modifier =
                                        Modifier.fillMaxSize()
                                          .combinedClickable(
                                            onClick = {
                                              when (item) {
                                                is SongItem ->
                                                  playerConnection.playQueue(
                                                    YouTubeQueue(
                                                      item.endpoint
                                                        ?: WatchEndpoint(videoId = item.id),
                                                      item.toMediaMetadata()
                                                    )
                                                  )
                                                is AlbumItem ->
                                                  navController.navigate("album/${item.id}")
                                                is ArtistItem ->
                                                  navController.navigate("artist/${item.id}")
                                                is PlaylistItem ->
                                                  navController.navigateToPlaylistItem(item)
                                              }
                                            },
                                            onLongClick = {
                                              haptic.performHapticFeedback(
                                                HapticFeedbackType.LongPress
                                              )
                                              menuState.show {
                                                when (item) {
                                                  is SongItem ->
                                                    YouTubeSongMenu(
                                                      song = item,
                                                      navController = navController,
                                                      onDismiss = menuState::dismiss
                                                    )
                                                  is AlbumItem ->
                                                    YouTubeAlbumMenu(
                                                      albumItem = item,
                                                      navController = navController,
                                                      onDismiss = menuState::dismiss
                                                    )
                                                  is ArtistItem ->
                                                    YouTubeArtistMenu(
                                                      artist = item,
                                                      onDismiss = menuState::dismiss
                                                    )
                                                  is PlaylistItem ->
                                                    YouTubePlaylistMenu(
                                                      playlist = item,
                                                      coroutineScope = scope,
                                                      onDismiss = menuState::dismiss
                                                    )
                                                }
                                              }
                                            }
                                          )
                                    )
                                  }
                                } else {
                                  Spacer(modifier = Modifier.width(itemWidth))
                                }
                              }
                            }
                          }
                        }
                      }

                      if (pagerState.pageCount > 1) {
                        Row(
                          modifier = Modifier.height(24.dp).fillMaxWidth(),
                          horizontalArrangement =
                            androidx.compose.foundation.layout.Arrangement.Center,
                          verticalAlignment = Alignment.CenterVertically
                        ) {
                          repeat(pagerState.pageCount) { iteration ->
                            val color =
                              if (pagerState.currentPage == iteration)
                                MaterialTheme.colorScheme.onSurface
                              else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            Box(
                              modifier =
                                Modifier.padding(4.dp)
                                  .clip(RoundedCornerShape(ThumbnailCornerRadius))
                                  .background(color)
                                  .size(8.dp)
                            )
                          }
                        }
                      }
                    }
                  }
                }
            }
            HomeSection.AiRecommendations -> {
              aiRecommendedPlaylist?.let { pair ->
                val (playlist, songs) = pair
                item(key = "ai_recommendation_title") {
                  val lastUpdatedStr =
                    playlist.playlist.lastUpdateTime?.let {
                      "Last updated: " +
                        it.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, h:mm a"))
                    }
                  NavigationTitle(
                    title = playlist.title,
                    label = lastUpdatedStr,
                    onClick = { navController.navigate("local_playlist/${playlist.id}") },
                    modifier = Modifier.animateItem()
                  )
                }
                item(key = "ai_recommendation_list") {
                  LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.animateItem()
                  ) {
                    items(items = songs.distinctBy { it.id }, key = { it.id }) { songObj ->
                      localGridItem(songObj)
                    }
                  }
                }
              }
            }
            HomeSection.QuickPicks -> {
              val dbQuickPicks = quickPicks?.takeIf { it.isNotEmpty() }?.distinctBy { it.id }?.filterNot { it.isBhojpuriSong() }?.take(24)?.map { song ->
                SongItem(
                  id = song.id,
                  title = song.title,
                  artists = song.artists.map { com.music.innertube.models.Artist(name = it.name, id = it.id) },
                  album = song.song.albumName?.let { com.music.innertube.models.Album(name = it, id = song.song.albumId ?: "") },
                  duration = song.song.duration.toInt(),
                  thumbnail = song.thumbnailUrl ?: "",
                  endpoint = null
                )
              }
              val homePageSongs = homePage?.sections?.flatMap { it.items.filterIsInstance<SongItem>() }?.filterNot { it.isBhojpuriSongItem() }?.take(24)
              val quickPicksSongs = when {
                !dbQuickPicks.isNullOrEmpty() -> dbQuickPicks
                !homePageSongs.isNullOrEmpty() -> homePageSongs
                trendingSongs.isNotEmpty() -> trendingSongs
                else -> spotlightSongs
              }

              if (quickPicksSongs.isNotEmpty()) {
                item(key = "quick_picks_columns_title") {
                  NavigationTitle(
                    title = stringResource(R.string.quick_picks),
                    onPlayAllClick = {
                      val mediaItems = quickPicksSongs.map { it.toMediaMetadata().toMediaItem() }
                      playerConnection.playQueue(
                        ListQueue(
                          title = "Quick picks",
                          items = mediaItems
                        )
                      )
                    },
                    modifier = Modifier.animateItem()
                  )
                }

                item(key = "quick_picks_columns_content") {
                  TuneifyYTMusicColumnCarousel(
                    songs = quickPicksSongs.distinctBy { it.id },
                    activeMediaId = mediaMetadata?.id,
                    isPlaying = isPlaying,
                    onSongClick = { songItem ->
                      if (songItem.id == mediaMetadata?.id) {
                        playerConnection.togglePlayPause()
                      } else {
                        playerConnection.playQueue(YouTubeQueue.radio(songItem.toMediaMetadata()))
                      }
                    },
                    onSongLongClick = { songItem ->
                      haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                      menuState.show {
                        YouTubeSongMenu(song = songItem, navController = navController, onDismiss = menuState::dismiss)
                      }
                    },
                    onSongMoreClick = { songItem ->
                      menuState.show {
                        YouTubeSongMenu(song = songItem, navController = navController, onDismiss = menuState::dismiss)
                      }
                    },
                    modifier = Modifier.animateItem()
                  )
                }
              }
            }
            HomeSection.FromTheCommunity -> {
              communityPlaylists
                ?.takeIf { it.isNotEmpty() }
                ?.let { playlists ->
                  item(key = "community_playlists_title") {
                    NavigationTitle(
                      title = stringResource(R.string.from_the_community),
                      modifier = Modifier.animateItem()
                    )
                  }

                  item(key = "community_playlists_content") {
                    LazyRow(
                      contentPadding = PaddingValues(horizontal = 16.dp),
                      horizontalArrangement = Arrangement.spacedBy(16.dp),
                      modifier = Modifier.animateItem()
                    ) {
                      items(playlists.distinctBy { it.playlist.id }, key = { it.playlist.id }) {
                        item ->
                        CommunityPlaylistCard(
                          item = item,
                          onClick = { navController.navigateToPlaylistItem(item.playlist) },
                          onSongClick = { song ->
                            playerConnection.playQueue(
                              YouTubeQueue(
                                song.endpoint ?: WatchEndpoint(videoId = song.id),
                                song.toMediaMetadata()
                              )
                            )
                          }
                        )
                      }
                    }
                  }
                }
            }
            HomeSection.TopChartPicks -> {
              if (topChartSongs.isNotEmpty()) {
                item(key = "top_chart_picks_title") {
                  NavigationTitle(
                    title = "Top Charts & Hits",
                    label = null,
                    modifier = Modifier.animateItem(),
                    onPlayAllClick = {
                      playerConnection.playQueue(
                        ListQueue(
                          title = "Top Charts & Hits",
                          items = topChartSongs.map { it.toMediaMetadata().toMediaItem() }
                        )
                      )
                    }
                  )
                }

                item(key = "top_chart_picks_carousel") {
                  TuneifyYTMusicColumnCarousel(
                    songs = topChartSongs.distinctBy { it.id },
                    activeMediaId = mediaMetadata?.id,
                    isPlaying = isPlaying,
                    onSongClick = { song ->
                      if (song.id == mediaMetadata?.id) {
                        playerConnection.togglePlayPause()
                      } else {
                        playerConnection.playQueue(YouTubeQueue.radio(song.toMediaMetadata()))
                      }
                    },
                    onSongLongClick = { song ->
                      haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                      menuState.show {
                        YouTubeSongMenu(song = song, navController = navController, onDismiss = menuState::dismiss)
                      }
                    },
                    onSongMoreClick = { song ->
                      menuState.show {
                        YouTubeSongMenu(song = song, navController = navController, onDismiss = menuState::dismiss)
                      }
                    },
                    modifier = Modifier.animateItem()
                  )
                }
              }
            }
            HomeSection.LateNightChill -> {
              if (lateNightSongs.isNotEmpty()) {
                item(key = "late_night_chill_title") {
                  NavigationTitle(
                    title = "Late Night Chill",
                    label = null,
                    modifier = Modifier.animateItem(),
                    onPlayAllClick = {
                      playerConnection.playQueue(
                        ListQueue(
                          title = "Late Night Chill",
                          items = lateNightSongs.map { it.toMediaMetadata().toMediaItem() }
                        )
                      )
                    }
                  )
                }

                item(key = "late_night_chill_carousel") {
                  TuneifyYTMusicColumnCarousel(
                    songs = lateNightSongs.distinctBy { it.id },
                    activeMediaId = mediaMetadata?.id,
                    isPlaying = isPlaying,
                    onSongClick = { song ->
                      if (song.id == mediaMetadata?.id) {
                        playerConnection.togglePlayPause()
                      } else {
                        playerConnection.playQueue(YouTubeQueue.radio(song.toMediaMetadata()))
                      }
                    },
                    onSongLongClick = { song ->
                      haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                      menuState.show {
                        YouTubeSongMenu(song = song, navController = navController, onDismiss = menuState::dismiss)
                      }
                    },
                    onSongMoreClick = { song ->
                      menuState.show {
                        YouTubeSongMenu(song = song, navController = navController, onDismiss = menuState::dismiss)
                      }
                    },
                    modifier = Modifier.animateItem()
                  )
                }
              }
            }
            HomeSection.EnergyBoosters -> {
              if (energySongs.isNotEmpty()) {
                item(key = "energy_boosters_title") {
                  NavigationTitle(
                    title = "Energy Boosters",
                    label = null,
                    modifier = Modifier.animateItem(),
                    onPlayAllClick = {
                      playerConnection.playQueue(
                        ListQueue(
                          title = "Energy Boosters",
                          items = energySongs.map { it.toMediaMetadata().toMediaItem() }
                        )
                      )
                    }
                  )
                }

                item(key = "energy_boosters_carousel") {
                  TuneifyYTMusicColumnCarousel(
                    songs = energySongs.distinctBy { it.id },
                    activeMediaId = mediaMetadata?.id,
                    isPlaying = isPlaying,
                    onSongClick = { song ->
                      if (song.id == mediaMetadata?.id) {
                        playerConnection.togglePlayPause()
                      } else {
                        playerConnection.playQueue(YouTubeQueue.radio(song.toMediaMetadata()))
                      }
                    },
                    onSongLongClick = { song ->
                      haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                      menuState.show {
                        YouTubeSongMenu(song = song, navController = navController, onDismiss = menuState::dismiss)
                      }
                    },
                    onSongMoreClick = { song ->
                      menuState.show {
                        YouTubeSongMenu(song = song, navController = navController, onDismiss = menuState::dismiss)
                      }
                    },
                    modifier = Modifier.animateItem()
                  )
                }
              }
            }
            HomeSection.KeepListening -> {}
            HomeSection.AccountPlaylists -> {
              accountPlaylists
                ?.takeIf { it.isNotEmpty() }
                ?.let { accountPlaylists ->
                  item(key = "account_playlists_title") {
                    NavigationTitle(
                      label = stringResource(R.string.your_youtube_playlists),
                      title = accountName,
                      thumbnail = {
                        if (url != null) {
                          AsyncImage(
                            model =
                              ImageRequest.Builder(LocalContext.current)
                                .data(url)
                                .diskCachePolicy(CachePolicy.ENABLED)
                                .diskCacheKey(url)
                                .crossfade(false)
                                .build(),
                            placeholder = painterResource(id = R.drawable.person),
                            error = painterResource(id = R.drawable.person),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier =
                              Modifier.size(ListThumbnailSize)
                                .clip(RoundedCornerShape(ThumbnailCornerRadius))
                          )
                        } else {
                          Icon(
                            painter = painterResource(id = R.drawable.person),
                            contentDescription = null,
                            modifier = Modifier.size(ListThumbnailSize)
                          )
                        }
                      },
                      onClick = { navController.navigate("account") },
                      modifier = Modifier.animateItem()
                    )
                  }

                  item(key = "account_playlists_list") {
                    LazyRow(
                      contentPadding = PaddingValues(horizontal = 16.dp),
                      horizontalArrangement = Arrangement.spacedBy(14.dp),
                      modifier = Modifier.animateItem()
                    ) {
                      items(
                        items = accountPlaylists.distinctBy { it.id },
                        key = { it.id },
                      ) { item ->
                        ytGridItem(item)
                      }
                    }
                  }
                }
            }
            HomeSection.ForgottenFavorites -> {
              val favSongs = forgottenFavorites?.takeIf { it.isNotEmpty() }?.map { song ->
                SongItem(
                  id = song.id,
                  title = song.title,
                  artists = song.artists.map { com.music.innertube.models.Artist(name = it.name, id = it.id) },
                  album = song.song.albumName?.let { com.music.innertube.models.Album(name = it, id = song.song.albumId ?: "") },
                  duration = song.song.duration.toInt(),
                  thumbnail = song.thumbnailUrl ?: "",
                  endpoint = null
                )
              }
              val ytFallbacks = homePage?.sections?.drop(2)?.flatMap { it.items.filterIsInstance<SongItem>() }?.take(16)
              val songsForCarousel = (favSongs ?: ytFallbacks ?: emptyList()).filterNot { it.isBhojpuriSongItem() }

              if (songsForCarousel.isNotEmpty()) {
                item(key = "forgotten_favorites_title") {
                  val forgottenFavoritesTitle = stringResource(R.string.forgotten_favorites)
                  NavigationTitle(
                    title = forgottenFavoritesTitle,
                    label = null,
                    modifier = Modifier.animateItem(),
                    onPlayAllClick = {
                      playerConnection.playQueue(
                        ListQueue(
                          title = forgottenFavoritesTitle,
                          items = songsForCarousel.map { it.toMediaMetadata().toMediaItem() }
                        )
                      )
                    }
                  )
                }

                item(key = "forgotten_favorites_carousel") {
                  TuneifyYTMusicColumnCarousel(
                    songs = songsForCarousel.distinctBy { it.id },
                    activeMediaId = mediaMetadata?.id,
                    isPlaying = isPlaying,
                    onSongClick = { song ->
                      if (song.id == mediaMetadata?.id) {
                        playerConnection.togglePlayPause()
                      } else {
                        playerConnection.playQueue(
                          YouTubeQueue.radio(song.toMediaMetadata())
                        )
                      }
                    },
                    onSongLongClick = { song ->
                      haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                      menuState.show {
                        YouTubeSongMenu(
                          song = song,
                          navController = navController,
                          onDismiss = menuState::dismiss
                        )
                      }
                    },
                    onSongMoreClick = { song ->
                      menuState.show {
                        YouTubeSongMenu(
                          song = song,
                          navController = navController,
                          onDismiss = menuState::dismiss
                        )
                      }
                    },
                    modifier = Modifier.animateItem()
                  )
                }
              }
            }
            HomeSection.TrendingStations -> {
              if (stationSongs.isNotEmpty()) {
                item(key = "trending_stations_title") {
                  NavigationTitle(
                    title = "Trending Stations",
                    label = null,
                    modifier = Modifier.animateItem(),
                    onPlayAllClick = {
                      playerConnection.playQueue(
                        ListQueue(
                          title = "Trending Stations",
                          items = stationSongs.map { it.toMediaMetadata().toMediaItem() }
                        )
                      )
                    }
                  )
                }

                item(key = "trending_stations_carousel") {
                  TuneifyYTMusicColumnCarousel(
                    songs = stationSongs.distinctBy { it.id },
                    activeMediaId = mediaMetadata?.id,
                    isPlaying = isPlaying,
                    onSongClick = { song ->
                      if (song.id == mediaMetadata?.id) {
                        playerConnection.togglePlayPause()
                      } else {
                        playerConnection.playQueue(YouTubeQueue.radio(song.toMediaMetadata()))
                      }
                    },
                    onSongLongClick = { song ->
                      haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                      menuState.show {
                        YouTubeSongMenu(song = song, navController = navController, onDismiss = menuState::dismiss)
                      }
                    },
                    onSongMoreClick = { song ->
                      menuState.show {
                        YouTubeSongMenu(song = song, navController = navController, onDismiss = menuState::dismiss)
                      }
                    },
                    modifier = Modifier.animateItem()
                  )
                }
              }
            }
            is HomeSection.SimilarRecommendation -> {
              val recommendation = similarRecommendations?.getOrNull(section.index)
              if (recommendation != null && recommendation.items.isNotEmpty()) {
                item(key = "similar_to_title_${section.index}") {
                  NavigationTitle(
                    label = stringResource(R.string.similar_to),
                    title = recommendation.title.title,
                    thumbnail =
                      recommendation.title.thumbnailUrl?.let { thumbnailUrl ->
                        {
                          val shape = RoundedCornerShape(ThumbnailCornerRadius)
                          AsyncImage(
                            model = thumbnailUrl,
                            contentDescription = null,
                            modifier = Modifier.size(ListThumbnailSize).clip(shape)
                          )
                        }
                      },
                    onClick = {
                      val recTitle = recommendation.title
                      when (recTitle) {
                        is Song -> navController.navigate("album/${recTitle.album!!.id}")
                        is Album -> navController.navigate("album/${recTitle.id}")
                        is Artist -> navController.navigate("artist/${recTitle.id}")
                        is Playlist -> {}
                      }
                    },
                    modifier = Modifier.animateItem()
                  )
                }

                item(key = "similar_to_list_${section.index}") {
                  LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.animateItem()
                  ) {
                    items(recommendation.items.distinctBy { it.id }, key = { it.id }) { item ->
                      ytGridItem(item)
                    }
                  }
                }
              }
            }
            is HomeSection.HomePageSection -> {
              val sectionData = homePage?.sections?.getOrNull(section.index)
              if (sectionData != null) {
                val isDuplicate = section.index > 0 && homePage?.sections?.take(section.index)?.any { it.title.trim().equals(sectionData.title.trim(), ignoreCase = true) } == true
                val nonBhojpuriItems = sectionData.items.filterNot {
                  (it as? SongItem)?.isBhojpuriSongItem() == true
                }

                if (!isDuplicate && nonBhojpuriItems.isNotEmpty()) {
                  val sectionSongs = nonBhojpuriItems.filterIsInstance<SongItem>()
                  val hasPlayableSongs = sectionSongs.isNotEmpty()
                  val isSongsOnlySection = nonBhojpuriItems.all { it is SongItem }

                  item(key = "home_section_title_${section.index}") {
                    NavigationTitle(
                      title = sectionData.title,
                      label = null,
                      thumbnail =
                        sectionData.thumbnail?.let { thumbnailUrl ->
                          {
                            val shape = RoundedCornerShape(ThumbnailCornerRadius)
                            AsyncImage(
                              model = thumbnailUrl,
                              contentDescription = null,
                              modifier = Modifier.size(ListThumbnailSize).clip(shape)
                            )
                          }
                        },
                      onClick =
                        sectionData.endpoint?.let { endpoint ->
                          {
                            when {
                              endpoint.browseId == "FEmusic_moods_and_genres" ->
                                navController.navigate("mood_and_genres")
                              endpoint.params != null ->
                                navController.navigate(
                                   "youtube_browse/${endpoint.browseId}?params=${endpoint.params}"
                                )
                              else -> navController.navigate("browse/${endpoint.browseId}")
                            }
                          }
                        },
                      onPlayAllClick =
                        if (hasPlayableSongs) {
                          {
                            playerConnection.playQueue(
                              ListQueue(
                                title = sectionData.title,
                                items = sectionSongs.map { it.toMediaMetadata().toMediaItem() }
                              )
                            )
                          }
                        } else null,
                      modifier = Modifier.animateItem()
                    )
                  }

                  if (isSongsOnlySection) {
                    item(key = "home_section_list_${section.index}") {
                      TuneifyYTMusicColumnCarousel(
                        songs = sectionSongs.distinctBy { it.id },
                        activeMediaId = mediaMetadata?.id,
                        isPlaying = isPlaying,
                        onSongClick = { song ->
                          if (song.id == mediaMetadata?.id) {
                            playerConnection.togglePlayPause()
                          } else {
                            playerConnection.playQueue(
                              YouTubeQueue.radio(song.toMediaMetadata())
                            )
                          }
                        },
                        onSongLongClick = { song ->
                          haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                          menuState.show {
                            YouTubeSongMenu(
                              song = song,
                              navController = navController,
                              onDismiss = menuState::dismiss
                            )
                          }
                        },
                        onSongMoreClick = { song ->
                          menuState.show {
                            YouTubeSongMenu(
                              song = song,
                              navController = navController,
                              onDismiss = menuState::dismiss
                            )
                          }
                        },
                        modifier = Modifier.animateItem()
                      )
                    }
                  } else {
                    item(key = "home_section_list_${section.index}") {
                      LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.animateItem()
                      ) {
                        items(nonBhojpuriItems.distinctBy { it.id }, key = { it.id }) { item ->
                          ytGridItem(item)
                        }
                      }
                    }
                  }
                }
              }
            }
            HomeSection.FeaturedArtists -> {
              if (featuredArtistItems.isNotEmpty()) {
                item(key = "featured_artists_title") {
                  NavigationTitle(
                    title = "Featured Artists",
                    label = null,
                    modifier = Modifier.animateItem()
                  )
                }
                item(key = "featured_artists_row") {
                  LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth().animateItem()
                  ) {
                    items(featuredArtistItems, key = { it.id }) { artist ->
                      TuneifyArtistExpressiveCard(
                        name = artist.title,
                        thumbnailUrl = artist.thumbnail,
                        shape = TuneifyCookieShape,
                        onClick = { navController.navigate("artist/${artist.id}") },
                        onLongClick = {
                          haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                          menuState.show { YouTubeArtistMenu(artist = artist, onDismiss = menuState::dismiss) }
                        }
                      )
                    }
                  }
                }
              }
            }
            HomeSection.MoodAndGenres -> {
              item(key = "mood_and_genres_title") {
                NavigationTitle(
                  title = stringResource(R.string.mood_and_genres),
                  label = null,
                  onClick = { navController.navigate("mood_and_genres") },
                  modifier = Modifier.animateItem()
                )
              }
              item(key = "mood_and_genres_2x8_grid") {
                val genres: List<MoodAndGenres.Item> = remember(explorePage?.moodAndGenres) {
                  val fromApi = explorePage?.moodAndGenres?.distinctBy { it.title } ?: emptyList()
                  val fallbackGenres = listOf(
                    "Chill", "Workout", "Party", "Focus", "Hip-Hop", "Rock", "Pop", "Indie",
                    "R&B", "Romance", "Sleep", "Energize", "Jazz", "Electronic", "Classical", "Folk"
                  ).map { title ->
                    MoodAndGenres.Item(
                      title = title,
                      stripeColor = 0L,
                      endpoint = BrowseEndpoint(browseId = "FEmusic_moods_and_genres_category", params = null)
                    )
                  }
                  (fromApi + fallbackGenres).distinctBy { it.title }.take(16)
                }

                val realThumbnails = remember(quickPicks, homePage?.sections) {
                  val qpThumbs = quickPicks?.mapNotNull { it.thumbnailUrl } ?: emptyList()
                  val hpThumbs = homePage?.sections?.flatMap { it.items }?.mapNotNull { it.thumbnail } ?: emptyList()
                  (qpThumbs + hpThumbs).distinct().filter { it.isNotBlank() }
                }

                val columns = remember(genres) { genres.chunked(2).take(8) }

                LazyRow(
                  contentPadding = PaddingValues(horizontal = 16.dp),
                  horizontalArrangement = Arrangement.spacedBy(10.dp),
                  modifier = Modifier.fillMaxWidth().animateItem()
                ) {
                  items(columns, key = { col -> col.firstOrNull()?.title ?: col.hashCode() }) { columnCards ->
                    Column(
                      modifier = Modifier.width(170.dp),
                      verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                      columnCards.forEachIndexed { rowIdx, genre ->
                        val globalIdx = genres.indexOf(genre).coerceAtLeast(0)
                        val posterUrl = realThumbnails.getOrNull(globalIdx % realThumbnails.size.coerceAtLeast(1))
                          ?: CuratedGenrePosters.getOrElse(globalIdx) { CuratedGenrePosters[globalIdx % CuratedGenrePosters.size] }
                        val gradient = CuratedGenreGradients.getOrElse(globalIdx) { CuratedGenreGradients[globalIdx % CuratedGenreGradients.size] }

                        TuneifyCascadingGenreCard(
                          title = genre.title,
                          posterUrl = posterUrl,
                          gradientColors = gradient,
                          onClick = {
                            navController.navigate(
                              "youtube_browse/${genre.endpoint.browseId}?params=${genre.endpoint.params}"
                            )
                          },
                          modifier = Modifier.fillMaxWidth()
                        )
                      }
                    }
                  }
                }
              }
            }
          }
        }

        val totalSections = homePage?.sections?.size ?: 0
        val baseCount = if (initialSectionsCount.intValue > 0) initialSectionsCount.intValue else totalSections.coerceAtMost(4)
        if (totalSections > baseCount) {
          for (cIndex in baseCount until totalSections) {
            val sectionData = homePage?.sections?.getOrNull(cIndex)
            if (sectionData != null) {
              val sectionSongs = sectionData.items.filterIsInstance<SongItem>().filterNot { it.isBhojpuriSongItem() }
              val hasPlayableSongs = sectionSongs.isNotEmpty()
              val isSongsOnlySection = sectionData.items.isNotEmpty() && sectionData.items.all { it is SongItem }

              item(key = "continuation_title_$cIndex") {
                NavigationTitle(
                  title = sectionData.title,
                  label = null,
                  thumbnail = sectionData.thumbnail?.let { thumbnailUrl ->
                    {
                      val shape = RoundedCornerShape(ThumbnailCornerRadius)
                      AsyncImage(
                        model = thumbnailUrl,
                        contentDescription = null,
                        modifier = Modifier.size(ListThumbnailSize).clip(shape)
                      )
                    }
                  },
                  onClick = sectionData.endpoint?.let { endpoint ->
                    {
                      when {
                        endpoint.browseId == "FEmusic_moods_and_genres" -> navController.navigate("mood_and_genres")
                        endpoint.params != null -> navController.navigate("youtube_browse/${endpoint.browseId}?params=${endpoint.params}")
                        else -> navController.navigate("browse/${endpoint.browseId}")
                      }
                    }
                  },
                  onPlayAllClick = if (hasPlayableSongs) {
                    {
                      playerConnection.playQueue(
                        ListQueue(title = sectionData.title, items = sectionSongs.map { it.toMediaMetadata().toMediaItem() })
                      )
                    }
                  } else null
                )
              }

              if (isSongsOnlySection) {
                item(key = "continuation_list_$cIndex") {
                  TuneifyYTMusicColumnCarousel(
                    songs = sectionSongs.distinctBy { it.id },
                    activeMediaId = mediaMetadata?.id,
                    isPlaying = isPlaying,
                    onSongClick = { song ->
                      if (song.id == mediaMetadata?.id) {
                        playerConnection.togglePlayPause()
                      } else {
                        playerConnection.playQueue(YouTubeQueue.radio(song.toMediaMetadata()))
                      }
                    },
                    onSongLongClick = { song ->
                      haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                      menuState.show { YouTubeSongMenu(song = song, navController = navController, onDismiss = menuState::dismiss) }
                    },
                    onSongMoreClick = { song ->
                      menuState.show { YouTubeSongMenu(song = song, navController = navController, onDismiss = menuState::dismiss) }
                    }
                  )
                }
              } else {
                item(key = "continuation_list_$cIndex") {
                  LazyRow(
                    contentPadding = WindowInsets.systemBars.only(WindowInsetsSides.Horizontal).asPaddingValues(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                  ) {
                    val nonBhojpuriItems = sectionData.items.filterNot { (it as? SongItem)?.isBhojpuriSongItem() == true }
                    items(nonBhojpuriItems.distinctBy { it.id }, key = { it.id }) { item ->
                      ytGridItem(item)
                    }
                  }
                }
              }
            }
          }
        }

        if (isLoadingMore) {
          item(key = "continuation_loading") {
            Box(
              modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
              contentAlignment = Alignment.Center
            ) {
              androidx.compose.material3.CircularProgressIndicator(
                modifier = Modifier.size(28.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 2.5.dp
              )
            }
          }
        } else if (isLoading && homePage?.sections.isNullOrEmpty()) {
          item(key = "loading_shimmer") {
            ShimmerHost(modifier = Modifier.animateItem()) {
              Row(
                modifier =
                  Modifier.horizontalScroll(rememberScrollState())
                    .padding(
                      WindowInsets.systemBars.only(WindowInsetsSides.Horizontal).asPaddingValues()
                    )
              ) {
                repeat(3) {
                  Spacer(
                    modifier =
                      Modifier.padding(horizontal = 8.dp, vertical = 12.dp)
                        .width(250.dp)
                        .height(290.dp)
                        .clip(MaterialTheme.shapes.extraLarge)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                  )
                }
              }

              TextPlaceholder(
                height = 36.dp,
                modifier = Modifier.padding(12.dp).width(200.dp),
              )
              Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)) {
                repeat(2) {
                  Row(modifier = Modifier.fillMaxWidth()) {
                    repeat(3) {
                      GridItemPlaceHolder(modifier = Modifier.weight(1f), fillMaxWidth = true)
                    }
                  }
                }
              }
            }
          }
        }

        item(key = "bottom_spacer") { Spacer(modifier = Modifier.height(30.dp)) }
      }
    }
  }
}
