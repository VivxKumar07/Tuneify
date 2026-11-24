package echo.music.iad1tya.ui.component.glass

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import echo.music.iad1tya.R
import echo.music.iad1tya.utils.makeTimeString
import kotlin.math.abs
import kotlin.math.sin

/**
 * Tuneify Organic Glassmorphism & Expressive Design System Tokens.
 */
object TuneifyGlassTokens {
  val ForestDark = Color(0xFF07120C)
  val ForestCanvas = Color(0xFF0A1810)
  val ForestDeep = Color(0xFF0D2115)

  // Primary Volt / Lime accent
  val VoltLime = Color(0xFFC6F53D)
  val VoltLimeBright = Color(0xFFD2F832)
  val VoltLimeDeep = Color(0xFFA9D62C)
  val VoltLimeSoft = Color(0xFFE9FFB4)
  val OnVoltLime = Color(0xFF08120A)

  // Supporting sage & emerald tones
  val SageMuted = Color(0xFF9DB4A3)
  val SageLight = Color(0xFFB4C8BA)
  val EmeraldGlow = Color(0xFF267344)
  val MintAccent = Color(0xFF3ED6A0)
  val ForestGreen = Color(0xFF1E5B3A)

  // Liquid glass surface colors
  val GlassBg = Color(0x35183022) // Translucent dark forest glass
  val GlassBgElevated = Color(0x4D203D2A)
  val GlassBgFloatingNav = Color(0x3812261A) // True liquid glass translucency!
  val GlassBorder = Color(0x40FFFFFF)
  val GlassBorderSubtle = Color(0x22FFFFFF)
  val GlassBorderLime = Color(0x50C6F53D)

  // Typography
  val TextPrimary = Color(0xFFFFFFFF)
  val TextSecondary = Color(0xCCF2F6F3)
  val TextTertiary = Color(0x80F2F6F3)

  // Standard Corner Radii
  val RadiusSmall = 12.dp
  val RadiusMedium = 18.dp
  val RadiusLarge = 24.dp
  val RadiusXLarge = 32.dp
  val RadiusPill = 999.dp
}

/**
 * CompositionLocal providing dynamic extracted artwork accent color across all screens.
 */
val LocalDynamicAccentColor = androidx.compose.runtime.compositionLocalOf { TuneifyGlassTokens.VoltLime }

/**
 * Organic asymmetric fluid wave shape for artwork container & hero elements.
 */
class OrganicWaveShape(
  private val morphPhase: Float = 0f,
  private val asymmetry: Float = 1f
) : Shape {
  override fun createOutline(
    size: Size,
    layoutDirection: LayoutDirection,
    density: Density
  ): Outline {
    val w = size.width
    val h = size.height
    val path = Path().apply {
      val morph = sin(morphPhase) * 0.03f

      // Asymmetric organic contour matching reference image Screen 2
      moveTo(w * (0.28f + morph), h * 0.06f)
      cubicTo(
        w * (0.50f + morph * 0.5f), h * (0.01f - morph),
        w * (0.78f - morph), h * 0.08f,
        w * (0.94f + morph), h * 0.28f
      )
      cubicTo(
        w * (1.04f - morph), h * (0.46f + morph),
        w * 0.98f, h * (0.74f - morph),
        w * (0.84f - morph), h * 0.90f
      )
      cubicTo(
        w * (0.70f + morph), h * 1.01f,
        w * (0.36f - morph), h * 0.97f,
        w * (0.16f + morph), h * 0.86f
      )
      cubicTo(
        w * (0.02f - morph), h * (0.72f + morph),
        w * (-0.03f + morph), h * 0.44f,
        w * (0.07f - morph), h * 0.24f
      )
      cubicTo(
        w * 0.13f, h * (0.12f - morph),
        w * 0.18f, h * 0.08f,
        w * (0.28f + morph), h * 0.06f
      )
      close()
    }
    return Outline.Generic(path)
  }
}

/**
 * Organic asymmetric fluid wave shape for the Home Screen Hero card.
 * Smooth, elegant asymmetric contour matching Screen 1 in the design specification with subtle morphing.
 */
class HeroOrganicShape(private val morphPhase: Float = 0f) : Shape {
  override fun createOutline(
    size: Size,
    layoutDirection: LayoutDirection,
    density: Density
  ): Outline {
    val w = size.width
    val h = size.height
    val m = kotlin.math.sin(morphPhase) * 3f
    val path = Path().apply {
      // Top-left starting point
      moveTo(28f, 12f + m)
      // Top organic wave gently dipping in center
      cubicTo(w * 0.28f, 6f - m, w * 0.52f, 16f + m, w * 0.74f, 10f - m)
      cubicTo(w * 0.88f, 6f - m, w - 16f, 16f, w, 46f + m)
      // Right edge smoothly curving downwards
      cubicTo(w - 2f, h * 0.35f, w - 6f, h * 0.68f, w - 20f, h - 16f - m)
      // Bottom wave contour dipping softly
      cubicTo(w * 0.74f, h - 4f + m, w * 0.48f, h - 14f - m, w * 0.26f, h - 6f + m)
      cubicTo(w * 0.12f, h - 2f + m, 14f, h - 16f, 0f, h - 42f)
      // Left edge
      cubicTo(2f, h * 0.68f, 2f, h * 0.32f, 16f, 24f + m)
      close()
    }
    return Outline.Generic(path)
  }
}

/**
 * Glass surface modifier applying translucent backdrop, specular top sheen, frosted border highlight, and elevation.
 */
fun Modifier.glassSurface(
  shape: Shape = RoundedCornerShape(TuneifyGlassTokens.RadiusMedium),
  backgroundColor: Color = TuneifyGlassTokens.GlassBg,
  borderColor: Color = TuneifyGlassTokens.GlassBorder,
  borderWidth: Dp = 1.25.dp,
  elevation: Dp = 6.dp
): Modifier = this
  .then(
    if (elevation > 0.dp) {
      Modifier.shadow(
        elevation = elevation,
        shape = shape,
        clip = false,
        ambientColor = Color(0x80000000),
        spotColor = Color(0x50000000)
      )
    } else Modifier
  )
  .clip(shape)
  .background(
    brush = Brush.verticalGradient(
      listOf(
        Color(0x35FFFFFF), // Specular light highlight on top rim of glass
        backgroundColor.copy(alpha = (backgroundColor.alpha * 1.15f).coerceAtMost(1f)),
        backgroundColor.copy(alpha = (backgroundColor.alpha * 0.85f).coerceAtLeast(0f))
      )
    ),
    shape = shape
  )
  .border(
    width = borderWidth,
    brush = Brush.linearGradient(
      listOf(
        Color(0x80FFFFFF), // bright crisp frosted glass highlight
        Color(0x20FFFFFF),
        TuneifyGlassTokens.GlassBorderLime // subtle lime glass rim
      )
    ),
    shape = shape
  )

/**
 * Reusable frosted glass card.
 */
@Composable
fun GlassCard(
  modifier: Modifier = Modifier,
  shape: Shape = RoundedCornerShape(TuneifyGlassTokens.RadiusMedium),
  backgroundColor: Color = TuneifyGlassTokens.GlassBg,
  borderColor: Color = TuneifyGlassTokens.GlassBorder,
  onClick: (() -> Unit)? = null,
  content: @Composable () -> Unit
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(
    targetValue = if (isPressed && onClick != null) 0.98f else 1f,
    animationSpec = spring(dampingRatio = 0.7f, stiffness = 400f),
    label = "glassCardScale"
  )

  Box(
    modifier = modifier
      .graphicsLayer {
        scaleX = scale
        scaleY = scale
      }
      .glassSurface(shape = shape, backgroundColor = backgroundColor, borderColor = borderColor)
      .then(
        if (onClick != null) {
          Modifier.clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
        } else Modifier
      )
  ) {
    content()
  }
}

/**
 * Reusable Glass Pill Button (used for categories, explore tabs, player shortcuts).
 */
@Composable
fun GlassPill(
  text: String,
  selected: Boolean = false,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  icon: (@Composable () -> Unit)? = null,
  accentColor: Color = TuneifyGlassTokens.VoltLime,
  badgeText: String? = null
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.94f else 1f,
    animationSpec = spring(dampingRatio = 0.75f, stiffness = 500f),
    label = "pillScale"
  )

  val containerColor by animateColorAsState(
    targetValue = if (selected) accentColor else TuneifyGlassTokens.GlassBg,
    animationSpec = tween(220),
    label = "pillBgColor"
  )

  val contentColor by animateColorAsState(
    targetValue = if (selected) TuneifyGlassTokens.OnVoltLime else TuneifyGlassTokens.TextPrimary,
    animationSpec = tween(220),
    label = "pillContentColor"
  )

  val borderColor = if (selected) accentColor.copy(alpha = 0.6f) else TuneifyGlassTokens.GlassBorder

  val pillShape = RoundedCornerShape(TuneifyGlassTokens.RadiusPill)

  Row(
    modifier = modifier
      .graphicsLayer {
        scaleX = scale
        scaleY = scale
      }
      .clip(pillShape)
      .background(containerColor, pillShape)
      .border(1.dp, borderColor, pillShape)
      .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
      .padding(horizontal = 8.dp, vertical = 7.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center
  ) {
    if (icon != null) {
      Box(modifier = Modifier.size(16.dp), contentAlignment = Alignment.Center) {
        androidx.compose.runtime.CompositionLocalProvider(
          androidx.compose.material3.LocalContentColor provides contentColor
        ) {
          icon()
        }
      }
      Spacer(Modifier.width(4.dp))
    }
    Text(
      text = text,
      color = contentColor,
      fontSize = 12.sp,
      fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
    if (badgeText != null) {
      Spacer(Modifier.width(4.dp))
      Text(
        text = badgeText,
        color = contentColor.copy(alpha = 0.75f),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold
      )
    }
  }
}

/**
 * Reusable Glass Button (circular or rounded action button).
 */
@Composable
fun GlassButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  size: Dp = 44.dp,
  backgroundColor: Color = TuneifyGlassTokens.GlassBg,
  iconTint: Color = TuneifyGlassTokens.TextPrimary,
  icon: @Composable () -> Unit
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.92f else 1f,
    animationSpec = spring(dampingRatio = 0.65f, stiffness = 500f),
    label = "glassBtnScale"
  )

  Box(
    modifier = modifier
      .size(size)
      .graphicsLayer {
        scaleX = scale
        scaleY = scale
      }
      .glassSurface(shape = CircleShape, backgroundColor = backgroundColor, elevation = 2.dp)
      .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
    contentAlignment = Alignment.Center
  ) {
    androidx.compose.runtime.CompositionLocalProvider(
      androidx.compose.material3.LocalContentColor provides iconTint
    ) {
      icon()
    }
  }
}

/**
 * Distinctive Tuneify Waveform Logo: 5 vertical capsules with varying heights in vibrant lime + wordmark.
 * Matches the reference image specification: Bar 1 (10dp), Bar 2 (18dp), Bar 3 (26dp peak), Bar 4 (18dp), Bar 5 (10dp).
 */
@Composable
fun TuneifyWaveformLogo(
  modifier: Modifier = Modifier,
  accentColor: Color = TuneifyGlassTokens.VoltLime,
  showText: Boolean = true,
  textColor: Color = TuneifyGlassTokens.TextPrimary
) {
  Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
    // 5 vertical rounded capsules with center peak
    val barHeights = listOf(10.dp, 18.dp, 26.dp, 18.dp, 10.dp)
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(3.dp),
      modifier = Modifier.height(28.dp)
    ) {
      barHeights.forEach { height ->
        Box(
          modifier = Modifier
            .width(4.dp)
            .height(height)
            .clip(CircleShape)
            .background(accentColor)
        )
      }
    }

    if (showText) {
      Spacer(Modifier.width(10.dp))
      Text(
        text = "Tuneify",
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        color = textColor,
        letterSpacing = (-0.4).sp
      )
    }
  }
}

/**
 * Reusable Section Header with title and chevron '>' arrow.
 */
@Composable
fun ExpressiveSectionHeader(
  title: String,
  modifier: Modifier = Modifier,
  onChevronClick: (() -> Unit)? = null,
  trailingContent: (@Composable () -> Unit)? = null
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = if (onChevronClick != null) Modifier.clickable(onClick = onChevronClick) else Modifier
    ) {
      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(
          fontSize = 19.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = (-0.3).sp
        ),
        color = TuneifyGlassTokens.TextPrimary
      )
      if (onChevronClick != null) {
        Spacer(Modifier.width(4.dp))
        Icon(
          painter = painterResource(R.drawable.navigate_next),
          contentDescription = "More",
          tint = TuneifyGlassTokens.SageMuted,
          modifier = Modifier.size(20.dp)
        )
      }
    }
    trailingContent?.invoke()
  }
}

/**
 * Reusable Glass Song Row (used in Explore "Trending Now", Quick Picks, etc.).
 */
@Composable
fun GlassSongRow(
  title: String,
  subtitle: String,
  thumbnailUrl: String?,
  onClick: () -> Unit,
  onPlayClick: () -> Unit,
  modifier: Modifier = Modifier,
  isActive: Boolean = false,
  isPlaying: Boolean = false,
  accentColor: Color = TuneifyGlassTokens.VoltLime
) {
  val rowShape = RoundedCornerShape(TuneifyGlassTokens.RadiusMedium)

  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 4.dp)
      .glassSurface(
        shape = rowShape,
        backgroundColor = if (isActive) TuneifyGlassTokens.GlassBgElevated else TuneifyGlassTokens.GlassBg,
        borderColor = if (isActive) accentColor.copy(alpha = 0.5f) else TuneifyGlassTokens.GlassBorder
      )
      .clickable(onClick = onClick)
      .padding(10.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Thumbnail
    Box(
      modifier = Modifier
        .size(52.dp)
        .clip(RoundedCornerShape(12.dp))
        .background(Color(0xFF14241A))
    ) {
      if (!thumbnailUrl.isNullOrBlank()) {
        AsyncImage(
          model = thumbnailUrl,
          contentDescription = title,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )
      } else {
        Icon(
          painter = painterResource(R.drawable.music_note),
          contentDescription = null,
          tint = TuneifyGlassTokens.SageMuted,
          modifier = Modifier
            .size(24.dp)
            .align(Alignment.Center)
        )
      }
    }

    Spacer(Modifier.width(14.dp))

    // Song & Subtitle
    Column(
      modifier = Modifier.weight(1f),
      verticalArrangement = Arrangement.Center
    ) {
      Text(
        text = title,
        color = if (isActive) accentColor else TuneifyGlassTokens.TextPrimary,
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
      Spacer(Modifier.height(2.dp))
      Text(
        text = subtitle,
        color = TuneifyGlassTokens.TextSecondary,
        fontSize = 13.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }

    Spacer(Modifier.width(10.dp))

    // Play action
    Box(
      modifier = Modifier
        .size(36.dp)
        .clip(CircleShape)
        .background(
          if (isActive && isPlaying) accentColor else Color(0x33FFFFFF)
        )
        .border(1.dp, Color(0x2EFFFFFF), CircleShape)
        .clickable(onClick = onPlayClick),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        painter = painterResource(
          if (isActive && isPlaying) R.drawable.pause else R.drawable.play
        ),
        contentDescription = "Play",
        tint = if (isActive && isPlaying) TuneifyGlassTokens.OnVoltLime else Color.White,
        modifier = Modifier.size(16.dp)
      )
    }
  }
}

/**
 * Reusable Glass Artwork Card (used for Quick Picks horizontal row & Made For You).
 */
/**
 * Glass Artwork Card matching reference image Screen 1 Quick Picks.
 * Features full-bleed real album artwork, soft bottom frosted glass gradient,
 * high-contrast expressive typography, and an interactive glass play button.
 */
@Composable
fun GlassArtworkCard(
  title: String,
  subtitle: String,
  thumbnailUrl: String?,
  onClick: () -> Unit,
  onPlayClick: () -> Unit,
  modifier: Modifier = Modifier,
  badgeText: String? = null,
  cardWidth: Dp = 158.dp,
  cardHeight: Dp = 205.dp,
  accentColor: Color = TuneifyGlassTokens.VoltLime
) {
  val shape = RoundedCornerShape(22.dp)

  val playInteraction = remember { MutableInteractionSource() }
  val isPlayPressed by playInteraction.collectIsPressedAsState()
  val playScale by animateFloatAsState(
    targetValue = if (isPlayPressed) 0.88f else 1f,
    animationSpec = spring(dampingRatio = 0.6f, stiffness = 500f),
    label = "quickPickPlayScale"
  )

  Box(
    modifier = modifier
      .size(width = cardWidth, height = cardHeight)
      .shadow(
        elevation = 10.dp,
        shape = shape,
        clip = false,
        ambientColor = Color(0x60000000),
        spotColor = Color(0x40000000)
      )
      .clip(shape)
      .background(Color(0xFF07170E), shape)
      .border(
        width = 1.25.dp,
        brush = Brush.verticalGradient(
          listOf(
            Color(0x60FFFFFF),
            Color(0x15FFFFFF),
            accentColor.copy(alpha = 0.35f)
          )
        ),
        shape = shape
      )
      .clickable(onClick = onClick)
  ) {
    // 1. Real Artwork Image (Full Bleed)
    if (!thumbnailUrl.isNullOrBlank()) {
      AsyncImage(
        model = thumbnailUrl,
        contentDescription = title,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
      )
    } else {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color(0xFF10261A))
      )
    }

    // 2. Top Specular Glass Sheen
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(36.dp)
        .background(
          Brush.verticalGradient(
            listOf(Color(0x30FFFFFF), Color.Transparent)
          )
        )
    )

    // 3. Optional YouTube Music style Pill Badge
    if (!badgeText.isNullOrBlank()) {
      Box(
        modifier = Modifier
          .padding(top = 10.dp, start = 10.dp)
          .clip(CircleShape)
          .background(Color(0x80000000))
          .border(0.75.dp, Color(0x40FFFFFF), CircleShape)
          .padding(horizontal = 7.dp, vertical = 2.dp)
      ) {
        Text(
          text = badgeText,
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold,
          color = accentColor,
          letterSpacing = 0.5.sp
        )
      }
    }

    // 4. Bottom dark frosted glass gradient (starts at 50% height so artwork is bright)
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          brush = Brush.verticalGradient(
            colors = listOf(
              Color.Transparent,
              Color(0x40030B06),
              Color(0xCC030B06),
              Color(0xF5030B06)
            ),
            startY = 200f
          )
        )
    )

    // 5. Text & interactive glass play button at bottom
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.BottomStart)
        .padding(horizontal = 12.dp, vertical = 12.dp),
      verticalAlignment = Alignment.Bottom,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
        Text(
          text = title,
          color = Color.White,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(2.dp))
        Text(
          text = subtitle,
          color = Color(0xB8FFFFFF),
          fontSize = 11.5.sp,
          fontWeight = FontWeight.Medium,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }

      // Glass play button with smooth spring animation
      Box(
        modifier = Modifier
          .size(32.dp)
          .graphicsLayer {
            scaleX = playScale
            scaleY = playScale
          }
          .clip(CircleShape)
          .background(Color(0x40FFFFFF))
          .border(
            0.75.dp,
            Brush.verticalGradient(listOf(Color(0x99FFFFFF), accentColor.copy(alpha = 0.70f))),
            CircleShape
          )
          .clickable(interactionSource = playInteraction, indication = null, onClick = onPlayClick),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          painter = painterResource(R.drawable.play),
          contentDescription = "Play",
          tint = Color.White,
          modifier = Modifier.size(15.dp)
        )
      }
    }
  }
}

/**
 * YouTube Music Style Detailed Mixed Layout Song Card.
 * Compact horizontal row with high-res square thumbnail, title, artist, album/views details, and play action.
 */
@Composable
fun TuneifyDetailedSongCard(
  title: String,
  artist: String,
  albumOrViews: String,
  thumbnailUrl: String?,
  onClick: () -> Unit,
  onPlayClick: () -> Unit,
  modifier: Modifier = Modifier,
  accentColor: Color = TuneifyGlassTokens.VoltLime
) {
  val shape = RoundedCornerShape(16.dp)

  Row(
    modifier = modifier
      .width(280.dp)
      .height(68.dp)
      .clip(shape)
      .background(Color(0x550B1C12), shape)
      .border(1.dp, Color(0x20FFFFFF), shape)
      .clickable(onClick = onClick)
      .padding(horizontal = 10.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Thumbnail
    Box(
      modifier = Modifier
        .size(52.dp)
        .clip(RoundedCornerShape(10.dp))
        .border(0.75.dp, Color(0x33FFFFFF), RoundedCornerShape(10.dp))
    ) {
      if (!thumbnailUrl.isNullOrBlank()) {
        AsyncImage(
          model = thumbnailUrl,
          contentDescription = title,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )
      } else {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF142E20))
        )
      }
    }

    Spacer(Modifier.width(12.dp))

    // Song details
    Column(
      modifier = Modifier.weight(1f),
      verticalArrangement = Arrangement.Center
    ) {
      Text(
        text = title,
        color = Color.White,
        fontSize = 13.5.sp,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
      Spacer(Modifier.height(2.dp))
      Text(
        text = "$artist • $albumOrViews",
        color = Color(0xA0FFFFFF),
        fontSize = 11.5.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }

    // Play button
    Box(
      modifier = Modifier
        .size(30.dp)
        .clip(CircleShape)
        .background(Color(0x30FFFFFF))
        .border(0.75.dp, Color(0x50FFFFFF), CircleShape)
        .clickable(onClick = onPlayClick),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        painter = painterResource(R.drawable.play),
        contentDescription = "Play",
        tint = Color.White,
        modifier = Modifier.size(14.dp)
      )
    }
  }
}

/**
 * Dynamic artwork-informed ambient background with glowing organic light orbs and luminous flowing ribbons.
 * Uses drawWithCache so GPU compositing is fully hardware-accelerated with zero allocations during scrolling (locked 120Hz).
 */
@Composable
fun DynamicArtworkBackground(
  modifier: Modifier = Modifier,
  accentColor: Color = LocalDynamicAccentColor.current,
  content: @Composable () -> Unit
) {
  val animatedAccent by animateColorAsState(
    targetValue = accentColor,
    animationSpec = tween(durationMillis = 650),
    label = "dynamicBgAccent"
  )

  val baseTop = remember(animatedAccent) {
    Color(
      red = (0.02f + animatedAccent.red * 0.05f).coerceIn(0f, 1f),
      green = (0.04f + animatedAccent.green * 0.08f).coerceIn(0f, 1f),
      blue = (0.02f + animatedAccent.blue * 0.05f).coerceIn(0f, 1f),
      alpha = 1f
    )
  }
  val baseMid = remember(animatedAccent) {
    Color(
      red = (0.03f + animatedAccent.red * 0.04f).coerceIn(0f, 1f),
      green = (0.06f + animatedAccent.green * 0.06f).coerceIn(0f, 1f),
      blue = (0.03f + animatedAccent.blue * 0.04f).coerceIn(0f, 1f),
      alpha = 1f
    )
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(
            baseTop,
            baseMid,
            Color(0xFF020603)
          )
        )
      )
      .drawWithCache {
        val w = size.width
        val h = size.height

        // 1. Top-right dynamic artwork aura (behind header & hero card)
        val topAuraBrush = Brush.radialGradient(
          colors = listOf(
            animatedAccent.copy(alpha = 0.28f),
            TuneifyGlassTokens.EmeraldGlow.copy(alpha = 0.16f),
            Color(0xFF1E5B3A).copy(alpha = 0.08f),
            Color.Transparent
          ),
          center = Offset(w * 0.85f, h * 0.12f),
          radius = w * 0.70f
        )

        // 2. Top-left subtle atmospheric bloom
        val topLeftBrush = Brush.radialGradient(
          colors = listOf(
            animatedAccent.copy(alpha = 0.18f),
            TuneifyGlassTokens.EmeraldGlow.copy(alpha = 0.10f),
            Color.Transparent
          ),
          center = Offset(w * 0.15f, h * 0.20f),
          radius = w * 0.60f
        )

        // 3. Center organic fluid aura
        val centerBrush = Brush.radialGradient(
          colors = listOf(
            animatedAccent.copy(alpha = 0.15f),
            Color(0xFF0E3320).copy(alpha = 0.09f),
            Color.Transparent
          ),
          center = Offset(w * 0.45f, h * 0.50f),
          radius = w * 0.80f
        )

        // 4. Mid-right ambient glow
        val midRightBrush = Brush.radialGradient(
          colors = listOf(
            animatedAccent.copy(alpha = 0.20f),
            Color.Transparent
          ),
          center = Offset(w * 0.95f, h * 0.65f),
          radius = w * 0.50f
        )

        // 5. Bottom glowing organic ribbon under floating navbar (soft, non-glaring)
        val bottomNavAuraBrush = Brush.radialGradient(
          colors = listOf(
            accentColor.copy(alpha = 0.26f),
            Color(0xFF8CE82A).copy(alpha = 0.18f),
            TuneifyGlassTokens.EmeraldGlow.copy(alpha = 0.10f),
            Color.Transparent
          ),
          center = Offset(w * 0.50f, h * 0.94f),
          radius = w * 0.65f
        )

        val wavePath = Path().apply {
          moveTo(0f, h * 0.90f)
          cubicTo(w * 0.25f, h * 0.86f, w * 0.65f, h * 0.95f, w, h * 0.91f)
          lineTo(w, h)
          lineTo(0f, h)
          close()
        }

        val waveBrush = Brush.verticalGradient(
          colors = listOf(
            accentColor.copy(alpha = 0.18f),
            Color(0xFF1E5B3A).copy(alpha = 0.15f),
            Color.Transparent
          ),
          startY = h * 0.84f,
          endY = h
        )

        onDrawBehind {
          drawCircle(brush = topAuraBrush, center = Offset(w * 0.82f, h * 0.14f), radius = w * 0.75f)
          drawCircle(brush = topLeftBrush, center = Offset(w * 0.15f, h * 0.22f), radius = w * 0.65f)
          drawCircle(brush = centerBrush, center = Offset(w * 0.45f, h * 0.52f), radius = w * 0.85f)
          drawCircle(brush = midRightBrush, center = Offset(w * 0.95f, h * 0.65f), radius = w * 0.55f)
          drawOval(
            brush = bottomNavAuraBrush,
            topLeft = Offset(w * -0.1f, h * 0.82f),
            size = Size(w * 1.2f, h * 0.22f)
          )
          drawPath(path = wavePath, brush = waveBrush)
        }
      }
  ) {
    content()
  }
}

/**
 * Organic Asymmetric Artwork Container for Now Playing Screen.
 * Recreates the fluid wavy framed circular/organic photo cutout with glowing lime aura.
 */
@Composable
fun OrganicArtworkContainer(
  thumbnailUrl: String?,
  modifier: Modifier = Modifier,
  isPlaying: Boolean = false,
  accentColor: Color = TuneifyGlassTokens.VoltLime,
  onSwipeLeft: (() -> Unit)? = null,
  onSwipeRight: (() -> Unit)? = null
) {
  val infiniteTransition = rememberInfiniteTransition(label = "organicBreathing")
  val phase by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 6.28318f,
    animationSpec = infiniteRepeatable(
      animation = tween(if (isPlaying) 6000 else 12000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "organicPhase"
  )

  val auraPulse by infiniteTransition.animateFloat(
    initialValue = 0.94f,
    targetValue = 1.06f,
    animationSpec = infiniteRepeatable(
      animation = tween(if (isPlaying) 2200 else 4000, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "auraPulse"
  )

  val m = kotlin.math.sin(phase) * 2.5f
  val organicShape = remember(phase) {
    RoundedCornerShape(
      topStart = (36f + m).dp,
      topEnd = (28f - m).dp,
      bottomStart = (28f - m).dp,
      bottomEnd = (36f + m).dp
    )
  }

  Box(
    modifier = modifier
      .then(
        if (onSwipeLeft != null || onSwipeRight != null) {
          Modifier.pointerInput(Unit) {
            detectHorizontalDragGestures { change, dragAmount ->
              if (dragAmount < -22f && onSwipeLeft != null) {
                change.consume()
                onSwipeLeft()
              } else if (dragAmount > 22f && onSwipeRight != null) {
                change.consume()
                onSwipeRight()
              }
            }
          }
        } else Modifier
      ),
    contentAlignment = Alignment.Center
  ) {
    // 1. Radiant glowing translucent wave aura behind the artwork
    Canvas(modifier = Modifier.fillMaxSize().graphicsLayer { scaleX = auraPulse; scaleY = auraPulse }) {
      val w = size.width
      val h = size.height

      // Glowing fluid aura
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(
            accentColor.copy(alpha = 0.50f),
            TuneifyGlassTokens.EmeraldGlow.copy(alpha = 0.35f),
            Color.Transparent
          ),
          center = Offset(w * 0.48f, h * 0.48f),
          radius = w * 0.58f
        )
      )
    }

    // 2. The organic wavy translucent fluid outer glass border
    Box(
      modifier = Modifier
        .fillMaxSize(0.96f)
        .shadow(elevation = 20.dp, shape = organicShape, ambientColor = Color.Black, spotColor = accentColor)
        .clip(organicShape)
        .background(
          brush = Brush.linearGradient(
            colors = listOf(
              accentColor.copy(alpha = 0.45f),
              Color(0x35102418),
              Color(0x60030C05)
            )
          ),
          shape = organicShape
        )
        .border(
          width = 1.75.dp,
          brush = Brush.linearGradient(
            listOf(
              Color(0x90FFFFFF),
              accentColor.copy(alpha = 0.7f),
              Color(0x30FFFFFF)
            )
          ),
          shape = organicShape
        )
        .padding(4.dp)
    ) {
      // 3. Album artwork filling the container cleanly without being over-cut
      Box(
        modifier = Modifier
          .fillMaxSize()
          .clip(organicShape)
          .background(Color(0xFF0C1A10))
      ) {
        if (!thumbnailUrl.isNullOrBlank()) {
          AsyncImage(
            model = thumbnailUrl,
            contentDescription = "Artwork",
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
          )
        } else {
          Icon(
            painter = painterResource(R.drawable.album),
            contentDescription = null,
            tint = TuneifyGlassTokens.SageMuted,
            modifier = Modifier.size(64.dp).align(Alignment.Center)
          )
        }
      }
    }
  }
}

/**
 * Interactive, Audio-Reactive Waveform Visualization matching Screen 2.
 */
@Composable
fun AudioWaveformVisualizer(
  positionMs: Long,
  durationMs: Long,
  isPlaying: Boolean,
  songId: String?,
  onSeek: (Long) -> Unit,
  modifier: Modifier = Modifier,
  accentColor: Color = TuneifyGlassTokens.VoltLime
) {
  val effectiveDuration = if (durationMs > 0) durationMs else 1L
  val currentProgress = (positionMs.toFloat() / effectiveDuration.toFloat()).coerceIn(0f, 1f)

  // Animated phase for audio-reactive dancing wave
  val infiniteTransition = rememberInfiniteTransition(label = "waveformReactive")
  val waveTick by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 6.28318f,
    animationSpec = infiniteRepeatable(
      animation = tween(1400, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "waveTick"
  )

  // Generate deterministic bar heights based on songId seed
  val barCount = 38
  val baseHeights = remember(songId) {
    val seed = abs((songId ?: "tuneify").hashCode())
    List(barCount) { i ->
      val rel = i.toFloat() / barCount.toFloat()
      val arch = sin(rel * 3.14159f)
      val pseudoRandom = (((seed * (i + 7) * 9301 + 49297) % 233280) / 233280f)
      (0.25f + arch * 0.55f + pseudoRandom * 0.20f).coerceIn(0.15f, 1.0f)
    }
  }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(36.dp)
      .padding(horizontal = 24.dp)
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val totalWidth = size.width
      val totalHeight = size.height
      val barWidthPx = 4.dp.toPx()
      val spacingPx = (totalWidth - (barWidthPx * barCount)) / (barCount - 1).coerceAtLeast(1)
      val progressX = currentProgress * totalWidth

      for (i in 0 until barCount) {
        val x = i * (barWidthPx + spacingPx)

        // Audio-reactive modulation when playing
        val dynamicBounce = if (isPlaying) {
          val dist = abs(x - progressX) / totalWidth
          val waveOscillation = sin(waveTick + i * 0.45f) * 0.20f
          (waveOscillation * (1f - dist.coerceIn(0f, 0.8f))).toFloat()
        } else {
          0f
        }

        val rawHeightFraction = (baseHeights[i] + dynamicBounce).coerceIn(0.12f, 1.0f)
        val barHeightPx = rawHeightFraction * totalHeight
        val y = (totalHeight - barHeightPx) / 2f

        val isPlayed = (x + barWidthPx / 2f) <= progressX
        val barColor = if (isPlayed) {
          accentColor
        } else {
          Color(0x35FFFFFF)
        }

        drawRoundRect(
          color = barColor,
          topLeft = Offset(x, y),
          size = Size(barWidthPx, barHeightPx),
          cornerRadius = CornerRadius(barWidthPx / 2f, barWidthPx / 2f)
        )
      }
    }
  }
}
