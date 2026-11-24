package echo.music.iad1tya.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import echo.music.iad1tya.ui.screens.Screens
import echo.music.iad1tya.ui.theme.TuneifyTokens

/**
 * Bauhaus Neo-Brutalist Floating Navigation Bar for Tuneify.
 */
@Composable
fun TuneifyFloatingNavBar(
  navigationItems: List<Screens>,
  currentRoute: String?,
  onItemClick: (Screens, Boolean) -> Unit,
  modifier: Modifier = Modifier,
  isDark: Boolean = isSystemInDarkTheme(),
  pureBlack: Boolean = false,
) {
  val haptic = LocalHapticFeedback.current

  val mainTabs = remember(navigationItems) {
    val desired = listOf(Screens.Home, Screens.Explore, Screens.Library, Screens.Search)
    desired.filter { screen ->
      navigationItems.any { it.route == screen.route }
    }.ifEmpty { navigationItems }
  }

  val containerBg = when {
    pureBlack && isDark -> Color(0xFF101010)
    isDark -> TuneifyTokens.DarkSurface
    else -> TuneifyTokens.LightSurface
  }

  val shadowColor = if (isDark) Color(0xFF000000) else Color(0xFF111111).copy(alpha = 0.85f)
  val borderColor = if (isDark) TuneifyTokens.DarkBorder else TuneifyTokens.LightBorder
  val navShape = RoundedCornerShape(8.dp)

  Box(
    modifier = modifier
      .padding(horizontal = 16.dp)
      .drawBehind {
        drawRoundRect(
          color = shadowColor,
          topLeft = Offset(3.dp.toPx(), 3.dp.toPx()),
          size = size,
        )
      }
      .clip(navShape)
      .background(containerBg, navShape)
      .border(BorderStroke(TuneifyTokens.BorderStandard, borderColor), navShape)
      .padding(horizontal = 6.dp, vertical = 5.dp),
    contentAlignment = Alignment.Center
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically
    ) {
      mainTabs.forEach { screen ->
        val isSelected = currentRoute == screen.route ||
          (screen == Screens.Search && currentRoute?.startsWith("search") == true) ||
          (currentRoute?.startsWith("${screen.route}/") == true)

        val activePillColor by animateColorAsState(
          targetValue = if (isSelected) TuneifyTokens.PrimaryYellow else Color.Transparent,
          animationSpec = tween(durationMillis = 180),
          label = "activePillBg"
        )

        val contentColor by animateColorAsState(
          targetValue = if (isSelected) {
            Color(0xFF111111)
          } else {
            if (isDark) TuneifyTokens.DarkTextSecondary else TuneifyTokens.LightTextSecondary
          },
          animationSpec = tween(durationMillis = 180),
          label = "contentColor"
        )

        val interactionSource = remember { MutableInteractionSource() }
        val itemShape = RoundedCornerShape(4.dp)

        Box(
          modifier = Modifier
            .weight(1f)
            .clip(itemShape)
            .background(activePillColor)
            .then(
              if (isSelected) {
                Modifier.border(1.dp, Color(0xFF111111), itemShape)
              } else Modifier
            )
            .clickable(
              interactionSource = interactionSource,
              indication = null
            ) {
              haptic.performHapticFeedback(HapticFeedbackType.LongPress)
              onItemClick(screen, isSelected)
            }
            .padding(vertical = 6.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            Icon(
              painter = painterResource(if (isSelected) screen.iconIdActive else screen.iconIdInactive),
              contentDescription = stringResource(screen.titleId),
              tint = contentColor,
              modifier = Modifier.size(20.dp)
            )

            Spacer(Modifier.height(3.dp))

            Text(
              text = stringResource(screen.titleId),
              fontSize = 11.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              color = contentColor,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }
      }
    }
  }
}
