package echo.music.iad1tya.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import echo.music.iad1tya.ui.theme.AeonikFontFamily

/**
 * Material Expressive AutoPlaylist Button.
 * Features an expressive squircle pill shape, inner tonal icon container, and sleek typography.
 */
@Composable
fun AutoPlaylistButton(
  title: String,
  icon: Int,
  iconTint: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val shape = RoundedCornerShape(20.dp)
  Surface(
    shape = shape,
    color = Color(0xFF12151D),
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      Color.White.copy(alpha = 0.08f)
    ),
    modifier = modifier.clip(shape).clickable(onClick = onClick)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(Color(0xFF1A1F2C)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          painter = painterResource(id = icon),
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(20.dp)
        )
      }
      Spacer(modifier = Modifier.width(12.dp))
      Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = Color(0xFFF1F5F9),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}
