package com.example.uthtodo.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.uthtodo.data.ApiClient
import com.example.uthtodo.data.CreateTaskRequest
import com.example.uthtodo.data.TaskDto
import com.example.uthtodo.data.UpdateTaskRequest
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class TodoCategory(val label: String) {
    STUDY("Học tập"),
    LIFE("Cuộc sống")
}

data class Subtask(val title: String, val done: Boolean)

data class TodoItem(
    val id: Int,
    val title: String,
    val category: TodoCategory,
    val time: String,
    val done: Boolean,
    val date: String = "",
    val note: String = "",
    val priority: String = "medium",
    val remind: Boolean = false,
    val subtasks: List<Subtask> = emptyList()
)

private fun TaskDto.toItem() = TodoItem(
    id = id,
    title = title.orEmpty(),
    category = if (category.equals("study", ignoreCase = true)) TodoCategory.STUDY else TodoCategory.LIFE,
    time = time.orEmpty(),
    done = done ?: false,
    date = date.orEmpty(),
    note = note.orEmpty(),
    priority = priority ?: "medium",
    remind = remind ?: false,
    subtasks = subtasks.orEmpty().map { Subtask(it.title.orEmpty(), it.done ?: false) }
)

/** Nơi giữ danh sách việc lấy từ API, dùng chung cho các màn hình. */
object TodoStore {
    private var all by mutableStateOf<List<TodoItem>>(emptyList())
    private var loaded = false

    var loading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    /**
     * Ngày đang hiển thị ở Trang chủ: hôm nay nếu có việc của hôm nay,
     * nếu không thì lấy ngày sớm nhất trong dữ liệu (dữ liệu mẫu của API cố định ngày).
     */
    val displayDate: String?
        get() {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
            return if (all.any { it.date == today }) today
            else all.map { it.date }.filter { it.isNotBlank() }.minOrNull()
        }

    /** Việc của ngày đang hiển thị (cho Trang chủ). */
    val items: List<TodoItem> get() = all.filter { it.date == displayDate }

    /** Toàn bộ việc (cho màn Danh sách). */
    val allItems: List<TodoItem> get() = all
    val allPending: Int get() = all.count { !it.done }

    val total: Int get() = items.size
    val doneCount: Int get() = items.count { it.done }
    val pendingCount: Int get() = total - doneCount
    val percent: Int get() = if (total == 0) 0 else doneCount * 100 / total

    fun remaining(category: TodoCategory): Int =
        items.count { it.category == category && !it.done }

    /** Nhãn ngày kiểu "Hôm nay, 14:00" tính theo ngày đang hiển thị. */
    fun dayLabel(item: TodoItem): String {
        val base = displayDate ?: return item.time
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val diff = try {
            ((fmt.parse(item.date)!!.time - fmt.parse(base)!!.time) / 86_400_000L).toInt()
        } catch (e: Exception) {
            return item.time
        }
        val day = when (diff) {
            0 -> "Hôm nay"
            1 -> "Ngày mai"
            in 2..7 -> "Tuần này"
            else -> item.date.takeLast(5).split("-").reversed().joinToString("/")
        }
        return "$day, ${item.time}"
    }

    /** Ngày gốc để tính "Hôm nay / Ngày mai / Tuần này" khi thêm việc mới. */
    fun baseDate(): String = displayDate ?: SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    /** POST /tasks: gửi việc mới lên API, thành công thì thêm vào đầu danh sách. */
    suspend fun create(item: TodoItem) {
        val response = ApiClient.api.createTask(
            CreateTaskRequest(
                title = item.title,
                note = item.note,
                category = if (item.category == TodoCategory.STUDY) "study" else "life",
                date = item.date,
                time = item.time,
                priority = item.priority,
                remind = item.remind
            )
        )
        if (!response.isSuccessful) throw HttpException(response)
        val newId = (all.maxOfOrNull { it.id } ?: 0) + 1
        all = listOf(item.copy(id = newId)) + all
    }

    fun itemById(id: Int): TodoItem? = all.firstOrNull { it.id == id }

    private fun update(id: Int, change: (TodoItem) -> TodoItem) {
        all = all.map { if (it.id == id) change(it) else it }
    }

    fun remove(id: Int) {
        all = all.filter { it.id != id }
    }

    fun toggleSubtask(id: Int, index: Int) = update(id) { item ->
        item.copy(subtasks = item.subtasks.mapIndexed { i, s -> if (i == index) s.copy(done = !s.done) else s })
    }

    fun removeSubtask(id: Int, index: Int) = update(id) { item ->
        item.copy(subtasks = item.subtasks.filterIndexed { i, _ -> i != index })
    }

    fun renameSubtask(id: Int, index: Int, title: String) = update(id) { item ->
        item.copy(subtasks = item.subtasks.mapIndexed { i, s -> if (i == index) s.copy(title = title) else s })
    }

    fun addSubtask(id: Int, title: String) = update(id) { item ->
        item.copy(subtasks = item.subtasks + Subtask(title, false))
    }

    /** GET /tasks/{id}: làm mới dữ liệu của việc đang xem (chỉ nhận nếu đúng id). */
    suspend fun refreshDetail(id: Int) {
        val fresh = ApiClient.api.getTask(id).toItem()
        if (fresh.id != id) return
        update(id) { old ->
            fresh.copy(
                title = fresh.title.ifBlank { old.title },
                date = fresh.date.ifBlank { old.date },
                time = fresh.time.ifBlank { old.time }
            )
        }
    }

    /** PATCH /tasks/{id}: đánh dấu hoàn thành hoặc bỏ đánh dấu. */
    suspend fun markDone(id: Int, done: Boolean) {
        val response = ApiClient.api.updateTask(id, UpdateTaskRequest(done))
        if (!response.isSuccessful) throw HttpException(response)
        update(id) { it.copy(done = done) }
    }

    /** DELETE /tasks/{id}: xóa công việc. */
    suspend fun delete(id: Int) {
        val response = ApiClient.api.deleteTask(id)
        if (!response.isSuccessful) throw HttpException(response)
        remove(id)
    }

    fun describeError(e: Exception): String = describe(e)

    fun toggle(id: Int) {
        all = all.map { if (it.id == id) it.copy(done = !it.done) else it }
    }

    suspend fun load(force: Boolean = false) {
        if (loaded && !force) return
        loading = true
        error = null
        try {
            all = ApiClient.api.getTasks().map { it.toItem() }
            loaded = true
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            error = describe(e)
        } finally {
            loading = false
        }
    }

    private fun describe(e: Exception): String = when (e) {
        is HttpException ->
            if (e.code() == 401 || e.code() == 403) "Token API không hợp lệ (mã ${e.code()})"
            else "Máy chủ báo lỗi (mã ${e.code()})"
        is IOException -> "Không có kết nối mạng, vui lòng thử lại"
        else -> "Không đọc được dữ liệu: ${e.localizedMessage ?: "lỗi không xác định"}"
    }
}
