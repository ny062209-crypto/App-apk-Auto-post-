package com.kira.autopost.model

data class TelegramAccount(
    val id: Long = 0L,
    val phone: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val username: String = "",
    val apiId: Int = 0,
    val apiHash: String = "",
    val isActive: Boolean = true,
    val sessionDirectory: String = ""
)

data class TelegramGroup(
    val id: Long = 0L,
    val title: String = "",
    val memberCount: Int = 0,
    val photoPath: String? = null,
    val isChannel: Boolean = false,
    val isSelected: Boolean = true
)

data class PostTemplate(
    val messageText: String = "សួស្តី! សូមស្វាគមន៍មកកាន់ក្រុមរបស់យើង។ សូមចុចចូលរួមឆានែលខាងក្រោមដើម្បីទទួលបានព័ត៌មានថ្មីៗ!",
    val mentionUsername: Boolean = true,
    val mediaUri: String? = null,
    val mediaType: String? = null, // "image" or "video"
    val links: List<String> = listOf("https://t.me/kiragaming", "https://t.me/kiraofficial")
)

enum class PostStatus {
    SUCCESS,
    FAILED,
    FLOOD_WAIT
}

data class PostReport(
    val id: Long = System.currentTimeMillis(),
    val timestamp: Long = System.currentTimeMillis(),
    val groupTitle: String = "",
    val memberName: String = "",
    val memberUsername: String = "",
    val status: PostStatus = PostStatus.SUCCESS,
    val floodWaitSeconds: Int = 0,
    val messagePreview: String = "",
    val errorMessage: String? = null
)
