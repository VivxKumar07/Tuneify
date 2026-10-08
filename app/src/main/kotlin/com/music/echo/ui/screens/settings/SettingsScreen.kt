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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import echo.music.iad1tya.LocalPlayerAwareWindowInsets
import echo.music.iad1tya.R
import echo.music.iad1tya.echomusic.updater.getUpdateAvailableState
import echo.music.iad1tya.ui.component.IconButton
import echo.music.iad1tya.ui.component.Material3SettingsGroup
import echo.music.iad1tya.ui.component.Material3SettingsItem
import echo.music.iad1tya.ui.screens.Screens
import echo.music.iad1tya.ui.utils.backToMain
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import echo.music.iad1tya.LocalDatabase
import echo.music.iad1tya.constants.AccountChannelHandleKey
import echo.music.iad1tya.constants.AccountNameKey
import echo.music.iad1tya.db.entities.PlaylistEntity
import echo.music.iad1tya.utils.rememberPreference

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

  val (innerTubeCookie) = rememberPreference(echo.music.iad1tya.constants.InnerTubeCookieKey, "")
  val (accountNamePref, _) = rememberPreference(AccountNameKey, "")
  val (accountChannelHandle, _) = rememberPreference(AccountChannelHandleKey, "")
  val isLoggedIn = remember(innerTubeCookie) { "SAPISID" in com.music.innertube.utils.parseCookieString(innerTubeCookie) }
  val displayName = if (isLoggedIn && accountNamePref.isNotBlank()) accountNamePref else "Guest"
  val displayHandle = if (isLoggedIn && accountChannelHandle.isNotBlank()) "@$accountChannelHandle" else "Tap to sign in"

  val scrollState = rememberScrollState()
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

    ProfileHeader(
      navController = navController,
      userName = displayName,
      userHandle = displayHandle,
      userImageUrl = null,
      onProfileClick = { navController.navigate("settings/account") },
      onSettingsClick = { /* scroll or focus */ }
    )

    Text(
      text = stringResource(R.string.settings).uppercase(),
      style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 12.dp)
    )

    TextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      placeholder = { Text(stringResource(R.string.search)) },
      leadingIcon = {
        Icon(
          imageVector = Icons.Rounded.Search,
          contentDescription = stringResource(R.string.search)
        )
      },
      trailingIcon = {
        if (searchQuery.isNotEmpty()) {
          IconButton(onClick = { searchQuery = "" }) {
            Icon(imageVector = Icons.Rounded.Clear, contentDescription = "Clear")
          }
        }
      },
      shape = RoundedCornerShape(100),
      colors =
        TextFieldDefaults.colors(
          focusedContainerColor = Color(0xFF141722),
          unfocusedContainerColor = Color(0xFF141722),
          focusedIndicatorColor = Color.Transparent,
          unfocusedIndicatorColor = Color.Transparent,
          disabledIndicatorColor = Color.Transparent
        ),
      modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 6.dp)
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
            customIcon = {
              Text(
                text = "Ai",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color =
                  if (highlightKey == aiLyricsText) MaterialTheme.colorScheme.onSurface
                  else MaterialTheme.colorScheme.onSurfaceVariant
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
            title = { Text(listenTogetherText) },
            description = { Text(listenTogetherDesc) },
            onClick = { navController.navigate(Screens.ListenTogether.route) }
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
            title = { Text(contentText) },
            description = { Text(contentDesc) },
            onClick = { navController.navigate("settings/content") }
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
            title = { Text(privacyText) },
            description = { Text(privacyDesc) },
            onClick = { navController.navigate("settings/privacy") }
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
            title = { Text(backupText) },
            description = { Text(backupDesc) },
            onClick = { navController.navigate("settings/backup_restore") }
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

      // Let's just create groups manually using itemsList which preserves order
      val accountGroup = itemsList.take(2) // Account, AI
      val playerGroup = itemsList.drop(2).take(3) // Appearance, Player, Listen Together
      val dataGroup = itemsList.drop(5).take(4) // Content, Privacy, Storage, Backup
      val systemGroup = itemsList.drop(9) // Update, Links, About

      if (accountGroup.isNotEmpty()) {
        Material3SettingsGroup(scrollState = scrollState, items = accountGroup)
        Spacer(modifier = Modifier.height(16.dp))
      }
      if (playerGroup.isNotEmpty()) {
        Material3SettingsGroup(scrollState = scrollState, items = playerGroup)
        Spacer(modifier = Modifier.height(16.dp))
      }
      if (dataGroup.isNotEmpty()) {
        Material3SettingsGroup(scrollState = scrollState, items = dataGroup)
        Spacer(modifier = Modifier.height(16.dp))
      }
      if (systemGroup.isNotEmpty()) {
        Material3SettingsGroup(scrollState = scrollState, items = systemGroup)
      }
    }

    Spacer(modifier = Modifier.height(50.dp))
    Spacer(
      Modifier.windowInsetsPadding(
        LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Bottom)
      )
    )
  }

  val isRoot = navController.previousBackStackEntry == null ||
      navController.currentDestination?.route == Screens.Profile.route ||
      navController.currentDestination?.route == "settings"

  if (!isRoot) {
    TopAppBar(
      title = {
        androidx.compose.animation.AnimatedVisibility(
          visible = scrollState.value > 100,
          enter = androidx.compose.animation.fadeIn(),
          exit = androidx.compose.animation.fadeOut()
        ) {
          Text(text = stringResource(R.string.settings), style = MaterialTheme.typography.titleLarge)
        }
      },
      navigationIcon = {
        IconButton(onClick = navController::navigateUp, onLongClick = navController::backToMain) {
          Icon(painterResource(R.drawable.arrow_back), contentDescription = null)
        }
      }
    )
  }
}

@Composable
fun ProfileHeader(
  navController: NavController,
  userName: String,
  userHandle: String,
  userImageUrl: String?,
  onProfileClick: () -> Unit,
  onSettingsClick: () -> Unit
) {
  val database = LocalDatabase.current
  val likedCount by database.likedSongsCount().collectAsState(initial = 0)
  val playlists by database.playlistsByNameAsc().collectAsState(initial = emptyList())
  val downloadedSongs by database.downloadedSongsByCreateDateAsc().collectAsState(initial = emptyList())
  val historyEvents by database.events().collectAsState(initial = emptyList())

  Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 12.dp)) {
    // Top Bar: "Profile" title (No gear button)
    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Profile",
        style = MaterialTheme.typography.headlineLarge.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 28.sp
        ),
        color = Color.White
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Profile Info Row: Avatar + Name + Handle
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(64.dp)
          .clip(CircleShape)
          .background(Color(0xFF1E2230)),
        contentAlignment = Alignment.Center
      ) {
        if (!userImageUrl.isNullOrBlank()) {
          AsyncImage(
            model = userImageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
        } else {
          Icon(
            painter = painterResource(R.drawable.person),
            contentDescription = null,
            tint = Color(0xFFF1F5F9),
            modifier = Modifier.size(36.dp)
          )
        }
      }

      Spacer(modifier = Modifier.width(16.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = userName,
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
          ),
          color = Color.White
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(
          modifier = Modifier.clickable(onClick = onProfileClick),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = userHandle,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF8E95A5)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Icon(
            painter = painterResource(R.drawable.navigate_next),
            contentDescription = null,
            tint = Color(0xFF8E95A5),
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // 4 Quick Action Items in 2x2 uncolored Obsidian Dark tile grid matching Search browse tile structure
    val likedSubtitle = if (likedCount > 0) "$likedCount songs" else "0 songs"
    val playlistSubtitle = if (playlists.isNotEmpty()) "${playlists.size} playlists" else "0 playlists"
    val downloadSubtitle = if (downloadedSongs.isNotEmpty()) "${downloadedSongs.size} songs" else "0 songs"
    val historySubtitle = if (historyEvents.isNotEmpty()) "${historyEvents.size} tracks" else "0 tracks"

    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        ProfileQuickActionTile(
          iconRes = R.drawable.favorite_border,
          title = "Liked Songs",
          subtitle = likedSubtitle,
          onClick = { navController.navigate("auto_playlist/liked") },
          modifier = Modifier.weight(1f)
        )
        ProfileQuickActionTile(
          iconRes = R.drawable.queue_music,
          title = "Playlists",
          subtitle = playlistSubtitle,
          onClick = { navController.navigate(Screens.Library.route) },
          modifier = Modifier.weight(1f)
        )
      }
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        ProfileQuickActionTile(
          iconRes = R.drawable.download,
          title = "Downloads",
          subtitle = downloadSubtitle,
          onClick = { navController.navigate("auto_playlist/downloaded") },
          modifier = Modifier.weight(1f)
        )
        ProfileQuickActionTile(
          iconRes = R.drawable.history,
          title = "Listening History",
          subtitle = historySubtitle,
          onClick = { navController.navigate("history") },
          modifier = Modifier.weight(1f)
        )
      }
    }
  }
}

@Composable
private fun ProfileQuickActionTile(
  iconRes: Int,
  title: String,
  subtitle: String?,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .height(68.dp)
      .clip(RoundedCornerShape(16.dp))
      .background(Color(0xFF141722))
      .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 14.dp, vertical = 8.dp),
    contentAlignment = Alignment.CenterStart
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Start
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(Color(0xFF1E2230)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          painter = painterResource(iconRes),
          contentDescription = null,
          tint = Color(0xFFF1F5F9),
          modifier = Modifier.size(18.dp)
        )
      }
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Text(
          text = title,
          style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          ),
          color = Color.White,
          maxLines = 1,
          overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
        if (subtitle != null) {
          Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = Color(0xFF8E95A5),
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
          )
        }
      }
    }
  }
}

