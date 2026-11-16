package com.example.uthtodo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uthtodo.model.TodoCategory
import com.example.uthtodo.model.TodoItem
import com.example.uthtodo.model.TodoStore
import com.example.uthtodo.ui.theme.UthBackground
import com.example.uthtodo.ui.theme.UthBlue
import com.example.uthtodo.ui.theme.UthBlueLight
import com.example.uthtodo.ui.theme.UthBorder
import com.example.uthtodo.ui.theme.UthOrange
import com.example.uthtodo.ui.theme.UthOrangeBg
import com.example.uthtodo.ui.theme.UthTextPrimary
import com.example.uthtodo.ui.theme.UthTextSecondary

private val UrgentRed = Color(0xFFE03131)
private val UrgentRedBg = Color(0xFFFFE3E3)

/** Bỏ dấu tiếng Việt để gõ "hop" vẫn tìm ra "Họp". */
private fun String.unaccent(): String =
    java.text.Normalizer.normalize(this, java.text.Normalizer.Form.NFD)
        .replace(Regex("\\p{Mn}+"), "")
        .replace('đ', 'd')
        .replace('Đ', 'D')

@Composable
fun ListScreen(
    onHome: () -> Unit,
    onNotifications: () -> Unit,
    onSettings: () -> Unit,
    onAdd: () -> Unit,
    onOpenTask: (Int) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf<TodoCategory?>(null) }
    var reloadKey by remember { mutableStateOf(0) }
    LaunchedEffect(reloadKey) { TodoStore.load(force = reloadKey > 0) }

    // Lọc theo nhóm và theo chữ tìm kiếm, rồi tách "cần làm" / "đã xong"
    val visible = TodoStore.allItems.filter {
        (filter == null || it.category == filter) &&
            it.title.unaccent().contains(query.trim().unaccent(), ignoreCase = true)
    }
    val todo = visible.filter { !it.done }
    val done = visible.filter { it.done }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(UthBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .statusBarsPadding(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text("Danh sách", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = UthTextPrimary)
                    Text("${TodoStore.allPending} việc chưa xong", fontSize = 12.sp, color = UthTextSecondary)
                }
            }
            item { SearchField(query = query, onChange = { query = it }) }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip("Tất cả", filter == null) { filter = null }
                    FilterChip(TodoCategory.STUDY.label, filter == TodoCategory.STUDY) { filter = TodoCategory.STUDY }
                    FilterChip(TodoCategory.LIFE.label, filter == TodoCategory.LIFE) { filter = TodoCategory.LIFE }
                }
            }

            if (TodoStore.loading && TodoStore.allItems.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = UthBlue)
                    }
                }
            }
            TodoStore.error?.let { message ->
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(message, fontSize = 13.sp, color = UthTextPrimary)
                        TextButton(onClick = { reloadKey++ }) {
                            Text("Thử lại", color = UthBlue, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
            if (!TodoStore.loading && TodoStore.error == null && visible.isEmpty()) {
                item {
                    Text(
                        "Không có việc nào phù hợp",
                        fontSize = 13.sp,
                        color = UthTextSecondary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            }

            if (todo.isNotEmpty()) {
                item { SectionLabel("CẦN LÀM") }
                items(todo, key = { it.id }) { task -> TaskCard(task) { onOpenTask(task.id) } }
            }
            if (done.isNotEmpty()) {
                item { SectionLabel("ĐÃ XONG") }
                items(done, key = { it.id }) { task -> TaskCard(task) { onOpenTask(task.id) } }
            }
        }

        AppBottomBar(
            selected = NavTab.LIST,
            onHome = onHome,
            onList = {},
            onNotifications = onNotifications,
            onSettings = onSettings,
            onAdd = onAdd
        )
    }
}

@Composable
private fun SearchField(query: String, onChange: (String) -> Unit) {
    TextField(
        value = query,
        onValueChange = onChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        placeholder = { Text("Tìm công việc", fontSize = 14.sp, color = UthTextSecondary) },
        leadingIcon = {
            Icon(Icons.Outlined.Search, contentDescription = null, tint = UthTextSecondary, modifier = Modifier.size(20.dp))
        },
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFECEEF4),
            unfocusedContainerColor = Color(0xFFECEEF4),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = UthBlue
        )
    )
}

@Composable
private fun FilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = label,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = if (selected) Color.White else UthTextPrimary,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) UthTextPrimary else Color.White)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp)
    )
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = UthTextSecondary,
        modifier = Modifier.padding(top = 6.dp)
    )
}

@Composable
private fun TaskCard(item: TodoItem, onOpen: () -> Unit) {
    val isStudy = item.category == TodoCategory.STUDY
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .clickable(onClick = onOpen)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .then(
                    if (item.done) Modifier.background(UthBlue)
                    else Modifier.border(1.5.dp, UthBorder, CircleShape)
                )
                .clickable { TodoStore.toggle(item.id) },
            contentAlignment = Alignment.Center
        ) {
            if (item.done) {
                Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = item.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (item.done) UthTextSecondary else UthTextPrimary,
                textDecoration = if (item.done) TextDecoration.LineThrough else TextDecoration.None
            )
            if (!item.done) {
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = item.category.label,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isStudy) UthBlue else UthOrange,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isStudy) UthBlueLight else UthOrangeBg)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                    Text(TodoStore.dayLabel(item), fontSize = 11.sp, color = UthTextSecondary)
                }
            }
        }
        if (!item.done && item.priority.equals("high", ignoreCase = true)) {
            Text(
                text = "Gấp",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = UrgentRed,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(UrgentRedBg)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
    }
}
