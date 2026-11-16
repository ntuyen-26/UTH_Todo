package com.example.uthtodo.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

enum class DayChoice(val label: String, val offsetDays: Int) {
    TODAY("Hôm nay", 0), TOMORROW("Ngày mai", 1), THIS_WEEK("Tuần này", 3)
}

enum class Priority(val label: String, val api: String) {
    LOW("Thấp", "low"), MEDIUM("Vừa", "medium"), HIGH("Cao", "high")
}

/** Trạng thái của form "Việc mới" (View chỉ đọc cái này). */
data class AddTaskUiState(
    val title: String = "",
    val note: String = "",
    val category: TodoCategory = TodoCategory.STUDY,
    val day: DayChoice = DayChoice.TODAY,
    val time: String = "19:00",
    val remind: Boolean = true,
    val priority: Priority = Priority.MEDIUM,
    val saving: Boolean = false
) {
    val canSave: Boolean get() = title.isNotBlank() && !saving
}

/** ViewModel của màn thêm mới: giữ dữ liệu form và xử lý việc lưu qua API. */
class AddTaskViewModel : ViewModel() {
    private val _state = MutableStateFlow(AddTaskUiState())
    val state: StateFlow<AddTaskUiState> = _state.asStateFlow()

    fun onTitle(v: String) = _state.update { it.copy(title = v) }
    fun onNote(v: String) = _state.update { it.copy(note = v) }
    fun onCategory(v: TodoCategory) = _state.update { it.copy(category = v) }
    fun onDay(v: DayChoice) = _state.update { it.copy(day = v) }
    fun onTime(v: String) = _state.update { it.copy(time = v) }
    fun onRemind(v: Boolean) = _state.update { it.copy(remind = v) }
    fun onPriority(v: Priority) = _state.update { it.copy(priority = v) }

    /** Gửi lên API (POST). Thành công -> onSuccess(), lỗi -> onError(message). */
    fun save(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val s = _state.value
        if (!s.canSave) return
        _state.update { it.copy(saving = true) }
        viewModelScope.launch {
            try {
                TodoStore.create(
                    TodoItem(
                        id = 0,
                        title = s.title.trim(),
                        category = s.category,
                        time = s.time,
                        done = false,
                        date = dateFor(s.day),
                        note = s.note.trim(),
                        priority = s.priority.api,
                        remind = s.remind
                    )
                )
                onSuccess()
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) throw e
                onError(TodoStore.describeError(e))
            } finally {
                _state.update { it.copy(saving = false) }
            }
        }
    }

    private fun dateFor(day: DayChoice): String {
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val cal = Calendar.getInstance()
        fmt.parse(TodoStore.baseDate())?.let { cal.time = it }
        cal.add(Calendar.DAY_OF_YEAR, day.offsetDays)
        return fmt.format(cal.time)
    }
}
