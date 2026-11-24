package echo.music.iad1tya.ui.screens.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.graphics.Brush
import echo.music.iad1tya.ui.component.glass.DynamicArtworkBackground
import echo.music.iad1tya.ui.component.glass.TuneifyGlassTokens
import echo.music.iad1tya.LocalPlayerAwareWindowInsets
import echo.music.iad1tya.R
import echo.music.iad1tya.echomusic.updater.getUpdateAvailableState
import echo.music.iad1tya.ui.component.IconButton
import echo.music.iad1tya.ui.component.Material3SettingsGroup
import echo.music.iad1tya.ui.theme.TuneifyTokens
import echo.music.iad1tya.ui.component.Material3SettingsItem
import echo.music.iad1tya.ui.screens.Screens
import echo.music.iad1tya.ui.utils.backToMain

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  navController: NavController,
  scrollBehavior: TopAppBarScrollBehavior,
  highlightKey: String? = null
) {
  val uriHandler = LocalUriHandler.current
  val context = LocalContext.current
  val isAndroid12OrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
  val isUpdateAvailable =
    getUpdateAvailableState(context) &&
      echo.music.iad1tya.echomusic.updater.getAutoUpdateCheckSetting(context)

  var searchQuery by rememberSaveable { mutableStateOf("") }
  val searchLower = searchQuery.lowercase()

  val accountText = stringResource(R.string.account)
  val appearanceText = stringResource(R.string.appearance)
  val playerText = stringResource(R.string.player_and_audio)
  val listenTogetherText = stringResource(R.string.listen_together)
  val contentText = stringResource(R.string.content)
  val aiLyricsText = stringResource(R.string.ai_lyrics_translation)
  val privacyText = stringResource(R.string.privacy)
  val storageText = stringResource(R.string.storage)
  val backupText = stringResource(R.string.backup_restore)
  val systemUpdateText = stringResource(R.string.system_update)
  val aboutText = stringResource(R.string.about)

  val accountDesc = stringResource(R.string.setting_desc_account)
  val appearanceDesc = stringResource(R.string.setting_desc_appearance)
  val playerDesc = stringResource(R.string.setting_desc_player)
  val listenTogetherDesc = stringResource(R.string.setting_desc_listen_together)
  val contentDesc = stringResource(R.string.setting_desc_content)
  val aiLyricsDesc = stringResource(R.string.setting_desc_ai)
  val privacyDesc = stringResource(R.string.setting_desc_privacy)
  val storageDesc = stringResource(R.string.setting_desc_storage)
  val backupDesc = stringResource(R.string.setting_desc_backup)
  val systemUpdateDesc = stringResource(R.string.setting_desc_update)
  val aboutDesc = stringResource(R.string.setting_desc_about)

  val scrollState = rememberScrollState()

  DynamicArtworkBackground {
    Box(modifier = Modifier.fillMaxSize()) {
      Column(
        Modifier.windowInsetsPadding(
            LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Horizontal)
          )
          .verticalScroll(scrollState)
          .padding(horizontal = 16.dp)
      ) {
        Spacer(
          Modifier.windowInsetsPadding(LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Top))
        )
        // Reserve the TopAppBar's height.
        Spacer(Modifier.height(64.dp))
        Text(
          text = stringResource(R.string.settings),
          style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold),
          color = Color.White,
          modifier = Modifier.padding(start = 8.dp, top = 8.dp)
        )
        Text(
          text = stringResource(R.string.settings_tagline),
          style = MaterialTheme.typography.bodyMedium,
          color = TuneifyGlassTokens.TextSecondary,
          modifier = Modifier.padding(start = 8.dp, top = 2.dp, bottom = 16.dp)
        )

        TextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          singleLine = true,
          maxLines = 1,
          textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
          placeholder = {
            Text(
              text = stringResource(R.string.search),
              style = MaterialTheme.typography.bodyLarge,
              color = Color.White.copy(alpha = 0.6f)
            )
          },
          leadingIcon = {
            Icon(
              imageVector = Icons.Rounded.Search,
              contentDescription = stringResource(R.string.search),
              tint = TuneifyGlassTokens.VoltLime,
              modifier = Modifier.size(22.dp)
            )
          },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(
                  imageVector = Icons.Rounded.Clear,
                  contentDescription = "Clear",
                  tint = Color.White.copy(alpha = 0.7f)
                )
              }
            }
          },
          shape = CircleShape,
          colors =
            TextFieldDefaults.colors(
              focusedContainerColor = Color(0x3514261B),
              unfocusedContainerColor = Color(0x28102015),
              focusedIndicatorColor = Color.Transparent,
              unfocusedIndicatorColor = Color.Transparent,
              disabledIndicatorColor = Color.Transparent,
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White,
              cursorColor = TuneifyGlassTokens.VoltLime
            ),
          modifier =
            Modifier.fillMaxWidth()
              .padding(bottom = 16.dp)
              .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                  listOf(
                    Color(0x55FFFFFF),
                    Color(0x18FFFFFF),
                    TuneifyGlassTokens.VoltLime.copy(alpha = 0.35f)
                  )
                ),
                shape = CircleShape
              )
        )

    val itemsList = buildList {
      if (
        accountText.lowercase().contains(searchLower) ||
          accountDesc.lowercase().contains(searchLower)
      ) {
        add(
          Material3SettingsItem(
            isHighlighted = (highlightKey == accountText),
            icon = painterResource(R.drawable.account),
            iconBackgroundColor = TuneifyTokens.Lime,
            title = { Text(accountText) },
            description = { Text(accountDesc) },
            onClick = { navController.navigate("settings/account") }
          )
        )
      }

      if (
        aiLyricsText.lowercase().contains(searchLower) ||
          aiLyricsDesc.lowercase().contains(searchLower)
      ) {
        add(
          Material3SettingsItem(
            isHighlighted = (highlightKey == aiLyricsText),
            iconBackgroundColor = TuneifyTokens.Violet,
            customIcon = {
              Text(
                text = "Ai",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = TuneifyTokens.onAccent(TuneifyTokens.Violet)
              )
            },
            title = { Text(aiLyricsText) },
            description = { Text(aiLyricsDesc) },
            onClick = { navController.navigate("settings/ai") }
          )
        )
      }

      if (
        appearanceText.lowercase().contains(searchLower) ||
          appearanceDesc.lowercase().contains(searchLower)
      ) {
        add(
          Material3SettingsItem(
            isHighlighted = (highlightKey == appearanceText),
            icon = painterResource(R.drawable.palette),
            iconBackgroundColor = TuneifyTokens.Magenta,
            title = { Text(appearanceText) },
            description = { Text(appearanceDesc) },
            onClick = { navController.navigate("settings/appearance") }
          )
        )
      }
      if (
        playerText.lowercase().contains(searchLower) || playerDesc.lowercase().contains(searchLower)
      ) {
        add(
          Material3SettingsItem(
            isHighlighted = (highlightKey == playerText),
            icon = painterResource(R.drawable.play),
            iconBackgroundColor = TuneifyTokens.Sky,
            title = { Text(playerText) },
            description = { Text(playerDesc) },
            onClick = { navController.navigate("settings/player") }
          )
        )
      }
      if (
        listenTogetherText.lowercase().contains(searchLower) ||
          listenTogetherDesc.lowercase().contains(searchLower)
      ) {
        add(
          Material3SettingsItem(
            isHighlighted = (highlightKey == listenTogetherText),
            icon = painterResource(R.drawable.group),
            iconBackgroundColor = TuneifyTokens.Coral,
            title = { Text(listenTogetherText) },
            description = { Text(listenTogetherDesc) },
            onClick = { navController.navigate(Screens.ListenTogether.route) }
          )
        )
      }

      if ("listening summary".contains(searchLower)) {
        add(
          Material3SettingsItem(
            icon = painterResource(echo.music.iad1tya.R.drawable.history),
            iconBackgroundColor = TuneifyTokens.Pink,
            title = { Text(stringResource(echo.music.iad1tya.R.string.listening_summary)) },
            description = {
              Text(stringResource(echo.music.iad1tya.R.string.listening_summary_desc))
            },
            onClick = { navController.navigate("settings/listening_summary") }
          )
        )
      }

      if (
        contentText.lowercase().contains(searchLower) ||
          contentDesc.lowercase().contains(searchLower)
      ) {
        add(
          Material3SettingsItem(
            isHighlighted = (highlightKey == contentText),
            icon = painterResource(R.drawable.language),
            iconBackgroundColor = TuneifyTokens.Mint,
            title = { Text(contentText) },
            description = { Text(contentDesc) },
            onClick = { navController.navigate("settings/content") }
          )
        )
      }

      if (
        storageText.lowercase().contains(searchLower) ||
          storageDesc.lowercase().contains(searchLower)
      ) {
        add(
          Material3SettingsItem(
            isHighlighted = (highlightKey == storageText),
            icon = painterResource(R.drawable.storage),
            iconBackgroundColor = TuneifyTokens.Teal,
            title = { Text(storageText) },
            description = { Text(storageDesc) },
            onClick = { navController.navigate("settings/storage") }
          )
        )
      }

      if (
        backupText.lowercase().contains(searchLower) || backupDesc.lowercase().contains(searchLower)
      ) {
        add(
          Material3SettingsItem(
            isHighlighted = (highlightKey == backupText),
            icon = painterResource(R.drawable.restore),
            iconBackgroundColor = TuneifyTokens.Amber,
            title = { Text(backupText) },
            description = { Text(backupDesc) },
            onClick = { navController.navigate("settings/backup_restore") }
          )
        )
      }

      if (
        privacyText.lowercase().contains(searchLower) ||
          privacyDesc.lowercase().contains(searchLower)
      ) {
        add(
          Material3SettingsItem(
            isHighlighted = (highlightKey == privacyText),
            icon = painterResource(R.drawable.security),
            iconBackgroundColor = TuneifyTokens.Forest,
            title = { Text(privacyText) },
            description = { Text(privacyDesc) },
            onClick = { navController.navigate("settings/privacy") }
          )
        )
      }
      if (
        systemUpdateText.lowercase().contains(searchLower) ||
          systemUpdateDesc.lowercase().contains(searchLower)
      ) {
        add(
          Material3SettingsItem(
            isHighlighted = (highlightKey == systemUpdateText),
            icon =
              painterResource(
                if (isUpdateAvailable) R.drawable.ic_launcher_nobg else R.drawable.update
              ),
            iconBackgroundColor = TuneifyTokens.Olive,
            title = { Text(systemUpdateText) },
            description =
              if (isUpdateAvailable) {
                {
                  Text(
                    text = stringResource(R.string.update_available),
                    color = MaterialTheme.colorScheme.error
                  )
                }
              } else {
                { Text(systemUpdateDesc) }
              },
            onClick = { navController.navigate("settings/update") }
          )
        )
      }
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if ("supported links".contains(searchLower)) {
          add(
            Material3SettingsItem(
              isHighlighted = (highlightKey == "supported links"),
              icon = painterResource(R.drawable.link),
              iconBackgroundColor = TuneifyTokens.Sky,
              title = { Text("Supported Links") },
              description = { Text("App linking settings") },
              onClick = {
                try {
                  val intent =
                    Intent(
                      Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS,
                      Uri.parse("package:${context.packageName}")
                    )
                  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                  context.startActivity(intent)
                } catch (e: Exception) {
                  when (e) {
                    is ActivityNotFoundException,
                    is SecurityException -> {
                      Toast.makeText(context, "Cannot open settings", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                      Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show()
                    }
                  }
                }
              }
            )
          )
        }
      }
      if (
        aboutText.lowercase().contains(searchLower) || aboutDesc.lowercase().contains(searchLower)
      ) {
        add(
          Material3SettingsItem(
            isHighlighted = (highlightKey == aboutText),
            icon = painterResource(R.drawable.info),
            iconBackgroundColor = Color(0xFF242424),
            tintIcon = false,
            title = { Text(aboutText) },
            description = { Text(aboutDesc) },
            onClick = { navController.navigate("settings/about") }
          )
        )
      }
    }

    val finalItemsList =
      if (searchQuery.isNotEmpty()) {
        val subSettings = getAllSearchableSettings()

        val matchedSubSettings =
          subSettings
            .filter {
              it.title.lowercase().contains(searchLower) ||
                (it.description?.lowercase()?.contains(searchLower) == true)
            }
            .map { setting ->
              Material3SettingsItem(
                icon = painterResource(R.drawable.search),
                title = { Text(setting.title) },
                description = {
                  if (setting.description != null) {
                    Text("${setting.category} • ${setting.description}")
                  } else {
                    Text(setting.category)
                  }
                },
                onClick = {
                  val encodedTitle = android.net.Uri.encode(setting.title)
                  val finalRoute =
                    if (setting.route.contains("?")) "${setting.route}&highlightKey=$encodedTitle"
                    else "${setting.route}?highlightKey=$encodedTitle"
                  navController.navigate(finalRoute)
                }
              )
            }

        itemsList + matchedSubSettings
      } else {
        itemsList
      }

    if (searchQuery.isNotEmpty() && finalItemsList.isEmpty()) {
      Spacer(modifier = Modifier.height(32.dp))
      Text(
        text = "No settings found for \"$searchQuery\"",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
      )
    } else if (searchQuery.isNotEmpty()) {
      Spacer(modifier = Modifier.height(16.dp))
      Material3SettingsGroup(scrollState = scrollState, items = finalItemsList)
    } else {
      Spacer(modifier = Modifier.height(16.dp))

      // Groups mirror the reference layout: Account / Appearance / Playback / Library / Other.
      // Slicing by index is safe because every row above is added unconditionally when the query is
      // empty; the only variable-length tail is the "Other" group, which is why it takes the rest.
      val accountGroup = itemsList.take(2) // Account, AI Lyrics
      val appearanceGroup = itemsList.drop(2).take(1) // Appearance
      val playbackGroup = itemsList.drop(3).take(2) // Player, Listen Together
      val libraryGroup = itemsList.drop(5).take(4) // Listening Summary, Content, Storage, Backup
      val otherGroup = itemsList.drop(9) // Privacy, Update, [Supported Links], About

      if (accountGroup.isNotEmpty()) {
        Material3SettingsGroup(
          title = stringResource(R.string.settings_group_account),
          scrollState = scrollState,
          items = accountGroup
        )
        Spacer(modifier = Modifier.height(20.dp))
      }
      if (appearanceGroup.isNotEmpty()) {
        Material3SettingsGroup(
          title = stringResource(R.string.settings_group_appearance),
          scrollState = scrollState,
          items = appearanceGroup
        )
        Spacer(modifier = Modifier.height(20.dp))
      }
      if (playbackGroup.isNotEmpty()) {
        Material3SettingsGroup(
          title = stringResource(R.string.settings_group_playback),
          scrollState = scrollState,
          items = playbackGroup
        )
        Spacer(modifier = Modifier.height(20.dp))
      }
      if (libraryGroup.isNotEmpty()) {
        Material3SettingsGroup(
          title = stringResource(R.string.settings_group_library),
          scrollState = scrollState,
          items = libraryGroup
        )
        Spacer(modifier = Modifier.height(20.dp))
      }
      if (otherGroup.isNotEmpty()) {
        Material3SettingsGroup(
          title = stringResource(R.string.settings_group_other),
          scrollState = scrollState,
          items = otherGroup
        )
      }
    }

    Spacer(modifier = Modifier.height(50.dp))
    Spacer(
      Modifier.windowInsetsPadding(
        LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Bottom)
      )
    )
  }

      TopAppBar(
        title = {
          androidx.compose.animation.AnimatedVisibility(
            visible = scrollState.value > 100,
            enter = androidx.compose.animation.fadeIn(),
            exit = androidx.compose.animation.fadeOut()
          ) {
            Text(
              text = stringResource(R.string.settings),
              style = MaterialTheme.typography.titleLarge,
              color = Color.White,
              fontWeight = FontWeight.Bold
            )
          }
        },
        navigationIcon = {
          IconButton(onClick = navController::navigateUp, onLongClick = navController::backToMain) {
            Icon(
              painterResource(R.drawable.arrow_back),
              contentDescription = null,
              tint = TuneifyGlassTokens.VoltLime
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = Color.Transparent,
          scrolledContainerColor = Color(0xD906140A)
        )
      )
    }
  }
}
