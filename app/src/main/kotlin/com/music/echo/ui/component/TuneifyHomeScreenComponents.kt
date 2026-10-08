package echo.music.iad1tya.ui.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.music.innertube.models.SongItem
import echo.music.iad1tya.R
import echo.music.iad1tya.ui.theme.AeonikFontFamily
import echo.music.iad1tya.ui.theme.TuneifyModernFontFamily
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI
import echo.music.iad1tya.ui.utils.resize
import echo.music.iad1tya.utils.makeTimeString
import java.time.LocalTime
import kotlin.random.Random

private val musicSignatures = listOf(
    "Where words fail, music speaks.",
    "Your soundtrack for every moment.",
    "Immerse in pure acoustic clarity.",
    "Discover rhythms crafted for you.",
    "Feel every frequency and beat.",
    "Music is the shorthand of emotion.",
    "Your personal audio universe.",
    "Turn up the melody of life."
)

/**
 * Top-left atmospheric feather light spreading effect with Good Morning / Evening header.
 * Radial feather out glow in Obsidian Purple originating from top-left, with zero harsh rectangular boundaries.
 * Uses Charger Pro for primary greeting and Google Sans for quote subtitle.
 */
@Composable
fun AtmosphericFeatherLightHeader(
    userName: String? = null,
    modifier: Modifier = Modifier
) {
    val hour = LocalTime.now().hour
    val timeGreeting = when (hour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        in 17..20 -> "Good evening"
        else -> "Good night"
    }

    val displayName = if (!userName.isNullOrBlank() && userName != "Guest") userName else "Guest"

    val signatureQuote = remember {
        musicSignatures[Random.nextInt(musicSignatures.size)]
    }

    val infiniteTransition = rememberInfiniteTransition(label = "feather_light_anim")
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 0.dp, bottom = 4.dp)
    ) {
        Canvas(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer { alpha = 0.90f }
        ) {
            val radius = size.width * 1.25f * glowPulse
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFA855F7).copy(alpha = 0.38f),
                        Color(0xFF7B2CBF).copy(alpha = 0.22f),
                        Color(0xFF3C096C).copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    center = Offset(0f, 0f),
                    radius = radius
                ),
                radius = radius,
                center = Offset(0f, 0f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Text(
                text = timeGreeting,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 15.sp,
                    lineHeight = 20.sp
                ),
                fontFamily = echo.music.iad1tya.ui.theme.GoogleSansFontFamily,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFDDD6FE).copy(alpha = 0.85f),
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = displayName,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 28.sp,
                    lineHeight = 34.sp,
                    letterSpacing = (-0.3).sp
                ),
                fontFamily = echo.music.iad1tya.ui.theme.GoogleSansFontFamily,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = signatureQuote,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 13.5.sp,
                    lineHeight = 18.sp
                ),
                fontFamily = echo.music.iad1tya.ui.theme.GoogleSansFontFamily,
                fontWeight = FontWeight.Normal,
                color = Color(0xFFC4B8DC).copy(alpha = 0.75f),
                letterSpacing = 0.1.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Material Expressive 3 Spotlight Hero Carousel with large rounded rectangle posters matching Home.png and Image 2.
 * Uses HorizontalPager with 24dp padding and 14dp spacing so every card has 100% full rounded corners and 360-degree borders.
 */
@Composable
fun TuneifySpotlightHeroCarousel(
    items: List<SongItem>,
    onItemClick: (SongItem) -> Unit,
    modifier: Modifier = Modifier
) {
    if (items.isEmpty()) return
    val count = items.size.coerceAtMost(8)
    val pagerState = rememberPagerState(pageCount = { count })

    HorizontalPager(
        state = pagerState,
        contentPadding = PaddingValues(horizontal = 24.dp),
        pageSpacing = 14.dp,
        modifier = modifier
            .fillMaxWidth()
            .height(245.dp)
    ) { page ->
        val song = items[page]
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(26.dp))
                .background(Color(0xFF171324))
                .border(
                    width = 1.dp,
                    color = Color(0xFFE0AAFF).copy(alpha = 0.35f),
                    shape = RoundedCornerShape(26.dp)
                )
                .clickable { onItemClick(song) }
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(song.thumbnail?.resize(800, 800))
                    .crossfade(true)
                    .build(),
                contentDescription = song.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Multi-stop cinematic dark purple gradient overlay matching Home.png
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xFF09070F).copy(alpha = 0.35f),
                                Color(0xFF09070F).copy(alpha = 0.94f)
                            ),
                            startY = 100f
                        )
                    )
            )

            // Song Info Overlay at bottom
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f).padding(end = 10.dp)
                ) {
                    Text(
                        text = song.title,
                        fontFamily = echo.music.iad1tya.ui.theme.GoogleSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        lineHeight = 23.sp,
                        color = Color(0xFFF7F4FD),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = song.artists.joinToString(", ") { it.name },
                        fontFamily = echo.music.iad1tya.ui.theme.GoogleSansFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.5.sp,
                        color = Color(0xFFD8CCF0),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0AAFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.play),
                        contentDescription = "Play",
                        tint = Color(0xFF0F0620),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * Material Expressive "Trending" Hero Card.
 * Wide aspect, generous rounded corners, dark gradient overlay, and floating play action.
 */
@Composable
fun TuneifyHeroCard(
    title: String,
    subtitle: String,
    imageUrl: String?,
    tag: String = "TRENDING",
    isPlaying: Boolean = false,
    isActive: Boolean = false,
    onClick: () -> Unit = {},
    onPlayClick: () -> Unit = onClick,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(205.dp)
            .clip(RoundedCornerShape(24.dp))
            .border(
                width = 1.dp,
                color = if (isActive) Color.White.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.08f),
                shape = RoundedCornerShape(24.dp)
            )
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Sophisticated multi-stop dark gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.20f),
                            Color.Transparent,
                            Color(0xFF080C14).copy(alpha = 0.70f),
                            Color(0xFF060910).copy(alpha = 0.94f)
                        ),
                        startY = 0f
                    )
                )
        )

        // Bottom Info Row & Floating Play Button
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f).padding(end = 12.dp)
            ) {
                Text(
                    text = tag,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    letterSpacing = 0.2.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = title,
                    fontFamily = AeonikFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.5.sp,
                    color = Color.White.copy(alpha = 0.70f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Play / Pause round action button
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .shadow(elevation = 6.dp, shape = CircleShape)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable(onClick = onPlayClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(
                        if (isActive && isPlaying) R.drawable.pause else R.drawable.play
                    ),
                    contentDescription = "Play",
                    tint = Color.Black,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

/**
 * Material Expressive Trending Music Carousel.
 * Swipeable hero cards of trending songs directly below category chips on Home screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TuneifyTrendingCarousel(
    songs: List<SongItem>,
    activeMediaId: String?,
    isPlaying: Boolean,
    onSongClick: (SongItem) -> Unit,
    modifier: Modifier = Modifier
) {
    if (songs.isEmpty()) return
    val carouselState = rememberCarouselState(itemCount = { songs.size })

    Column(modifier = modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Trending Now",
                fontFamily = AeonikFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp,
                color = Color.White
            )
        }

        HorizontalMultiBrowseCarousel(
            state = carouselState,
            preferredItemWidth = 295.dp,
            itemSpacing = 12.dp,
            modifier = Modifier.fillMaxWidth().height(178.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) { i ->
            val song = songs[i]
            val isActive = song.id == activeMediaId

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(22.dp))
                    .border(
                        width = 1.dp,
                        color = if (isActive) Color(0xFF64B5F6).copy(alpha = 0.5f) else Color.White.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(22.dp)
                    )
                    .clickable { onSongClick(song) }
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(song.thumbnail?.resize(800, 800))
                        .crossfade(true)
                        .build(),
                    contentDescription = song.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0xFF060910).copy(alpha = 0.50f),
                                    Color(0xFF04060A).copy(alpha = 0.95f)
                                ),
                                startY = 100f
                            )
                        )
                )

                // Top Tag
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .background(
                            color = Color(0xFF0A0F1D).copy(alpha = 0.85f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .border(
                            width = 0.5.dp,
                            color = Color.White.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "TRENDING",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE2E8F0),
                        letterSpacing = 0.8.sp
                    )
                }

                // Bottom Info
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    ) {
                        Text(
                            text = song.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = song.artists.joinToString { it.name },
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF94A3B8),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(
                                if (isActive && isPlaying) R.drawable.pause else R.drawable.play
                            ),
                            contentDescription = "Play",
                            tint = Color.Black,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * YouTube Music-Style Compact Detailed Track Row.
 * Left thumbnail (52dp, 8dp radius), Title, [E] Explicit + Subtitle details, 3-dots on right.
 */
@Composable
fun TuneifyCompactTrackRow(
    title: String,
    subtitle: String,
    thumbnailUrl: String?,
    isActive: Boolean = false,
    isPlaying: Boolean = false,
    isExplicit: Boolean = false,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    onMoreClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isActive) Color(0xFF2C1C4D).copy(alpha = 0.6f) else Color.Transparent
            )
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF24173D))
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(thumbnailUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            if (isActive && isPlaying) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.45f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.volume_up),
                        contentDescription = null,
                        tint = Color(0xFFE0AAFF),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Titles
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isActive) Color(0xFFE0AAFF) else Color(0xFFF3EEFA),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isExplicit) {
                    Box(
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .background(Color(0xFF453566), RoundedCornerShape(3.dp))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "E",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD4BBFC)
                        )
                    }
                }
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFAFA5C2),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // More Actions (3 dots)
        IconButton(
            onClick = onMoreClick,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.more_vert),
                contentDescription = "Options",
                tint = Color(0xFFAFA5C2),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/**
 * YouTube Music signature 4-track column carousel:
 * Displays songs chunked into columns of 4 tracks each inside a horizontal scrolling row.
 */
@Composable
fun TuneifyYTMusicColumnCarousel(
    songs: List<SongItem>,
    activeMediaId: String?,
    isPlaying: Boolean,
    onSongClick: (SongItem) -> Unit,
    onSongLongClick: (SongItem) -> Unit,
    onSongMoreClick: (SongItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val columnWidth = (configuration.screenWidthDp.dp - 36.dp).coerceAtMost(340.dp)
    val columns = remember(songs) { songs.chunked(4) }

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        items(columns, key = { col -> col.firstOrNull()?.id ?: col.hashCode() }) { columnSongs ->
            Box(
                modifier = Modifier
                    .width(columnWidth)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF171324))
                    .border(
                        width = 1.dp,
                        color = Color(0xFF38235E).copy(alpha = 0.35f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(vertical = 6.dp, horizontal = 4.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    columnSongs.forEach { song ->
                        val isActive = song.id == activeMediaId
                        val durationStr = song.duration?.let { " • ${makeTimeString(it * 1000L)}" } ?: ""
                        TuneifyCompactTrackRow(
                            title = song.title,
                            subtitle = song.artists.joinToString { it.name } + durationStr,
                            thumbnailUrl = song.thumbnail?.resize(544, 544),
                            isActive = isActive,
                            isPlaying = isPlaying,
                            isExplicit = song.explicit,
                            onClick = { onSongClick(song) },
                            onLongClick = { onSongLongClick(song) },
                            onMoreClick = { onSongMoreClick(song) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}



/**
 * 7-lobed Material Expressive Cookie shape for artists/singers.
 * Creates a distinct, organic scalloped flower/cookie outline.
 */
class CookieShape7 : Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path()
        val numLobes = 7
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val maxRadius = minOf(centerX, centerY)
        val minRadius = maxRadius * 0.82f
        val points = 70

        for (i in 0..points) {
            val angle = (i.toFloat() / points) * 2f * PI.toFloat() - (PI.toFloat() / 2f)
            val lobeFactor = (cos(numLobes * angle) + 1f) / 2f
            val r = minRadius + (maxRadius - minRadius) * lobeFactor
            val x = centerX + r * cos(angle)
            val y = centerY + r * sin(angle)
            if (i == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        path.close()
        return Outline.Generic(path)
    }
}

val TuneifyCookieShape: Shape = CookieShape7()

/**
 * Material Expressive shapes for artist / singer cards:
 * Enforces the signature 7-lobed cookie shape across all artist cards as requested.
 */
val MaterialExpressiveShapes: List<Shape> = listOf(
    TuneifyCookieShape
)

/**
 * Material Expressive Singer / Artist Card.
 * Uses distinct geometric expressive shapes.
 */
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun TuneifyArtistExpressiveCard(
    name: String,
    thumbnailUrl: String?,
    shape: Shape = TuneifyCookieShape,
    onClick: () -> Unit = {},
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(104.dp)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(92.dp)
                .clip(shape)
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.08f),
                    shape = shape
                )
                .background(Color(0xFF0F172A))
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(thumbnailUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = name,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFF1F5F9),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = "Artist",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF64748B)
        )
    }
}

/**
 * Rich subtle obsidian gradients for the 2x8 genres grid.
 */
val CuratedGenrePosters = emptyList<String>()

val CuratedGenreGradients = listOf(
    listOf(Color(0xFF1E3A8A), Color(0xFF090D16)), // Sapphire navy
    listOf(Color(0xFF4C1D95), Color(0xFF090D16)), // Deep violet
    listOf(Color(0xFF065F46), Color(0xFF090D16)), // Emerald teal
    listOf(Color(0xFF881337), Color(0xFF090D16)), // Burgundy wine
    listOf(Color(0xFF0E7490), Color(0xFF090D16)), // Deep cyan
    listOf(Color(0xFF334155), Color(0xFF090D16)), // Slate charcoal
    listOf(Color(0xFF854D0E), Color(0xFF090D16)), // Amber bronze
    listOf(Color(0xFF374151), Color(0xFF090D16)), // Cool graphite
    listOf(Color(0xFF701A75), Color(0xFF090D16)), // Deep fuchsia
    listOf(Color(0xFF14532D), Color(0xFF090D16)), // Forest green
    listOf(Color(0xFF9A3412), Color(0xFF090D16)), // Rusty copper
    listOf(Color(0xFF1E293B), Color(0xFF090D16)), // Midnight slate
    listOf(Color(0xFF0369A1), Color(0xFF090D16)), // Ocean blue
    listOf(Color(0xFF581C87), Color(0xFF090D16)), // Royal purple
    listOf(Color(0xFF3F6212), Color(0xFF090D16)), // Olive deep
    listOf(Color(0xFF475569), Color(0xFF090D16))  // Steel smoke
)

/**
 * Genre tile with rich colored gradient and cascading / angled song poster in the corner.
 */
val CuratedGenreCovers = listOf(
    "https://i.ytimg.com/vi/4NRXx6U8ABQ/hqdefault.jpg",
    "https://i.ytimg.com/vi/njA3vYpXk5M/hqdefault.jpg",
    "https://i.ytimg.com/vi/bpOSxM0rNPM/hqdefault.jpg",
    "https://i.ytimg.com/vi/u9n7Cw-4_PE/hqdefault.jpg",
    "https://i.ytimg.com/vi/2v8I_Q-q4t0/hqdefault.jpg",
    "https://i.ytimg.com/vi/1G4isv_Fylg/hqdefault.jpg",
    "https://i.ytimg.com/vi/kJQP7kiw5Fk/hqdefault.jpg",
    "https://i.ytimg.com/vi/JGwWNGJdvx8/hqdefault.jpg"
)

@Composable
fun TuneifyCascadingGenreCard(
    title: String,
    posterUrl: String?,
    gradientColors: List<Color>,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val finalPoster = if (!posterUrl.isNullOrBlank()) posterUrl else CuratedGenreCovers[kotlin.math.abs(title.hashCode()) % CuratedGenreCovers.size]
    Box(
        modifier = modifier
            .height(96.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = gradientColors,
                    start = Offset.Zero,
                    end = Offset.Infinite
                )
            )
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 4.dp, bottom = 4.dp)
                .size(60.dp)
                .rotate(14f)
                .shadow(8.dp, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Black.copy(alpha = 0.2f))
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(finalPoster)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Text(
            text = title,
            fontFamily = echo.music.iad1tya.ui.theme.ChargerProFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .fillMaxWidth(0.65f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * 1-to-1 Tuneify Top Bar matching the design reference.
 * Left: "Tuneify" bold title.
 * Right: Notification bell icon + circular user profile avatar.
 */
@Composable
fun TuneifyHomeTopBar(
    accountImageUrl: String? = null,
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Tuneify",
            fontFamily = AeonikFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = Color.White,
            letterSpacing = (-0.5).sp
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            IconButton(
                onClick = onNotificationClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.notification),
                    contentDescription = "Notifications",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
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
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF334155), Color(0xFF1E293B))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.person),
                            contentDescription = "Profile",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * "Made for you" Daily Mix card matching Screen 1 in design reference.
 */
@Composable
fun TuneifyDailyMixCard(
    title: String,
    subtitle: String,
    imageUrl: String?,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(155.dp)
            .height(155.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF141722))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.08f),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
    ) {
        if (!imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF0A0D15).copy(alpha = 0.50f),
                            Color(0xFF080A0F).copy(alpha = 0.95f)
                        ),
                        startY = 40f
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(14.dp)
        ) {
            Text(
                text = title,
                fontFamily = AeonikFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = Color(0xFF94A3B8),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Modern Tuneify square card for albums, playlists, and mixes.
 * Matches Screen 1 in design reference.
 */
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun TuneifyAlbumCard(
    title: String,
    subtitle: String?,
    thumbnailUrl: String?,
    onClick: () -> Unit = {},
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(135.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        Box(
            modifier = Modifier
                .size(135.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                .background(Color(0xFF141722))
        ) {
            if (!thumbnailUrl.isNullOrBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(thumbnailUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            ),
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        if (!subtitle.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = Color(0xFF8E95A5),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Obsidian Purple pulsing skeleton placeholder box.
 */
@Composable
fun TuneifySkeletonBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "skeleton_shimmer")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "skeleton_alpha"
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(Color(0xFF1F1A30).copy(alpha = alpha))
            .border(1.dp, Color(0xFF38235E).copy(alpha = 0.30f), shape)
    )
}

/**
 * Complete polished skeleton layout for Home screen matching the real feed structure.
 */
@Composable
fun TuneifyHomeScreenSkeleton(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Chips Row Skeleton
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(5) { i ->
                val width = if (i == 0) 48.dp else if (i == 1) 80.dp else 96.dp
                TuneifySkeletonBox(
                    modifier = Modifier
                        .width(width)
                        .height(34.dp),
                    shape = RoundedCornerShape(100.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Hero Spotlight Carousel Skeleton
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            TuneifySkeletonBox(
                modifier = Modifier
                    .weight(1f)
                    .height(245.dp),
                shape = RoundedCornerShape(26.dp)
            )
            TuneifySkeletonBox(
                modifier = Modifier
                    .width(40.dp)
                    .height(245.dp),
                shape = RoundedCornerShape(26.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section Title Skeleton
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TuneifySkeletonBox(
                modifier = Modifier
                    .width(140.dp)
                    .height(20.dp),
                shape = RoundedCornerShape(6.dp)
            )
            TuneifySkeletonBox(
                modifier = Modifier
                    .width(70.dp)
                    .height(28.dp),
                shape = RoundedCornerShape(100.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // YT Music 4-row Column Carousel Skeleton
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            repeat(2) {
                Box(
                    modifier = Modifier
                        .width(320.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF171324))
                        .border(1.dp, Color(0xFF38235E).copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        repeat(4) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                TuneifySkeletonBox(
                                    modifier = Modifier.size(50.dp),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    TuneifySkeletonBox(
                                        modifier = Modifier.fillMaxWidth(0.85f).height(14.dp),
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    TuneifySkeletonBox(
                                        modifier = Modifier.fillMaxWidth(0.55f).height(11.dp),
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


