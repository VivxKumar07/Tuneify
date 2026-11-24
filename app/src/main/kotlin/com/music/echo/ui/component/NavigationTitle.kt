package echo.music.iad1tya.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import echo.music.iad1tya.R
import echo.music.iad1tya.ui.theme.TuneifyTokens

/**
 * The universal screen / section header.
 *
 * Material 3 Expressive: a large editorial title carries the screen; an optional eyebrow sits above
 * it. No decorative motif — the type itself is the identity. The trailing action is a tonal pill.
 */
@Composable
fun NavigationTitle(
  title: String,
  modifier: Modifier = Modifier,
  label: String? = null,
  thumbnail: (@Composable () -> Unit)? = null,
  onClick: (() -> Unit)? = null,
  onPlayAllClick: (() -> Unit)? = null,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    modifier =
      modifier
        .fillMaxWidth()
        .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal))
        .clickable(enabled = onClick != null) { onClick?.invoke() }
        .padding(start = TuneifyTokens.Space.Gutter, end = 8.dp, top = 20.dp, bottom = 8.dp)
  ) {
    thumbnail?.invoke()

    Column(verticalArrangement = Arrangement.Center, modifier = Modifier.weight(1f)) {
      label?.let { label ->
        Text(
          text = label.uppercase(),
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          overflow = TextOverflow.Ellipsis,
        )
      }

      Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.onSurface,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
      )
    }

    onPlayAllClick?.let { playAllClick ->
      FilledTonalButton(
        onClick = playAllClick,
        shape = RoundedCornerShape(TuneifyTokens.RadiusPill),
        colors =
          ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface,
          ),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 0.dp),
        modifier = Modifier.height(36.dp)
      ) {
        Text(text = stringResource(R.string.play_all), style = MaterialTheme.typography.labelMedium)
      }
    }

    if (onClick != null) {
      Icon(
        painter = painterResource(R.drawable.arrow_forward),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(end = 8.dp)
      )
    }
  }
}
