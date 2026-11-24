package echo.music.iad1tya.ui.component.glass

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.music.innertube.models.SongItem
import com.music.innertube.models.PlaylistItem
import com.music.innertube.models.YTItem
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import echo.music.iad1tya.R

/**
 * Top header of Home screen matching reference image Screen 1:
 * - Tuneify logo with distinctive 5-bar waveform symbol in Volt lime.
 * - Notification bell button in frosted glass circle.
 * - Profile/avatar button in frosted glass circle.
 */
@Composable
fun TuneifyHeaderRow(
  onNotificationClick: () -> Unit,
  onProfileClick: () -> Unit,
  accountImageUrl: String?,
  modifier: Modifier = Modifier,
  accentColor: Color = TuneifyGlassTokens.VoltLime
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 12.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Logo with waveform symbol
    TuneifyWaveformLogo(accentColor = accentColor)

    // Right action buttons: Notification + Profile
    Row(
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Notification Button
      GlassButton(
        onClick = onNotificationClick,
        size = 40.dp
      ) {
        Icon(
          painter = painterResource(R.drawable.notification),
          contentDescription = "Notifications",
          tint = TuneifyGlassTokens.TextPrimary,
          modifier = Modifier.size(20.dp)
        )
      }

      // Profile Avatar Button
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .border(1.5.dp, TuneifyGlassTokens.GlassBorder, CircleShape)
          .background(Color(0xFF14261C))
          .clickable(onClick = onProfileClick),
        contentAlignment = Alignment.Center
      ) {
        if (!accountImageUrl.isNullOrBlank()) {
          AsyncImage(
            model = accountImageUrl,
            contentDescription = "Profile",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
        } else {
          Icon(
            painter = painterResource(R.drawable.account),
            contentDescription = "Profile",
            tint = TuneifyGlassTokens.SageLight,
            modifier = Modifier.size(22.dp)
          )
        }
      }
    }
  }
}

/**
 * Expressive Greeting Header:
 * "Good Evening," (regular/light)
 * "Vivek" (bold, prominent)
 */
@Composable
fun TuneifyGreeting(
  greeting: String,
  userName: String,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 6.dp)
  ) {
    Text(
      text = greeting,
      style = MaterialTheme.typography.titleMedium.copy(
        fontSize = 17.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = (-0.2).sp
      ),
      color = TuneifyGlassTokens.TextSecondary
    )
    Text(
      text = userName,
      style = MaterialTheme.typography.headlineMedium.copy(
        fontSize = 28.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.5).sp
      ),
      color = TuneifyGlassTokens.TextPrimary
    )
  }
}

/**
 * Hero Section: Large organic-shaped glass card with promotional artist artwork,
 * artist/playlist title, subtitle, prominent lime play button, and carousel indicators.
 */
/**
 * Spotlight item model for the Hero Carousel.
 */
data class SpotlightItem(
  val id: String,
  val title: String,
  val subtitle: String,
  val artworkUrl: String,
  val videoId: String,
  val artistId: String? = null
)

/**
 * Interactive Hero Spotlight Carousel featuring 3 promotional artists / soundtracks
 * with organic glass shapes, real artist photography, interactive indicator pills, and instant playback.
 */
@Composable
fun HeroSpotlightCarousel(
  spotlights: List<SpotlightItem>,
  onPlayClick: (SpotlightItem) -> Unit,
  onClick: (SpotlightItem) -> Unit,
  modifier: Modifier = Modifier,
  accentColor: Color = TuneifyGlassTokens.VoltLime
) {
  if (spotlights.isEmpty()) return

  val pagerState = rememberPagerState(pageCount = { spotlights.size })
  val coroutineScope = rememberCoroutineScope()

  // Auto-advance spotlight carousel every 4.2 seconds
  LaunchedEffect(pagerState, spotlights.size) {
    if (spotlights.size > 1) {
      while (true) {
        delay(4200)
        val nextPage = (pagerState.currentPage + 1) % spotlights.size
        pagerState.animateScrollToPage(nextPage)
      }
    }
  }

  // 3D subtle floating and organic liquid wave breathing animations
  val infiniteTransition = rememberInfiniteTransition(label = "heroFloatingTransition")
  val floatY by infiniteTransition.animateFloat(
    initialValue = -3.5f,
    targetValue = 3.5f,
    animationSpec = infiniteRepeatable(
      animation = tween(2800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "heroFloatY"
  )
  val floatTilt by infiniteTransition.animateFloat(
    initialValue = -0.5f,
    targetValue = 0.5f,
    animationSpec = infiniteRepeatable(
      animation = tween(3800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "heroFloatTilt"
  )

  // Material Expressive tilted asymmetric pill shape
  val shape = remember {
    RoundedCornerShape(
      topStart = 38.dp,
      bottomEnd = 38.dp,
      topEnd = 20.dp,
      bottomStart = 20.dp
    )
  }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(210.dp)
      .padding(horizontal = 20.dp, vertical = 4.dp)
      .graphicsLayer {
        translationY = floatY
        rotationZ = -1.4f + floatTilt
        cameraDistance = 14f * density
      }
      .shadow(
        elevation = 16.dp,
        shape = shape,
        clip = false,
        ambientColor = Color(0x99000000),
        spotColor = Color(0x75000000)
      )
      .clip(shape)
      .background(Color(0xFF06140A), shape)
      .border(
        width = 1.25.dp,
        brush = Brush.linearGradient(
          listOf(
            Color(0x70FFFFFF),
            Color(0x20FFFFFF),
            accentColor.copy(alpha = 0.45f)
          )
        ),
        shape = shape
      )
  ) {
    HorizontalPager(
      state = pagerState,
      modifier = Modifier.fillMaxSize()
    ) { page ->
      val currentItem = spotlights[page]

      val playInteraction = remember { MutableInteractionSource() }
      val isPlayPressed by playInteraction.collectIsPressedAsState()
      val playScale by animateFloatAsState(
        targetValue = if (isPlayPressed) 0.90f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 500f),
        label = "heroPlayScale_$page"
      )

      Box(
        modifier = Modifier
          .fillMaxSize()
          .clickable { onClick(currentItem) }
      ) {
        // Real promotional artist artwork filling card with face visible on right
        AsyncImage(
          model = currentItem.artworkUrl,
          contentDescription = currentItem.title,
          contentScale = ContentScale.Crop,
          alignment = Alignment.CenterEnd,
          modifier = Modifier.fillMaxSize()
        )

        // Frosted dark gradient overlay for text readability on left side while keeping artist face vivid on right
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.horizontalGradient(
                colors = listOf(
                  Color(0xF0030A05),
                  Color(0xA5030A05),
                  Color(0x30030A05),
                  Color.Transparent
                ),
                startX = 0f,
                endX = 420f
              )
            )
        )

        // Top specular glass sheen line
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(
              Brush.verticalGradient(
                listOf(Color(0x35FFFFFF), Color.Transparent)
              )
            )
        )

        // Content: Title, subtitle, prominent lime play button
        Row(
          modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 22.dp, vertical = 18.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column(
            modifier = Modifier.weight(1f).padding(end = 12.dp),
            verticalArrangement = Arrangement.Center
          ) {
            Text(
              text = currentItem.title,
              color = Color.White,
              fontSize = 21.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = (-0.4).sp,
              lineHeight = 26.sp,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            Text(
              text = currentItem.subtitle,
              color = TuneifyGlassTokens.TextSecondary,
              fontSize = 13.sp,
              fontWeight = FontWeight.Medium,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }

          // Prominent circular Play button in vibrant lime matching reference
          Box(
            modifier = Modifier
              .size(52.dp)
              .graphicsLayer {
                scaleX = playScale
                scaleY = playScale
              }
              .shadow(elevation = 12.dp, shape = CircleShape, ambientColor = accentColor, spotColor = accentColor)
              .clip(CircleShape)
              .background(accentColor)
              .border(1.dp, Color(0x66FFFFFF), CircleShape)
              .clickable(interactionSource = playInteraction, indication = null) {
                onPlayClick(currentItem)
              },
            contentAlignment = Alignment.Center
          ) {
            Icon(
              painter = painterResource(R.drawable.play),
              contentDescription = "Play",
              tint = TuneifyGlassTokens.OnVoltLime,
              modifier = Modifier.size(24.dp)
            )
          }
        }
      }
    }

    // Interactive Carousel indicator dots at bottom center
    Row(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 12.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      spotlights.forEachIndexed { idx, _ ->
        val isIndicatorActive = idx == pagerState.currentPage
        val indicatorWidth by animateDpAsState(
          targetValue = if (isIndicatorActive) 22.dp else 6.dp,
          animationSpec = spring<androidx.compose.ui.unit.Dp>(dampingRatio = 0.75f, stiffness = 400f),
          label = "heroDotWidth_$idx"
        )

        Box(
          modifier = Modifier
            .width(indicatorWidth)
            .height(5.dp)
            .clip(CircleShape)
            .background(if (isIndicatorActive) accentColor else Color(0x55FFFFFF))
            .clickable {
              coroutineScope.launch {
                pagerState.animateScrollToPage(idx)
              }
            }
        )
      }
    }
  }
}

/**
 * Large organic-shaped glass card with promotional artist artwork.
 */
@Composable
fun HeroArtistCard(
  title: String,
  subtitle: String,
  artworkUrl: String?,
  onPlayClick: () -> Unit,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  accentColor: Color = TuneifyGlassTokens.VoltLime
) {
  val shape = remember { HeroOrganicShape() }

  val playInteraction = remember { MutableInteractionSource() }
  val isPlayPressed by playInteraction.collectIsPressedAsState()
  val playScale by animateFloatAsState(
    targetValue = if (isPlayPressed) 0.90f else 1f,
    animationSpec = spring(dampingRatio = 0.6f, stiffness = 500f),
    label = "heroPlayScale"
  )

  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(215.dp)
      .padding(horizontal = 20.dp, vertical = 6.dp)
      .shadow(
        elevation = 12.dp,
        shape = shape,
        clip = false,
        ambientColor = Color(0x90000000),
        spotColor = Color(0x70000000)
      )
      .clip(shape)
      .background(Color(0xFF0F2618), shape)
      .border(
        width = 1.25.dp,
        brush = Brush.linearGradient(
          listOf(
            Color(0x80FFFFFF),
            Color(0x20FFFFFF),
            accentColor.copy(alpha = 0.50f)
          )
        ),
        shape = shape
      )
      .clickable(onClick = onClick)
  ) {
    // Artist promotional artwork
    if (!artworkUrl.isNullOrBlank()) {
      AsyncImage(
        model = artworkUrl,
        contentDescription = title,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
      )
    } else {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.linearGradient(
              listOf(Color(0xFF1E462E), Color(0xFF0A1C12))
            )
          )
      )
    }

    // Frosted dark gradient overlay for text readability
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.horizontalGradient(
            colors = listOf(
              Color(0xF007140B),
              Color(0xBD07140B),
              Color(0x2507140B)
            )
          )
        )
    )

    // Top specular glass sheen line
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .background(
          Brush.verticalGradient(
            listOf(Color(0x35FFFFFF), Color.Transparent)
          )
        )
    )

    // Content: Title, subtitle, prominent lime play button
    Row(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 22.dp, vertical = 20.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column(
        modifier = Modifier.weight(1f).padding(end = 12.dp),
        verticalArrangement = Arrangement.Center
      ) {
        Text(
          text = title,
          color = Color.White,
          fontSize = 22.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = (-0.5).sp,
          lineHeight = 27.sp,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(6.dp))
        Text(
          text = subtitle,
          color = TuneifyGlassTokens.TextSecondary,
          fontSize = 13.sp,
          fontWeight = FontWeight.Medium,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }

      // Prominent circular Play button in vibrant lime matching reference
      Box(
        modifier = Modifier
          .size(52.dp)
          .graphicsLayer {
            scaleX = playScale
            scaleY = playScale
          }
          .shadow(elevation = 10.dp, shape = CircleShape, ambientColor = accentColor, spotColor = accentColor)
          .clip(CircleShape)
          .background(accentColor)
          .clickable(interactionSource = playInteraction, indication = null, onClick = onPlayClick),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          painter = painterResource(R.drawable.play),
          contentDescription = "Play",
          tint = TuneifyGlassTokens.OnVoltLime,
          modifier = Modifier.size(24.dp)
        )
      }
    }

    // Carousel indicator dots at bottom center
    Row(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 12.dp),
      horizontalArrangement = Arrangement.spacedBy(6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .width(18.dp)
          .height(4.dp)
          .clip(RoundedCornerShape(2.dp))
          .background(accentColor)
      )
      Box(
        modifier = Modifier
          .size(4.dp)
          .clip(CircleShape)
          .background(Color(0x80FFFFFF))
      )
      Box(
        modifier = Modifier
          .size(4.dp)
          .clip(CircleShape)
          .background(Color(0x40FFFFFF))
      )
    }
  }
}

/**
 * Music Category Selector Pills:
 * - Music (active lime pill by default)
 * - Top Charts (opens real Top Charts)
 * - Trending (opens real Explore / Trending)
 */
@Composable
fun CategorySelectorRow(
  selectedCategory: String,
  onCategorySelected: (String) -> Unit,
  modifier: Modifier = Modifier,
  accentColor: Color = TuneifyGlassTokens.VoltLime
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 10.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Music pill
    GlassPill(
      text = "Music",
      selected = selectedCategory == "Music",
      onClick = { onCategorySelected("Music") },
      accentColor = accentColor,
      icon = {
        Row(
          horizontalArrangement = Arrangement.spacedBy(1.5.dp),
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.height(12.dp)
        ) {
          listOf(6.dp, 12.dp, 8.dp, 10.dp).forEach { h ->
            Box(
              modifier = Modifier
                .width(2.dp)
                .height(h)
                .clip(RoundedCornerShape(1.dp))
                .background(if (selectedCategory == "Music") TuneifyGlassTokens.OnVoltLime else Color.White)
            )
          }
        }
      }
    )

    // Top Charts pill (real working YouTube Music charts)
    GlassPill(
      text = "Top Charts",
      selected = selectedCategory == "Top Charts",
      onClick = { onCategorySelected("Top Charts") },
      accentColor = accentColor,
      icon = {
        Icon(
          painter = painterResource(R.drawable.ic_chart),
          contentDescription = null,
          modifier = Modifier.size(15.dp)
        )
      }
    )

    // Trending pill (real working YouTube Music trending)
    GlassPill(
      text = "Trending",
      selected = selectedCategory == "Trending",
      onClick = { onCategorySelected("Trending") },
      accentColor = accentColor,
      icon = {
        Icon(
          painter = painterResource(R.drawable.trending_up),
          contentDescription = null,
          modifier = Modifier.size(15.dp)
        )
      }
    )
  }
}

/**
 * Made For You card with distinct stylized mesh/gradient background and optional artwork.
 */
@Composable
fun MadeForYouCard(
  title: String,
  gradientColors: List<Color>,
  onClick: () -> Unit,
  onPlayClick: () -> Unit,
  modifier: Modifier = Modifier,
  artworkUrl: String? = null,
  cardWidth: androidx.compose.ui.unit.Dp = 160.dp,
  cardHeight: androidx.compose.ui.unit.Dp = 105.dp
) {
  val shape = RoundedCornerShape(20.dp)

  Box(
    modifier = modifier
      .size(width = cardWidth, height = cardHeight)
      .shadow(elevation = 8.dp, shape = shape, clip = false, ambientColor = Color.Black)
      .clip(shape)
      .background(Brush.linearGradient(gradientColors))
      .border(
        width = 1.25.dp,
        brush = Brush.linearGradient(
          listOf(Color(0x70FFFFFF), Color(0x18FFFFFF), Color(0x40C6F53D))
        ),
        shape = shape
      )
      .clickable(onClick = onClick)
  ) {
    if (!artworkUrl.isNullOrBlank()) {
      AsyncImage(
        model = artworkUrl,
        contentDescription = title,
        contentScale = ContentScale.Crop,
        modifier = Modifier
          .fillMaxSize()
          .graphicsLayer { alpha = 0.50f }
      )
    }

    // Frosted glass overlay
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            listOf(Color(0x25FFFFFF), Color(0x40000000), Color(0x90000000))
          )
        )
    )

    Row(
      modifier = Modifier
        .fillMaxSize()
        .padding(14.dp),
      verticalAlignment = Alignment.Bottom,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(
        text = title,
        color = Color.White,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        maxLines = 2,
        modifier = Modifier.weight(1f).padding(end = 6.dp)
      )

      Box(
        modifier = Modifier
          .size(34.dp)
          .clip(CircleShape)
          .background(Color(0x66FFFFFF))
          .border(1.dp, Color(0x88FFFFFF), CircleShape)
          .clickable(onClick = onPlayClick),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          painter = painterResource(R.drawable.play),
          contentDescription = "Play",
          tint = Color.White,
          modifier = Modifier.size(16.dp)
        )
      }
    }
  }
}

/**
 * Featured Music Banner for Explore Screen (e.g. "Bollywood Top Hits").
 */
@Composable
fun ExploreFeaturedBanner(
  title: String,
  thumbnailUrl: String?,
  onPlayClick: () -> Unit,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val shape = RoundedCornerShape(26.dp)
  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(185.dp)
      .padding(horizontal = 20.dp, vertical = 6.dp)
      .shadow(
        elevation = 12.dp,
        shape = shape,
        clip = false,
        ambientColor = Color(0x90000000),
        spotColor = Color(0x60000000)
      )
      .clip(shape)
      .background(
        Brush.linearGradient(
          listOf(
            Color(0xFF5A391A), // Warm amber bronze from reference Screen 3
            Color(0xFF382312),
            Color(0xFF141F16)
          )
        ),
        shape
      )
      .border(
        width = 1.25.dp,
        brush = Brush.linearGradient(
          listOf(
            Color(0x80FFFFFF),
            Color(0x20FFFFFF),
            TuneifyGlassTokens.GlassBorderLime
          )
        ),
        shape = shape
      )
      .clickable(onClick = onClick)
  ) {
    if (!thumbnailUrl.isNullOrBlank()) {
      AsyncImage(
        model = thumbnailUrl,
        contentDescription = title,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
      )
    }

    // Warm atmospheric amber-to-dark gradient overlay
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.horizontalGradient(
            listOf(
              Color(0xEA1A1208),
              Color(0xB51A1208),
              Color(0x251A1208)
            )
          )
        )
    )

    // Top specular glass highlight
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(45.dp)
        .background(
          Brush.verticalGradient(
            listOf(Color(0x30FFFFFF), Color.Transparent)
          )
        )
    )

    Row(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 22.dp, vertical = 20.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(
        text = title,
        color = Color.White,
        fontSize = 25.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.5).sp,
        lineHeight = 30.sp,
        maxLines = 2,
        modifier = Modifier.weight(1f).padding(end = 12.dp)
      )

      Box(
        modifier = Modifier
          .size(48.dp)
          .shadow(elevation = 6.dp, shape = CircleShape, ambientColor = Color.Black)
          .clip(CircleShape)
          .background(Color(0x55FFFFFF))
          .border(1.25.dp, Color(0x90FFFFFF), CircleShape)
          .clickable(onClick = onPlayClick),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          painter = painterResource(R.drawable.play),
          contentDescription = "Play",
          tint = Color.White,
          modifier = Modifier.size(22.dp)
        )
      }
    }
  }
}

/**
 * 1. Tuneify 4-Item Stack Carousel (The YouTube Music "Song Mixes" / "Heard in Shorts" / "Trending songs" Layout)
 * Displays a horizontal carousel where each column contains 4 tracks stacked vertically.
 */
@Composable
fun TuneifySongColumnGridCarousel(
  songs: List<SongItem>,
  onSongClick: (SongItem) -> Unit,
  modifier: Modifier = Modifier,
  accentColor: Color = TuneifyGlassTokens.VoltLime
) {
  if (songs.isEmpty()) return
  val columns = remember(songs) { songs.chunked(4) }

  LazyRow(
    contentPadding = PaddingValues(horizontal = 20.dp),
    horizontalArrangement = Arrangement.spacedBy(16.dp),
    modifier = modifier.fillMaxWidth()
  ) {
    items(columns) { columnSongs ->
      Column(
        modifier = Modifier.width(310.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        columnSongs.forEach { song ->
          val interactionSource = remember { MutableInteractionSource() }
          val isPressed by interactionSource.collectIsPressedAsState()
          val scale by animateFloatAsState(
            targetValue = if (isPressed) 0.98f else 1f,
            animationSpec = spring(dampingRatio = 0.7f, stiffness = 500f),
            label = "songRowScale"
          )

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .graphicsLayer { scaleX = scale; scaleY = scale }
              .clip(RoundedCornerShape(14.dp))
              .background(if (isPressed) Color(0x3514261B) else Color(0x18102015))
              .border(
                0.75.dp,
                if (isPressed) accentColor.copy(alpha = 0.4f) else Color(0x15FFFFFF),
                RoundedCornerShape(14.dp)
              )
              .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { onSongClick(song) }
              )
              .padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // High quality album art
            AsyncImage(
              model = song.thumbnail,
              contentDescription = song.title,
              contentScale = ContentScale.Crop,
              modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(10.dp))
                .border(0.75.dp, Color(0x25FFFFFF), RoundedCornerShape(10.dp))
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = song.title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Spacer(Modifier.height(3.dp))
              val artistName = song.artists.joinToString(", ") { it.name }
              val subtext = song.album?.name?.let { "$artistName • $it" } ?: artistName
              Text(
                text = subtext,
                color = TuneifyGlassTokens.TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }

            Spacer(Modifier.width(8.dp))

            // Play action indicator
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0x25FFFFFF)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                painter = painterResource(R.drawable.play),
                contentDescription = "Play",
                tint = accentColor,
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }
      }
    }
  }
}

/**
 * 2. Tuneify Speed Dial Row (The YouTube Music "Speed Dial" compact cards)
 * Compact square cards with full bleed poster and dark bottom gradient title overlay.
 */
@Composable
fun TuneifySpeedDialRow(
  items: List<SongItem>,
  onSongClick: (SongItem) -> Unit,
  modifier: Modifier = Modifier
) {
  if (items.isEmpty()) return

  LazyRow(
    contentPadding = PaddingValues(horizontal = 20.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    modifier = modifier.fillMaxWidth()
  ) {
    items(items) { song ->
      val interaction = remember { MutableInteractionSource() }
      val isPressed by interaction.collectIsPressedAsState()
      val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 500f),
        label = "speedDialScale"
      )

      Box(
        modifier = Modifier
          .size(118.dp)
          .graphicsLayer { scaleX = scale; scaleY = scale }
          .shadow(6.dp, RoundedCornerShape(14.dp), ambientColor = Color(0x60000000))
          .clip(RoundedCornerShape(14.dp))
          .background(Color(0xFF07140B))
          .border(0.75.dp, Color(0x25FFFFFF), RoundedCornerShape(14.dp))
          .clickable(interactionSource = interaction, indication = null, onClick = { onSongClick(song) }),
        contentAlignment = Alignment.BottomStart
      ) {
        AsyncImage(
          model = song.thumbnail,
          contentDescription = song.title,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )

        // Dark gradient scrim at the bottom for crisp legibility
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(
              Brush.verticalGradient(
                listOf(Color.Transparent, Color(0xCC030804), Color(0xF5030804))
              )
            )
        )

        Text(
          text = song.title,
          color = Color.White,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
        )
      }
    }
  }
}

/**
 * 3. Tuneify Square Album Carousel (The YouTube Music "New Releases" / "Forgotten favourites" big posters)
 * Large 150dp square cards with 16dp rounded corners + bold title and subtitle below.
 */
@Composable
fun TuneifySquareAlbumCarousel(
  items: List<YTItem>,
  onItemClick: (YTItem) -> Unit,
  modifier: Modifier = Modifier,
  accentColor: Color = TuneifyGlassTokens.VoltLime
) {
  if (items.isEmpty()) return

  LazyRow(
    contentPadding = PaddingValues(horizontal = 20.dp),
    horizontalArrangement = Arrangement.spacedBy(16.dp),
    modifier = modifier.fillMaxWidth()
  ) {
    items(items) { item ->
      val interaction = remember { MutableInteractionSource() }
      val isPressed by interaction.collectIsPressedAsState()
      val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 500f),
        label = "squareAlbumScale"
      )

      Column(
        modifier = Modifier
          .width(152.dp)
          .graphicsLayer { scaleX = scale; scaleY = scale }
          .clickable(interactionSource = interaction, indication = null, onClick = { onItemClick(item) })
      ) {
        Box(
          modifier = Modifier
            .size(152.dp)
            .shadow(10.dp, RoundedCornerShape(16.dp), ambientColor = Color(0x60000000))
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF07140B))
            .border(0.75.dp, Color(0x25FFFFFF), RoundedCornerShape(16.dp)),
          contentAlignment = Alignment.BottomEnd
        ) {
          AsyncImage(
            model = item.thumbnail,
            contentDescription = item.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )

          // Play pill badge in bottom right corner
          Box(
            modifier = Modifier
              .padding(8.dp)
              .size(36.dp)
              .shadow(6.dp, CircleShape, ambientColor = Color.Black)
              .clip(CircleShape)
              .background(Color(0xD908180E))
              .border(0.75.dp, accentColor.copy(alpha = 0.6f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              painter = painterResource(R.drawable.play),
              contentDescription = "Play",
              tint = accentColor,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Spacer(Modifier.height(8.dp))

        Text(
          text = item.title,
          color = Color.White,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(Modifier.height(2.dp))

        val subtitle = when (item) {
          is SongItem -> item.artists.joinToString(", ") { it.name }
          else -> item.explicit.let { "Popular" }
        }
        Text(
          text = subtitle,
          color = TuneifyGlassTokens.TextSecondary,
          fontSize = 12.sp,
          fontWeight = FontWeight.Normal,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }
}

/**
 * 4. Tuneify Community Collage Card (The YouTube Music "From the community" 2x2 collage playlists)
 */
@Composable
fun TuneifyCommunityCollageRow(
  playlists: List<PlaylistItem>,
  onPlaylistClick: (PlaylistItem) -> Unit,
  modifier: Modifier = Modifier
) {
  if (playlists.isEmpty()) return

  LazyRow(
    contentPadding = PaddingValues(horizontal = 20.dp),
    horizontalArrangement = Arrangement.spacedBy(16.dp),
    modifier = modifier.fillMaxWidth()
  ) {
    items(playlists) { playlist ->
      val interaction = remember { MutableInteractionSource() }
      val isPressed by interaction.collectIsPressedAsState()
      val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 500f),
        label = "commCollageScale"
      )

      Column(
        modifier = Modifier
          .width(150.dp)
          .graphicsLayer { scaleX = scale; scaleY = scale }
          .clickable(interactionSource = interaction, indication = null, onClick = { onPlaylistClick(playlist) })
      ) {
        Box(
          modifier = Modifier
            .size(150.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp), ambientColor = Color(0x60000000))
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF07140B))
            .border(0.75.dp, Color(0x25FFFFFF), RoundedCornerShape(16.dp))
        ) {
          AsyncImage(
            model = playlist.thumbnail,
            contentDescription = playlist.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
        }

        Spacer(Modifier.height(8.dp))

        Text(
          text = playlist.title,
          color = Color.White,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(Modifier.height(2.dp))

        val info = playlist.author?.name?.let { "$it • Community" } ?: "Community Mix"
        Text(
          text = info,
          color = TuneifyGlassTokens.TextSecondary,
          fontSize = 12.sp,
          fontWeight = FontWeight.Normal,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }
}

/**
 * 5. Tuneify Featured Playlist Card (The YouTube Music "Trending Community Playlists" Layout)
 * A rich card with 2x2 header, title, view/song count, prominent Play button, and 3 embedded preview tracks.
 */
@Composable
fun TuneifyFeaturedPlaylistCard(
  title: String,
  subtitle: String,
  thumbnailUrl: String?,
  previewSongs: List<SongItem>,
  onPlayClick: () -> Unit,
  onCardClick: () -> Unit,
  onSongClick: (SongItem) -> Unit,
  modifier: Modifier = Modifier,
  accentColor: Color = TuneifyGlassTokens.VoltLime
) {
  val cardShape = RoundedCornerShape(22.dp)

  Box(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 6.dp)
      .shadow(12.dp, cardShape, ambientColor = Color(0x80000000))
      .clip(cardShape)
      .background(Color(0x3514261B))
      .border(
        width = 1.dp,
        brush = Brush.linearGradient(
          listOf(
            Color(0x50FFFFFF),
            Color(0x15FFFFFF),
            accentColor.copy(alpha = 0.35f)
          )
        ),
        shape = cardShape
      )
      .clickable(onClick = onCardClick)
      .padding(18.dp)
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      // Top header: artwork on left, title & play button on right
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        AsyncImage(
          model = thumbnailUrl,
          contentDescription = title,
          contentScale = ContentScale.Crop,
          modifier = Modifier
            .size(90.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(0.75.dp, Color(0x35FFFFFF), RoundedCornerShape(14.dp))
        )

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = title,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Spacer(Modifier.height(4.dp))
          Text(
            text = subtitle,
            color = TuneifyGlassTokens.TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        // Circular Volt Lime Play Button
        Box(
          modifier = Modifier
            .size(46.dp)
            .shadow(8.dp, CircleShape, ambientColor = accentColor, spotColor = accentColor)
            .clip(CircleShape)
            .background(accentColor)
            .clickable(onClick = onPlayClick),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            painter = painterResource(R.drawable.play),
            contentDescription = "Play",
            tint = TuneifyGlassTokens.OnVoltLime,
            modifier = Modifier.size(22.dp)
          )
        }
      }

      if (previewSongs.isNotEmpty()) {
        Spacer(Modifier.height(14.dp))

        // Embedded preview song rows
        previewSongs.take(3).forEach { song ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0x1F0A1810))
              .clickable { onSongClick(song) }
              .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            AsyncImage(
              model = song.thumbnail,
              contentDescription = song.title,
              contentScale = ContentScale.Crop,
              modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(8.dp))
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = song.title,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Text(
                text = song.artists.joinToString(", ") { it.name },
                color = TuneifyGlassTokens.TextSecondary,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }
        }
      }
    }
  }
}

/**
 * 6. Tuneify Promo Banner Card (The YouTube Music "Discover..." banner card)
 * Wide frosted glass card with headline + circular action arrow on left, big artwork on right.
 */
@Composable
fun TuneifyPromoBannerCard(
  title: String,
  artworkUrl: String?,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  accentColor: Color = TuneifyGlassTokens.VoltLime
) {
  val shape = RoundedCornerShape(20.dp)

  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(138.dp)
      .padding(horizontal = 20.dp, vertical = 4.dp)
      .shadow(10.dp, shape, ambientColor = Color(0x60000000))
      .clip(shape)
      .background(Color(0x3514261B))
      .border(0.75.dp, Color(0x25FFFFFF), shape)
      .clickable(onClick = onClick)
  ) {
    Row(
      modifier = Modifier.fillMaxSize(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(
        modifier = Modifier
          .weight(1.1f)
          .padding(start = 20.dp, end = 12.dp, top = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = title,
          color = Color.White,
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = (-0.3).sp,
          lineHeight = 22.sp,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )

        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color(0x30FFFFFF))
            .border(1.dp, Color(0x60FFFFFF), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            painter = painterResource(R.drawable.chevron_right_px),
            contentDescription = "Explore",
            tint = accentColor,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      if (!artworkUrl.isNullOrBlank()) {
        AsyncImage(
          model = artworkUrl,
          contentDescription = title,
          contentScale = ContentScale.Crop,
          modifier = Modifier
            .weight(0.9f)
            .fillMaxSize()
            .clip(RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp))
        )
      }
    }
  }
}
