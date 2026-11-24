package echo.music.iad1tya.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import echo.music.iad1tya.ui.component.glass.TuneifyGlassTokens
import echo.music.iad1tya.ui.screens.Screens

/**
 * Tuneify Perfect-Pill Liquid Glass Bottom Navigation Bar.
 *
 * Implements a floating liquid glass pill container with a sliding Volt Lime highlighter pill
 * that smoothly glides with spring animation between navigation options.
 */
@Composable
fun ExpressiveBottomNavBar(
  navigationItems: List<Screens>,
  currentRoute: String?,
  onItemClick: (Screens, Boolean) -> Unit,
  modifier: Modifier = Modifier,
  pureBlack: Boolean = false,
  accountImageUrl: String? = null,
  isProfileActive: Boolean = false,
  accentColor: Color = TuneifyGlassTokens.VoltLime,
) {
  val haptic = LocalHapticFeedback.current

  val dynamicAccent by animateColorAsState(
    targetValue = accentColor,
    animationSpec = tween(durationMillis = 350),
    label = "navDynamicAccent"
  )

  // Perfect pill container shape
  val navShape = CircleShape
  val containerBrush = if (pureBlack) {
    Brush.verticalGradient(listOf(Color(0xE0121614), Color(0xF5050806)))
  } else {
    Brush.verticalGradient(
      listOf(
        Color(0x55284B35), // Top specular liquid glass sheen
        Color(0x3514261B),
        Color(0x3D0D1911)
      )
    )
  }
  val inactiveTint = Color(0xCCFFFFFF)

  // Find active item index
  val activeIndex = navigationItems.indexOfFirst { screen ->
    when (screen) {
      Screens.Home -> currentRoute == Screens.Home.route
      Screens.Explore -> currentRoute == Screens.Explore.route || currentRoute?.startsWith("explore") == true
      Screens.Search -> currentRoute == Screens.Search.route || currentRoute?.startsWith("search") == true
      Screens.Library -> currentRoute == Screens.Library.route || currentRoute?.startsWith("library") == true
      Screens.ListenTogether -> currentRoute == Screens.ListenTogether.route || currentRoute?.startsWith("listen_together") == true
      Screens.Profile -> isProfileActive || currentRoute?.startsWith("settings") == true
      else -> currentRoute == screen.route
    }
  }.let { if (it >= 0) it else 0 }

  // Animated sliding position across items
  val animatedIndex by animateFloatAsState(
    targetValue = activeIndex.toFloat(),
    animationSpec = spring(dampingRatio = 0.80f, stiffness = 420f),
    label = "navSliderPill"
  )

  Box(
    modifier = modifier
      .padding(horizontal = 20.dp, vertical = 6.dp)
      .height(66.dp)
      .shadow(
        elevation = 20.dp,
        shape = navShape,
        clip = false,
        ambientColor = Color(0x99000000),
        spotColor = Color(0x80000000)
      )
      .clip(navShape)
      .background(Color(0xD908170E), navShape)
      .background(containerBrush, navShape)
      .border(
        width = 1.25.dp,
        brush = Brush.linearGradient(
          listOf(
            Color(0x70FFFFFF), // Bright frosted top edge
            Color(0x18FFFFFF),
            Color(0x35C6F53D)  // Subtle lime rim highlight
          )
        ),
        shape = navShape
      )
      .padding(horizontal = 6.dp, vertical = 4.dp),
    contentAlignment = Alignment.CenterStart
  ) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
      val totalWidth = maxWidth
      val count = navigationItems.size.coerceAtLeast(1)
      val itemWidth = totalWidth / count
      val pillWidth = (itemWidth - 6.dp).coerceAtLeast(56.dp)
      val pillHeight = 54.dp
      val pillShape = CircleShape

      // 1. Sliding Refractive Liquid Glass-Green Highlighter Pill
      Box(
        modifier = Modifier
          .offset(x = itemWidth * animatedIndex + (itemWidth - pillWidth) / 2f)
          .width(pillWidth)
          .height(pillHeight)
          .align(Alignment.CenterStart)
          .shadow(
            elevation = 12.dp,
            shape = pillShape,
            clip = false,
            ambientColor = dynamicAccent.copy(alpha = 0.40f),
            spotColor = dynamicAccent.copy(alpha = 0.60f)
          )
          .clip(pillShape)
          // Liquid glass-green gradient: dynamic artwork refraction
          .background(
            Brush.verticalGradient(
              listOf(
                dynamicAccent.copy(alpha = 0.90f), // Top bright luminous glass highlight
                dynamicAccent.copy(alpha = 0.75f), // Middle caustic refractive tone
                dynamicAccent.copy(alpha = 0.50f)  // Translucent glass base
              )
            ),
            pillShape
          )
          // Glass specular reflection highlight (top sheen)
          .border(
            width = 1.25.dp,
            brush = Brush.verticalGradient(
              listOf(
                Color(0xE6FFFFFF), // Top specular white reflection
                dynamicAccent.copy(alpha = 0.65f),
                dynamicAccent.copy(alpha = 0.25f)
              )
            ),
            shape = pillShape
          )
      ) {
        // Inner upper-half specular caustic glow
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(pillHeight / 2)
            .background(
              Brush.verticalGradient(
                listOf(
                  Color(0x45FFFFFF),
                  Color.Transparent
                )
              )
            )
        )
      }

      // 2. Navigation items row above the sliding pill
      Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        navigationItems.forEachIndexed { index, screen ->
          val isSelected = index == activeIndex

          val isLuminous = (dynamicAccent.red * 0.299f + dynamicAccent.green * 0.587f + dynamicAccent.blue * 0.114f) > 0.45f
          val activeContentTint = if (isLuminous) Color(0xFF071A0B) else Color.White
          val inactiveContentTint = Color(0xD9FFFFFF)

          val contentTint by animateColorAsState(
            targetValue = if (isSelected) activeContentTint else inactiveContentTint,
            animationSpec = tween(durationMillis = 200),
            label = "navContentTint_$index"
          )

          val itemScale by animateFloatAsState(
            targetValue = if (isSelected) 1.04f else 0.96f,
            animationSpec = spring(dampingRatio = 0.7f, stiffness = 400f),
            label = "navItemScale_$index"
          )

          val interactionSource = remember { MutableInteractionSource() }

          Box(
            modifier = Modifier
              .weight(1f)
              .fillMaxHeight()
              .graphicsLayer {
                scaleX = itemScale
                scaleY = itemScale
              }
              .clickable(
                interactionSource = interactionSource,
                indication = null
              ) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onItemClick(screen, isSelected)
              },
            contentAlignment = Alignment.Center
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              if (screen == Screens.Profile && !accountImageUrl.isNullOrBlank()) {
                AsyncImage(
                  model = accountImageUrl,
                  contentDescription = stringResource(screen.titleId),
                  contentScale = ContentScale.Crop,
                  modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .border(
                      if (isSelected) 1.5.dp else 1.dp,
                      if (isSelected) activeContentTint else Color(0x44FFFFFF),
                      CircleShape
                    )
                )
              } else {
                Icon(
                  painter = painterResource(if (isSelected) screen.iconIdActive else screen.iconIdInactive),
                  contentDescription = stringResource(screen.titleId),
                  tint = contentTint,
                  modifier = Modifier.size(23.dp)
                )
              }

              Text(
                text = stringResource(screen.titleId),
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                color = contentTint,
                maxLines = 1,
                lineHeight = 11.sp,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 1.dp)
              )
            }
          }
        }
      }
    }
  }
}
