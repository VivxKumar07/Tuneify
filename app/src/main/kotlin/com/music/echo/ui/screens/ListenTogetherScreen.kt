package echo.music.iad1tya.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import echo.music.iad1tya.ui.theme.TuneifyTokens
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton as MaterialIconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import echo.music.iad1tya.LocalListenTogetherManager
import echo.music.iad1tya.LocalPlayerAwareWindowInsets
import echo.music.iad1tya.R
import echo.music.iad1tya.constants.AppBarHeight
import echo.music.iad1tya.constants.ListenTogetherInTopBarKey
import echo.music.iad1tya.constants.ListenTogetherUsernameKey
import echo.music.iad1tya.listentogether.ConnectionState
import echo.music.iad1tya.listentogether.JoinRequestPayload
import echo.music.iad1tya.listentogether.ListenTogetherEvent
import echo.music.iad1tya.listentogether.SuggestionReceivedPayload
import echo.music.iad1tya.listentogether.UserInfo
import echo.music.iad1tya.ui.component.DefaultDialog
import echo.music.iad1tya.ui.component.IconButton
import echo.music.iad1tya.ui.component.Material3SettingsGroup
import echo.music.iad1tya.ui.component.Material3SettingsItem
import echo.music.iad1tya.ui.utils.backToMain
import echo.music.iad1tya.utils.rememberPreference
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ListenTogetherScreen(navController: NavController, showTopBar: Boolean = false) {
  val context = LocalContext.current
  val listenTogetherManager = LocalListenTogetherManager.current
  val windowInsets = LocalPlayerAwareWindowInsets.current

  if (listenTogetherManager == null) {
    NotConfiguredContent()
    return
  }

  val connectionState by listenTogetherManager.connectionState.collectAsState()
  val roomState by listenTogetherManager.roomState.collectAsState()
  val userId by listenTogetherManager.userId.collectAsState()
  val pendingJoinRequests by listenTogetherManager.pendingJoinRequests.collectAsState()
  val pendingSuggestions by listenTogetherManager.pendingSuggestions.collectAsState()

  val (listenTogetherInTopBar) = rememberPreference(ListenTogetherInTopBarKey, defaultValue = true)
  val shouldShowTopBar = showTopBar || listenTogetherInTopBar

  var savedUsername by rememberPreference(ListenTogetherUsernameKey, "")
  var roomCodeInput by rememberSaveable { mutableStateOf("") }
  var usernameInput by rememberSaveable { mutableStateOf(savedUsername) }

  var isCreatingRoom by rememberSaveable { mutableStateOf(false) }
  var isJoiningRoom by rememberSaveable { mutableStateOf(false) }
  var joinErrorMessage by rememberSaveable { mutableStateOf<String?>(null) }

  var selectedUserForMenu by rememberSaveable { mutableStateOf<String?>(null) }
  var selectedUsername by rememberSaveable { mutableStateOf<String?>(null) }

  val waitingForApprovalText = stringResource(R.string.waiting_for_approval)
  val invalidRoomCodeText = stringResource(R.string.invalid_room_code)
  val joinRequestDeniedText = stringResource(R.string.join_request_denied)

  LaunchedEffect(savedUsername) {
    if (usernameInput.isBlank() && savedUsername.isNotBlank()) {
      usernameInput = savedUsername
    }
  }

  LaunchedEffect(listenTogetherManager) {
    if (
      connectionState == ConnectionState.DISCONNECTED || connectionState == ConnectionState.ERROR
    ) {
      listenTogetherManager.connect()
    }
    listenTogetherManager.events.collect { event ->
      when (event) {
        is ListenTogetherEvent.JoinRejected -> {
          val reason = event.reason
          joinErrorMessage =
            when {
              reason.isNullOrBlank() -> joinRequestDeniedText
              reason.contains("invalid", ignoreCase = true) -> invalidRoomCodeText
              else -> "$joinRequestDeniedText: $reason"
            }
          isJoiningRoom = false
          isCreatingRoom = false
        }
        is ListenTogetherEvent.JoinApproved -> {
          isJoiningRoom = false
          joinErrorMessage = null
        }
        is ListenTogetherEvent.RoomCreated -> {
          isCreatingRoom = false
          val clipboard =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
          val clip = android.content.ClipData.newPlainText("ListenTogetherRoom", event.roomCode)
          clipboard.setPrimaryClip(clip)
        }
        else -> {}
      }
    }
  }

  val isInRoom = listenTogetherManager.isInRoom
  val isHost = roomState?.hostId == userId

  if (selectedUserForMenu != null && selectedUsername != null) {
    UserActionDialog(
      username = selectedUsername ?: "",
      onKick = {
        selectedUserForMenu?.let { listenTogetherManager.kickUser(it, "Removed by host") }
        selectedUserForMenu = null
        selectedUsername = null
      },
      onPermanentKick = {
        selectedUserForMenu?.let { userId ->
          selectedUsername?.let { username ->
            listenTogetherManager.blockUser(username)
            listenTogetherManager.kickUser(userId, R.string.user_blocked_by_host.toString())
          }
        }
        selectedUserForMenu = null
        selectedUsername = null
      },
      onTransferOwnership = {
        selectedUserForMenu?.let { listenTogetherManager.transferHost(it) }
        selectedUserForMenu = null
        selectedUsername = null
      },
      onDismiss = {
        selectedUserForMenu = null
        selectedUsername = null
      }
    )
  }

  val lazyListState = rememberLazyListState()
  val coroutineScope = rememberCoroutineScope()
  val bringIntoViewRequester = remember { BringIntoViewRequester() }

  val backStackEntry by navController.currentBackStackEntryAsState()
  val scrollToTop =
    backStackEntry?.savedStateHandle?.getStateFlow("scrollToTop", false)?.collectAsState()

  LaunchedEffect(scrollToTop?.value) {
    if (scrollToTop?.value == true) {
      lazyListState.animateScrollToItem(0)
      backStackEntry?.savedStateHandle?.set("scrollToTop", false)
    }
  }

  LazyColumn(
    state = lazyListState,
    modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).imePadding(),
    contentPadding =
      PaddingValues(
        start = 16.dp,
        end = 16.dp,
        top = windowInsets.asPaddingValues().calculateTopPadding() + 16.dp,
        bottom = windowInsets.asPaddingValues().calculateBottomPadding() + 16.dp + AppBarHeight
      ),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    if (connectionState == ConnectionState.CONNECTED && !isInRoom) {
      item {
        Text(
          text = stringResource(R.string.listen_together_background_disconnect_note),
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = TextAlign.Center,
          modifier = Modifier.fillMaxWidth()
        )
      }
    }

    if (isInRoom) {

      roomState?.let { room ->
        item {
          RoomStatusCard(
            roomCode = room.roomCode,
            isHost = isHost,
            allowParticipantControl = room.allowParticipantControl,
            onAllowParticipantControlChange = { enabled ->
              listenTogetherManager.updateRoomSettings(enabled)
            },
            context = context,
            navController = navController
          )
        }

        val connectedUsers = room.users.filter { it.isConnected }
        val currentUserIdValue = userId ?: ""
        item {
          ConnectedUsersSection(
            users = connectedUsers,
            isHost = isHost,
            currentUserId = currentUserIdValue,
            onUserClick = { clickedUserId, username ->
              if (isHost && clickedUserId != currentUserIdValue) {
                selectedUserForMenu = clickedUserId
                selectedUsername = username
              }
            }
          )
        }

        if (isHost && pendingJoinRequests.isNotEmpty()) {
          item {
            PendingJoinRequestsSection(
              requests = pendingJoinRequests,
              onApprove = { listenTogetherManager.approveJoin(it) },
              onReject = { listenTogetherManager.rejectJoin(it, "Rejected by host") }
            )
          }
        }

        if (isHost && pendingSuggestions.isNotEmpty()) {
          item {
            PendingSuggestionsSection(
              suggestions = pendingSuggestions,
              onApprove = { listenTogetherManager.approveSuggestion(it) },
              onReject = { listenTogetherManager.rejectSuggestion(it, "Rejected by host") }
            )
          }
        }

        item {
          Button(
            onClick = { listenTogetherManager.leaveRoom() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            shape = RoundedCornerShape(16.dp)
          ) {
            Icon(
              painter = painterResource(R.drawable.logout),
              contentDescription = null,
              modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.leave_room), fontWeight = FontWeight.SemiBold)
          }
        }
      }
    } else {

      item {
        JoinCreateRoomSection(
          usernameInput = usernameInput,
          onUsernameChange = { usernameInput = it },
          roomCodeInput = roomCodeInput,
          onRoomCodeChange = { roomCodeInput = it },
          savedUsername = savedUsername,
          isJoiningRoom = isJoiningRoom,
          isCreatingRoom = isCreatingRoom,
          joinErrorMessage = joinErrorMessage,
          waitingForApprovalText = waitingForApprovalText,
          bringIntoViewRequester = bringIntoViewRequester,
          onCreateRoom = {
            val username = usernameInput.takeIf { it.isNotBlank() } ?: savedUsername
            val finalUsername = username.trim()
            if (finalUsername.isNotBlank()) {
              savedUsername = finalUsername
              Toast.makeText(context, R.string.creating_room, Toast.LENGTH_SHORT).show()
              isCreatingRoom = true
              isJoiningRoom = false
              joinErrorMessage = null

              listenTogetherManager.connect()
              listenTogetherManager.createRoom(finalUsername)

              // Simulate a second click in the background to handle dropped first messages
              coroutineScope.launch {
                kotlinx.coroutines.delay(600)
                if (isCreatingRoom) {
                  listenTogetherManager.connect()
                  listenTogetherManager.createRoom(finalUsername)
                }
              }
            } else {
              Toast.makeText(context, R.string.error_username_empty, Toast.LENGTH_SHORT).show()
            }
          },
          onJoinRoom = {
            val username = usernameInput.takeIf { it.isNotBlank() } ?: savedUsername
            val finalUsername = username.trim()
            val finalRoomCode = roomCodeInput.trim()

            if (finalUsername.isNotBlank() && finalRoomCode.length == 8) {
              savedUsername = finalUsername
              Toast.makeText(
                  context,
                  context.getString(R.string.joining_room, finalRoomCode),
                  Toast.LENGTH_SHORT
                )
                .show()
              isJoiningRoom = true
              isCreatingRoom = false
              joinErrorMessage = null

              listenTogetherManager.connect()
              listenTogetherManager.joinRoom(finalRoomCode, finalUsername)

              // Simulate a second click in the background to handle dropped first messages
              coroutineScope.launch {
                kotlinx.coroutines.delay(600)
                if (isJoiningRoom) {
                  listenTogetherManager.connect()
                  listenTogetherManager.joinRoom(finalRoomCode, finalUsername)
                }
              }
            } else {
              Toast.makeText(context, R.string.error_username_empty, Toast.LENGTH_SHORT).show()
            }
          },
          onFieldFocused = { coroutineScope.launch { bringIntoViewRequester.bringIntoView() } }
        )
      }
    }

    if (isInRoom && isHost) {
      item {
        ParticipantControlCard(
          allowParticipantControl = roomState?.allowParticipantControl == true,
          onAllowParticipantControlChange = { enabled ->
            listenTogetherManager.updateRoomSettings(enabled)
          }
        )
      }
    }

    item {
      SettingsLinkCard(
        onClick = { navController.navigate("settings/integrations/listen_together") }
      )
    }

    if (!isInRoom) {
      item {
        val shape = RoundedCornerShape(8.dp)
        val isDark = isSystemInDarkTheme()
        val shadowColor = if (isDark) Color(0xFF000000) else Color(0xFF111111).copy(alpha = 0.85f)
        val borderColor = if (isDark) TuneifyTokens.Dark.Border else TuneifyTokens.Light.Border
        Card(
          modifier =
            Modifier.fillMaxWidth()
,
          shape = shape,
          border = BorderStroke(1.5.dp, borderColor),
          colors =
            CardDefaults.cardColors(containerColor = TuneifyTokens.Purple),
          elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
          Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Text(
              text = "HOW IT WORKS",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Black,
              letterSpacing = 0.5.sp,
              color = Color(0xFF111111)
            )

            InstructionStep(
              title = "1. Create a Room",
              description = "Start a session and share the unique code.",
              textColor = Color(0xFF111111)
            )

            InstructionStep(
              title = "2. Join a Friend",
              description = "Enter their room code to join.",
              textColor = Color(0xFF111111)
            )

            InstructionStep(
              title = "3. Sync Playback",
              description = "The host controls the music. Everyone listens together.",
              textColor = Color(0xFF111111)
            )
          }
        }
      }
    }
  }

  if (shouldShowTopBar) {
    TopAppBar(
      title = { Text(stringResource(R.string.listen_together)) },
      navigationIcon = {
        IconButton(onClick = navController::navigateUp, onLongClick = navController::backToMain) {
          Icon(painterResource(R.drawable.arrow_back), contentDescription = null)
        }
      },
      actions = {
        if (
          connectionState == ConnectionState.DISCONNECTED ||
            connectionState == ConnectionState.ERROR
        ) {
          TextButton(onClick = { listenTogetherManager.connect() }) {
            Text(stringResource(R.string.connect))
          }
        } else if (connectionState == ConnectionState.CONNECTED) {
          TextButton(onClick = { listenTogetherManager.disconnect() }) {
            Text(stringResource(R.string.disconnect))
          }
        } else {
          CircularProgressIndicator(
            modifier = Modifier.padding(end = 16.dp).size(20.dp),
            strokeWidth = 2.dp,
            color = MaterialTheme.colorScheme.onSurface
          )
        }
      }
    )
  }
}

@Composable
private fun InstructionStep(title: String, description: String, textColor: Color? = null) {
  Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
    Text(
      text = title,
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold,
      color = textColor ?: MaterialTheme.colorScheme.onSurface
    )
    Text(
      text = description,
      style = MaterialTheme.typography.bodyMedium,
      color = textColor?.copy(alpha = 0.85f) ?: MaterialTheme.colorScheme.onSurfaceVariant,
      lineHeight = 18.sp
    )
  }
}

@Composable
private fun NotConfiguredContent() {
  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
      Icon(
        painter = painterResource(R.drawable.group),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.size(64.dp)
      )
      Spacer(modifier = Modifier.height(16.dp))
      Text(
        text = stringResource(R.string.listen_together),
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = stringResource(R.string.listen_together_not_configured),
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

@Composable
private fun ConnectionStatusCard(
  connectionState: ConnectionState,
  onConnect: () -> Unit,
  onDisconnect: () -> Unit,
  onReconnect: () -> Unit
) {
  Card(
    modifier =
      Modifier.fillMaxWidth()
        .animateContentSize(
          animationSpec =
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        ),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
      ) {
        Box(
          modifier =
            Modifier.size(10.dp)
              .clip(CircleShape)
              .background(
                color =
                  when (connectionState) {
                    ConnectionState.CONNECTED -> MaterialTheme.colorScheme.onSurface
                    ConnectionState.CONNECTING,
                    ConnectionState.RECONNECTING -> MaterialTheme.colorScheme.tertiary
                    ConnectionState.ERROR -> MaterialTheme.colorScheme.error
                    ConnectionState.DISCONNECTED -> MaterialTheme.colorScheme.outline
                  }
              )
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text =
            when (connectionState) {
              ConnectionState.CONNECTED -> stringResource(R.string.listen_together_connected)
              ConnectionState.CONNECTING -> stringResource(R.string.listen_together_connecting)
              ConnectionState.RECONNECTING -> stringResource(R.string.listen_together_reconnecting)
              ConnectionState.ERROR -> stringResource(R.string.listen_together_error)
              ConnectionState.DISCONNECTED -> stringResource(R.string.listen_together_disconnected)
            },
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color =
            when (connectionState) {
              ConnectionState.CONNECTED -> MaterialTheme.colorScheme.onSurface
              ConnectionState.CONNECTING,
              ConnectionState.RECONNECTING -> MaterialTheme.colorScheme.tertiary
              ConnectionState.ERROR -> MaterialTheme.colorScheme.error
              ConnectionState.DISCONNECTED -> MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
      }

      if (
        connectionState == ConnectionState.CONNECTING ||
          connectionState == ConnectionState.RECONNECTING
      ) {
        Spacer(modifier = Modifier.height(12.dp))
        LinearProgressIndicator(
          modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
          color = MaterialTheme.colorScheme.onSurface
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        if (
          connectionState == ConnectionState.DISCONNECTED ||
            connectionState == ConnectionState.ERROR
        ) {
          Button(
            onClick = onConnect,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
            colors =
              ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSurface)
          ) {
            Icon(
              painter = painterResource(R.drawable.link),
              contentDescription = null,
              modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(stringResource(R.string.connect), fontWeight = FontWeight.SemiBold)
          }
        } else {
          Button(
            onClick = onDisconnect,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
            colors =
              ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSurface)
          ) {
            Text(stringResource(R.string.disconnect), fontWeight = FontWeight.SemiBold)
          }
          FilledTonalButton(
            onClick = onReconnect,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text("Reconnect", fontWeight = FontWeight.SemiBold)
          }
        }
      }
    }
  }
}

@Composable
private fun RoomStatusCard(
  roomCode: String,
  isHost: Boolean,
  allowParticipantControl: Boolean,
  onAllowParticipantControlChange: (Boolean) -> Unit,
  context: Context,
  navController: NavController
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = stringResource(R.string.room_code),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
      )
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = roomCode,
        style = MaterialTheme.typography.displaySmall,
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.Bold,
        letterSpacing = 6.sp,
        textAlign = TextAlign.Center
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text =
          if (isHost) stringResource(R.string.listen_together_you_are_host)
          else if (allowParticipantControl)
            stringResource(R.string.listen_together_participant_control_enabled)
          else stringResource(R.string.listen_together_you_are_guest),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
      )

      if (isHost) {
        Spacer(modifier = Modifier.height(16.dp))
        val inviteLink =
          remember(roomCode) { "https://metroserverx.meowery.eu/listen?code=$roomCode" }
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
          modifier = Modifier.fillMaxWidth()
        ) {
          FilledTonalButton(
            onClick = {
              val clipboard =
                context.getSystemService(Context.CLIPBOARD_SERVICE)
                  as android.content.ClipboardManager
              val clip = android.content.ClipData.newPlainText("Listen Together Link", inviteLink)
              clipboard.setPrimaryClip(clip)
              Toast.makeText(context, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show()
            },
            shape = RoundedCornerShape(8.dp)
          ) {
            Icon(
              painter = painterResource(R.drawable.link),
              contentDescription = stringResource(R.string.copy_link),
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.copy_link))
          }

          FilledTonalButton(
            onClick = {
              val clipboard =
                context.getSystemService(Context.CLIPBOARD_SERVICE)
                  as android.content.ClipboardManager
              val clip = android.content.ClipData.newPlainText("Room Code", roomCode)
              clipboard.setPrimaryClip(clip)
              Toast.makeText(context, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show()
            },
            shape = RoundedCornerShape(8.dp)
          ) {
            Icon(
              painter = painterResource(R.drawable.content_copy),
              contentDescription = stringResource(R.string.copy_code),
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.copy_code))
          }
        }
      }
    }
  }
}

@Composable
private fun ConnectedUsersSection(
  users: List<UserInfo>,
  isHost: Boolean,
  currentUserId: String,
  onUserClick: (String, String) -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text(
        text = "${stringResource(R.string.connected_users)} (${users.size})",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        users.forEach { user ->
          UserAvatar(
            user = user,
            isCurrentUser = user.userId == currentUserId,
            isClickable = isHost && user.userId != currentUserId,
            onClick = { onUserClick(user.userId, user.username) }
          )
        }
      }
    }
  }
}

@Composable
private fun UserAvatar(
  user: UserInfo,
  isCurrentUser: Boolean,
  isClickable: Boolean,
  onClick: () -> Unit
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.width(72.dp).clickable(enabled = isClickable, onClick = onClick)
  ) {
    Box(contentAlignment = Alignment.Center) {
      Surface(
        modifier = Modifier.size(56.dp),
        shape = CircleShape,
        color =
          when {
            user.isHost -> MaterialTheme.colorScheme.onSurface
            isCurrentUser -> MaterialTheme.colorScheme.surfaceContainerHigh
            else -> MaterialTheme.colorScheme.surfaceVariant
          }
      ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
          Text(
            text = user.username.take(1).uppercase(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color =
              when {
                user.isHost -> MaterialTheme.colorScheme.surface
                isCurrentUser -> MaterialTheme.colorScheme.onSurface
                else -> MaterialTheme.colorScheme.onSurfaceVariant
              }
          )
        }
      }

      if (user.isHost || isCurrentUser) {
        Surface(
          modifier = Modifier.align(Alignment.BottomEnd).offset(x = 4.dp, y = 4.dp).size(20.dp),
          shape = CircleShape,
          color =
            if (user.isHost) MaterialTheme.colorScheme.onSurface
            else MaterialTheme.colorScheme.onSurfaceVariant
        ) {
          Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Icon(
              painter = painterResource(if (user.isHost) R.drawable.crown else R.drawable.person),
              contentDescription = null,
              tint = MaterialTheme.colorScheme.surface,
              modifier = Modifier.size(12.dp)
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    Text(
      text = user.username,
      style = MaterialTheme.typography.labelMedium,
      fontWeight = if (isCurrentUser) FontWeight.Bold else FontWeight.Medium,
      color =
        if (user.isHost) MaterialTheme.colorScheme.onSurface
        else MaterialTheme.colorScheme.onSurface,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      textAlign = TextAlign.Center
    )

    if (user.isHost) {
      Text(
        text = stringResource(R.string.host_label),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
      )
    } else if (isCurrentUser) {
      Text(
        text = stringResource(R.string.you_label),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

@Composable
private fun PendingJoinRequestsSection(
  requests: List<JoinRequestPayload>,
  onApprove: (String) -> Unit,
  onReject: (String) -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text(
        text = stringResource(R.string.listen_together_join_requests),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(12.dp))

      requests.forEach { request ->
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
          Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainerHigh
          ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
              Text(
                text = request.username.take(1).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }
          Spacer(Modifier.width(12.dp))
          Text(
            text = request.username,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
          )
          MaterialIconButton(onClick = { onApprove(request.userId) }) {
            Icon(
              painter = painterResource(R.drawable.check),
              contentDescription = stringResource(R.string.approve),
              tint = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.size(24.dp)
            )
          }
          MaterialIconButton(onClick = { onReject(request.userId) }) {
            Icon(
              painter = painterResource(R.drawable.close),
              contentDescription = stringResource(R.string.reject),
              tint = MaterialTheme.colorScheme.error,
              modifier = Modifier.size(24.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
private fun PendingSuggestionsSection(
  suggestions: List<SuggestionReceivedPayload>,
  onApprove: (String) -> Unit,
  onReject: (String) -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text(
        text = stringResource(R.string.pending_suggestions),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(12.dp))

      suggestions.forEach { suggestion ->
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
          Icon(
            painter = painterResource(R.drawable.queue_music),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(24.dp)
          )
          Spacer(Modifier.width(12.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = suggestion.trackInfo.title,
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.Medium,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Text(
              text = suggestion.fromUsername,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
          MaterialIconButton(onClick = { onApprove(suggestion.suggestionId) }) {
            Icon(
              painter = painterResource(R.drawable.check),
              contentDescription = stringResource(R.string.approve),
              tint = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.size(24.dp)
            )
          }
          MaterialIconButton(onClick = { onReject(suggestion.suggestionId) }) {
            Icon(
              painter = painterResource(R.drawable.close),
              contentDescription = stringResource(R.string.reject),
              tint = MaterialTheme.colorScheme.error,
              modifier = Modifier.size(24.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
private fun JoinCreateRoomSection(
  usernameInput: String,
  onUsernameChange: (String) -> Unit,
  roomCodeInput: String,
  onRoomCodeChange: (String) -> Unit,
  savedUsername: String,
  isJoiningRoom: Boolean,
  isCreatingRoom: Boolean,
  joinErrorMessage: String?,
  waitingForApprovalText: String,
  bringIntoViewRequester: BringIntoViewRequester,
  onCreateRoom: () -> Unit,
  onJoinRoom: () -> Unit,
  onFieldFocused: () -> Unit = {}
) {
  var selectedTab by rememberSaveable { mutableStateOf(0) }

  val shape = RoundedCornerShape(8.dp)
  val isDark = isSystemInDarkTheme()
  val shadowColor = if (isDark) Color(0xFF000000) else Color(0xFF111111).copy(alpha = 0.85f)
  val borderColor = if (isDark) TuneifyTokens.Dark.Border else TuneifyTokens.Light.Border

  Card(
    modifier =
      Modifier.fillMaxWidth()
,
    shape = shape,
    border = BorderStroke(1.5.dp, borderColor),
    colors = CardDefaults.cardColors(
      containerColor = if (isDark) TuneifyTokens.Dark.Surface else TuneifyTokens.Light.Surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(18.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Row(
        modifier =
          Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isDark) Color(0xFF1A1A1A) else Color(0xFFF2EFE6))
            .border(BorderStroke(1.dp, borderColor.copy(alpha = 0.5f)), RoundedCornerShape(8.dp)),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
      ) {
        Button(
          onClick = { selectedTab = 0 },
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(8.dp),
          colors =
            ButtonDefaults.buttonColors(
              containerColor =
                if (selectedTab == 0) TuneifyTokens.Yellow else Color.Transparent,
              contentColor =
                if (selectedTab == 0) Color(0xFF111111)
                else if (isDark) TuneifyTokens.Dark.TextSecondary else TuneifyTokens.Light.TextSecondary
            ),
          elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
          Icon(
            painter = painterResource(R.drawable.add),
            contentDescription = null,
            tint = if (selectedTab == 0) Color(0xFF111111) else if (isDark) TuneifyTokens.Dark.TextSecondary else TuneifyTokens.Light.TextSecondary,
            modifier = Modifier.size(18.dp)
          )
          Spacer(Modifier.width(6.dp))
          Text(stringResource(R.string.create_room), fontWeight = FontWeight.Bold)
        }
        Button(
          onClick = { selectedTab = 1 },
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(8.dp),
          colors =
            ButtonDefaults.buttonColors(
              containerColor =
                if (selectedTab == 1) TuneifyTokens.Yellow else Color.Transparent,
              contentColor =
                if (selectedTab == 1) Color(0xFF111111)
                else if (isDark) TuneifyTokens.Dark.TextSecondary else TuneifyTokens.Light.TextSecondary
            ),
          elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
          Icon(
            painter = painterResource(R.drawable.login),
            contentDescription = null,
            tint = if (selectedTab == 1) Color(0xFF111111) else if (isDark) TuneifyTokens.Dark.TextSecondary else TuneifyTokens.Light.TextSecondary,
            modifier = Modifier.size(18.dp)
          )
          Spacer(Modifier.width(6.dp))
          Text(stringResource(R.string.join_room), fontWeight = FontWeight.Bold)
        }
      }

      // Retro Illustrated Banner: MUSIC SOUNDS BETTER TOGETHER
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(Color(0xFF171717))
          .border(BorderStroke(1.dp, borderColor.copy(alpha = 0.4f)), RoundedCornerShape(8.dp))
          .padding(horizontal = 16.dp, vertical = 12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "MUSIC\nSOUNDS\nBETTER\nTOGETHER.",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 0.8.sp,
                lineHeight = 16.sp,
                fontSize = 14.sp
              ),
              color = Color(0xFFF5F1E8)
            )
          }
          Box(
            modifier = Modifier
              .size(52.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(TuneifyTokens.Yellow)
              .border(1.dp, Color(0xFF111111), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              painter = painterResource(R.drawable.group),
              contentDescription = null,
              tint = Color(0xFF111111),
              modifier = Modifier.size(30.dp)
            )
          }
        }
      }

      OutlinedTextField(
        value = usernameInput,
        onValueChange = onUsernameChange,
        label = { Text(stringResource(R.string.username)) },
        placeholder = { Text(stringResource(R.string.enter_username)) },
        leadingIcon = {
          Icon(painterResource(R.drawable.person), null, tint = MaterialTheme.colorScheme.onSurface)
        },
        trailingIcon = {
          if (usernameInput.isNotBlank()) {
            MaterialIconButton(onClick = { onUsernameChange("") }) {
              Icon(painterResource(R.drawable.close), null)
            }
          }
        },
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        colors =
          OutlinedTextFieldDefaults.colors(
            focusedBorderColor = borderColor,
            unfocusedBorderColor = borderColor.copy(alpha = 0.5f),
            focusedContainerColor = if (isDark) TuneifyTokens.Dark.Elevated else Color(0xFFFBF8F1),
            unfocusedContainerColor = if (isDark) TuneifyTokens.Dark.Elevated else Color(0xFFFBF8F1)
          ),
        modifier = Modifier.fillMaxWidth().onFocusChanged { if (it.isFocused) onFieldFocused() }
      )

      AnimatedVisibility(
        visible = selectedTab == 1,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
      ) {
        OutlinedTextField(
          value = roomCodeInput,
          onValueChange = { if (it.length <= 8) onRoomCodeChange(it.uppercase()) },
          label = { Text(stringResource(R.string.room_code)) },
          placeholder = { Text(stringResource(R.string.enter_room_code)) },
          leadingIcon = {
            Icon(
              painterResource(R.drawable.group),
              null,
              tint = MaterialTheme.colorScheme.onSurface
            )
          },
          trailingIcon = {
            if (roomCodeInput.isNotBlank()) {
              MaterialIconButton(onClick = { onRoomCodeChange("") }) {
                Icon(painterResource(R.drawable.close), null)
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(8.dp),
          colors =
            OutlinedTextFieldDefaults.colors(
              focusedBorderColor = borderColor,
              unfocusedBorderColor = borderColor.copy(alpha = 0.5f),
              focusedContainerColor = if (isDark) TuneifyTokens.Dark.Elevated else Color(0xFFFBF8F1),
              unfocusedContainerColor = if (isDark) TuneifyTokens.Dark.Elevated else Color(0xFFFBF8F1)
            ),
          modifier =
            Modifier.fillMaxWidth().bringIntoViewRequester(bringIntoViewRequester).onFocusChanged {
              if (it.isFocused) onFieldFocused()
            }
        )
      }

      AnimatedVisibility(
        visible = isJoiningRoom,
        enter = fadeIn() + slideInVertically(),
        exit = fadeOut() + slideOutVertically()
      ) {
        Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(8.dp),
          color = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
          ) {
            CircularProgressIndicator(
              modifier = Modifier.size(20.dp),
              strokeWidth = 2.dp,
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
              text = waitingForApprovalText,
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurface,
              fontWeight = FontWeight.Medium,
              textAlign = TextAlign.Center
            )
          }
        }
      }

      AnimatedVisibility(
        visible = joinErrorMessage != null,
        enter = fadeIn() + slideInVertically(),
        exit = fadeOut() + slideOutVertically()
      ) {
        Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(8.dp),
          color = MaterialTheme.colorScheme.errorContainer
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
          ) {
            Icon(
              painterResource(R.drawable.error),
              contentDescription = null,
              modifier = Modifier.size(20.dp),
              tint = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
              text = joinErrorMessage ?: "",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onErrorContainer,
              fontWeight = FontWeight.Medium,
              textAlign = TextAlign.Center
            )
          }
        }
      }

      val hasUsername = usernameInput.trim().isNotBlank() || savedUsername.isNotBlank()
      val hasRoomCode = roomCodeInput.length == 8

      if (selectedTab == 0) {
        val btnShape = RoundedCornerShape(8.dp)
        Button(
          onClick = onCreateRoom,
          modifier =
            Modifier.fillMaxWidth()
,
          enabled = hasUsername && !isCreatingRoom && !isJoiningRoom,
          shape = btnShape,
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
          colors =
            ButtonDefaults.buttonColors(
              containerColor = TuneifyTokens.SignatureYellow,
              contentColor = Color(0xFF111111),
              disabledContainerColor = TuneifyTokens.SignatureYellow.copy(alpha = 0.5f),
              disabledContentColor = Color(0xFF111111).copy(alpha = 0.5f)
            )
        ) {
          Icon(
            painter = painterResource(R.drawable.add),
            contentDescription = null,
            tint = Color(0xFF111111),
            modifier = Modifier.size(20.dp)
          )
          Spacer(Modifier.width(8.dp))
          Text(
            stringResource(R.string.create_room),
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111111)
          )
        }
      } else {
        val btnShape = RoundedCornerShape(8.dp)
        Button(
          onClick = onJoinRoom,
          modifier =
            Modifier.fillMaxWidth()
,
          enabled = hasUsername && hasRoomCode && !isCreatingRoom && !isJoiningRoom,
          shape = btnShape,
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
          colors =
            ButtonDefaults.buttonColors(
              containerColor = TuneifyTokens.SignatureYellow,
              contentColor = Color(0xFF111111),
              disabledContainerColor = TuneifyTokens.SignatureYellow.copy(alpha = 0.5f),
              disabledContentColor = Color(0xFF111111).copy(alpha = 0.5f)
            )
        ) {
          Icon(
            painter = painterResource(R.drawable.login),
            contentDescription = null,
            tint = Color(0xFF111111),
            modifier = Modifier.size(20.dp)
          )
          Spacer(Modifier.width(8.dp))
          Text(
            stringResource(R.string.join_room),
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111111)
          )
        }
      }
    }
  }
}

@Composable
private fun SettingsLinkCard(onClick: () -> Unit) {
  Material3SettingsGroup(
    items =
      listOf(
        Material3SettingsItem(
          icon = painterResource(R.drawable.settings),
          title = { Text(stringResource(R.string.settings)) },
          description = { Text(stringResource(R.string.listen_together_settings_desc)) },
          onClick = onClick
        )
      )
  )
}

@Composable
private fun ParticipantControlCard(
  allowParticipantControl: Boolean,
  onAllowParticipantControlChange: (Boolean) -> Unit
) {
  Material3SettingsGroup(
    items =
      listOf(
        Material3SettingsItem(
          icon = painterResource(R.drawable.music_note),
          title = { Text(stringResource(R.string.listen_together_allow_participant_control)) },
          description = {
            Text(stringResource(R.string.listen_together_allow_participant_control_desc))
          },
          trailingContent = {
            Switch(
              checked = allowParticipantControl,
              onCheckedChange = onAllowParticipantControlChange
            )
          },
          onClick = { onAllowParticipantControlChange(!allowParticipantControl) }
        )
      )
  )
}

@Composable
private fun UserActionDialog(
  username: String,
  onKick: () -> Unit,
  onPermanentKick: () -> Unit,
  onTransferOwnership: () -> Unit,
  onDismiss: () -> Unit
) {
  DefaultDialog(
    onDismiss = onDismiss,
    icon = {
      Icon(
        painter = painterResource(R.drawable.group),
        contentDescription = null,
        modifier = Modifier.size(28.dp)
      )
    },
    title = {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = stringResource(R.string.manage_user), fontWeight = FontWeight.Bold)
        Text(
          text = username,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    },
    buttons = { TextButton(onClick = onDismiss) { Text(stringResource(android.R.string.cancel)) } }
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onKick),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.errorContainer
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
          Icon(
            painter = painterResource(R.drawable.close),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(16.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = stringResource(R.string.kick_user),
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.error
            )
            Text(
              text = stringResource(R.string.kick_user_desc),
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onPermanentKick),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
          Icon(
            painter = painterResource(R.drawable.close),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(16.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = stringResource(R.string.permanently_kick_user),
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.SemiBold
            )
            Text(
              text = stringResource(R.string.permanently_kick_user_desc),
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onTransferOwnership),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
          Icon(
            painter = painterResource(R.drawable.crown),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(16.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = stringResource(R.string.transfer_ownership),
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = stringResource(R.string.transfer_ownership_desc),
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }
  }
}
