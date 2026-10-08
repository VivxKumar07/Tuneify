package echo.music.iad1tya.ui.screens.search.suggestions

import com.valentinilk.shimmer.shimmer
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import echo.music.iad1tya.playback.queues.YouTubeQueue
import echo.music.iad1tya.ui.component.TuneifyCascadingGenreCard
import echo.music.iad1tya.ui.component.MaterialExpressiveShapes
import echo.music.iad1tya.ui.component.TuneifyCookieShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import echo.music.iad1tya.R
import echo.music.iad1tya.constants.SuggestionRegionKey
import echo.music.iad1tya.constants.SuggestionRegionSlugToName
import echo.music.iad1tya.utils.rememberPreference
import kotlin.math.abs
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SuggestionsTabContent(
  navController: NavController,
  viewModel: SuggestionsViewModel = hiltViewModel(),
  contentPadding: PaddingValues = PaddingValues(0.dp)
) {
  val suggestionTracks by viewModel.suggestionTracks.collectAsState()
  val suggestionArtists by viewModel.suggestionArtists.collectAsState()
  val suggestionAlbums by viewModel.suggestionAlbums.collectAsState()
  val suggestionVideos by viewModel.suggestionVideos.collectAsState()
  val isLoading by viewModel.isLoading.collectAsState()
  val isManualLoading by viewModel.isManualLoading.collectAsState()
  val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
  val playerConnection = echo.music.iad1tya.LocalPlayerConnection.current
  val context = LocalContext.current
  val (regionCode, _) = rememberPreference(key = SuggestionRegionKey, defaultValue = "system")

  androidx.compose.runtime.LaunchedEffect(regionCode) { viewModel.refresh(regionCode) }

  val realThumbnails = remember(suggestionTracks, suggestionAlbums) {
    val tThumbs = suggestionTracks?.mapNotNull { it.thumbnailUrl } ?: emptyList()
    val aThumbs = suggestionAlbums?.mapNotNull { it.thumbnailUrl } ?: emptyList()
    (tThumbs + aThumbs).filter { it.isNotBlank() }
  }

  val pullToRefreshState = rememberPullToRefreshState()
  val scope = rememberCoroutineScope()

  PullToRefreshBox(
    isRefreshing = isManualLoading,
    onRefresh = { viewModel.refresh(regionCode, force = true) },
    state = pullToRefreshState,
    indicator = {
      PullToRefreshDefaults.LoadingIndicator(
        state = pullToRefreshState,
        isRefreshing = isManualLoading,
        modifier = Modifier.align(Alignment.TopCenter),
      )
    },
    modifier = Modifier.fillMaxSize()
  ) {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = contentPadding) {
      item {
        SearchCategoriesSection(navController = navController, realThumbnails = realThumbnails)
      }

      // Regional music posters fetched directly according to user's device region
      item {
        val regionName = remember(regionCode) {
          if (regionCode == "system") {
            java.util.Locale.getDefault().displayCountry.ifBlank { "Your Region" }
          } else {
            java.util.Locale("", regionCode).displayCountry.ifBlank { regionCode.uppercase() }
          }
        }
        val regionalItems = remember(suggestionTracks, suggestionAlbums) {
          val tracks = suggestionTracks?.map { track ->
            RegionalMusicPoster(
              id = track.id ?: "",
              title = track.title,
              subtitle = track.artist,
              thumbnailUrl = track.thumbnailUrl
            )
          }.orEmpty()
          val albums = suggestionAlbums?.map { album ->
            RegionalMusicPoster(
              id = album.id ?: "",
              title = album.title,
              subtitle = album.artist,
              thumbnailUrl = album.thumbnailUrl
            )
          }.orEmpty()
          (tracks.shuffled() + albums.shuffled()).filter { it.id.isNotBlank() }.distinctBy { it.id }.take(10)
        }
        if (regionalItems.isNotEmpty()) {
          RegionalMusicSection(
            regionName = regionName,
            items = regionalItems,
            onItemClick = { item ->
              playerConnection?.playQueue(
                YouTubeQueue(
                  com.music.innertube.models.WatchEndpoint(videoId = item.id),
                  echo.music.iad1tya.models.MediaMetadata(
                    id = item.id,
                    title = item.title,
                    artists = listOf(echo.music.iad1tya.models.MediaMetadata.Artist(id = null, name = item.subtitle)),
                    duration = 200,
                    thumbnailUrl = item.thumbnailUrl
                  )
                )
              )
            }
          )
        }
      }

      suggestionAlbums?.takeIf { it.isNotEmpty() }?.let { albums ->
        item {
          TrendingAlbumsSection(
            albums = albums,
            onAlbumClick = { album ->
              viewModel.navigateToAlbum(album, navController)
            },
            onMoreClick = {
              navController.navigate("youtube_browse/FEmusic_new_releases_albums")
            }
          )
        }
      }

      suggestionTracks?.let { tracks ->
        item {
          TrendingMusicSection(
            tracks = tracks,
            countryCode = regionCode,
            onTrackClick = { track ->
              viewModel.playTrack(track, playerConnection)
            },
            onMoreClick = {
              navController.navigate("youtube_browse/FEmusic_charts")
            }
          )
        }
      }

      suggestionArtists?.let { artists ->
        item {
          TopArtistsSection(
            artists = artists,
            onArtistClick = { artist ->
              android.widget.Toast.makeText(
                  context,
                  "Loading ${artist.name}...",
                  android.widget.Toast.LENGTH_SHORT
                )
                .show()
              viewModel.navigateToArtist(artist, navController)
            }
          )
        }
      }

      suggestionVideos?.let { videos ->
        item {
          TrendingVideosSection(
            videos = videos,
            onVideoClick = { video ->
              viewModel.playVideo(video, playerConnection)
            },
            onMoreClick = {
              navController.navigate("youtube_browse/FEmusic_charts")
            }
          )
        }
      }

      if (
        suggestionTracks == null &&
          suggestionArtists == null &&
          suggestionAlbums == null &&
          suggestionVideos == null &&
          !isLoading
      ) {
        item {
          Box(
            modifier = Modifier.fillMaxWidth().padding(top = 100.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "No suggestions available at the moment.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.height(16.dp))
              OutlinedButton(onClick = { viewModel.refresh(regionCode, force = true) }) {
                Text("Refresh")
              }
            }
          }
        }
      }

      if (
        suggestionTracks != null ||
          suggestionArtists != null ||
          suggestionAlbums != null ||
          suggestionVideos != null
      ) {
        item {
          Column(
            modifier = Modifier.fillMaxWidth().padding(top = 48.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "Tuneify Music Discovery",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TrendingMusicSection(
  tracks: List<SuggestionTrack>,
  countryCode: String,
  onTrackClick: (SuggestionTrack) -> Unit,
  onMoreClick: () -> Unit
) {
  if (tracks.isEmpty()) return
  val displayTracks = tracks.take(29)
  val totalItems = displayTracks.size + 1
  val pagerState = rememberPagerState(pageCount = { (totalItems + 4) / 5 })
  val coroutineScope = rememberCoroutineScope()

  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = "Trending Music",
      style = MaterialTheme.typography.titleLarge,
      fontWeight = FontWeight.Bold,
      color = Color.White,
      modifier = Modifier.padding(horizontal = 16.dp).padding(top = 24.dp)
    )
    Text(
      text = "Top trending hits • " + (SuggestionRegionSlugToName[countryCode] ?: "Global Charts"),
      style = MaterialTheme.typography.bodyMedium,
      color = Color(0xFF94A3B8),
      modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 8.dp)
    )
    HorizontalPager(
      state = pagerState,
      verticalAlignment = Alignment.Top,
      modifier =
        Modifier.fillMaxWidth().animateContentSize(tween(300, easing = FastOutSlowInEasing))
    ) { page ->
      Column(
        verticalArrangement = Arrangement.spacedBy(0.dp),
        modifier = Modifier.fillMaxWidth().padding(16.dp)
      ) {
        val startIdx = page * 5
        val endIdx = minOf(startIdx + 5, totalItems)
        for (i in startIdx until endIdx) {
          val isMoreCard = i == 29
          val isTop = i == startIdx
          val isBottom = i == endIdx - 1
          val shape =
            when {
              isTop && isBottom -> RoundedCornerShape(24.dp)
              isTop ->
                RoundedCornerShape(
                  topStart = 24.dp,
                  topEnd = 24.dp,
                  bottomStart = 2.dp,
                  bottomEnd = 2.dp
                )
              isBottom ->
                RoundedCornerShape(
                  topStart = 2.dp,
                  topEnd = 2.dp,
                  bottomStart = 24.dp,
                  bottomEnd = 24.dp
                )
              else -> RoundedCornerShape(2.dp)
            }
          if (isMoreCard) {
            Row(
              modifier =
                Modifier.fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                  .clickable { onMoreClick() }
                  .padding(16.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Icon(
                painterResource(R.drawable.globe_search),
                null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(24.dp)
              )
              Spacer(Modifier.width(12.dp))
              Text(
                "Explore Top Charts",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
              )
            }
          } else if (i < displayTracks.size) {
            val track = displayTracks[i]
            Row(
              modifier =
                Modifier.fillMaxWidth()
                  .padding(vertical = 1.dp)
                  .clip(shape)
                  .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                  .clickable { onTrackClick(track) }
                  .padding(horizontal = 8.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "${track.rank}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(36.dp),
                textAlign = TextAlign.Center
              )
              if (track.thumbnailUrl != null) {
                SubcomposeAsyncImage(
                  model = track.thumbnailUrl,
                  contentDescription = null,
                  contentScale = ContentScale.Crop,
                  loading = {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                      LoadingIndicator()
                    }
                  },
                  modifier = Modifier.clip(RoundedCornerShape(12.dp)).size(52.dp)
                )
              }
              Column(Modifier.weight(1f).padding(start = 14.dp)) {
                Text(
                  track.title,
                  style = MaterialTheme.typography.titleMedium,
                  color = MaterialTheme.colorScheme.onSurface,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.padding(top = 2.dp)
                ) {
                  Text(
                    track.artist,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                  Spacer(Modifier.width(6.dp))
                  val playCount =
                    remember(track.rank) {
                      val base = 2_500_000 / (track.rank + 2)
                      if (base >= 1_000_000) String.format("%.1fM plays", base / 1_000_000f)
                      else String.format("%dk plays", base / 1_000)
                    }
                  Text(
                    "• $playCount",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                  )
                }
              }

              IconButton(
                onClick = { onTrackClick(track) },
                modifier = Modifier.size(36.dp)
              ) {
                Icon(
                  painter = painterResource(R.drawable.more_vert),
                  contentDescription = "Options",
                  tint = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(18.dp)
                )
              }
            }
          }
        }
      }
    }
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
          Modifier.clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainer)
      ) {
        IconButton(
          onClick = {
            coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
          },
          enabled = pagerState.currentPage > 0
        ) {
          Icon(painterResource(R.drawable.chevron_leftpx), "Previous")
        }
        Text(
          stringResource(R.string.page_indicator, pagerState.currentPage + 1, pagerState.pageCount),
          style = MaterialTheme.typography.bodyMedium
        )
        IconButton(
          onClick = {
            coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
          },
          enabled = pagerState.currentPage < pagerState.pageCount - 1
        ) {
          Icon(painterResource(R.drawable.chevron_right_px), "Next")
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TopArtistsSection(artists: List<SuggestionArtist>, onArtistClick: (SuggestionArtist) -> Unit) {
  if (artists.isEmpty()) return
  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = "Trending Artists",
      style = MaterialTheme.typography.titleLarge,
      modifier = Modifier.padding(horizontal = 16.dp).padding(top = 16.dp)
    )
    LazyRow(
      contentPadding = PaddingValues(horizontal = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(16.dp),
      modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
    ) {
      itemsIndexed(artists) { index, artist ->
        val shape = TuneifyCookieShape
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.width(108.dp).clickable { onArtistClick(artist) }
        ) {
          Box(contentAlignment = Alignment.BottomEnd) {
            SubcomposeAsyncImage(
              model = artist.thumbnailUrl,
              contentDescription = artist.name,
              contentScale = ContentScale.Crop,
              loading = {
                Box(Modifier.fillMaxSize().shimmer().background(MaterialTheme.colorScheme.surfaceVariant))
              },
              modifier =
                Modifier.size(100.dp)
                  .clip(shape)
                  .background(Color(0xFF0F172A))
            )
            Surface(
              modifier = Modifier.size(26.dp).offset((-2).dp, (-2).dp),
              shape = CircleShape,
              color = MaterialTheme.colorScheme.onSurface,
              tonalElevation = 4.dp
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text(
                  artist.rank.toString(),
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.surface,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
          Spacer(Modifier.height(8.dp))
          Text(
            artist.name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
          )
          val playCount =
            remember(artist.rank) {
              val base = 15_000_000 / (artist.rank + 8)
              if (base >= 1_000_000) String.format("%.1fM plays", base / 1_000_000f)
              else String.format("%dk plays", base / 1_000)
            }
          Text(
            playCount,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
          )
        }
      }
    }
  }
}

@Composable
fun TrendingAlbumsSection(
  albums: List<SuggestionAlbum>,
  onAlbumClick: (SuggestionAlbum) -> Unit,
  onMoreClick: () -> Unit
) {
  if (albums.isEmpty()) return
  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clickable(onClick = onMoreClick)
        .padding(horizontal = 16.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "New Releases",
        style = MaterialTheme.typography.titleLarge.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 20.sp
        ),
        color = Color.White
      )
      Icon(
        painter = painterResource(R.drawable.navigate_next),
        contentDescription = "More",
        tint = Color.White.copy(alpha = 0.7f),
        modifier = Modifier.size(20.dp)
      )
    }
    LazyRow(
      contentPadding = PaddingValues(horizontal = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(16.dp),
      modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp)
    ) {
      items(albums) { album ->
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.width(120.dp).clickable { onAlbumClick(album) }
        ) {
          Box(contentAlignment = Alignment.BottomEnd) {
            SubcomposeAsyncImage(
              model = album.thumbnailUrl,
              contentDescription = album.title,
              contentScale = ContentScale.Crop,
              loading = {
                Box(Modifier.fillMaxSize().shimmer().background(MaterialTheme.colorScheme.surfaceVariant))
              },
              modifier =
                Modifier.size(120.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            Surface(
              modifier = Modifier.size(28.dp).offset((-4).dp, (-4).dp),
              shape = CircleShape,
              color = MaterialTheme.colorScheme.onSurface,
              tonalElevation = 4.dp
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text(
                  album.rank.toString(),
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.surface,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
          Spacer(Modifier.height(8.dp))
          Text(
            album.title,
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
          )
          Text(
            album.artist,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
          )
        }
      }

      item {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.width(100.dp).padding(bottom = 20.dp).clickable { onMoreClick() }
        ) {
          Box(
            contentAlignment = Alignment.Center,
            modifier =
              Modifier.size(120.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
          ) {
            Icon(
              painter = painterResource(R.drawable.globe_search),
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(32.dp)
            )
          }
          Spacer(Modifier.height(8.dp))
          Text(
            text = "More",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrendingVideosSection(
  videos: List<SuggestionTrack>,
  onVideoClick: (SuggestionTrack) -> Unit,
  onMoreClick: () -> Unit
) {
  if (videos.isEmpty()) return

  val carouselState = rememberCarouselState(itemCount = { videos.size })
  val context = LocalContext.current

  Column(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Trending Music Videos",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
      )

      Text(
        text = "More",
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.clickable { onMoreClick() }
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    HorizontalMultiBrowseCarousel(
      state = carouselState,
      preferredItemWidth = 320.dp,
      itemSpacing = 12.dp,
      modifier = Modifier.fillMaxWidth().height(200.dp),
      contentPadding = PaddingValues(horizontal = 16.dp)
    ) { i ->
      val video = videos[i]
      var isCardFocused by remember { mutableStateOf(false) }

      Box(
        modifier =
          Modifier.fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .onGloballyPositioned { coordinates ->
              val cardCenter = coordinates.boundsInRoot().center.x
              val screenWidth = context.resources.displayMetrics.widthPixels
              val screenCenter = screenWidth / 2f
              isCardFocused = abs(cardCenter - screenCenter) < 150
            }
            .clickable { onVideoClick(video) }
      ) {
        AsyncImage(
          model = video.thumbnailUrl,
          contentDescription = null,
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop
        )

        Box(
          modifier =
            Modifier.fillMaxSize()
              .background(
                brush =
                  Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                    startY = 300f
                  )
              )
        )

        Column(modifier = Modifier.align(Alignment.BottomStart).padding(12.dp)) {
          Text(
            text = video.title,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = video.artist,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.7f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }
    }
  }
}

@Composable
fun SearchCategoriesSection(
  navController: NavController,
  realThumbnails: List<String> = emptyList()
) {
  val categories = listOf(
    Pair("Pop", listOf(Color(0xFF8B5CF6), Color(0xFF5B21B6))),
    Pair("Hip Hop", listOf(Color(0xFFEA580C), Color(0xFF9A3412))),
    Pair("Rock", listOf(Color(0xFF2563EB), Color(0xFF1E3A8A))),
    Pair("Indie", listOf(Color(0xFF06B6D4), Color(0xFF0E7490))),
    Pair("Mood", listOf(Color(0xFFFB923C), Color(0xFFBE123C))),
    Pair("Chill", listOf(Color(0xFFA855F7), Color(0xFF6B21A8))),
    Pair("Workout", listOf(Color(0xFF0284C7), Color(0xFF075985))),
    Pair("Party", listOf(Color(0xFFD946EF), Color(0xFF86198F)))
  )

  Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
    Text(
      text = "Browse all",
      style = MaterialTheme.typography.titleLarge.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
      ),
      color = Color.White,
      modifier = Modifier.padding(bottom = 12.dp)
    )
    for (i in categories.indices step 2) {
      Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        val cat1 = categories[i]
        val thumb1 = realThumbnails.getOrNull(i % realThumbnails.size.coerceAtLeast(1))
        TuneifyCascadingGenreCard(
          title = cat1.first,
          posterUrl = thumb1,
          gradientColors = cat1.second,
          onClick = { navController.navigate("search/${cat1.first}") },
          modifier = Modifier.weight(1f)
        )
        if (i + 1 < categories.size) {
          val cat2 = categories[i + 1]
          val thumb2 = realThumbnails.getOrNull((i + 1) % realThumbnails.size.coerceAtLeast(1))
          TuneifyCascadingGenreCard(
            title = cat2.first,
            posterUrl = thumb2,
            gradientColors = cat2.second,
            onClick = { navController.navigate("search/${cat2.first}") },
            modifier = Modifier.weight(1f)
          )
        } else {
          Spacer(modifier = Modifier.weight(1f))
        }
      }
    }
  }
}

data class RegionalMusicPoster(
  val id: String,
  val title: String,
  val subtitle: String,
  val thumbnailUrl: String?
)

@Composable
fun RegionalMusicSection(
  regionName: String,
  items: List<RegionalMusicPoster>,
  onItemClick: (RegionalMusicPoster) -> Unit
) {
  if (items.isEmpty()) return
  Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
    Text(
      text = "Popular in $regionName",
      style = MaterialTheme.typography.titleLarge.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
      ),
      color = Color.White,
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
    LazyRow(
      contentPadding = PaddingValues(horizontal = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(14.dp),
      modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
    ) {
      items(items, key = { it.id }) { item ->
        Column(
          modifier = Modifier
            .width(136.dp)
            .clickable { onItemClick(item) }
        ) {
          Box(
            modifier = Modifier
              .size(136.dp)
              .clip(RoundedCornerShape(16.dp))
              .background(Color(0xFF141722))
              .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
          ) {
            SubcomposeAsyncImage(
              model = item.thumbnailUrl,
              contentDescription = item.title,
              contentScale = ContentScale.Crop,
              loading = {
                Box(Modifier.fillMaxSize().shimmer().background(MaterialTheme.colorScheme.surfaceVariant))
              },
              modifier = Modifier.fillMaxSize()
            )
          }
          Spacer(Modifier.height(8.dp))
          Text(
            text = item.title,
            style = MaterialTheme.typography.bodyMedium.copy(
              fontWeight = FontWeight.SemiBold,
              fontSize = 14.sp
            ),
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Spacer(Modifier.height(2.dp))
          Text(
            text = item.subtitle,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
            color = Color(0xFF94A3B8),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }
    }
  }
}

