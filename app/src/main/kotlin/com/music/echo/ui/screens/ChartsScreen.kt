package echo.music.iad1tya.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.music.innertube.models.SongItem
import com.music.innertube.models.WatchEndpoint
import echo.music.iad1tya.extensions.toMediaItem
import echo.music.iad1tya.LocalPlayerAwareWindowInsets
import echo.music.iad1tya.LocalPlayerConnection
import echo.music.iad1tya.R
import echo.music.iad1tya.models.toMediaMetadata
import echo.music.iad1tya.playback.queues.ListQueue
import echo.music.iad1tya.playback.queues.YouTubeQueue
import echo.music.iad1tya.ui.component.IconButton
import echo.music.iad1tya.ui.component.LocalMenuState
import echo.music.iad1tya.ui.component.glass.DynamicArtworkBackground
import echo.music.iad1tya.ui.component.glass.LocalDynamicAccentColor
import echo.music.iad1tya.ui.component.glass.TuneifyGlassTokens
import echo.music.iad1tya.ui.component.shimmer.ShimmerHost
import echo.music.iad1tya.ui.component.shimmer.TextPlaceholder
import echo.music.iad1tya.ui.menu.YouTubeSongMenu
import echo.music.iad1tya.ui.utils.backToMain
import echo.music.iad1tya.viewmodels.ChartsViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChartsScreen(
  navController: NavController,
  viewModel: ChartsViewModel = hiltViewModel(),
) {
  val menuState = LocalMenuState.current
  val haptic = LocalHapticFeedback.current
  val playerConnection = LocalPlayerConnection.current ?: return
  val isPlaying by playerConnection.isEffectivelyPlaying.collectAsState()
  val mediaMetadata by playerConnection.mediaMetadata.collectAsState()

  val chartsPage by viewModel.chartsPage.collectAsState()
  val isLoading by viewModel.isLoading.collectAsState()

  val lazyListState = rememberLazyListState()

  LaunchedEffect(Unit) {
    if (chartsPage == null) {
      viewModel.loadCharts()
    }
  }

  val allChartSongs = remember(chartsPage) {
    chartsPage?.sections?.flatMap { it.items }?.filterIsInstance<SongItem>()?.distinctBy { it.id } ?: emptyList()
  }

  DynamicArtworkBackground(
    accentColor = LocalDynamicAccentColor.current
  ) {
    Scaffold(
      containerColor = Color.Transparent,
      topBar = {
        TopAppBar(
          title = {
            Text(
              text = "Top Charts",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 20.sp
            )
          },
          navigationIcon = {
            IconButton(
              onClick = { navController.navigateUp() },
              onLongClick = { navController.backToMain() }
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
      Box(
        modifier = Modifier.fillMaxSize().padding(paddingValues),
      ) {
        if (isLoading && allChartSongs.isEmpty()) {
          ShimmerHost(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 10.dp),
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
              repeat(10) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.fillMaxWidth().height(64.dp)
                ) {
                  Box(
                    modifier = Modifier.size(52.dp).clip(RoundedCornerShape(12.dp)).background(Color(0x25FFFFFF))
                  )
                  Spacer(Modifier.width(16.dp))
                  Column(modifier = Modifier.weight(1f)) {
                    TextPlaceholder(height = 18.dp, modifier = Modifier.fillMaxWidth(0.6f))
                    Spacer(Modifier.height(6.dp))
                    TextPlaceholder(height = 14.dp, modifier = Modifier.fillMaxWidth(0.35f))
                  }
                }
              }
            }
          }
        } else {
          LazyColumn(
            state = lazyListState,
            contentPadding = PaddingValues(
              start = 20.dp,
              end = 20.dp,
              top = 8.dp,
              bottom = LocalPlayerAwareWindowInsets.current.asPaddingValues().calculateBottomPadding() + 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
          ) {
            // Play All & Shuffle Hero Header
            if (allChartSongs.isNotEmpty()) {
              item(key = "charts_hero_header") {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                  horizontalArrangement = Arrangement.spacedBy(12.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  // Play All Button
                  Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                      .weight(1f)
                      .height(48.dp)
                      .clip(RoundedCornerShape(14.dp))
                      .background(TuneifyGlassTokens.VoltLime)
                      .clickable {
                        playerConnection.playQueue(
                          ListQueue(
                            title = "Top Charts",
                            items = allChartSongs.map { it.toMediaMetadata().toMediaItem() }
                          )
                        )
                      }
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(
                        painter = painterResource(R.drawable.play),
                        contentDescription = null,
                        tint = Color(0xFF041208),
                        modifier = Modifier.size(20.dp)
                      )
                      Spacer(Modifier.width(8.dp))
                      Text(
                        text = "Play All (${allChartSongs.size})",
                        color = Color(0xFF041208),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                      )
                    }
                  }

                  // Shuffle Button
                  Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                      .size(48.dp)
                      .clip(RoundedCornerShape(14.dp))
                      .background(Color(0x3514261B))
                      .clickable {
                        playerConnection.playQueue(
                          ListQueue(
                            title = "Top Charts",
                            items = allChartSongs.shuffled().map { it.toMediaMetadata().toMediaItem() }
                          )
                        )
                      }
                  ) {
                    Icon(
                      painter = painterResource(R.drawable.shuffle),
                      contentDescription = "Shuffle",
                      tint = Color.White,
                      modifier = Modifier.size(22.dp)
                    )
                  }
                }
              }
            }

            // Full Vertical List of 50+ Songs with Rank Badges
            itemsIndexed(
              items = allChartSongs,
              key = { _, song -> song.id }
            ) { index, song ->
              val isCurrent = song.id == mediaMetadata?.id
              val rank = index + 1

              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(16.dp))
                  .background(if (isCurrent) Color(0x35C6F53D) else Color(0x2214261B))
                  .combinedClickable(
                    onClick = {
                      if (isCurrent) {
                        playerConnection.togglePlayPause()
                      } else {
                        playerConnection.playQueue(
                          YouTubeQueue(
                            endpoint = song.endpoint ?: WatchEndpoint(videoId = song.id),
                            preloadItem = song.toMediaMetadata()
                          )
                        )
                      }
                    },
                    onLongClick = {
                      haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                      menuState.show {
                        YouTubeSongMenu(
                          song = song,
                          navController = navController,
                          onDismiss = menuState::dismiss
                        )
                      }
                    }
                  )
                  .padding(horizontal = 12.dp, vertical = 8.dp)
                  .animateItem()
              ) {
                // Rank number
                Text(
                  text = "$rank",
                  color = if (rank <= 3) TuneifyGlassTokens.VoltLime else Color.White.copy(alpha = 0.6f),
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.width(28.dp)
                )

                // Thumbnail
                Box(
                  modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0C1F14)),
                  contentAlignment = Alignment.Center
                ) {
                  AsyncImage(
                    model = song.thumbnail,
                    contentDescription = song.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                  )
                  if (isCurrent && isPlaying) {
                    Box(
                      modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x80000000)),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(
                        painter = painterResource(R.drawable.pause),
                        contentDescription = null,
                        tint = TuneifyGlassTokens.VoltLime,
                        modifier = Modifier.size(20.dp)
                      )
                    }
                  }
                }

                Spacer(Modifier.width(12.dp))

                // Title & Artist
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = song.title,
                    color = if (isCurrent) TuneifyGlassTokens.VoltLime else Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                  Spacer(Modifier.height(2.dp))
                  Text(
                    text = song.artists.joinToString { it.name }.ifEmpty { "Popular Artist" },
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                }

                // More Options Icon
                IconButton(
                  onClick = {
                    menuState.show {
                      YouTubeSongMenu(
                        song = song,
                        navController = navController,
                        onDismiss = menuState::dismiss
                      )
                    }
                  },
                  onLongClick = {}
                ) {
                  Icon(
                    painter = painterResource(R.drawable.more_vert),
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
