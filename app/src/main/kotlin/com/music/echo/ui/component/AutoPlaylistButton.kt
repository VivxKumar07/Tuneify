package echo.music.iad1tya.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import echo.music.iad1tya.ui.component.glass.TuneifyGlassTokens
import echo.music.iad1tya.ui.theme.TuneifyTokens

/**
 * Tuneify Frosted Glass AutoPlaylist Button for Library.
 * Features translucent glass surface, vibrant glow icon container, frosted border, and crisp typography.
 */
@Composable
fun AutoPlaylistButton(
  title: String,
  icon: Int,
  iconTint: Color = Color.Unspecified,
  subtitle: String? = null,
  backgroundColor: Color? = null,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  isDark: Boolean = isSystemInDarkTheme(),
) {
  val shape = RoundedCornerShape(18.dp)
  val accent = backgroundColor ?: TuneifyGlassTokens.VoltLime

  Box(
    modifier = modifier
      .defaultMinSize(minHeight = 74.dp)
      .shadow(elevation = 8.dp, shape = shape, ambientColor = Color(0x60000000), spotColor = Color(0x40000000))
      .clip(shape)
      .background(
        Brush.linearGradient(
          listOf(
            Color(0x44224230),
            Color(0x2812241A),
            Color(0x200A160F)
          )
        ),
        shape
      )
      .border(
        width = 1.dp,
        brush = Brush.linearGradient(
          listOf(
            Color(0x65FFFFFF),
            Color(0x18FFFFFF),
            accent.copy(alpha = 0.40f)
          )
        ),
        shape = shape
      )
      .clickable(onClick = onClick)
      .padding(horizontal = 14.dp, vertical = 14.dp),
    contentAlignment = Alignment.CenterStart
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.fillMaxWidth()
    ) {
      // Vibrant glowing glass circle for the icon
      Box(
        modifier = Modifier
          .size(40.dp)
          .shadow(elevation = 6.dp, shape = CircleShape, ambientColor = accent.copy(alpha = 0.4f), spotColor = accent.copy(alpha = 0.3f))
          .clip(CircleShape)
          .background(accent.copy(alpha = 0.22f))
          .border(1.dp, accent.copy(alpha = 0.55f), CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          painter = painterResource(id = icon),
          contentDescription = null,
          tint = accent,
          modifier = Modifier.size(20.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.Center
      ) {
        Text(
          text = title,
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
          ),
          color = Color.White,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        if (!subtitle.isNullOrBlank()) {
          Spacer(Modifier.height(2.dp))
          Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall.copy(
              fontSize = 11.5.sp,
              fontWeight = FontWeight.Medium
            ),
            color = TuneifyGlassTokens.TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }
    }
  }
}
