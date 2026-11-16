package com.example.uthtodo.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uthtodo.model.TodoStore
import com.example.uthtodo.ui.theme.UthBackground
import com.example.uthtodo.ui.theme.UthBlue
import com.example.uthtodo.ui.theme.UthBlueLight
import com.example.uthtodo.ui.theme.UthBorder
import com.example.uthtodo.ui.theme.UthOrange
import com.example.uthtodo.ui.theme.UthOrangeBg
import com.example.uthtodo.ui.theme.UthTextPrimary
import com.example.uthtodo.ui.theme.UthTextSecondary
import kotlinx.coroutines.launch

private val DangerRed = Color(0xFFE5484D)

@Composable
fun DetailScreen(taskId: Int, onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val item = TodoStore.itemById(taskId)

    var busy by remember { mutableStateOf(false) }
    var confirmDelete by remember { mutableStateOf(false) }
    var editIndex by remember { mutableStateOf<Int?>(null) }
    var editText by remember { mutableStateOf("") }
    var newSub by remember { mutableStateOf("") }

    // Làm mới từ API (GET /tasks/{id}); lỗi mạng thì giữ dữ liệu đang có.
    LaunchedEffect(taskId) {
        try { TodoStore.refreshDetail(taskId) } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
        }
    }
    // Việc không còn (đã xóa) -> quay lại đúng một lần.
    if (item == null) {
        LaunchedEffect(Unit) { onBack() }
        Box(Modifier.fillMaxSize().background(Color.White), Alignment.Center) { CircularProgressIndicator() }
        return
    }

    fun toast(msg: String) = Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Text("‹ Trang chủ", color = UthBlue, fontSize = 16.sp, modifier = Modifier.clickable(onClick = onBack))
            Row(Modifier.clickable { toast("Tính năng sắp ra mắt") }, verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Edit, null, tint = UthBlue, modifier = Modifier.size(18.dp))
                Spacer(Modifier.size(4.dp))
                Text("Sửa", color = UthBlue, fontSize = 16.sp)
            }
        }

        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Chip(item.category.label, UthBlueLight, UthBlue)
            val (pText, pColor) = when (item.priority.lowercase()) {
                "high" -> "Ưu tiên cao" to DangerRed
                "low" -> "Ưu tiên thấp" to UthTextSecondary
                else -> "Ưu tiên vừa" to UthOrange
            }
            Chip(pText, UthOrangeBg, pColor)
        }
        Spacer(Modifier.height(12.dp))
        Text(item.title, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = UthTextPrimary)

        Spacer(Modifier.height(16.dp))
        Column(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(UthBackground).padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InfoRow(Icons.Outlined.CalendarToday, "Hạn chót", TodoStore.dayLabel(item))
            InfoRow(Icons.Outlined.Notifications, "Nhắc nhở", if (item.remind) "Trước 15 phút" else "Không nhắc")
        }

        Spacer(Modifier.height(16.dp))
        Text("Ghi chú", fontWeight = FontWeight.SemiBold, color = UthTextPrimary)
        Spacer(Modifier.height(6.dp))
        Text(
            item.note.ifBlank { "Chưa có ghi chú" },
            color = if (item.note.isBlank()) UthTextSecondary else UthTextPrimary,
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(UthBackground).padding(14.dp)
        )

        Spacer(Modifier.height(16.dp))
        val subDone = item.subtasks.count { it.done }
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            Text("Việc nhỏ", fontWeight = FontWeight.SemiBold, color = UthTextPrimary)
            Text("$subDone/${item.subtasks.size}", color = UthTextSecondary)
        }
        Spacer(Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { if (item.subtasks.isEmpty()) 0f else subDone.toFloat() / item.subtasks.size },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = UthBlue, trackColor = UthBorder
        )
        Spacer(Modifier.height(8.dp))
        item.subtasks.forEachIndexed { i, s ->
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = s.done,
                    onCheckedChange = { TodoStore.toggleSubtask(item.id, i) },
                    colors = CheckboxDefaults.colors(checkedColor = UthBlue)
                )
                Text(
                    s.title,
                    modifier = Modifier.weight(1f),
                    color = if (s.done) UthTextSecondary else UthTextPrimary,
                    textDecoration = if (s.done) TextDecoration.LineThrough else null
                )
                Icon(Icons.Outlined.Edit, "Sửa", tint = UthTextSecondary,
                    modifier = Modifier.clickable { editIndex = i; editText = s.title }.padding(8.dp).size(20.dp))
                Icon(Icons.Outlined.Delete, "Xóa", tint = DangerRed,
                    modifier = Modifier.clickable { TodoStore.removeSubtask(item.id, i) }.padding(8.dp).size(20.dp))
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = newSub, onValueChange = { newSub = it },
                placeholder = { Text("Thêm việc nhỏ") }, singleLine = true,
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = UthBackground, unfocusedContainerColor = UthBackground,
                    focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent
                )
            )
            TextButton(onClick = {
                if (newSub.isNotBlank()) { TodoStore.addSubtask(item.id, newSub.trim()); newSub = "" }
            }) { Text("Thêm", color = UthBlue) }
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = {
                scope.launch {
                    busy = true
                    try {
                        TodoStore.markDone(item.id, !item.done)
                        toast(if (!item.done) "Đã đánh dấu hoàn thành" else "Đã bỏ đánh dấu")
                    } catch (e: Exception) {
                        if (e is kotlinx.coroutines.CancellationException) throw e
                        toast(TodoStore.describeError(e))
                    } finally { busy = false }
                }
            },
            enabled = !busy,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = UthBlue)
        ) { Text(if (item.done) "Bỏ đánh dấu hoàn thành" else "Đánh dấu hoàn thành", fontSize = 16.sp) }

        Spacer(Modifier.height(10.dp))
        Row(
            Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(14.dp))
                .border(1.dp, DangerRed, RoundedCornerShape(14.dp))
                .clickable(enabled = !busy) { confirmDelete = true },
            horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.Delete, null, tint = DangerRed)
            Spacer(Modifier.size(6.dp))
            Text("Xóa công việc", color = DangerRed, fontSize = 16.sp)
        }
        Spacer(Modifier.height(24.dp))
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Xóa công việc?") },
            text = { Text("Bạn có chắc muốn xóa \"${item.title}\" không?") },
            confirmButton = {
                TextButton(onClick = {
                    confirmDelete = false
                    scope.launch {
                        busy = true
                        try {
                            TodoStore.delete(item.id)
                            toast("Đã xóa công việc")
                        } catch (e: Exception) {
                            if (e is kotlinx.coroutines.CancellationException) throw e
                            toast(TodoStore.describeError(e))
                        } finally { busy = false }
                    }
                }) { Text("Xóa", color = DangerRed) }
            },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Hủy") } }
        )
    }

    editIndex?.let { idx ->
        AlertDialog(
            onDismissRequest = { editIndex = null },
            title = { Text("Sửa việc nhỏ") },
            text = { TextField(value = editText, onValueChange = { editText = it }, singleLine = true) },
            confirmButton = {
                TextButton(onClick = {
                    if (editText.isNotBlank()) TodoStore.renameSubtask(item.id, idx, editText.trim())
                    editIndex = null
                }) { Text("Lưu") }
            },
            dismissButton = { TextButton(onClick = { editIndex = null }) { Text("Hủy") } }
        )
    }
}

@Composable
private fun Chip(text: String, bg: Color, fg: Color) {
    Text(
        text, color = fg, fontSize = 12.sp, fontWeight = FontWeight.Medium,
        modifier = Modifier.clip(RoundedCornerShape(50)).background(bg).padding(horizontal = 12.dp, vertical = 5.dp)
    )
}

@Composable
private fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = UthBlue, modifier = Modifier.size(20.dp))
        Spacer(Modifier.size(10.dp))
        Text(label, color = UthTextSecondary, modifier = Modifier.weight(1f))
        Text(value, color = UthTextPrimary, fontWeight = FontWeight.Medium)
    }
}
