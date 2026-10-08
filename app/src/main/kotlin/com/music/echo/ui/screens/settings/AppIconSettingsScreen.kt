@file:OptIn(ExperimentalMaterial3Api::class)

package echo.music.iad1tya.ui.screens.settings

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import echo.music.iad1tya.LocalPlayerAwareWindowInsets
import echo.music.iad1tya.R
import echo.music.iad1tya.constants.AppIconTypeKey
import echo.music.iad1tya.utils.AppIconType
import echo.music.iad1tya.utils.IconUtils
import echo.music.iad1tya.utils.rememberEnumPreference
import kotlinx.coroutines.launch

@Composable
fun AppIconSettingsScreen(
  navController: NavController,
  activity: Activity,
  snackbarHostState: SnackbarHostState
) {
  val context = LocalContext.current
  val (appIconType, onAppIconTypeChange) =
    rememberEnumPreference(AppIconTypeKey, defaultValue = AppIconType.DEFAULT)
  val (_, onThemeColorChange) =
    echo.music.iad1tya.utils.rememberPreference(echo.music.iad1tya.constants.SelectedThemeColorKey, defaultValue = Color(0xFF7B2CBF).toArgb())
  val coroutineScope = rememberCoroutineScope()

  val icons =
    listOf(
      AppIconOption(
        AppIconType.DEFAULT,
        "Tuneify Obsidian Purple (Default)",
        "Signature deep obsidian purple frequency aura",
        themeColor = Color(0xFF7B2CBF),
        tintColor = Color(0xFF9D4EDD)
      ),
      AppIconOption(
        AppIconType.LEGACY,
        "Tuneify Obsidian Slate",
        "Monochromatic obsidian charcoal minimalism",
        themeColor = Color(0xFF64748B),
        tintColor = Color(0xFF94A3B8)
      ),
      AppIconOption(
        AppIconType.CRAZY_BLUE,
        "Tuneify Electric Blue",
        "Vibrant sapphire acoustic gradient",
        themeColor = Color(0xFF0284C7),
        tintColor = Color(0xFF38BDF8)
      ),
      AppIconOption(
        AppIconType.SKY,
        "Tuneify Emerald Green",
        "Luminous botanical emerald frequency",
        themeColor = Color(0xFF10B981),
        tintColor = Color(0xFF34D399)
      ),
      AppIconOption(
        AppIconType.STATIC,
        "Tuneify Sunset Crimson",
        "High-contrast scarlet flame gradient",
        themeColor = Color(0xFFE11D48),
        tintColor = Color(0xFFF43F5E)
      ),
      AppIconOption(
        AppIconType.POOKIE,
        "Tuneify Royal Amethyst",
        "Regal violet crystal audio ribbon",
        themeColor = Color(0xFFA855F7),
        tintColor = Color(0xFFC084FC)
      )
    )

  fun handleIconChange(option: AppIconOption) {
    onAppIconTypeChange(option.type)
    IconUtils.setIcon(activity, option.type)
    option.themeColor?.let { color ->
      onThemeColorChange(color.toArgb())
    }
    coroutineScope.launch {
      val result =
        snackbarHostState.showSnackbar(
          message = "App icon and dynamic theme updated!",
          actionLabel = "Restart"
        )
      if (result == SnackbarResult.ActionPerformed) {
        val packageManager = activity.packageManager
        val intent = packageManager.getLaunchIntentForPackage(activity.packageName)
        if (intent != null) {
          val componentName = intent.component
          val mainIntent = android.content.Intent.makeRestartActivityTask(componentName)
          activity.startActivity(mainIntent)
          Runtime.getRuntime().exit(0)
        }
      }
    }
  }

  Scaffold(
    containerColor = MaterialTheme.colorScheme.surface,
    topBar = {
      TopAppBar(
        title = { Text("App Icon & Palette", fontWeight = FontWeight.Bold) },
        navigationIcon = {
          IconButton(onClick = { navController.navigateUp() }) {
            Icon(
              painter = painterResource(R.drawable.arrow_back),
              contentDescription = "Back"
            )
          }
        },
        colors =
          TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
          )
      )
    }
  ) { innerPadding ->
    Column(
      modifier =
        Modifier.fillMaxSize()
          .background(MaterialTheme.colorScheme.surface)
          .verticalScroll(rememberScrollState())
          .windowInsetsPadding(
            LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Horizontal)
          )
          .padding(innerPadding)
          .padding(horizontal = 16.dp)
    ) {
      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "Choose your launcher icon",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
      )

      Column(modifier = Modifier.fillMaxWidth()) {
        icons.forEachIndexed { index, option ->
          val shape =
            when {
              icons.size == 1 -> RoundedCornerShape(24.dp)
              index == 0 ->
                RoundedCornerShape(
                  topStart = 24.dp,
                  topEnd = 24.dp,
                  bottomStart = 4.dp,
                  bottomEnd = 4.dp
                )
              index == icons.size - 1 ->
                RoundedCornerShape(
                  topStart = 4.dp,
                  topEnd = 4.dp,
                  bottomStart = 24.dp,
                  bottomEnd = 24.dp
                )
              else -> RoundedCornerShape(4.dp)
            }

          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = shape,
            colors =
              CardDefaults.cardColors(
                containerColor =
                  if (appIconType == option.type)
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                  else MaterialTheme.colorScheme.surfaceContainerHigh
              ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
          ) {
            AppIconRow(
              option = option,
              isSelected = (appIconType == option.type),
              onClick = { handleIconChange(option) }
            )
          }

          if (index < icons.size - 1) {
            Spacer(modifier = Modifier.height(2.dp))
          }
        }
      }

      Spacer(
        modifier =
          Modifier.windowInsetsPadding(
            LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Bottom)
          )
      )
      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

data class AppIconOption(
  val type: AppIconType,
  val title: String,
  val description: String,
  val themeColor: Color? = null,
  val tintColor: Color? = null,
)

@Composable
fun AppIconRow(option: AppIconOption, isSelected: Boolean, onClick: () -> Unit) {
  Row(
    modifier =
      Modifier.fillMaxWidth()
        .clickable(onClick = onClick)
        .padding(horizontal = 20.dp, vertical = 16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    val context = LocalContext.current
    val bitmap =
      remember {
        val drawable = ContextCompat.getDrawable(context, R.mipmap.ic_launcher)
        drawable?.toBitmap(width = 192, height = 192)?.asImageBitmap()
      }

    Box(
      modifier = Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)),
      contentAlignment = Alignment.Center
    ) {
      if (bitmap != null) {
        Image(
          bitmap = bitmap,
          contentDescription = null,
          colorFilter = option.tintColor?.let { androidx.compose.ui.graphics.ColorFilter.tint(it, androidx.compose.ui.graphics.BlendMode.SrcAtop) },
          modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp))
        )
      }
    }

    Spacer(modifier = Modifier.width(20.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = option.title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = option.description,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    Spacer(modifier = Modifier.width(8.dp))
    RadioButton(selected = isSelected, onClick = onClick)
  }
}
