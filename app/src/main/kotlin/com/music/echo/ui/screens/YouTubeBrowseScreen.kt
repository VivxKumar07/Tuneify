package echo.music.iad1tya.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.music.innertube.models.AlbumItem
import com.music.innertube.models.ArtistItem
import com.music.innertube.models.PlaylistItem
import com.music.innertube.models.SongItem
import echo.music.iad1tya.LocalPlayerAwareWindowInsets
import echo.music.iad1tya.LocalPlayerConnection
import echo.music.iad1tya.R
import echo.music.iad1tya.models.toMediaMetadata
import echo.music.iad1tya.playback.queues.YouTubeQueue
import echo.music.iad1tya.ui.component.IconButton
import echo.music.iad1tya.ui.component.glass.DynamicArtworkBackground
import echo.music.iad1tya.ui.component.glass.GlassArtworkCard
import echo.music.iad1tya.ui.component.glass.LocalDynamicAccentColor
import echo.music.iad1tya.ui.component.glass.TuneifyGlassTokens
import echo.music.iad1tya.ui.component.shimmer.GridItemPlaceHolder
import echo.music.iad1tya.ui.component.shimmer.ShimmerHost
import echo.music.iad1tya.ui.utils.backToMain
import echo.music.iad1tya.viewmodels.YouTubeBrowseViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun YouTubeBrowseScreen(
  navController: NavController,
  viewModel: YouTubeBrowseViewModel = hiltViewModel(),
) {
  val playerConnection = LocalPlayerConnection.current ?: return
  val isPlaying by playerConnection.isEffectivelyPlaying.collectAsState()
  val mediaMetadata by playerConnection.mediaMetadata.collectAsState()

  val browseResult by viewModel.result.collectAsState()
  val allItems = browseResult?.items?.flatMap { it.items } ?: emptyList()

  DynamicArtworkBackground(accentColor = LocalDynamicAccentColor.current) {
    Scaffold(
      containerColor = Color.Transparent,
      topBar = {
        TopAppBar(
          title = {
            Text(
              text = browseResult?.title.orEmpty().ifEmpty { "Browse" },
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 20.sp
            )
          },
          navigationIcon = {
            IconButton(
              onClick = navController::navigateUp,
              onLongClick = navController::backToMain
            ) {
              Icon(
                painter = painterResource(R.drawable.arrow_back),
                contentDescription = null,
                tint = TuneifyGlassTokens.VoltLime
              )
            }
          },
          colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White
          )
        )
      }
    ) { paddingValues ->
      Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
        LazyVerticalGrid(
          columns = GridCells.Adaptive(minSize = 150.dp),
          contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 10.dp,
            bottom = LocalPlayerAwareWindowInsets.current.asPaddingValues().calculateBottomPadding() + 24.dp
          ),
          horizontalArrangement = Arrangement.spacedBy(14.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp),
          modifier = Modifier.fillMaxSize()
        ) {
          if (browseResult == null) {
            items(8) { ShimmerHost { GridItemPlaceHolder(fillMaxWidth = true) } }
          }

          items(items = allItems.distinctBy { it.id }, key = { it.id }) { item ->
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
              cardWidth = 150.dp,
              onClick = {
                when (item) {
                  is SongItem -> {
                    if (item.id == mediaMetadata?.id) {
                      playerConnection.togglePlayPause()
                    } else {
                      playerConnection.playQueue(YouTubeQueue.radio(item.toMediaMetadata()))
                    }
                  }
                  is AlbumItem -> navController.navigate("album/${item.id}")
                  is ArtistItem -> navController.navigate("artist/${item.id}")
                  is PlaylistItem -> navController.navigate("online_playlist/${item.id}")
                }
              },
              onPlayClick = {
                when (item) {
                  is SongItem -> playerConnection.playQueue(YouTubeQueue.radio(item.toMediaMetadata()))
                  is AlbumItem -> navController.navigate("album/${item.id}")
                  is ArtistItem -> navController.navigate("artist/${item.id}")
                  is PlaylistItem -> navController.navigate("online_playlist/${item.id}")
                }
              },
              modifier = Modifier.animateItem()
            )
          }
        }
      }
    }
  }
}
