package echo.music.iad1tya.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.border
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import echo.music.iad1tya.ui.component.glass.TuneifyGlassTokens
import echo.music.iad1tya.ui.theme.TuneifyTokens
import echo.music.iad1tya.ui.utils.scrollToOnHighlight

@Composable
fun Material3SettingsGroup(
  title: String? = null,
  compact: Boolean = false,
  scrollState: ScrollState? = null,
  items: List<Material3SettingsItem>
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    title?.let {
      Text(
        text = it,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = TuneifyGlassTokens.VoltLime,
        modifier =
          Modifier.padding(
            bottom = if (compact) 6.dp else 10.dp,
            top = if (compact) 6.dp else 14.dp,
            start = 8.dp
          )
      )
    }

    val groupShape = RoundedCornerShape(20.dp)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .shadow(elevation = 8.dp, shape = groupShape, ambientColor = Color(0x60000000), spotColor = Color(0x40000000))
        .clip(groupShape)
        .background(
          Brush.linearGradient(
            listOf(
              Color(0x3D1E462E),
              Color(0x2812261B),
              Color(0x200A1C12)
            )
          ),
          groupShape
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
          shape = groupShape
        )
        .animateContentSize()
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        items.forEachIndexed { index, item ->
          Material3SettingsItemRow(item = item, compact = compact, scrollState = scrollState)

          if (index != items.lastIndex) {
            HorizontalDivider(
              thickness = 1.dp,
              color = Color(0x18FFFFFF),
              modifier = Modifier.padding(start = if (compact) 62.dp else 72.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
private fun Material3SettingsItemRow(
  item: Material3SettingsItem,
  compact: Boolean = false,
  scrollState: ScrollState? = null
) {
  Row(
    modifier =
      Modifier.fillMaxWidth()
        .clickable(
          enabled = item.enabled && item.onClick != null,
          onClick = { item.onClick?.invoke() }
        )
        .then(
          if (scrollState != null) Modifier.scrollToOnHighlight(scrollState, item.isHighlighted)
          else Modifier
        )
        .padding(
          horizontal = if (compact) 14.dp else 20.dp,
          vertical = if (compact) 10.dp else 16.dp
        ),
    verticalAlignment = Alignment.CenterVertically
  ) {
    if (item.customIcon != null) {
      Box(
        modifier =
          Modifier.size(if (compact) 36.dp else 40.dp)
            .clip(item.iconShape ?: RoundedCornerShape(TuneifyTokens.RadiusMedium))
            .then(
              if (item.iconBackgroundColor != null) Modifier.background(item.iconBackgroundColor)
              else Modifier
            ),
        contentAlignment = Alignment.Center
      ) {
        item.customIcon.invoke()
      }
      Spacer(modifier = Modifier.width(if (compact) 14.dp else 16.dp))
    } else item.icon?.let { icon ->
      val iconBg = item.iconBackgroundColor ?: MaterialTheme.colorScheme.surfaceContainerHigh
      val iconTint =
        if (item.iconBackgroundColor != null) TuneifyTokens.onAccent(item.iconBackgroundColor)
        else MaterialTheme.colorScheme.onSurfaceVariant

      Box(
        modifier =
          Modifier.size(if (compact) 36.dp else 40.dp)
            .clip(item.iconShape ?: RoundedCornerShape(TuneifyTokens.RadiusMedium))
            .background(iconBg),
        contentAlignment = Alignment.Center
      ) {
        if (item.showBadge) {
          BadgedBox(badge = { Badge(containerColor = MaterialTheme.colorScheme.error) }) {
            Icon(
              painter = icon,
              contentDescription = null,
              tint = if (item.tintIcon) iconTint else Color.Unspecified,
              modifier = Modifier.size(20.dp)
            )
          }
        } else {
          Icon(
            painter = icon,
            contentDescription = null,
            tint = if (item.tintIcon) iconTint else Color.Unspecified,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      Spacer(modifier = Modifier.width(if (compact) 14.dp else 16.dp))
    }

    Column(modifier = Modifier.weight(1f)) {
      ProvideTextStyle(
        MaterialTheme.typography.titleMedium.copy(
          color =
            if (!item.enabled) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            else MaterialTheme.colorScheme.onSurface,
          fontWeight = FontWeight.SemiBold
        )
      ) {
        item.title()
      }

      item.description?.let { desc ->
        Spacer(modifier = Modifier.height(2.dp))
        ProvideTextStyle(
          MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        ) {
          desc()
        }
      }
    }

    if (item.trailingContent != null) {
      Spacer(modifier = Modifier.width(8.dp))
      item.trailingContent.invoke()
    } else if (item.onClick != null) {
      Icon(
        painter = painterResource(echo.music.iad1tya.R.drawable.navigate_next),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.size(20.dp)
      )
    }
  }
}

data class Material3SettingsItem(
  val icon: Painter? = null,
  val customIcon: (@Composable () -> Unit)? = null,
  val title: @Composable () -> Unit,
  val description: (@Composable () -> Unit)? = null,
  val trailingContent: (@Composable () -> Unit)? = null,
  val showBadge: Boolean = false,
  val isHighlighted: Boolean = false,
  val tintIcon: Boolean = true,
  val iconShape: Shape? = null,
  val enabled: Boolean = true,
  val iconBackgroundColor: Color? = null,
  val onClick: (() -> Unit)? = null
)
