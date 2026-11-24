package echo.music.iad1tya.ui.component

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import echo.music.iad1tya.R
import echo.music.iad1tya.ui.screens.OptionStats

@Composable
fun <E> ChipsRow(
  chips: List<Pair<E, String>>,
  currentValue: E,
  onValueUpdate: (E) -> Unit,
  modifier: Modifier = Modifier,
  containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
) {
  Row(
    modifier =
      modifier
        .fillMaxWidth()
        .padding(top = 8.dp, bottom = 6.dp)
        .horizontalScroll(rememberScrollState())
        .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)),
  ) {
    Spacer(Modifier.width(16.dp))

    chips.forEach { (value, label) ->
      val isSelected = currentValue == value

      FilterChip(
        label = {
          Text(
            text = label,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 13.sp,
          )
        },
        selected = isSelected,
        colors =
          FilterChipDefaults.filterChipColors(
            containerColor = containerColor,
            labelColor = MaterialTheme.colorScheme.onSurface,
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary,
          ),
        onClick = { onValueUpdate(value) },
        leadingIcon = null,
        shape = RoundedCornerShape(4.dp),
        border =
          BorderStroke(
            1.dp,
            if (isSelected) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
          ),
        modifier =
          Modifier.animateContentSize(
            animationSpec = tween(durationMillis = 150)
          )
      )

      Spacer(Modifier.width(8.dp))
    }
  }
}

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Composable
fun <Int> ChoiceChipsRow(
  chips: List<Pair<Int, String>>,
  options: List<Pair<OptionStats, String>>,
  selectedOption: OptionStats,
  onSelectionChange: (OptionStats) -> Unit,
  currentValue: Int,
  onValueUpdate: (Int) -> Unit,
  modifier: Modifier = Modifier,
  containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
) {
  var menuExpanded by remember { mutableStateOf(false) }

  var expandIconDegree by remember { mutableFloatStateOf(0f) }
  val rotationAnimation by
    animateFloatAsState(
      targetValue = expandIconDegree,
      animationSpec = tween(durationMillis = 400),
      label = "rotation",
    )

  Column(modifier = modifier.fillMaxWidth()) {
    Row(
      modifier =
        Modifier.fillMaxWidth()
          .horizontalScroll(rememberScrollState())
          .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)),
    ) {
      Spacer(Modifier.width(12.dp))
      Box(contentAlignment = Alignment.Center) {
        FilterChip(
          selected = false,
          modifier = Modifier.padding(horizontal = 4.dp),
          onClick = {
            menuExpanded = !menuExpanded
            expandIconDegree -= 180
          },
          label = {
            Text(
              text =
                when (selectedOption) {
                  OptionStats.WEEKS -> stringResource(id = R.string.weeks)
                  OptionStats.MONTHS -> stringResource(id = R.string.months)
                  OptionStats.YEARS -> stringResource(id = R.string.years)
                  OptionStats.CONTINUOUS -> stringResource(id = R.string.continuous)
                }
            )
          },
          leadingIcon = {
            Icon(
              imageVector = Icons.Filled.Tune,
              contentDescription = null,
              modifier = Modifier.size(FilterChipDefaults.IconSize),
            )
          },
          trailingIcon = {
            Icon(
              painter = painterResource(R.drawable.expand_more),
              contentDescription = null,
              modifier = Modifier.graphicsLayer(rotationZ = rotationAnimation)
            )
          },
          shape = RoundedCornerShape(16.dp),
          border = null,
          colors =
            FilterChipDefaults.filterChipColors(
              containerColor = containerColor,
              selectedContainerColor = MaterialTheme.colorScheme.onSurface,
              labelColor = MaterialTheme.colorScheme.onSurface,
              selectedLabelColor = MaterialTheme.colorScheme.surface
            )
        )

        DropdownMenu(
          expanded = menuExpanded,
          onDismissRequest = {
            menuExpanded = false
            expandIconDegree += 180
          },
        ) {
          options.forEach { option ->
            DropdownMenuItem(
              text = { Text(text = option.second) },
              onClick = {
                onSelectionChange(option.first)
                expandIconDegree += 180
                menuExpanded = false
              },
            )
          }
        }
      }

      Box(
        Modifier.height(FilterChipDefaults.Height)
          .padding(horizontal = 4.dp)
          .align(Alignment.CenterVertically)
      ) {
        VerticalDivider()
      }

      chips.forEach { (value, label) ->
        val isSelected = currentValue == value

        FilterChip(
          label = { Text(label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
          selected = isSelected,
          colors =
            FilterChipDefaults.filterChipColors(
              containerColor = containerColor,
              selectedContainerColor = MaterialTheme.colorScheme.primary,
              selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
              selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
            ),
          onClick = { onValueUpdate(value) },
          leadingIcon =
            if (isSelected) {
              {
                Icon(
                  imageVector = Icons.Filled.Done,
                  contentDescription = null,
                  modifier = Modifier.size(FilterChipDefaults.IconSize),
                )
              }
            } else {
              null
            },
          shape = RoundedCornerShape(4.dp),
          border =
            BorderStroke(
              1.dp,
              if (isSelected) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
            ),
          modifier =
            Modifier.padding(horizontal = 4.dp)
              .animateContentSize(
                animationSpec = tween(durationMillis = 150)
              )
        )
      }
    }
  }
}
