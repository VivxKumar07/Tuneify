package echo.music.iad1tya.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import echo.music.iad1tya.R
import echo.music.iad1tya.ui.theme.TuneifyMotion
import echo.music.iad1tya.ui.theme.TuneifyTokens

/**
 * The Tuneify brand mark — a vinyl record whose centre label carries a play head cut out of it.
 *
 * It is repeated everywhere on purpose — section headers, empty states, the home header, the
 * about screen — so the app has one instantly recognisable symbol without adding visual clutter.
 * One mark, many placements, is how a brand gets remembered.
 *
 * The mark degrades gracefully: at large sizes the play cut-out reads clearly, and at 16–20dp it
 * collapses into a spindle dot, so it still looks deliberate instead of broken.
 */
@Composable
fun TuneifyMark(
  modifier: Modifier = Modifier,
  size: Dp = 20.dp,
  tint: Color = MaterialTheme.colorScheme.primary,
) {
  Icon(
    painter = painterResource(R.drawable.ic_tuneify_logo),
    contentDescription = null,
    tint = tint,
    modifier = modifier.size(size),
  )
}

/**
 * The stacked brand lockup: mark + wordmark. Used on the home header, the about screen and the
 * splash. The wordmark is set in the brand face with tight tracking so it reads as one designed
 * object with the mark rather than "icon next to text".
 */
@Composable
fun TuneifyWordmark(
  modifier: Modifier = Modifier,
  markSize: Dp = 26.dp,
  fontSize: Dp = 22.dp,
  markTint: Color = MaterialTheme.colorScheme.primary,
  textColor: Color = MaterialTheme.colorScheme.onBackground,
) {
  Row(modifier, verticalAlignment = Alignment.CenterVertically) {
    TuneifyMark(size = markSize, tint = markTint)
    Spacer(Modifier.width(9.dp))
    Text(
      text = "Tuneify",
      fontWeight = FontWeight.ExtraBold,
      fontSize = fontSize.value.sp,
      color = textColor,
      letterSpacing = (-0.5).sp,
    )
  }
}

/**
 * Legacy name for the brand tick. It used to render three Bauhaus bars; it now renders the Tuneify
 * mark so every existing placement rebrands at once. The width/gap parameters are ignored.
 */
@Composable
fun TuneifyTriBar(
  modifier: Modifier = Modifier,
  height: Dp = 14.dp,
  barWidth: Dp = 3.dp,
  gap: Dp = 2.dp,
) {
  TuneifyMark(modifier = modifier, size = height, tint = MaterialTheme.colorScheme.primary)
}

/** Small section eyebrow. Sentence case, quiet — it labels without decorating. */
@Composable
fun TuneifyEyebrow(
  text: String,
  modifier: Modifier = Modifier,
  color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
  Text(
    text = text,
    style = MaterialTheme.typography.labelMedium,
    fontWeight = FontWeight.SemiBold,
    color = color,
    modifier = modifier,
  )
}

/**
 * Standard section header: an editorial title with an optional trailing action.
 * Material 3 Expressive keeps the type bold and lets the content below carry the identity, so no
 * mark or rule is repeated here.
 */
@Composable
fun TuneifySectionHeader(
  title: String,
  modifier: Modifier = Modifier,
  trailing: (@Composable () -> Unit)? = null,
) {
  Row(
    modifier = modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    Text(
      text = title,
      style = MaterialTheme.typography.titleLarge,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onSurface,
      modifier = Modifier.weight(1f),
    )
    trailing?.invoke()
  }
}

/**
 * Press feedback: a short scale-down with a snappy spring back. Applied through motion instead of a
 * permanent drop shadow.
 */
@Composable
fun Modifier.tuneifyPressScale(
  interactionSource: MutableInteractionSource,
  enabled: Boolean = true,
  pressedScale: Float = TuneifyMotion.PressScale,
): Modifier {
  val pressed by interactionSource.collectIsPressedAsState()
  val scale by
    animateFloatAsState(
      targetValue = if (pressed && enabled) pressedScale else 1f,
      animationSpec = TuneifyMotion.snappy(),
      label = "tuneify-press-scale",
    )
  return this.graphicsLayer {
    scaleX = scale
    scaleY = scale
  }
}

/**
 * Soft elevation for the surfaces allowed to lift: now-playing artwork, the main play button, and
 * the active navigation indicator. Everything else separates with tonal colour steps.
 */
fun Modifier.tuneifyHeroShadow(
  isDark: Boolean,
  shape: Shape = TuneifyTokens.ShapeCard,
  accent: Color? = null,
): Modifier =
  echo.music.iad1tya.ui.theme.neoShadow(
    color = accent ?: Color.Black,
    offsetX = TuneifyTokens.ShadowOffsetStandard,
    offsetY = TuneifyTokens.ShadowOffsetStandard,
    shape = shape,
  )
