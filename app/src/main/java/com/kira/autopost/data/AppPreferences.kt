package com.kira.autopost.data

import android.content.Context
import android.content.SharedPreferences
import com.kira.autopost.model.PostReport
import com.kira.autopost.model.PostStatus
import com.kira.autopost.model.PostTemplate
import com.kira.autopost.model.TelegramAccount
import com.kira.autopost.model.TelegramGroup
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class AppPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("kira_auto_post_prefs", Context.MODE_PRIVATE)

    // Reactive StateFlows for UI
    private val _isPostingActive = MutableStateFlow(prefs.getBoolean(KEY_IS_POSTING_ACTIVE, false))
    val isPostingActive: StateFlow<Boolean> = _isPostingActive.asStateFlow()

    private val _safeDelaySeconds = MutableStateFlow(prefs.getInt(KEY_SAFE_DELAY, 40))
    val safeDelaySeconds: StateFlow<Int> = _safeDelaySeconds.asStateFlow()

    private val _postTemplate = MutableStateFlow(loadPostTemplate())
    val postTemplate: StateFlow<PostTemplate> = _postTemplate.asStateFlow()

    private val _accounts = MutableStateFlow(loadAccounts())
    val accounts: StateFlow<List<TelegramAccount>> = _accounts.asStateFlow()

    private val _selectedGroupIds = MutableStateFlow(loadSelectedGroupIds())
    val selectedGroupIds: StateFlow<Set<Long>> = _selectedGroupIds.asStateFlow()

    private val _reports = MutableStateFlow(loadReports())
    val reports: StateFlow<List<PostReport>> = _reports.asStateFlow()

    private val _successCount = MutableStateFlow(prefs.getInt(KEY_SUCCESS_COUNT, 1))
    val successCount: StateFlow<Int> = _successCount.asStateFlow()

    private val _failedCount = MutableStateFlow(prefs.getInt(KEY_FAILED_COUNT, 0))
    val failedCount: StateFlow<Int> = _failedCount.asStateFlow()

    private val _floodWaitCount = MutableStateFlow(prefs.getInt(KEY_FLOOD_WAIT_COUNT, 0))
    val floodWaitCount: StateFlow<Int> = _floodWaitCount.asStateFlow()

    fun setPostingActive(active: Boolean) {
        prefs.edit().putBoolean(KEY_IS_POSTING_ACTIVE, active).apply()
        _isPostingActive.value = active
    }

    fun setSafeDelaySeconds(seconds: Int) {
        prefs.edit().putInt(KEY_SAFE_DELAY, seconds).apply()
        _safeDelaySeconds.value = seconds
    }

    fun savePostTemplate(template: PostTemplate) {
        val json = JSONObject().apply {
            put("messageText", template.messageText)
            put("mentionUsername", template.mentionUsername)
            put("mediaUri", template.mediaUri ?: "")
            put("mediaType", template.mediaType ?: "")
            val linksArr = JSONArray()
            template.links.forEach { linksArr.put(it) }
            put("links", linksArr)
        }
        prefs.edit().putString(KEY_POST_TEMPLATE, json.toString()).apply()
        _postTemplate.value = template
    }

    private fun loadPostTemplate(): PostTemplate {
        val str = prefs.getString(KEY_POST_TEMPLATE, null) ?: return PostTemplate()
        return try {
            val json = JSONObject(str)
            val linksArr = json.optJSONArray("links")
            val links = mutableListOf<String>()
            if (linksArr != null) {
                for (i in 0 until linksArr.length()) {
                    links.add(linksArr.getString(i))
                }
            }
            PostTemplate(
                messageText = json.optString("messageText", ""),
                mentionUsername = json.optBoolean("mentionUsername", true),
                mediaUri = json.optString("mediaUri").takeIf { it.isNotEmpty() },
                mediaType = json.optString("mediaType").takeIf { it.isNotEmpty() },
                links = links
            )
        } catch (e: Exception) {
            PostTemplate()
        }
    }

    fun saveAccounts(list: List<TelegramAccount>) {
        val arr = JSONArray()
        list.forEach { acc ->
            val obj = JSONObject().apply {
                put("id", acc.id)
                put("phone", acc.phone)
                put("firstName", acc.firstName)
                put("lastName", acc.lastName)
                put("username", acc.username)
                put("apiId", acc.apiId)
                put("apiHash", acc.apiHash)
                put("isActive", acc.isActive)
                put("sessionDirectory", acc.sessionDirectory)
            }
            arr.put(obj)
        }
        prefs.edit().putString(KEY_ACCOUNTS, arr.toString()).apply()
        _accounts.value = list
    }

    private fun loadAccounts(): List<TelegramAccount> {
        val str = prefs.getString(KEY_ACCOUNTS, null)
        if (str.isNullOrEmpty()) {
            return listOf(
                TelegramAccount(
                    id = 1001L,
                    phone = "+855 96 **** 123",
                    firstName = "KIRA",
                    lastName = "Gaming",
                    username = "kiragaming",
                    isActive = true
                )
            )
        }
        val list = mutableListOf<TelegramAccount>()
        try {
            val arr = JSONArray(str)
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                list.add(
                    TelegramAccount(
                        id = o.optLong("id"),
                        phone = o.optString("phone"),
                        firstName = o.optString("firstName"),
                        lastName = o.optString("lastName"),
                        username = o.optString("username"),
                        apiId = o.optInt("apiId"),
                        apiHash = o.optString("apiHash"),
                        isActive = o.optBoolean("isActive", true),
                        sessionDirectory = o.optString("sessionDirectory")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun setSelectedGroupIds(ids: Set<Long>) {
        val stringSet = ids.map { it.toString() }.toSet()
        prefs.edit().putStringSet(KEY_SELECTED_GROUPS, stringSet).apply()
        _selectedGroupIds.value = ids
    }

    private fun loadSelectedGroupIds(): Set<Long> {
        val set = prefs.getStringSet(KEY_SELECTED_GROUPS, null)
        return set?.mapNotNull { it.toLongOrNull() }?.toSet() ?: setOf(2001L)
    }

    fun addReport(report: PostReport) {
        val updated = listOf(report) + _reports.value.take(99)
        saveReports(updated)

        when (report.status) {
            PostStatus.SUCCESS -> {
                val c = _successCount.value + 1
                prefs.edit().putInt(KEY_SUCCESS_COUNT, c).apply()
                _successCount.value = c
            }
            PostStatus.FAILED -> {
                val c = _failedCount.value + 1
                prefs.edit().putInt(KEY_FAILED_COUNT, c).apply()
                _failedCount.value = c
            }
            PostStatus.FLOOD_WAIT -> {
                val c = _floodWaitCount.value + 1
                prefs.edit().putInt(KEY_FLOOD_WAIT_COUNT, c).apply()
                _floodWaitCount.value = c
            }
        }
    }

    private fun saveReports(list: List<PostReport>) {
        val arr = JSONArray()
        list.forEach { r ->
            val obj = JSONObject().apply {
                put("id", r.id)
                put("timestamp", r.timestamp)
                put("groupTitle", r.groupTitle)
                put("memberName", r.memberName)
                put("memberUsername", r.memberUsername)
                put("status", r.status.name)
                put("floodWaitSeconds", r.floodWaitSeconds)
                put("messagePreview", r.messagePreview)
                put("errorMessage", r.errorMessage ?: "")
            }
            arr.put(obj)
        }
        prefs.edit().putString(KEY_REPORTS, arr.toString()).apply()
        _reports.value = list
    }

    private fun loadReports(): List<PostReport> {
        val str = prefs.getString(KEY_REPORTS, null)
        if (str.isNullOrEmpty()) {
            return listOf(
                PostReport(
                    id = 1L,
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 15,
                    groupTitle = "KIRA Gaming Community KH",
                    memberName = "Sokha Mean",
                    memberUsername = "sokha_kh",
                    status = PostStatus.SUCCESS,
                    messagePreview = "សួស្តី @sokha_kh សូមស្វាគមន៍មកកាន់ក្រុម KIRA Gaming!"
                )
            )
        }
        val list = mutableListOf<PostReport>()
        try {
            val arr = JSONArray(str)
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                list.add(
                    PostReport(
                        id = o.optLong("id"),
                        timestamp = o.optLong("timestamp"),
                        groupTitle = o.optString("groupTitle"),
                        memberName = o.optString("memberName"),
                        memberUsername = o.optString("memberUsername"),
                        status = try {
                            PostStatus.valueOf(o.optString("status"))
                        } catch (e: Exception) {
                            PostStatus.SUCCESS
                        },
                        floodWaitSeconds = o.optInt("floodWaitSeconds"),
                        messagePreview = o.optString("messagePreview"),
                        errorMessage = o.optString("errorMessage").takeIf { it.isNotEmpty() }
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun clearReports() {
        prefs.edit().remove(KEY_REPORTS).apply()
        _reports.value = emptyList()
    }

    companion object {
        private const val KEY_IS_POSTING_ACTIVE = "is_posting_active"
        private const val KEY_SAFE_DELAY = "safe_delay"
        private const val KEY_POST_TEMPLATE = "post_template"
        private const val KEY_ACCOUNTS = "accounts"
        private const val KEY_SELECTED_GROUPS = "selected_groups"
        private const val KEY_REPORTS = "reports"
        private const val KEY_SUCCESS_COUNT = "success_count"
        private const val KEY_FAILED_COUNT = "failed_count"
        private const val KEY_FLOOD_WAIT_COUNT = "flood_wait_count"
    }
}
