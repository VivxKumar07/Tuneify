package echo.music.iad1tya.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import echo.music.iad1tya.R
import echo.music.iad1tya.db.entities.Song
import echo.music.iad1tya.ui.theme.TuneifyTokens

/**
 * Display model for recently played items on Tuneify Home.
 */
data class TuneifyRecentSongItem(
  val id: String,
  val title: String,
  val artist: String,
  val artworkUrl: String?,
  val genreName: String,
  val genreColor: Color,
  val song: Song? = null
)

/**
 * Home header: the brand lockup and two rounded utility controls.
 *
 * The wordmark carries the screen; the controls are tonal circles so they read as tools rather than
 * decoration.
 */
@Composable
fun TuneifyHomeHeader(
  onSearchClick: () -> Unit,
  onSettingsClick: () -> Unit,
  modifier: Modifier = Modifier,
  isDark: Boolean = isSystemInDarkTheme()
) {
  Row(
    modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    TuneifyWordmark(
      markSize = 26.dp,
      fontSize = 22.dp,
      textColor = MaterialTheme.colorScheme.onBackground,
    )

    Row(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      HeaderIconButton(
        iconRes = R.drawable.search,
        contentDescription = stringResource(R.string.search),
        onClick = onSearchClick
      )
      HeaderIconButton(
        iconRes = R.drawable.settings,
        contentDescription = stringResource(R.string.settings),
        onClick = onSettingsClick
      )
    }
  }
}

@Composable
private fun HeaderIconButton(
  iconRes: Int,
  contentDescription: String,
  onClick: () -> Unit,
) {
  Box(
    modifier =
      Modifier.size(42.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
        .clickable(onClick = onClick),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      painter = painterResource(iconRes),
      contentDescription = contentDescription,
      tint = MaterialTheme.colorScheme.onSurface,
      modifier = Modifier.size(20.dp)
    )
  }
}

/**
 * Colored category block for Home.
 *
 * Material 3 Expressive: a large rounded tonal field, a soft lift, an editorial title, and stacked
 * artwork chips. Colour-blocking carries the identity; the shadow only lifts it off the canvas.
 */
@Composable
fun TuneifyCategoryCard(
  title: String,
  subtitle: String,
  count: String?,
  countLabel: String,
  backgroundColor: Color,
  artworkThumbnails: List<String>,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  isDark: Boolean = isSystemInDarkTheme()
) {
  val shape = RoundedCornerShape(TuneifyTokens.RadiusLarge)
  val onColor = TuneifyTokens.onAccent(backgroundColor)

  Box(
    modifier =
      modifier
        .shadow(
          elevation = TuneifyTokens.ElevationMedium,
          shape = shape,
          clip = false,
          ambientColor = Color.Black,
          spotColor = Color.Black,
        )
        .clip(shape)
        .background(backgroundColor, shape)
        .clickable(onClick = onClick)
        .padding(horizontal = 16.dp, vertical = 16.dp)
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = onColor,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.weight(1f)
        )
        Box(
          modifier =
            Modifier.size(26.dp)
              .clip(CircleShape)
              .background(onColor.copy(alpha = 0.16f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            painter = painterResource(R.drawable.navigate_next),
            contentDescription = null,
            tint = onColor.copy(alpha = 0.85f),
            modifier = Modifier.size(16.dp)
          )
        }
      }

      Spacer(Modifier.height(3.dp))

      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = onColor.copy(alpha = 0.72f),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(Modifier.height(16.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
      ) {
        ArtworkStack(thumbnails = artworkThumbnails, onColor = onColor)

        if (count != null) {
          Column(horizontalAlignment = Alignment.End) {
            Text(
              text = count,
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.ExtraBold,
              color = onColor
            )
            Text(
              text = countLabel.uppercase(),
              style = MaterialTheme.typography.labelSmall,
              color = onColor.copy(alpha = 0.7f)
            )
          }
        }
      }
    }
  }
}

/**
 * Three overlapping artwork chips. Overlap is kept small (4dp) so the thumbnails stay readable
 * instead of turning into a single smear.
 */
@Composable
private fun ArtworkStack(
  thumbnails: List<String>,
  onColor: Color,
) {
  val valid = thumbnails.filter { it.isNotBlank() }.take(3)
  val itemShape = RoundedCornerShape(TuneifyTokens.RadiusSmall)

  Row(verticalAlignment = Alignment.CenterVertically) {
    repeat(3) { index ->
      val url = valid.getOrNull(index)
      Box(
        modifier =
          Modifier.size(32.dp)
            .clip(itemShape)
            .background(onColor.copy(alpha = if (url == null) 0.10f else 0.18f)),
        contentAlignment = Alignment.Center
      ) {
        if (url != null) {
          AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(url).crossfade(true).build(),
            placeholder = painterResource(R.drawable.music_note),
            error = painterResource(R.drawable.music_note),
            fallback = painterResource(R.drawable.music_note),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
        } else {
          Icon(
            painter = painterResource(R.drawable.music_note),
            contentDescription = null,
            tint = onColor.copy(alpha = 0.45f),
            modifier = Modifier.size(15.dp)
          )
        }
      }
      if (index < 2) Spacer(Modifier.width(4.dp))
    }
  }
}

/**
 * Music card used in the "recently played" rail.
 *
 * Material 3 Expressive: the artwork is inset inside a soft card rather than bleeding to the edge,
 * so the surface reads as a container. Metadata sits directly on the card — no hairline rule — and
 * the genre is a small tonal chip.
 */
@Composable
fun TuneifyMusicCard(
  title: String,
  artist: String,
  artworkUrl: String?,
  genreName: String,
  genreColor: Color,
  isActive: Boolean,
  isPlaying: Boolean,
  onClick: () -> Unit,
  onLongClick: () -> Unit,
  modifier: Modifier = Modifier,
  isDark: Boolean = isSystemInDarkTheme()
) {
  val shape = RoundedCornerShape(TuneifyTokens.RadiusCard)
  val surface = MaterialTheme.colorScheme.surfaceContainerLow

  Box(
    modifier =
      modifier
        .width(150.dp)
        .shadow(
          elevation = TuneifyTokens.ElevationLow,
          shape = shape,
          clip = false,
          ambientColor = Color.Black,
          spotColor = Color.Black,
        )
        .clip(shape)
        .background(surface, shape)
        .combinedClickable(onClick = onClick, onLongClick = onLongClick)
  ) {
    Column(modifier = Modifier.padding(8.dp)) {
      Box(
        modifier =
          Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(TuneifyTokens.RadiusMedium))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh),
        contentAlignment = Alignment.Center
      ) {
        AsyncImage(
          model = ImageRequest.Builder(LocalContext.current).data(artworkUrl).crossfade(true).build(),
          placeholder = painterResource(R.drawable.music_note),
          error = painterResource(R.drawable.music_note),
          fallback = painterResource(R.drawable.music_note),
          contentDescription = null,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )

        if (isActive) {
          Box(
            modifier =
              Modifier.align(Alignment.TopEnd)
                .padding(6.dp)
                .size(24.dp)
                .clip(CircleShape)
                .background(TuneifyTokens.Lime),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              painter = painterResource(R.drawable.volume_up),
              contentDescription = null,
              tint = TuneifyTokens.OnLime,
              modifier = Modifier.size(14.dp)
            )
          }
        }
      }

      Spacer(Modifier.height(10.dp))

      Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.padding(horizontal = 4.dp)
      )

      Spacer(Modifier.height(2.dp))

      Text(
        text = artist,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.padding(horizontal = 4.dp)
      )

      Spacer(Modifier.height(8.dp))

      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
          Modifier.padding(horizontal = 4.dp)
            .clip(RoundedCornerShape(TuneifyTokens.RadiusPill))
            .background(genreColor.copy(alpha = 0.16f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
      ) {
        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(genreColor))
        Spacer(Modifier.width(5.dp))
        Text(
          text = genreName,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }

      Spacer(Modifier.height(4.dp))
    }
  }
}

/**
 * The Home greeting: a quiet time-of-day line above the listener's name in display type.
 *
 * Material 3 Expressive opens a screen with a large statement, so the name is the biggest text on
 * Home — bigger than the brand lockup above it.
 */
@Composable
fun TuneifyGreetingHeader(
  greeting: String,
  name: String,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
    Text(
      text = greeting,
      style = MaterialTheme.typography.titleMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
    )
    Text(
      text = name,
      style = MaterialTheme.typography.headlineMedium,
      fontWeight = FontWeight.ExtraBold,
      color = MaterialTheme.colorScheme.onBackground,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
    )
  }
}

/**
 * Tappable search affordance. It is a read-only pill: tapping it opens the real search screen, which
 * owns the text field. Keeping it here means Home never steals focus from the list.
 */
@Composable
fun TuneifySearchBar(
  placeholder: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier =
      modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)
        .height(56.dp)
        .clip(RoundedCornerShape(TuneifyTokens.RadiusPill))
        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
        .clickable(onClick = onClick)
        .padding(horizontal = 18.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      painter = painterResource(R.drawable.search),
      contentDescription = null,
      tint = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.size(22.dp),
    )
    Spacer(Modifier.width(12.dp))
    Text(
      text = placeholder,
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      modifier = Modifier.weight(1f),
    )
  }
}
