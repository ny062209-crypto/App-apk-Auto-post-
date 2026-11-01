package com.kira.autopost.tdlib

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.kira.autopost.data.AppPreferences
import com.kira.autopost.model.PostReport
import com.kira.autopost.model.PostStatus
import com.kira.autopost.model.TelegramAccount
import com.kira.autopost.model.TelegramGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.drinkless.tdlib.Client
import org.drinkless.tdlib.TdApi
import java.io.File
import kotlin.random.Random

sealed class AuthState {
    object Idle : AuthState()
    object WaitPhoneNumber : AuthState()
    object WaitCode : AuthState()
    object WaitPassword : AuthState()
    data class Authenticated(val user: TdApi.User) : AuthState()
    data class Error(val message: String) : AuthState()
}

class TdLibManager(
    private val context: Context,
    private val appPreferences: AppPreferences
) : Client.ResultHandler {

    private val tag = "TdLibManager"
    private var client: Client? = null
    private val scope = CoroutineScope(Dispatchers.IO)
    private val mainHandler = Handler(Looper.getMainLooper())

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _groups = MutableStateFlow<List<TelegramGroup>>(emptyList())
    val groups: StateFlow<List<TelegramGroup>> = _groups.asStateFlow()

    private var currentApiId: Int = 0
    private var currentApiHash: String = ""
    private var isTdLibLibraryLoaded = false

    init {
        try {
            System.loadLibrary("tdjni")
            isTdLibLibraryLoaded = true
            Log.d(tag, "TDLib native library loaded successfully")
        } catch (t: Throwable) {
            Log.w(tag, "Native TDLib library not loaded yet: ${t.message}")
        }

        loadInitialGroups()
    }

    private fun loadInitialGroups() {
        _groups.value = listOf(
            TelegramGroup(
                id = 2001L,
                title = "KIRA Gaming Community KH",
                memberCount = 14200,
                photoPath = null,
                isChannel = false,
                isSelected = true
            ),
            TelegramGroup(
                id = 2002L,
                title = "Telegram Auto Post & Tools KH",
                memberCount = 8540,
                photoPath = null,
                isChannel = false,
                isSelected = true
            ),
            TelegramGroup(
                id = 2003L,
                title = "Crypto & Airdrop Cambodia",
                memberCount = 22100,
                photoPath = null,
                isChannel = false,
                isSelected = false
            ),
            TelegramGroup(
                id = 2004L,
                title = "E-Commerce & Online Shop KH",
                memberCount = 6300,
                photoPath = null,
                isChannel = false,
                isSelected = false
            )
        )
    }

    fun startClient(apiId: Int, apiHash: String) {
        if (!isTdLibLibraryLoaded) {
            Log.i(tag, "Simulating TDLib auth state transition")
            _authState.value = AuthState.WaitPhoneNumber
            return
        }

        this.currentApiId = apiId
        this.currentApiHash = apiHash

        try {
            client = Client.create(this, null, null)
            val dbDir = File(context.filesDir, "tdlib_db").absolutePath

            val parameters = TdApi.SetTdlibParameters()
            parameters.databaseDirectory = dbDir
            parameters.useMessageDatabase = true
            parameters.useSecretChats = false
            parameters.apiId = currentApiId
            parameters.apiHash = currentApiHash
            parameters.systemLanguageCode = "en"
            parameters.deviceModel = "Android"
            parameters.applicationVersion = "1.0.0"

            client?.send(parameters, this)
        } catch (e: Exception) {
            Log.e(tag, "Error starting TDLib client", e)
            _authState.value = AuthState.Error(e.message ?: "Failed to start TDLib")
        }
    }

    fun sendPhoneNumber(phone: String, apiId: Int = 0, apiHash: String = "") {
        if (!isTdLibLibraryLoaded) {
            _authState.value = AuthState.WaitCode
            return
        }
        val req = TdApi.SetAuthenticationPhoneNumber(phone, null)
        client?.send(req, this)
    }

    fun checkCode(code: String) {
        if (!isTdLibLibraryLoaded) {
            val user = TdApi.User()
            user.id = 998877L
            user.firstName = "KIRA"
            user.lastName = "Gaming"
            _authState.value = AuthState.Authenticated(user)
            return
        }
        val req = TdApi.CheckAuthenticationCode(code)
        client?.send(req, this)
    }

    fun checkPassword(password: String) {
        if (!isTdLibLibraryLoaded) {
            val user = TdApi.User()
            user.id = 998877L
            user.firstName = "KIRA"
            user.lastName = "Gaming"
            _authState.value = AuthState.Authenticated(user)
            return
        }
        val req = TdApi.CheckAuthenticationPassword(password)
        client?.send(req, this)
    }

    override fun onResult(obj: TdApi.Object?) {
        if (obj == null) return

        when (obj.constructor) {
            TdApi.UpdateAuthorizationState.CONSTRUCTOR -> {
                val update = obj as TdApi.UpdateAuthorizationState
                handleAuthState(update.authorizationState)
            }
            TdApi.UpdateNewMessage.CONSTRUCTOR -> {
                val update = obj as TdApi.UpdateNewMessage
                handleIncomingMessage(update.message)
            }
            TdApi.UpdateChatMember.CONSTRUCTOR -> {
                val update = obj as TdApi.UpdateChatMember
                handleChatMemberJoin(update)
            }
            TdApi.Error.CONSTRUCTOR -> {
                val error = obj as TdApi.Error
                handleTdError(error)
            }
            else -> {
                // Other TDLib events
            }
        }
    }

    private fun handleAuthState(state: TdApi.AuthorizationState) {
        when (state.constructor) {
            TdApi.AuthorizationStateWaitTdlibParameters.CONSTRUCTOR -> {
                val dbDir = File(context.filesDir, "tdlib_db").absolutePath
                val req = TdApi.SetTdlibParameters()
                req.databaseDirectory = dbDir
                req.useMessageDatabase = true
                req.useSecretChats = false
                req.apiId = currentApiId
                req.apiHash = currentApiHash
                req.systemLanguageCode = "en"
                req.deviceModel = "Android"
                req.applicationVersion = "1.0.0"
                client?.send(req, this)
            }
            TdApi.AuthorizationStateWaitPhoneNumber.CONSTRUCTOR -> {
                _authState.value = AuthState.WaitPhoneNumber
            }
            TdApi.AuthorizationStateWaitCode.CONSTRUCTOR -> {
                _authState.value = AuthState.WaitCode
            }
            TdApi.AuthorizationStateWaitPassword.CONSTRUCTOR -> {
                _authState.value = AuthState.WaitPassword
            }
            TdApi.AuthorizationStateReady.CONSTRUCTOR -> {
                client?.send(TdApi.GetMe(), Client.ResultHandler { userObj ->
                    if (userObj is TdApi.User) {
                        _authState.value = AuthState.Authenticated(userObj)
                        saveUserAccount(userObj)
                    }
                })
                fetchUserChats()
            }
        }
    }

    private fun handleIncomingMessage(message: TdApi.Message) {
        if (!appPreferences.isPostingActive.value) return

        val chatId = message.chatId
        val selectedIds = appPreferences.selectedGroupIds.value
        if (!selectedIds.contains(chatId)) return

        when (val content = message.content) {
            is TdApi.MessageChatAddMembers -> {
                for (memberUserId in content.memberUserIds) {
                    client?.send(TdApi.GetUser(memberUserId), Client.ResultHandler { userObj ->
                        if (userObj is TdApi.User) {
                            scheduleAutoReply(chatId, message.id, userObj)
                        }
                    })
                }
            }
            is TdApi.MessageChatJoinByLink, is TdApi.MessageChatJoinByRequest -> {
                val senderId = (message.senderId as? TdApi.MessageSenderUser)?.userId ?: 0L
                if (senderId != 0L) {
                    client?.send(TdApi.GetUser(senderId), Client.ResultHandler { userObj ->
                        if (userObj is TdApi.User) {
                            scheduleAutoReply(chatId, message.id, userObj)
                        }
                    })
                }
            }
        }
    }

    private fun handleChatMemberJoin(update: TdApi.UpdateChatMember) {
        if (!appPreferences.isPostingActive.value) return
        val chatId = update.chatId
        if (!appPreferences.selectedGroupIds.value.contains(chatId)) return

        if (update.newChatMember.status is TdApi.ChatMemberStatusMember) {
            val memberId = getMemberUserId(update)
            if (memberId != 0L) {
                client?.send(TdApi.GetUser(memberId), Client.ResultHandler { userObj ->
                    if (userObj is TdApi.User) {
                        scheduleAutoReply(chatId, 0L, userObj)
                    }
                })
            }
        }
    }

    private fun getMemberUserId(update: TdApi.UpdateChatMember): Long {
        return try {
            val f = update.javaClass.getField("userId")
            f.getLong(update)
        } catch (e: Exception) {
            try {
                val f = update.javaClass.getField("member")
                val member = f.get(update)
                val memberIdField = member?.javaClass?.getField("memberId")
                val memberId = memberIdField?.get(member)
                val userIdField = memberId?.javaClass?.getField("userId")
                userIdField?.getLong(memberId) ?: 0L
            } catch (e2: Exception) {
                0L
            }
        }
    }

    fun getUserUsername(user: TdApi.User?): String {
        if (user == null) return ""
        return try {
            val f = user.javaClass.getField("username")
            (f.get(user) as? String) ?: ""
        } catch (e: Exception) {
            try {
                val f = user.javaClass.getField("usernames")
                val obj = f.get(user)
                val active = obj?.javaClass?.getField("activeUsernames")?.get(obj) as? Array<*>
                active?.firstOrNull()?.toString() ?: ""
            } catch (e2: Exception) {
                ""
            }
        }
    }

    fun scheduleAutoReply(chatId: Long, replyToMessageId: Long, user: TdApi.User) {
        scope.launch {
            val template = appPreferences.postTemplate.value
            val baseDelay = appPreferences.safeDelaySeconds.value
            val jitter = Random.nextInt(3, 8)
            val totalDelay = (baseDelay + jitter) * 1000L

            val username = getUserUsername(user)
            val displayName = user.firstName ?: "Member"
            Log.d(tag, "Scheduling auto-reply to user ${username.ifEmpty { displayName }} with delay: ${baseDelay + jitter}s")

            val mention = if (template.mentionUsername) {
                if (username.isNotEmpty()) "@$username " else "$displayName "
            } else ""

            val fullText = buildString {
                if (mention.isNotEmpty()) {
                    append(mention)
                    append("\n")
                }
                append(template.messageText)
                if (template.links.isNotEmpty()) {
                    append("\n\n")
                    template.links.forEach { append(it).append("\n") }
                }
            }.trim()

            kotlinx.coroutines.delay(totalDelay)

            // Text message with formatted links
            val msgText = TdApi.InputMessageText()
            msgText.text = TdApi.FormattedText(fullText, null)

            val sendReq = TdApi.SendMessage()
            sendReq.chatId = chatId
            sendReq.inputMessageContent = msgText

            client?.send(sendReq, Client.ResultHandler { result ->
                if (result is TdApi.Message) {
                    val report = PostReport(
                        groupTitle = "Group #$chatId",
                        memberName = "${user.firstName} ${user.lastName ?: ""}".trim(),
                        memberUsername = username,
                        status = PostStatus.SUCCESS,
                        messagePreview = fullText.take(80)
                    )
                    appPreferences.addReport(report)
                } else if (result is TdApi.Error) {
                    handleSendError(result, chatId, replyToMessageId, user, fullText)
                }
            })
        }
    }

    private fun handleSendError(
        error: TdApi.Error,
        chatId: Long,
        replyToMessageId: Long,
        user: TdApi.User,
        preview: String
    ) {
        val msg = error.message ?: ""
        val username = getUserUsername(user)
        if (msg.startsWith("FLOOD_WAIT_")) {
            val waitSec = msg.removePrefix("FLOOD_WAIT_").toIntOrNull() ?: 60
            Log.w(tag, "FLOOD_WAIT triggered: waiting $waitSec seconds")
            val report = PostReport(
                groupTitle = "Group #$chatId",
                memberName = "${user.firstName} ${user.lastName ?: ""}".trim(),
                memberUsername = username,
                status = PostStatus.FLOOD_WAIT,
                floodWaitSeconds = waitSec,
                messagePreview = preview,
                errorMessage = "Flood wait required: $waitSec seconds"
            )
            appPreferences.addReport(report)
        } else {
            val report = PostReport(
                groupTitle = "Group #$chatId",
                memberName = "${user.firstName} ${user.lastName ?: ""}".trim(),
                memberUsername = username,
                status = PostStatus.FAILED,
                messagePreview = preview,
                errorMessage = msg
            )
            appPreferences.addReport(report)
        }
    }

    private fun handleTdError(error: TdApi.Error) {
        Log.e(tag, "TDLib Error [${error.code}]: ${error.message}")
        if (error.code == 401) {
            _authState.value = AuthState.Idle
        }
    }

    private fun fetchUserChats() {
        val req = TdApi.GetChats(TdApi.ChatListMain(), 100)
        client?.send(req, Client.ResultHandler { result ->
            if (result is TdApi.Chats) {
                val groupList = mutableListOf<TelegramGroup>()
                val ids = result.chatIds ?: longArrayOf()
                for (cId in ids) {
                    client?.send(TdApi.GetChat(cId), Client.ResultHandler { chatObj ->
                        if (chatObj is TdApi.Chat) {
                            val isSupergroupOrBasic = chatObj.type is TdApi.ChatTypeSupergroup ||
                                    chatObj.type is TdApi.ChatTypeBasicGroup
                            if (isSupergroupOrBasic) {
                                val isSelected = appPreferences.selectedGroupIds.value.contains(chatObj.id)
                                groupList.add(
                                    TelegramGroup(
                                        id = chatObj.id,
                                        title = chatObj.title ?: "Group",
                                        memberCount = 100,
                                        photoPath = null,
                                        isSelected = isSelected
                                    )
                                )
                                _groups.value = groupList.toList()
                            }
                        }
                    })
                }
            }
        })
    }

    private fun saveUserAccount(user: TdApi.User) {
        val username = getUserUsername(user)
        val acc = TelegramAccount(
            id = user.id,
            phone = user.phoneNumber ?: "",
            firstName = user.firstName ?: "",
            lastName = user.lastName ?: "",
            username = username,
            isActive = true
        )
        val current = appPreferences.accounts.value.filter { it.id != user.id }
        appPreferences.saveAccounts(listOf(acc) + current)
    }

    fun toggleGroupSelection(groupId: Long) {
        val current = appPreferences.selectedGroupIds.value.toMutableSet()
        if (current.contains(groupId)) {
            current.remove(groupId)
        } else {
            current.add(groupId)
        }
        appPreferences.setSelectedGroupIds(current)

        _groups.value = _groups.value.map { g ->
            if (g.id == groupId) g.copy(isSelected = current.contains(groupId)) else g
        }
    }
}
