package echo.music.iad1tya.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.draw.shadow
import echo.music.iad1tya.ui.component.glass.LocalDynamicAccentColor
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
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
import com.music.echo.utils.hapticScrollEdge
import com.music.innertube.YouTube
import com.music.innertube.models.AlbumItem
import com.music.innertube.models.ArtistItem
import com.music.innertube.models.PlaylistItem
import com.music.innertube.models.SongItem
import com.music.innertube.models.WatchEndpoint
import com.music.innertube.models.YTItem
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
import echo.music.iad1tya.ui.component.NavigationTitle
import echo.music.iad1tya.ui.component.RandomizeGridItem
import echo.music.iad1tya.ui.component.SongGridItem
import echo.music.iad1tya.ui.component.SongListItem
import echo.music.iad1tya.ui.component.SpeedDialGridItem
import echo.music.iad1tya.ui.component.TuneifyCategoryCard
import echo.music.iad1tya.ui.component.TuneifyGreetingHeader
import echo.music.iad1tya.ui.component.TuneifyHomeHeader
import echo.music.iad1tya.ui.component.TuneifySearchBar
import echo.music.iad1tya.ui.component.TuneifyMusicCard
import echo.music.iad1tya.ui.component.TuneifyRecentSongItem
import echo.music.iad1tya.ui.component.YouTubeGridItem
import echo.music.iad1tya.ui.component.YouTubeListItem
import echo.music.iad1tya.ui.component.glass.CategorySelectorRow
import echo.music.iad1tya.ui.component.glass.GlassPill
import echo.music.iad1tya.ui.component.glass.DynamicArtworkBackground
import echo.music.iad1tya.ui.component.glass.ExpressiveSectionHeader
import echo.music.iad1tya.ui.component.glass.GlassArtworkCard
import echo.music.iad1tya.ui.component.glass.HeroArtistCard
import echo.music.iad1tya.ui.component.glass.HeroSpotlightCarousel
import echo.music.iad1tya.ui.component.glass.SpotlightItem
import echo.music.iad1tya.ui.component.glass.TuneifyDetailedSongCard
import echo.music.iad1tya.ui.component.glass.MadeForYouCard
import echo.music.iad1tya.ui.component.glass.TuneifyGlassTokens
import echo.music.iad1tya.ui.component.glass.TuneifyGreeting
import echo.music.iad1tya.ui.component.glass.TuneifyHeaderRow
import echo.music.iad1tya.ui.component.glass.TuneifySongColumnGridCarousel
import echo.music.iad1tya.ui.component.glass.TuneifySpeedDialRow
import echo.music.iad1tya.ui.component.glass.TuneifySquareAlbumCarousel
import echo.music.iad1tya.ui.component.glass.TuneifyCommunityCollageRow
import echo.music.iad1tya.ui.component.glass.TuneifyFeaturedPlaylistCard
import echo.music.iad1tya.ui.component.glass.TuneifyPromoBannerCard
import echo.music.iad1tya.ui.theme.TuneifyTokens
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

data class TuneifyFallbackPick(
  val title: String,
  val artist: String,
  val artworkUrl: String,
  val videoId: String,
  val badge: String
)

sealed class HomeSection(val id: String, val baseWeight: Int) {
  data object SpeedDial : HomeSection("speed_dial", 100)

  data object AiRecommendations : HomeSection("ai_recommendations", 95)

  data object QuickPicks : HomeSection("quick_picks", 90)

  data object DailyDiscover : HomeSection("daily_discover", 80)

  data object KeepListening : HomeSection("keep_listening", 50)

  data object AccountPlaylists : HomeSection("account_playlists", 40)

  data object ForgottenFavorites : HomeSection("forgotten_favorites", 30)

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

  val dbPlaylist by database.playlistByBrowseId(item.playlist.id).collectAsState(initial = null)
  val isBookmarked = dbPlaylist?.playlist?.bookmarkedAt != null
  val dynamicAccent = LocalDynamicAccentColor.current
  val cardShape = RoundedCornerShape(22.dp)

  Card(
    modifier = modifier.width(320.dp).height(420.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0x3514261B)),
    shape = cardShape,
    border = BorderStroke(1.dp, Brush.linearGradient(listOf(Color(0x35FFFFFF), Color(0x10FFFFFF)))),
    onClick = onClick
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Box(modifier = Modifier.size(100.dp).clip(RoundedCornerShape(12.dp)).border(0.75.dp, Color(0x30FFFFFF), RoundedCornerShape(12.dp))) {
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
                .clip(RoundedCornerShape(4.dp))
                .combinedClickable(onClick = { onSongClick(song) }),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            AsyncImage(
              model = song.thumbnail.resize(544, 544),
              contentDescription = null,
              modifier = Modifier.size(56.dp).clip(RoundedCornerShape(10.dp)).border(0.75.dp, Color(0x25FFFFFF), RoundedCornerShape(10.dp)),
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
            Modifier
              .size(48.dp)
              .shadow(8.dp, CircleShape, ambientColor = dynamicAccent, spotColor = dynamicAccent)
              .clip(CircleShape)
              .background(dynamicAccent)
        ) {
          Icon(
            painter = painterResource(R.drawable.ic_widget_play),
            contentDescription = null,
            tint = TuneifyGlassTokens.OnVoltLime,
            modifier = Modifier.size(24.dp)
          )
        }

        IconButton(
          onClick = {
            item.playlist.radioEndpoint?.let { playerConnection?.playQueue(YouTubeQueue(it)) }
          },
          modifier =
            Modifier.size(48.dp)
              .clip(CircleShape)
              .background(Color(0x25FFFFFF))
              .border(0.75.dp, Color(0x35FFFFFF), CircleShape)
        ) {
          Icon(
            painter = painterResource(R.drawable.radio),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(22.dp)
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
              .clip(CircleShape)
              .background(Color(0x25FFFFFF))
              .border(0.75.dp, Color(0x35FFFFFF), CircleShape)
        ) {
          Icon(
            painter =
              painterResource(
                if (isBookmarked) R.drawable.library_add_check else R.drawable.library_add
              ),
            contentDescription = null,
            tint = if (isBookmarked) dynamicAccent else Color.White,
            modifier = Modifier.size(22.dp)
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
        .clip(RoundedCornerShape(22.dp))
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
        containerColor = Color(0x3514261B),
      ),
    shape = RoundedCornerShape(22.dp),
    border = BorderStroke(1.dp, Brush.linearGradient(listOf(Color(0x35FFFFFF), Color(0x10FFFFFF))))
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
  val events by database.events().collectAsState(initial = emptyList())

  val allAvailableThumbnails = remember(homePage, explorePage, quickPicks, communityPlaylists, events) {
    val result = linkedSetOf<String>()
    explorePage?.newReleaseAlbums?.forEach { album ->
      if (album.thumbnail.isNotBlank()) result.add(album.thumbnail)
    }
    homePage?.sections?.forEach { section ->
      section.items.forEach { item ->
        item.thumbnail?.takeIf { it.isNotBlank() }?.let { result.add(it) }
      }
    }
    quickPicks?.forEach { s ->
      s.thumbnailUrl?.takeIf { it.isNotBlank() }?.let { result.add(it) }
    }
    communityPlaylists?.forEach { cp ->
      cp.songs.forEach { s ->
        if (s.thumbnail.isNotBlank()) result.add(s.thumbnail)
      }
    }
    events.forEach { e ->
      e.song.thumbnailUrl?.takeIf { it.isNotBlank() }?.let { result.add(it) }
    }
    result.toList()
  }

  // Shared pool of every thumbnail we already have, used only to top up a block that does not yet
  // have enough of its own art. Each block draws from a different offset so the four cards never
  // end up showing the exact same three posters.
  val artPool = remember(allAvailableThumbnails) { allAvailableThumbnails.distinct() }

  fun topUp(seed: List<String>, offset: Int, target: Int = 3): List<String> {
    val out = seed.filter { it.isNotBlank() }.distinct().toMutableList()
    if (out.size < target && artPool.isNotEmpty()) {
      var index = offset
      var guard = 0
      while (out.size < target && guard < artPool.size) {
        val url = artPool[index % artPool.size]
        if (!out.contains(url)) out.add(url)
        index++
        guard++
      }
    }
    return out.take(target)
  }

  val lateNightsArtwork = remember(aiRecommendedPlaylist, keepListening, artPool) {
    val seed = mutableListOf<String>()
    aiRecommendedPlaylist?.second?.forEach { s ->
      s.thumbnailUrl?.takeIf { it.isNotBlank() }?.let { seed.add(it) }
    }
    keepListening?.forEach { item ->
      when (item) {
        is Song -> item.thumbnailUrl?.takeIf { it.isNotBlank() }?.let { seed.add(it) }
        is Album -> item.thumbnailUrl?.takeIf { it.isNotBlank() }?.let { seed.add(it) }
        else -> {}
      }
    }
    topUp(seed, offset = 0)
  }

  val newNotedArtwork = remember(explorePage, artPool) {
    val seed = mutableListOf<String>()
    explorePage?.newReleaseAlbums?.forEach { if (it.thumbnail.isNotBlank()) seed.add(it.thumbnail) }
    topUp(seed, offset = 4)
  }

  val onRepeatArtwork = remember(events, quickPicks, artPool) {
    val seed = mutableListOf<String>()
    events.forEach { e -> e.song.thumbnailUrl?.takeIf { it.isNotBlank() }?.let { seed.add(it) } }
    quickPicks?.forEach { s -> s.thumbnailUrl?.takeIf { it.isNotBlank() }?.let { seed.add(it) } }
    topUp(seed, offset = 8)
  }

  val indieArtwork = remember(communityPlaylists, artPool) {
    val seed = mutableListOf<String>()
    communityPlaylists?.forEach { cp ->
      cp.songs.forEach { s -> if (s.thumbnail.isNotBlank()) seed.add(s.thumbnail) }
    }
    topUp(seed, offset = 12)
  }

  // Real counts only. When a block has no real number yet we show nothing instead of a fake figure.
  val lateNightsCount = remember(keepListening) { keepListening?.size?.takeIf { it > 0 }?.toString() }
  val newNotedCount = remember(explorePage) {
    explorePage?.newReleaseAlbums?.size?.takeIf { it > 0 }?.toString()
  }
  val onRepeatCount = remember(events, quickPicks) {
    events.size.takeIf { it > 0 }?.toString()
      ?: quickPicks?.size?.takeIf { it > 0 }?.toString()
  }
  val indieCount = remember(communityPlaylists) {
    communityPlaylists?.size?.takeIf { it > 0 }?.toString()
  }

  val recentDisplaySongs = remember(events, quickPicks, homePage) {
    val historySongs = events.map { it.song }.distinctBy { it.id }.take(10)
    if (historySongs.isNotEmpty()) {
      historySongs.mapIndexed { index, song ->
        val (genre, color) = when (index % 5) {
          0 -> "R&B" to TuneifyTokens.Red
          1 -> "Pop" to TuneifyTokens.Yellow
          2 -> "Electronic" to TuneifyTokens.Purple
          3 -> "Indie" to TuneifyTokens.Green
          else -> "Alternative" to TuneifyTokens.Blue
        }
        TuneifyRecentSongItem(
          id = song.id,
          title = song.title,
          artist = song.artists.joinToString { it.name },
          artworkUrl = song.thumbnailUrl,
          genreName = genre,
          genreColor = color,
          song = song
        )
      }
    } else if (!quickPicks.isNullOrEmpty()) {
      quickPicks!!.take(10).mapIndexed { index, song ->
        val (genre, color) = when (index % 5) {
          0 -> "R&B" to TuneifyTokens.Red
          1 -> "Pop" to TuneifyTokens.Yellow
          2 -> "Electronic" to TuneifyTokens.Purple
          3 -> "Indie" to TuneifyTokens.Green
          else -> "Alternative" to TuneifyTokens.Blue
        }
        TuneifyRecentSongItem(
          id = song.id,
          title = song.title,
          artist = song.artists.joinToString { it.name },
          artworkUrl = song.thumbnailUrl,
          genreName = genre,
          genreColor = color,
          song = song
        )
      }
    } else {
      val realYtSongs = homePage?.sections?.flatMap { it.items }?.filterIsInstance<SongItem>()?.distinctBy { it.id }?.take(10)
      if (!realYtSongs.isNullOrEmpty()) {
        realYtSongs.mapIndexed { index, songItem ->
          val (genre, color) = when (index % 5) {
            0 -> "R&B" to TuneifyTokens.Red
            1 -> "Pop" to TuneifyTokens.Yellow
            2 -> "Electronic" to TuneifyTokens.Purple
            3 -> "Indie" to TuneifyTokens.Green
            else -> "Alternative" to TuneifyTokens.Blue
          }
          TuneifyRecentSongItem(
            id = songItem.id,
            title = songItem.title,
            artist = songItem.artists.joinToString { it.name },
            artworkUrl = songItem.thumbnail,
            genreName = genre,
            genreColor = color,
            song = null
          )
        }
      } else {
        emptyList()
      }
    }
  }

  val isLoading: Boolean by viewModel.isLoading.collectAsState()
  val isMoodAndGenresLoading = isLoading && explorePage?.moodAndGenres == null
  val isRefreshing by viewModel.isRefreshing.collectAsState()
  val isRandomizing by viewModel.isRandomizing.collectAsState()
  val pullRefreshState = rememberPullToRefreshState()

  val quickPicksLazyGridState = rememberLazyGridState()
  val forgottenFavoritesLazyGridState = rememberLazyGridState()

  val accountName by viewModel.accountName.collectAsState()
  val accountImageUrl by viewModel.accountImageUrl.collectAsState()

  val timeGreeting =
    remember {
      when (java.time.LocalTime.now().hour) {
        in 0..11 -> "Good morning,"
        in 12..16 -> "Good afternoon,"
        else -> "Good evening,"
      }
    }
  val innerTubeCookie by rememberPreference(InnerTubeCookieKey, "")
  val randomizeHomeOrder = false
  val showSpeedDial = false

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

  LaunchedEffect(Unit) {
    snapshotFlow { lazylistState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
      .collect { lastVisibleIndex ->
        val len = lazylistState.layoutInfo.totalItemsCount
        if (lastVisibleIndex != null && lastVisibleIndex >= len - 3) {
          viewModel.loadMoreYouTubeItems(homePage?.continuation)
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
    val title = item.title
    val subtitle = when (item) {
      is SongItem -> item.artists.joinToString { it.name }
      is AlbumItem -> item.artists?.joinToString { it.name } ?: "Album"
      is ArtistItem -> "Artist"
      is PlaylistItem -> item.author?.name ?: "Playlist"
      else -> ""
    }
    val badge = when (item) {
      is SongItem -> "SONG"
      is AlbumItem -> "ALBUM"
      is ArtistItem -> "ARTIST"
      is PlaylistItem -> "PLAYLIST"
      else -> null
    }

    GlassArtworkCard(
      title = title,
      subtitle = subtitle,
      thumbnailUrl = item.thumbnail,
      badgeText = badge,
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
      onPlayClick = {
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
      }
    )
  }

  val homeSections =
    remember(
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

      // SpeedDial is disabled to maintain Tuneify's premium organic glass layout
      if (aiRecommendedPlaylist != null && aiRecommendedPlaylist!!.second.isNotEmpty())
        list.add(HomeSection.AiRecommendations)
      if (communityPlaylists?.isNotEmpty() == true) list.add(HomeSection.FromTheCommunity)
      if (dailyDiscover?.isNotEmpty() == true) list.add(HomeSection.DailyDiscover)
      if (keepListening?.isNotEmpty() == true) list.add(HomeSection.KeepListening)
      if (accountPlaylists?.isNotEmpty() == true) list.add(HomeSection.AccountPlaylists)
      if (forgottenFavorites?.isNotEmpty() == true) list.add(HomeSection.ForgottenFavorites)

      similarRecommendations?.indices?.forEach { i ->
        list.add(HomeSection.SimilarRecommendation(i))
      }

      homePage?.sections?.indices?.forEach { i -> list.add(HomeSection.HomePageSection(i)) }

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
            .padding(LocalPlayerAwareWindowInsets.current.asPaddingValues()),
      )
    }
  ) {
    DynamicArtworkBackground {
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

      var selectedMusicCategory by rememberSaveable { mutableStateOf("Music") }

      val homeSongs = remember(homePage) {
        homePage?.sections?.flatMap { it.items }?.filterIsInstance<SongItem>()?.distinctBy { it.id } ?: emptyList()
      }

      val defaultHits = remember {
        listOf(
          SongItem(id = "HrnrqYxYrbk", title = "Satranga", artists = listOf(com.music.innertube.models.Artist("Arijit Singh", null)), album = null, duration = null, thumbnail = "https://i.ytimg.com/vi/HrnrqYxYrbk/hqdefault.jpg"),
          SongItem(id = "VAdGW7QDJiU", title = "Chaleya", artists = listOf(com.music.innertube.models.Artist("Arijit Singh", null)), album = null, duration = null, thumbnail = "https://i.ytimg.com/vi/VAdGW7QDJiU/hqdefault.jpg"),
          SongItem(id = "ElZfdU54Cp8", title = "Apna Bana Le", artists = listOf(com.music.innertube.models.Artist("Arijit Singh", null)), album = null, duration = null, thumbnail = "https://i.ytimg.com/vi/ElZfdU54Cp8/hqdefault.jpg"),
          SongItem(id = "BddP6PYo2gs", title = "Kesariya", artists = listOf(com.music.innertube.models.Artist("Arijit Singh", null)), album = null, duration = null, thumbnail = "https://i.ytimg.com/vi/BddP6PYo2gs/hqdefault.jpg"),
          SongItem(id = "RLzC55ai0eo", title = "Heeriye", artists = listOf(com.music.innertube.models.Artist("Jasleen Royal", null)), album = null, duration = null, thumbnail = "https://i.ytimg.com/vi/RLzC55ai0eo/hqdefault.jpg"),
          SongItem(id = "SlPhMPnQ58k", title = "Tum Hi Ho", artists = listOf(com.music.innertube.models.Artist("Arijit Singh", null)), album = null, duration = null, thumbnail = "https://i.ytimg.com/vi/SlPhMPnQ58k/hqdefault.jpg"),
          SongItem(id = "xRb8hxwN5zc", title = "Agar Tum Saath Ho", artists = listOf(com.music.innertube.models.Artist("Arijit Singh", null)), album = null, duration = null, thumbnail = "https://i.ytimg.com/vi/xRb8hxwN5zc/hqdefault.jpg"),
          SongItem(id = "K-pX4qwtAxA", title = "Chaiyya Chaiyya", artists = listOf(com.music.innertube.models.Artist("Sukhwinder Singh", null)), album = null, duration = null, thumbnail = "https://i.ytimg.com/vi/K-pX4qwtAxA/hqdefault.jpg"),
          SongItem(id = "6FURuLYrR_Q", title = "Paro", artists = listOf(com.music.innertube.models.Artist("Aditya Rikhari", null)), album = null, duration = null, thumbnail = "https://i.ytimg.com/vi/6FURuLYrR_Q/hqdefault.jpg"),
          SongItem(id = "sAzlWScHTc4", title = "Jaana Samjho Na", artists = listOf(com.music.innertube.models.Artist("Aditya Rikhari", null)), album = null, duration = null, thumbnail = "https://i.ytimg.com/vi/sAzlWScHTc4/hqdefault.jpg"),
          SongItem(id = "9-7mQ6lV-yI", title = "Sajna Ve", artists = listOf(com.music.innertube.models.Artist("Aditya Rikhari", null)), album = null, duration = null, thumbnail = "https://i.ytimg.com/vi/9-7mQ6lV-yI/hqdefault.jpg"),
          SongItem(id = "2Vv-BfVoq4g", title = "Perfect", artists = listOf(com.music.innertube.models.Artist("Ed Sheeran", null)), album = null, duration = null, thumbnail = "https://i.ytimg.com/vi/2Vv-BfVoq4g/hqdefault.jpg")
        )
      }

      val songsForCarousel = remember(quickPicks, homeSongs, defaultHits) {
        val list = mutableListOf<SongItem>()
        if (!quickPicks.isNullOrEmpty()) {
          list.addAll(quickPicks!!.map { pick ->
            SongItem(
              id = pick.id,
              title = pick.title,
              artists = pick.artists.map { com.music.innertube.models.Artist(name = it.name, id = it.id) },
              album = null,
              duration = null,
              thumbnail = pick.thumbnailUrl ?: "https://i.ytimg.com/vi/${pick.id}/hqdefault.jpg",
              endpoint = WatchEndpoint(videoId = pick.id)
            )
          })
        }
        list.addAll(homeSongs)
        list.addAll(defaultHits)
        list.distinctBy { it.id }.take(16)
      }

      val trendingSongs = remember(homeSongs, defaultHits) {
        val list = mutableListOf<SongItem>()
        if (homeSongs.size > 4) list.addAll(homeSongs.drop(4))
        list.addAll(defaultHits.reversed())
        list.distinctBy { it.id }.take(12)
      }

      val dailyDiscoveries = remember(homeSongs, defaultHits, songsForCarousel) {
        val list = mutableListOf<SongItem>()
        if (homeSongs.size > 8) list.addAll(homeSongs.drop(8))
        list.addAll(defaultHits.shuffled())
        list.distinctBy { it.id }.take(12)
      }

      val spotlightMixes = remember(defaultHits, homeSongs) {
        val list = mutableListOf<SongItem>()
        list.addAll(defaultHits.takeLast(8))
        list.addAll(homeSongs)
        list.distinctBy { it.id }.take(8)
      }

      LazyColumn(
        state = lazylistState,
        modifier = Modifier.hapticScrollEdge(lazylistState),
        contentPadding = PaddingValues(
          top = LocalPlayerAwareWindowInsets.current.asPaddingValues().calculateTopPadding() + 8.dp,
          bottom = 180.dp
        )
      ) {
        // 1. Header with Tuneify waveform logo, notification, profile
        item(key = "tuneify_home_header") {
          TuneifyHeaderRow(
            onNotificationClick = { navController.navigate("update") },
            onSettingsClick = { navController.navigate("settings") },
            onProfileClick = { navController.navigate("settings/account") },
            accountImageUrl = accountImageUrl
          )
        }

        // 2. Expressive Greeting: "Good afternoon," "Guest" (or real account name if signed in)
        item(key = "tuneify_greeting") {
          val resolvedUserName = if (isLoggedIn && !accountName.isNullOrBlank() && !accountName.equals("Guest", ignoreCase = true)) {
            accountName!!
          } else {
            "Guest"
          }
          TuneifyGreeting(
            greeting = timeGreeting,
            userName = resolvedUserName
          )
        }

        // 3. Hero Section: Interactive Spotlight Carousel dynamically driven by user's recent music
        item(key = "tuneify_hero_card") {
          val heroSpotlights = remember(recentDisplaySongs, quickPicks, homeSongs) {
            val list = mutableListOf<SpotlightItem>()

            // 1. Prioritize user's actual recently listened tracks
            recentDisplaySongs.take(4).forEach { recent ->
              val artwork = recent.artworkUrl?.takeIf { it.isNotBlank() }
                ?: "https://i.ytimg.com/vi/${recent.id}/hqdefault.jpg"
              list.add(
                SpotlightItem(
                  id = recent.id,
                  title = recent.title,
                  subtitle = recent.artist ?: "Recently Played",
                  artworkUrl = artwork,
                  videoId = recent.id
                )
              )
            }

            // 2. If fewer than 3, add from quickPicks / homeSongs with real YouTube music posters
            if (list.size < 3 && !quickPicks.isNullOrEmpty()) {
              quickPicks!!.forEach { pick ->
                if (list.none { it.videoId == pick.id }) {
                  val artwork = pick.thumbnailUrl ?: "https://i.ytimg.com/vi/${pick.id}/hqdefault.jpg"
                  list.add(
                    SpotlightItem(
                      id = pick.id,
                      title = pick.title,
                      subtitle = pick.artists.joinToString { it.name }.ifEmpty { "Recommended for you" },
                      artworkUrl = artwork,
                      videoId = pick.id
                    )
                  )
                }
              }
            }

            if (list.size < 3 && homeSongs.isNotEmpty()) {
              homeSongs.forEach { song ->
                if (list.none { it.videoId == song.id }) {
                  val artwork = song.thumbnail ?: "https://i.ytimg.com/vi/${song.id}/hqdefault.jpg"
                  list.add(
                    SpotlightItem(
                      id = song.id,
                      title = song.title,
                      subtitle = song.artists.joinToString { it.name }.ifEmpty { "Trending Hit" },
                      artworkUrl = artwork,
                      videoId = song.id
                    )
                  )
                }
              }
            }

            // 3. Guaranteed fallback with REAL YouTube Music video posters (No stock photos!)
            val verifiedFallbackHits = listOf(
              SpotlightItem(
                id = "HrnrqYxYrbk",
                title = "Satranga",
                subtitle = "Arijit Singh",
                artworkUrl = "https://i.ytimg.com/vi/HrnrqYxYrbk/hqdefault.jpg",
                videoId = "HrnrqYxYrbk"
              ),
              SpotlightItem(
                id = "VAdGW7QDJiU",
                title = "Chaleya",
                subtitle = "Arijit Singh",
                artworkUrl = "https://i.ytimg.com/vi/VAdGW7QDJiU/hqdefault.jpg",
                videoId = "VAdGW7QDJiU"
              ),
              SpotlightItem(
                id = "6FURuLYrR_Q",
                title = "Paro",
                subtitle = "Aditya Rikhari",
                artworkUrl = "https://i.ytimg.com/vi/6FURuLYrR_Q/hqdefault.jpg",
                videoId = "6FURuLYrR_Q"
              ),
              SpotlightItem(
                id = "4NRXx6U8ABQ",
                title = "Blinding Lights",
                subtitle = "The Weeknd",
                artworkUrl = "https://i.ytimg.com/vi/4NRXx6U8ABQ/hqdefault.jpg",
                videoId = "4NRXx6U8ABQ"
              )
            )

            verifiedFallbackHits.forEach { fallback ->
              if (list.size < 3 && list.none { it.videoId == fallback.videoId }) {
                list.add(fallback)
              }
            }

            list.take(4)
          }

          HeroSpotlightCarousel(
            spotlights = heroSpotlights,
            onPlayClick = { spotlight ->
              playerConnection.playQueue(
                YouTubeQueue(endpoint = WatchEndpoint(videoId = spotlight.videoId))
              )
            },
            onClick = { spotlight ->
              val targetArtistId = homeSongs.firstOrNull()?.artists?.firstOrNull()?.id
              if (targetArtistId != null) {
                navController.navigate("artist/$targetArtistId")
              } else {
                playerConnection.playQueue(
                  YouTubeQueue(endpoint = WatchEndpoint(videoId = spotlight.videoId))
                )
              }
            }
          )
        }

        // 4. Mood & Genres Row at the top of Home (Horizontally scrollable glass pills)
        item(key = "tuneify_mood_and_genres_top") {
          val defaultMoods = remember {
            listOf(
              "Chill" to "FEmusic_moods_and_genres",
              "Workout" to "FEmusic_moods_and_genres",
              "Romance" to "FEmusic_moods_and_genres",
              "Party" to "FEmusic_moods_and_genres",
              "Focus" to "FEmusic_moods_and_genres",
              "Feel Good" to "FEmusic_moods_and_genres",
              "Sleep" to "FEmusic_moods_and_genres",
              "Energize" to "FEmusic_moods_and_genres"
            )
          }
          val moodItems = explorePage?.moodAndGenres?.take(10)

          LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(bottom = 16.dp)
          ) {
            if (!moodItems.isNullOrEmpty()) {
              items(moodItems.distinctBy { it.title }, key = { it.title }) { item ->
                GlassPill(
                  text = item.title,
                  selected = false,
                  onClick = {
                    navController.navigate(
                      "youtube_browse/${item.endpoint.browseId}?params=${item.endpoint.params}"
                    )
                  }
                )
              }
            } else {
              items(defaultMoods, key = { it.first }) { (title, _) ->
                GlassPill(
                  text = title,
                  selected = false,
                  onClick = {
                    navController.navigate("mood_and_genres")
                  }
                )
              }
            }
          }
        }

        // 5. Recently Played (Placed directly below Hero Carousel & Mood Pills as requested)
        if (recentDisplaySongs.isNotEmpty()) {
          item(key = "tuneify_recently_played_header") {
            NavigationTitle(
              title = "Recently played",
              onClick = { navController.navigate("history") },
              modifier = Modifier.animateItem()
            )
          }

          item(key = "tuneify_recently_played_content") {
            LazyRow(
              contentPadding = PaddingValues(horizontal = 20.dp),
              horizontalArrangement = Arrangement.spacedBy(14.dp),
              modifier = Modifier.padding(bottom = 16.dp)
            ) {
              items(recentDisplaySongs, key = { it.id }) { item ->
                TuneifyMusicCard(
                  title = item.title,
                  artist = item.artist,
                  artworkUrl = item.artworkUrl,
                  genreName = item.genreName,
                  genreColor = item.genreColor,
                  isActive = (item.song?.id ?: item.id) == mediaMetadata?.id,
                  isPlaying = isPlaying,
                  onClick = {
                    if (item.song != null) {
                      if (item.song.id == mediaMetadata?.id) {
                        playerConnection.togglePlayPause()
                      } else {
                        playerConnection.playQueue(
                          YouTubeQueue.radio(item.song.toMediaMetadata())
                        )
                      }
                    } else {
                      playerConnection.playQueue(
                        YouTubeQueue(WatchEndpoint(videoId = item.id))
                      )
                    }
                  },
                  onLongClick = {
                    if (item.song != null) {
                      haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                      menuState.show {
                        SongMenu(
                          originalSong = item.song,
                          navController = navController,
                          onDismiss = menuState::dismiss
                        )
                      }
                    }
                  }
                )
              }
            }
          }
        }

        // 6. Signature 4-Track Stack Carousel #1: Song Mixes (reverted & guaranteed full columns)
        item(key = "tuneify_quick_picks_header") {
          ExpressiveSectionHeader(
            title = "Song Mixes",
            onChevronClick = { navController.navigate("auto_playlist/top") }
          )
        }

        item(key = "tuneify_quick_picks_row") {
          TuneifySongColumnGridCarousel(
            songs = songsForCarousel,
            onSongClick = { song ->
              playerConnection.playQueue(
                YouTubeQueue(
                  endpoint = song.endpoint ?: WatchEndpoint(videoId = song.id),
                  preloadItem = song.toMediaMetadata()
                )
              )
            },
            modifier = Modifier.padding(bottom = 14.dp)
          )
        }

        // 7. Discover Trending Charts & Essentials (Banner)
        item(key = "tuneify_promo_banner") {
          TuneifyPromoBannerCard(
            title = "Discover Trending Charts & Essentials",
            artworkUrl = songsForCarousel.firstOrNull()?.thumbnail
              ?: "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600&auto=format&fit=crop&q=80",
            onClick = { navController.navigate("charts_screen") },
            modifier = Modifier.padding(bottom = 16.dp)
          )
        }

        // 8. Signature 4-Track Stack Carousel #2: Trending Hits
        if (trendingSongs.size >= 4) {
          item(key = "tuneify_trending_hits_header") {
            ExpressiveSectionHeader(
              title = "Trending Hits",
              onChevronClick = { navController.navigate("charts_screen") }
            )
          }

          item(key = "tuneify_trending_hits_row") {
            TuneifySongColumnGridCarousel(
              songs = trendingSongs,
              onSongClick = { song ->
                playerConnection.playQueue(
                  YouTubeQueue(
                    endpoint = song.endpoint ?: WatchEndpoint(videoId = song.id),
                    preloadItem = song.toMediaMetadata()
                  )
                )
              },
              modifier = Modifier.padding(bottom = 16.dp)
            )
          }
        }

        // 9. Made For You Section (Standard cute 150.dp glass rounded square cards)
        item(key = "tuneify_made_for_you_header") {
          ExpressiveSectionHeader(
            title = "Made For You",
            onChevronClick = { navController.navigate("explore") }
          )
        }

        item(key = "tuneify_made_for_you_row") {
          LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.padding(bottom = 16.dp)
          ) {
            item {
              GlassArtworkCard(
                title = "Daily Mix 1",
                subtitle = "Made for you",
                thumbnailUrl = "https://i.ytimg.com/vi/6FURuLYrR_Q/hqdefault.jpg",
                onClick = { navController.navigate("auto_playlist/top") },
                onPlayClick = {
                  val playTarget = homeSongs.firstOrNull()?.toMediaMetadata()
                    ?: quickPicks?.firstOrNull()?.toMediaMetadata()
                  if (playTarget != null) {
                    playerConnection.playQueue(YouTubeQueue.radio(playTarget))
                  } else {
                    playerConnection.playQueue(YouTubeQueue(WatchEndpoint(videoId = "6FURuLYrR_Q")))
                  }
                }
              )
            }
            item {
              GlassArtworkCard(
                title = "Chill Beats",
                subtitle = "Relax & Focus",
                thumbnailUrl = "https://i.ytimg.com/vi/sAzlWScHTc4/hqdefault.jpg",
                onClick = { navController.navigate("mood_and_genres") },
                onPlayClick = {
                  val playTarget = homeSongs.drop(1).firstOrNull()?.toMediaMetadata()
                    ?: quickPicks?.drop(1)?.firstOrNull()?.toMediaMetadata()
                  if (playTarget != null) {
                    playerConnection.playQueue(YouTubeQueue.radio(playTarget))
                  } else {
                    playerConnection.playQueue(YouTubeQueue(WatchEndpoint(videoId = "sAzlWScHTc4")))
                  }
                }
              )
            }
            item {
              GlassArtworkCard(
                title = "Discover Weekly",
                subtitle = "Fresh discoveries",
                thumbnailUrl = "https://i.ytimg.com/vi/RLzC55ai0eo/hqdefault.jpg",
                onClick = { navController.navigate("new_release") },
                onPlayClick = {
                  val playTarget = homeSongs.drop(2).firstOrNull()?.toMediaMetadata()
                    ?: quickPicks?.drop(2)?.firstOrNull()?.toMediaMetadata()
                  if (playTarget != null) {
                    playerConnection.playQueue(YouTubeQueue.radio(playTarget))
                  } else {
                    playerConnection.playQueue(YouTubeQueue(WatchEndpoint(videoId = "RLzC55ai0eo")))
                  }
                }
              )
            }
          }
        }

        // 10. Signature 4-Track Stack Carousel: Spotlight Mixes
        if (spotlightMixes.size >= 4) {
          item(key = "tuneify_spotlight_mixes_header") {
            ExpressiveSectionHeader(
              title = "Spotlight Mixes",
              onChevronClick = { navController.navigate("auto_playlist/top") }
            )
          }

          item(key = "tuneify_spotlight_mixes_row") {
            TuneifySongColumnGridCarousel(
              songs = spotlightMixes,
              onSongClick = { song ->
                playerConnection.playQueue(
                  YouTubeQueue(
                    endpoint = song.endpoint ?: WatchEndpoint(videoId = song.id),
                    preloadItem = song.toMediaMetadata()
                  )
                )
              },
              modifier = Modifier.padding(bottom = 16.dp)
            )
          }
        }



        if (isLoading && homePage?.chips.isNullOrEmpty()) {
          item(key = "chips_shimmer") {
            ShimmerHost {
              Row(
                modifier =
                  Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                repeat(5) {
                  TextPlaceholder(
                    height = 30.dp,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.width(72.dp)
                  )
                }
              }
            }
          }
        }

        homeSections.forEach { section ->
          when (section) {
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
              // Quick Picks are prominently featured in the approved ON REPEAT category card and RECENTLY PLAYED section
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
            HomeSection.DailyDiscover -> {
              dailyDiscover
                ?.takeIf { it.isNotEmpty() }
                ?.let { discoverList ->
                  item(key = "daily_discover_title") {
                    val title = stringResource(R.string.your_daily_discover)
                    NavigationTitle(
                      title = title,
                      onPlayAllClick = {
                        val queueItems =
                          discoverList.mapNotNull {
                            (it.recommendation as? SongItem)?.toMediaMetadata()
                          }

                        if (queueItems.isNotEmpty()) {
                          playerConnection.playQueue(
                            ListQueue(title = title, items = queueItems.map { it.toMediaItem() })
                          )
                        }
                      }
                    )
                  }
                  item(key = "daily_discover_content") {
                    Box(
                      modifier = Modifier.fillMaxWidth().height(340.dp).padding(horizontal = 16.dp),
                      contentAlignment = Alignment.Center
                    ) {
                      val carouselState = rememberCarouselState { discoverList.size }
                      HorizontalMultiBrowseCarousel(
                        state = carouselState,
                        preferredItemWidth = 320.dp,
                        itemSpacing = 16.dp,
                        modifier = Modifier.fillMaxWidth().height(320.dp)
                      ) { i ->
                        val item = discoverList[i]
                        DailyDiscoverCard(
                          dailyDiscover = item,
                          onClick = {
                            val song = item.recommendation as? SongItem
                            val mediaMetadata = song?.toMediaMetadata()
                            if (mediaMetadata != null) {
                              playerConnection.playQueue(
                                YouTubeQueue(
                                  song.endpoint ?: WatchEndpoint(videoId = song.id),
                                  mediaMetadata
                                )
                              )
                            }
                          },
                          navController = navController,
                          modifier = Modifier.maskClip(MaterialTheme.shapes.extraLarge)
                        )
                      }
                    }
                  }
                }
            }
            HomeSection.KeepListening -> {
              keepListening
                ?.takeIf { it.isNotEmpty() }
                ?.let { keepListening ->
                  item(key = "keep_listening_title") {
                    NavigationTitle(
                      title = stringResource(R.string.keep_listening),
                      modifier = Modifier.animateItem()
                    )
                  }

                  item(key = "keep_listening_list") {
                    val rows = if (keepListening.size > 6) 2 else 1
                    LazyHorizontalGrid(
                      state = rememberLazyGridState(),
                      rows = GridCells.Fixed(rows),
                      contentPadding = PaddingValues(horizontal = 20.dp),
                      horizontalArrangement = Arrangement.spacedBy(14.dp),
                      verticalArrangement = Arrangement.spacedBy(10.dp),
                      modifier =
                        Modifier.fillMaxWidth()
                          .height(
                            (currentGridHeight +
                              with(LocalDensity.current) {
                                MaterialTheme.typography.bodyLarge.lineHeight.toDp() * 2 +
                                  MaterialTheme.typography.bodyMedium.lineHeight.toDp() * 2
                              }) * rows
                          )
                          .animateItem()
                    ) {
                      items(keepListening.distinctBy { it.id }, key = { it.id }) {
                        localGridItem(it)
                      }
                    }
                  }
                }
            }
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
                      contentPadding = PaddingValues(horizontal = 20.dp),
                      horizontalArrangement = Arrangement.spacedBy(14.dp),
                      modifier = Modifier.animateItem()
                    ) {
                      items(
                        items = accountPlaylists.distinctBy { it.title.trim().lowercase() }.filter { it.title.isNotBlank() },
                        key = { it.id },
                      ) { item ->
                        ytGridItem(item)
                      }
                    }
                  }
                }
            }
            HomeSection.ForgottenFavorites -> {
              forgottenFavorites
                ?.takeIf { it.isNotEmpty() }
                ?.let { forgottenFavorites ->
                  item(key = "forgotten_favorites_title") {
                    val forgottenFavoritesTitle = stringResource(R.string.forgotten_favorites)
                    NavigationTitle(
                      title = forgottenFavoritesTitle,
                      modifier = Modifier.animateItem(),
                      onPlayAllClick = {
                        playerConnection.playQueue(
                          ListQueue(
                            title = forgottenFavoritesTitle,
                            items = forgottenFavorites.distinctBy { it.id }.map { it.toMediaItem() }
                          )
                        )
                      }
                    )
                  }

                  item(key = "forgotten_favorites_list") {
                    val rows = min(4, forgottenFavorites.size)
                    LazyHorizontalGrid(
                      state = forgottenFavoritesLazyGridState,
                      rows = GridCells.Fixed(rows),
                      contentPadding = PaddingValues(horizontal = 20.dp),
                      horizontalArrangement = Arrangement.spacedBy(14.dp),
                      verticalArrangement = Arrangement.spacedBy(10.dp),
                      flingBehavior =
                        rememberSnapFlingBehavior(forgottenFavoritesSnapLayoutInfoProvider),
                      modifier = Modifier.fillMaxWidth().height(ListItemHeight * rows).animateItem()
                    ) {
                      itemsIndexed(
                        items = forgottenFavorites.distinctBy { it.id },
                        key = { _, it -> it.id }
                      ) { index, originalSong ->
                        val song by
                          database.song(originalSong.id).collectAsState(initial = originalSong)

                        SongListItem(
                          song = song!!,
                          showInLibraryIcon = true,
                          isActive = song!!.id == mediaMetadata?.id,
                          isPlaying = isPlaying,
                          isSwipeable = false,
                          shape = listItemShape(index = index % rows, count = rows),
                          trailingContent = {
                            IconButton(
                              onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                menuState.show {
                                  SongMenu(
                                    originalSong = song!!,
                                    navController = navController,
                                    onDismiss = menuState::dismiss
                                  )
                                }
                              }
                            ) {
                              Icon(
                                painter = painterResource(R.drawable.more_vert),
                                contentDescription = null
                              )
                            }
                          },
                          modifier =
                            Modifier.width(horizontalLazyGridItemWidth)
                              .combinedClickable(
                                onClick = {
                                  if (song!!.id == mediaMetadata?.id) {
                                    playerConnection.togglePlayPause()
                                  } else {
                                    playerConnection.playQueue(
                                      YouTubeQueue.radio(song!!.toMediaMetadata())
                                    )
                                  }
                                },
                                onLongClick = {
                                  haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                  menuState.show {
                                    SongMenu(
                                      originalSong = song!!,
                                      navController = navController,
                                      onDismiss = menuState::dismiss
                                    )
                                  }
                                }
                              )
                        )
                      }
                    }
                  }
                }
            }
            is HomeSection.SimilarRecommendation -> {
              val recommendation = similarRecommendations?.getOrNull(section.index)
              recommendation?.let {
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
                    contentPadding = PaddingValues(horizontal = 20.dp),
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
              sectionData?.let {
                val sectionSongs = sectionData.items.filterIsInstance<SongItem>()
                val hasPlayableSongs = sectionSongs.isNotEmpty()

                val isSongsOnlySection =
                  sectionData.items.isNotEmpty() && sectionData.items.all { it is SongItem }

                item(key = "home_section_title_${section.index}") {
                  ExpressiveSectionHeader(
                    title = sectionData.title,
                    onChevronClick = sectionData.endpoint?.let { endpoint ->
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
                    }
                  )
                }

                when {
                  // 1. If section has songs: YouTube Music 4-Song Stack Carousel
                  hasPlayableSongs && sectionSongs.size >= 4 -> {
                    item(key = "home_section_stack_${section.index}") {
                      TuneifySongColumnGridCarousel(
                        songs = sectionSongs,
                        onSongClick = { song ->
                          playerConnection.playQueue(
                            YouTubeQueue(
                              song.endpoint ?: WatchEndpoint(videoId = song.id),
                              song.toMediaMetadata()
                            )
                          )
                        },
                        modifier = Modifier.padding(bottom = 12.dp).animateItem()
                      )
                    }
                  }
                  // 2. Uniform cute 150.dp rounded square cards (ytGridItem)
                  else -> {
                    item(key = "home_section_list_${section.index}") {
                      LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.padding(bottom = 14.dp).animateItem()
                      ) {
                        items(sectionData.items.distinctBy { it.id }, key = { it.id }) { item ->
                          ytGridItem(item)
                        }
                      }
                    }
                  }
                }
              }
            }
            HomeSection.MoodAndGenres -> {}
          }
        }

        if (
          isLoading || homePage?.continuation != null && homePage?.sections?.isNotEmpty() == true
        ) {
          item(key = "loading_shimmer") {
            ShimmerHost(modifier = Modifier.animateItem()) {
              Row(
                modifier =
                  Modifier.horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
              ) {
                repeat(4) {
                  Box(
                    modifier =
                      Modifier.width(155.dp)
                        .height(205.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0x3514261B))
                        .border(1.dp, Color(0x30FFFFFF), RoundedCornerShape(20.dp))
                  )
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
}
