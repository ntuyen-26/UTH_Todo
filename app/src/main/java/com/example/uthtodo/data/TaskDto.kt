package com.example.uthtodo.data

import com.google.gson.annotations.SerializedName

/** Một công việc đúng như API /mock/teach/tasks trả về. */
data class TaskDto(
    val id: Int,
    val title: String?,
    val note: String?,
    val category: String?,
    val date: String?,
    val time: String?,
    val priority: String?,
    val remind: Boolean?,
    val done: Boolean?,
    val subtasks: List<SubtaskDto>?
)

data class SubtaskDto(
    // Chấp nhận vài tên trường thường gặp phòng khi API đặt khác "title"/"done"
    @SerializedName(value = "title", alternate = ["text", "name", "label", "content"])
    val title: String?,
    @SerializedName(value = "done", alternate = ["completed", "checked", "isDone"])
    val done: Boolean?
)

/** Nội dung gửi lên khi tạo việc mới (POST). */
data class CreateTaskRequest(
    val title: String,
    val note: String,
    val category: String,
    val date: String,
    val time: String,
    val priority: String,
    val remind: Boolean,
    val done: Boolean = false
)

/** Nội dung gửi lên khi cập nhật một việc (PATCH). */
data class UpdateTaskRequest(val done: Boolean)
