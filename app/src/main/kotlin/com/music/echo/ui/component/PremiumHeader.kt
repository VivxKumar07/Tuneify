package echo.music.iad1tya.ui.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalTime
import kotlin.random.Random

private val ObsidianDeep = Color(0xFF080C14)
private val ObsidianGlow = Color(0xFF1E3A8A)
private val SoftWhite = Color(0xFFF0F0F5)

private val musicQuotes = listOf(
    "Music is the soundtrack of your soul.",
    "Where words fail, music speaks.",
    "Life is a song, love is the music.",
    "One good thing about music, when it hits you feel no pain.",
    "Without music, life would be a mistake.",
    "Music expresses that which cannot be put into words.",
    "After silence, that which comes nearest is music.",
    "Music is the shorthand of emotion.",
    "The only truth is music.",
    "Music gives a soul to the universe."
)

@Composable
fun PremiumHeader(modifier: Modifier = Modifier) {
    val hour = LocalTime.now().hour
    val greeting = when {
        hour in 5..11 -> "Good Morning"
        hour in 12..16 -> "Good Afternoon"
        hour in 17..20 -> "Good Evening"
        else -> "Good Night"
    }
    val quoteIndex = remember { Random.nextInt(musicQuotes.size) }
    val quote = musicQuotes[quoteIndex]

    val infiniteTransition = rememberInfiniteTransition(label = "obsidian_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )
    val featherSpread by infiniteTransition.animateFloat(
        initialValue = 120f,
        targetValue = 180f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "feather_spread"
    )

    val accentColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawRect(color = ObsidianDeep)

            drawCircle(
                color = accentColor.copy(alpha = glowAlpha * 0.4f),
                radius = featherSpread,
                center = Offset(0f, 0f)
            )
            drawCircle(
                color = ObsidianGlow.copy(alpha = glowAlpha * 0.25f),
                radius = featherSpread * 0.7f,
                center = Offset(0f, 0f)
            )
            drawCircle(
                color = Color.White.copy(alpha = glowAlpha * 0.08f),
                radius = featherSpread * 0.3f,
                center = Offset(0f, 0f)
            )

            for (i in 0..3) {
                val y = size.height * (0.3f + i * 0.15f)
                val lineAlpha = 0.03f - (i * 0.006f)
                if (lineAlpha > 0f) {
                    drawLine(
                        color = SoftWhite.copy(alpha = lineAlpha),
                        start = Offset(0f, y),
                        end = Offset(size.width * 0.5f, y - 30f),
                        strokeWidth = 1f
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 24.dp, bottom = 24.dp, end = 24.dp)
        ) {
            Text(
                text = greeting,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = SoftWhite,
                letterSpacing = (-0.5).sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = quote,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = SoftWhite.copy(alpha = 0.6f),
                lineHeight = 20.sp,
                maxLines = 2
            )
        }
    }
}
