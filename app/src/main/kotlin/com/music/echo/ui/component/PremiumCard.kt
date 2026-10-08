package echo.music.iad1tya.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage

enum class CardStyle {
    HERO_WIDE,
    TALL_PORTRAIT,
    COMPACT_SQUARE,
    WIDE_LANDSCAPE
}

@Composable
fun PremiumCinemaCard(
    imageUrl: String?,
    title: String,
    subtitle: String? = null,
    style: CardStyle = CardStyle.COMPACT_SQUARE,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val shape = when (style) {
        CardStyle.HERO_WIDE -> RoundedCornerShape(28.dp)
        CardStyle.TALL_PORTRAIT -> RoundedCornerShape(20.dp)
        CardStyle.COMPACT_SQUARE -> RoundedCornerShape(16.dp)
        CardStyle.WIDE_LANDSCAPE -> RoundedCornerShape(24.dp)
    }

    val cardModifier = when (style) {
        CardStyle.HERO_WIDE -> modifier.fillMaxWidth().height(220.dp)
        CardStyle.TALL_PORTRAIT -> modifier.width(160.dp).height(240.dp)
        CardStyle.COMPACT_SQUARE -> modifier.width(150.dp).height(150.dp)
        CardStyle.WIDE_LANDSCAPE -> modifier.width(280.dp).height(160.dp)
    }

    Box(
        modifier = cardModifier
            .clip(shape)
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.7f)
                        ),
                        startY = 100f
                    )
                )
        )

        if (style == CardStyle.HERO_WIDE || style == CardStyle.WIDE_LANDSCAPE) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
