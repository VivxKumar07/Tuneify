package echo.music.iad1tya.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import echo.music.iad1tya.R
import echo.music.iad1tya.ui.theme.AeonikFontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * YouTube Music-style opening motion on startup:
 * 1. Pitch-dark backdrop (#05070B)
 * 2. Center 3D logo smoothly scales up with subtle overshoot and soft radial ambient breathing
 * 3. Sleek "Tuneify" typography in Aeonik Bold smoothly fades in
 * 4. Seamless, cinematic dissolve transition into the home screen
 */
@Composable
fun TuneifySplashLaunchOverlay(
    onSplashFinished: () -> Unit
) {
    val logoScale = remember { Animatable(0.72f) }
    val logoAlpha = remember { Animatable(0f) }
    val titleAlpha = remember { Animatable(0f) }
    val exitAlpha = remember { Animatable(1f) }
    val exitScale = remember { Animatable(1.0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_glow")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_glow_anim"
    )

    LaunchedEffect(Unit) {
        // Entrance: logo scale and alpha
        launch {
            logoAlpha.animateTo(1f, tween(400, easing = LinearOutSlowInEasing))
        }
        launch {
            logoScale.animateTo(1.0f, tween(600, easing = FastOutSlowInEasing))
        }

        // Title fade in
        delay(250)
        launch {
            titleAlpha.animateTo(1f, tween(400, easing = LinearOutSlowInEasing))
        }

        // Hold momentarily like YT Music
        delay(850)

        // Exit: cinematic dissolve & slight scale expansion
        launch {
            exitScale.animateTo(1.08f, tween(400, easing = FastOutSlowInEasing))
        }
        exitAlpha.animateTo(0f, tween(400, easing = FastOutSlowInEasing))

        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF05070B))
            .graphicsLayer {
                alpha = exitAlpha.value
                scaleX = exitScale.value
                scaleY = exitScale.value
            },
        contentAlignment = Alignment.Center
    ) {
        // Subtle ambient radial glow behind logo
        Box(
            modifier = Modifier
                .size(240.dp)
                .scale(pulseGlow)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF2563EB).copy(alpha = 0.18f),
                            Color(0xFF1D4ED8).copy(alpha = 0.08f),
                            Color.Transparent
                        ),
                        center = Offset.Unspecified,
                        radius = 320f
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.tuneify_logo),
                contentDescription = "Tuneify Logo",
                modifier = Modifier
                    .size(112.dp)
                    .graphicsLayer {
                        scaleX = logoScale.value
                        scaleY = logoScale.value
                        alpha = logoAlpha.value
                    }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Tuneify",
                fontFamily = AeonikFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                letterSpacing = 1.2.sp,
                color = Color.White,
                modifier = Modifier.graphicsLayer {
                    alpha = titleAlpha.value
                }
            )
        }
    }
}
