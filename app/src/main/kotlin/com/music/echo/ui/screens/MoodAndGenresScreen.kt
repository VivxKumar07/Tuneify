package echo.music.iad1tya.ui.screens

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import echo.music.iad1tya.LocalPlayerAwareWindowInsets
import echo.music.iad1tya.R
import echo.music.iad1tya.ui.component.IconButton
import echo.music.iad1tya.ui.component.glass.DynamicArtworkBackground
import echo.music.iad1tya.ui.component.glass.ExpressiveSectionHeader
import echo.music.iad1tya.ui.component.glass.TuneifyGlassTokens
import echo.music.iad1tya.ui.component.shimmer.ShimmerHost
import echo.music.iad1tya.ui.utils.backToMain
import echo.music.iad1tya.viewmodels.MoodAndGenresViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodAndGenresScreen(
  navController: NavController,
  scrollBehavior: TopAppBarScrollBehavior,
  viewModel: MoodAndGenresViewModel = hiltViewModel(),
) {
  val localConfiguration = LocalConfiguration.current
  val itemsPerRow = if (localConfiguration.orientation == ORIENTATION_LANDSCAPE) 3 else 2

  val moodAndGenresList by viewModel.moodAndGenres.collectAsState()

  DynamicArtworkBackground(accentColor = TuneifyGlassTokens.VoltLime) {
    Box(modifier = Modifier.fillMaxSize()) {
      LazyColumn(
        contentPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues(),
        modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp)
      ) {
        item {
          Spacer(Modifier.height(68.dp))
        }

        if (moodAndGenresList == null) {
          item(key = "mood_and_genres_shimmer") {
            ShimmerHost(modifier = Modifier.animateItem()) {
              repeat(6) {
                Row(
                  modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                  horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  repeat(itemsPerRow) {
                    Box(
                      modifier = Modifier
                        .weight(1f)
                        .height(MoodAndGenresButtonHeight)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0x3514261B))
                    )
                  }
                }
              }
            }
          }
        }

        moodAndGenresList?.forEachIndexed { index, moodAndGenres ->
          item(key = "mood_and_genres_section_$index") {
            Column(
              modifier = Modifier.animateItem().padding(vertical = 8.dp),
            ) {
              ExpressiveSectionHeader(
                title = moodAndGenres.title,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
              )
              moodAndGenres.items.chunked(itemsPerRow).forEach { row ->
                Row(
                  modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                  horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  row.forEach {
                    MoodAndGenresButton(
                      title = it.title,
                      onClick = {
                        navController.navigate(
                          "youtube_browse/${it.endpoint.browseId}?params=${it.endpoint.params}"
                        )
                      },
                      modifier = Modifier.weight(1f),
                    )
                  }

                  repeat(itemsPerRow - row.size) { Spacer(Modifier.weight(1f)) }
                }
              }
            }
          }
        }

        item {
          Spacer(Modifier.height(30.dp))
        }
      }

      TopAppBar(
        title = {
          Text(
            text = stringResource(R.string.mood_and_genres),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
          )
        },
        navigationIcon = {
          IconButton(
            onClick = navController::navigateUp,
            onLongClick = navController::backToMain,
          ) {
            Icon(
              painterResource(R.drawable.arrow_back),
              contentDescription = null,
              tint = TuneifyGlassTokens.VoltLime
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = Color(0xD906140A),
          scrolledContainerColor = Color(0xF206140A)
        )
      )
    }
  }
}

@Composable
fun MoodAndGenresButton(
  title: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val shape = RoundedCornerShape(14.dp)
  Box(
    contentAlignment = Alignment.CenterStart,
    modifier = modifier
      .height(MoodAndGenresButtonHeight)
      .shadow(elevation = 6.dp, shape = shape, ambientColor = Color(0x60000000), spotColor = Color(0x40000000))
      .clip(shape)
      .background(
        Brush.linearGradient(
          listOf(
            Color(0x3D1E462E),
            Color(0x2812261B),
            Color(0x220A1C12)
          )
        ),
        shape
      )
      .border(
        width = 1.dp,
        brush = Brush.linearGradient(
          listOf(
            Color(0x60FFFFFF),
            Color(0x18FFFFFF),
            TuneifyGlassTokens.VoltLime.copy(alpha = 0.35f)
          )
        ),
        shape = shape
      )
      .clickable(onClick = onClick)
      .padding(horizontal = 14.dp),
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(6.dp)
          .clip(CircleShape)
          .background(TuneifyGlassTokens.VoltLime)
      )
      Spacer(Modifier.width(10.dp))
      Text(
        text = title,
        color = Color.White,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
    }
  }
}

val MoodAndGenresButtonHeight = 54.dp
