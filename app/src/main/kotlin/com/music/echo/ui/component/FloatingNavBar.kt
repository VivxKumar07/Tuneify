/** vivimusic Project (C) 2026 Licensed under GPL-3.0 | See git history for contributors */
@file:OptIn(ExperimentalSharedTransitionApi::class)

package echo.music.iad1tya.ui.component

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import echo.music.iad1tya.R
import echo.music.iad1tya.ui.component.floatingtabbar.FloatingTabBar
import echo.music.iad1tya.ui.component.floatingtabbar.FloatingTabBarDefaults
import echo.music.iad1tya.ui.component.floatingtabbar.FloatingTabBarScrollConnection
import echo.music.iad1tya.ui.player.FloatingMiniPlayer
import echo.music.iad1tya.ui.screens.Screens
import echo.music.iad1tya.ui.component.PremiumGlow

@Composable
fun AppFloatingNavBar(
  navigationItems: List<Screens>,
  currentRoute: String?,
  onItemClick: (Screens, Boolean) -> Unit,
  scrollConnection: FloatingTabBarScrollConnection,
  modifier: Modifier = Modifier,
  pureBlack: Boolean = false,
  showPlayerAccessory: Boolean = false,
  onAccessoryClick: () -> Unit = {},
  onMusicRecognitionClick: (() -> Unit)? = null,
  musicRecognitionContentDescription: String = "",
  onSearchLongClick: (() -> Unit)? = null,
  onHistoryClick: (() -> Unit)? = null,
  onStatsClick: (() -> Unit)? = null,
  onListenTogetherClick: (() -> Unit)? = null,
  onSettingsClick: (() -> Unit)? = null,
  showHistoryButton: Boolean = false,
  showListenTogether: Boolean = false,
  accountImageUrl: String? = null,
) {
  val glassConfig = LocalGlassEffectConfig.current
  val useGlass = glassConfig.isEnabledFor(GlassComponent.NAV_BAR)
  val isShrunk = scrollConnection.isInline

  val computedSelectedIndex = remember(currentRoute, navigationItems) {
    navigationItems.indexOfFirst { isRouteSelected(currentRoute, it.route, navigationItems) }.coerceAtLeast(0)
  }
  var optimisticIndex by remember { androidx.compose.runtime.mutableIntStateOf(computedSelectedIndex) }

  LaunchedEffect(computedSelectedIndex) {
    optimisticIndex = computedSelectedIndex
  }

  val navBarHeight by animateDpAsState(
    targetValue = if (isShrunk) 48.dp else 58.dp,
    animationSpec = spring(dampingRatio = 0.8f, stiffness = 380f),
    label = "navBarHeight"
  )

  Column(
    modifier = modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    if (showPlayerAccessory) {
      FloatingMiniPlayer(
        isInline = false,
        contentColor = Color.White,
        onClick = onAccessoryClick,
        modifier = Modifier
          .padding(horizontal = 16.dp, vertical = 6.dp)
          .fillMaxWidth()
      )
    }

    // Obsidian Purple Dark stadium pill navbar
    Box(
      modifier = Modifier
        .shadow(
          elevation = 22.dp,
          shape = RoundedCornerShape(36.dp),
          spotColor = Color(0xFF7B2CBF).copy(alpha = 0.35f)
        )
        .background(
          color = if (pureBlack) Color.Black else Color(0xFF0F0B18),
          shape = RoundedCornerShape(36.dp)
        )
        .border(
          width = 1.dp,
          color = Color(0xFF38235E).copy(alpha = 0.5f),
          shape = RoundedCornerShape(36.dp)
        )
        .height(navBarHeight)
        .padding(horizontal = 6.dp)
        .wrapContentWidth(),
      contentAlignment = Alignment.Center
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(if (isShrunk) 4.dp else 6.dp)
      ) {
        navigationItems.forEachIndexed { index, screen ->
          val isSelected = index == optimisticIndex
          val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
          val isPressed by interactionSource.collectIsPressedAsState()
          val pressScale by androidx.compose.animation.core.animateFloatAsState(
            targetValue = if (isPressed) 0.88f else 1.0f,
            animationSpec = spring(dampingRatio = 0.6f, stiffness = 600f),
            label = "pressScale_$index"
          )

          Box(
            modifier = Modifier
              .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
              }
              .clip(RoundedCornerShape(26.dp))
              .background(
                if (isSelected) Color(0xFF2C1C4D) else Color.Transparent
              )
              .clickable(
                interactionSource = interactionSource,
                indication = null
              ) {
                optimisticIndex = index
                onItemClick(screen, isSelected)
              }
              .padding(
                horizontal = if (isSelected && !isShrunk) 16.dp else 12.dp,
                vertical = if (isShrunk) 8.dp else 10.dp
              ),
            contentAlignment = Alignment.Center
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              if (screen == Screens.Profile && !accountImageUrl.isNullOrBlank()) {
                AsyncImage(
                  model = accountImageUrl,
                  contentDescription = stringResource(screen.titleId),
                  contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                  modifier = Modifier
                    .size(if (isShrunk) 20.dp else 22.dp)
                    .clip(CircleShape)
                )
              } else {
                Icon(
                  painter = painterResource(if (isSelected) screen.iconIdActive else screen.iconIdInactive),
                  contentDescription = stringResource(screen.titleId),
                  tint = if (isSelected) Color(0xFFF3EEFA) else Color(0xFFAFA5C2),
                  modifier = Modifier.size(if (isShrunk) 20.dp else 22.dp)
                )
              }

              // Text label expands only for selected tab in idle (not shrunk) state
              androidx.compose.animation.AnimatedVisibility(
                visible = isSelected && !isShrunk,
                enter = androidx.compose.animation.fadeIn(tween(180)) + androidx.compose.animation.expandHorizontally(tween(220, easing = FastOutSlowInEasing)),
                exit = androidx.compose.animation.fadeOut(tween(140)) + androidx.compose.animation.shrinkHorizontally(tween(180, easing = FastOutSlowInEasing))
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = stringResource(screen.titleId),
                    color = Color(0xFFF3EEFA),
                    fontFamily = echo.music.iad1tya.ui.theme.MonumentExtendedFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
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
  }
}

@Composable
fun MergedActionButtons(
  onHistoryClick: (() -> Unit)?, onStatsClick: (() -> Unit)?, onListenTogetherClick: (() -> Unit)?,
  onSettingsClick: (() -> Unit)?, showHistoryButton: Boolean, showListenTogether: Boolean,
  accountImageUrl: String?, iconColor: Color, modifier: Modifier = Modifier,
) {
  Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
    if (showHistoryButton && onHistoryClick != null) {
      IconButton(onClick = onHistoryClick, modifier = Modifier.size(32.dp)) { Icon(painter = painterResource(R.drawable.music_history), contentDescription = stringResource(R.string.history), tint = iconColor.copy(alpha = 0.7f), modifier = Modifier.size(18.dp)) }
    }
    if (onStatsClick != null) {
      IconButton(onClick = onStatsClick, modifier = Modifier.size(32.dp)) { Icon(painter = painterResource(R.drawable.stats), contentDescription = stringResource(R.string.stats), tint = iconColor.copy(alpha = 0.7f), modifier = Modifier.size(18.dp)) }
    }
    if (showListenTogether && onListenTogetherClick != null) {
      IconButton(onClick = onListenTogetherClick, modifier = Modifier.size(32.dp)) { Icon(painter = painterResource(R.drawable.group_outlined), contentDescription = stringResource(R.string.together), tint = iconColor.copy(alpha = 0.7f), modifier = Modifier.size(18.dp)) }
    }
    if (onSettingsClick != null) {
      IconButton(onClick = onSettingsClick, modifier = Modifier.size(32.dp)) {
        if (accountImageUrl != null) { AsyncImage(model = accountImageUrl, contentDescription = stringResource(R.string.account), modifier = Modifier.size(20.dp).clip(RoundedCornerShape(50))) }
        else { Icon(painter = painterResource(R.drawable.settings), contentDescription = stringResource(R.string.account), tint = iconColor.copy(alpha = 0.7f), modifier = Modifier.size(18.dp)) }
      }
    }
  }
}
