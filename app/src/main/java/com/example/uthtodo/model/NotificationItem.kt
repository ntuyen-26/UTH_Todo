package com.example.uthtodo.model

/** Loại thông báo (quyết định icon và màu). */
enum class NotificationType { DUE_SOON, OVERDUE, GOOD_JOB, REMINDER }

/** Model: một thông báo. */
data class NotificationItem(
    val id: Int,
    val type: NotificationType,
    val title: String,
    val message: String,
    val time: String,
    val read: Boolean = false
)
