package com.example.uthtodo.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel: giữ danh sách thông báo và xử lý logic.
 * View (NotificationScreen) chỉ đọc [items] và gọi các hàm bên dưới.
 */
class NotificationViewModel : ViewModel() {

    private val _items = MutableStateFlow(sampleNotifications())
    val items: StateFlow<List<NotificationItem>> = _items.asStateFlow()

    init {
        // Sau 10 giây cho danh sách về rỗng -> màn hình hiện emptyView
        viewModelScope.launch {
            delay(10_000)
            _items.value = emptyList()
        }
    }

    fun markAllRead() {
        _items.update { list -> list.map { it.copy(read = true) } }
    }

    private fun sampleNotifications() = listOf(
        NotificationItem(1, NotificationType.DUE_SOON, "Sắp đến hạn", "Ôn chương 3 – Cấu trúc dữ liệu lúc 14:00.", "13:45"),
        NotificationItem(2, NotificationType.OVERDUE, "Đã quá hạn", "Bài đọc tham khảo môn Mạng máy tính chưa xong.", "10:10"),
        NotificationItem(3, NotificationType.GOOD_JOB, "Làm tốt lắm!", "Bạn đã xong 2 việc trong sáng nay.", "11:30", read = true),
        NotificationItem(4, NotificationType.REMINDER, "Nhắc việc", "Họp nhóm đồ án trong tuần này.", "Hôm qua", read = true)
    )
}
