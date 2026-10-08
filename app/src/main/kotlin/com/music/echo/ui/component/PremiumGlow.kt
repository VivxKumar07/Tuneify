package echo.music.iad1tya.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun PremiumGlow(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF1F4068),
    height: Dp = 48.dp,
    intensity: Float = 0.35f
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        color.copy(alpha = intensity),
                        color.copy(alpha = intensity * 0.4f),
                        Color.Transparent
                    )
                )
            )
    )
}

@Composable
fun PremiumGlowUnderlay(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF1F4068),
    intensity: Float = 0.25f
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        color.copy(alpha = intensity),
                        Color.Transparent
                    ),
                    radius = 600f
                )
            )
    )
}

@Composable
fun PremiumSettingsCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFF1B1B2F).copy(alpha = 0.6f),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF1F4068).copy(alpha = 0.5f),
                            Color.Transparent
                        )
                    )
                )
                .align(Alignment.BottomCenter)
        )
        content()
    }
}
