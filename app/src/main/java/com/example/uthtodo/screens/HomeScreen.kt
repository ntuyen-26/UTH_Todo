package com.example.uthtodo.screens

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uthtodo.auth.AuthHelper
import com.example.uthtodo.auth.displayLabel
import com.example.uthtodo.auth.initialsOf
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun HomeScreen(
    onOpenProfile: () -> Unit,
    onOpenList: () -> Unit,
    onOpenNotifications: () -> Unit,
    onAdd: () -> Unit,
    onOpenTask: (Int) -> Unit
) {
    val context = LocalContext.current
    val name = AuthHelper.currentUser.displayLabel()
    val soon: () -> Unit = {
        Toast.makeText(context, "Tính năng sắp ra mắt", Toast.LENGTH_SHORT).show()
    }

    // Tải việc từ API khi mở màn hình; bấm "Thử lại" thì tăng reloadKey để tải lại
    var reloadKey by remember { mutableStateOf(0) }
    LaunchedEffect(reloadKey) { TodoStore.load(force = reloadKey > 0) }

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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { HomeHeader(name = name, onAvatarClick = onOpenProfile, onBell = soon) }
            item { ProgressCard() }
            item { CategoryRow() }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Việc hôm nay", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = UthTextPrimary)
                    Text(
                        "Xem tất cả",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = UthBlue,
                        modifier = Modifier.clickable(onClick = onOpenList)
                    )
                }
            }
            if (TodoStore.loading && TodoStore.items.isEmpty()) {
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
                item { ErrorCard(message = message, onRetry = { reloadKey++ }) }
            }
            if (!TodoStore.loading && TodoStore.error == null && TodoStore.items.isEmpty()) {
                item {
                    Text(
                        "Chưa có việc nào",
                        fontSize = 13.sp,
                        color = UthTextSecondary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            }
            items(TodoStore.items, key = { it.id }) { item ->
                TodoRow(
                    item = item,
                    onToggle = { TodoStore.toggle(item.id) },
                    onOpen = { onOpenTask(item.id) }
                )
            }
        }
        AppBottomBar(
            selected = NavTab.HOME,
            onHome = {},
            onList = onOpenList,
            onNotifications = onOpenNotifications,
            onSettings = onOpenProfile,
            onAdd = onAdd
        )
    }
}

private fun todayLabel(): String {
    val c = Calendar.getInstance()
    // Dùng ngày của dữ liệu đang hiển thị (nếu có)
    TodoStore.displayDate?.let { d ->
        try {
            SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(d)?.let { c.time = it }
        } catch (_: Exception) {
        }
    }
    val days = arrayOf("Chủ nhật", "Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy")
    return "${days[c.get(Calendar.DAY_OF_WEEK) - 1]}, ${c.get(Calendar.DAY_OF_MONTH)} tháng ${c.get(Calendar.MONTH) + 1}"
}

@Composable
private fun HomeHeader(name: String, onAvatarClick: () -> Unit, onBell: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(UthBlueLight)
                .clickable(onClick = onAvatarClick),
            contentAlignment = Alignment.Center
        ) {
            Text(initialsOf(name), color = UthBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        ) {
            Text(todayLabel(), fontSize = 12.sp, color = UthTextSecondary)
            Text("Chào $name!", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = UthTextPrimary)
        }
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White)
                .clickable(onClick = onBell),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.Notifications, contentDescription = "Thông báo", tint = UthTextPrimary, modifier = Modifier.size(20.dp))
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 9.dp, end = 10.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFA5252))
            )
        }
    }
}

@Composable
private fun ProgressCard() {
    val percent = TodoStore.percent
    val motivation = when {
        percent == 100 -> "Tuyệt vời, bạn đã xong hết!"
        percent >= 50 -> "Đã hơn nửa chặng, cố lên!"
        else -> "Bắt đầu thôi nào, cố lên!"
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(UthBlue)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text("Tiến độ hôm nay", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
            Text(
                "${TodoStore.doneCount}/${TodoStore.total} việc",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(top = 2.dp)
            )
            Text(motivation, fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f), modifier = Modifier.padding(top = 4.dp))
        }
        ProgressRing(percent = percent)
    }
}

@Composable
private fun ProgressRing(percent: Int) {
    Box(modifier = Modifier.size(60.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = 6.dp.toPx()
            val topLeft = Offset(stroke / 2, stroke / 2)
            val arcSize = Size(size.width - stroke, size.height - stroke)
            drawArc(
                color = Color.White.copy(alpha = 0.25f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke)
            )
            drawArc(
                color = Color.White,
                startAngle = -90f,
                sweepAngle = 360f * percent / 100f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }
        Text("$percent%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
private fun CategoryRow() {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        CategoryCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Outlined.School,
            tint = UthBlue,
            tintBg = UthBlueLight,
            title = TodoCategory.STUDY.label,
            remaining = TodoStore.remaining(TodoCategory.STUDY)
        )
        CategoryCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Outlined.Home,
            tint = UthOrange,
            tintBg = UthOrangeBg,
            title = TodoCategory.LIFE.label,
            remaining = TodoStore.remaining(TodoCategory.LIFE)
        )
    }
}

@Composable
private fun CategoryCard(
    modifier: Modifier,
    icon: ImageVector,
    tint: Color,
    tintBg: Color,
    title: String,
    remaining: Int
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(tintBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
        }
        Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = UthTextPrimary, modifier = Modifier.padding(top = 10.dp))
        Text("$remaining việc còn lại", fontSize = 11.sp, color = UthTextSecondary)
    }
}

@Composable
private fun TodoRow(item: TodoItem, onToggle: () -> Unit, onOpen: () -> Unit) {
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
                .clickable(onClick = onToggle),
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
                Text(item.time, fontSize = 11.sp, color = UthTextSecondary)
            }
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = UthTextSecondary, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun ErrorCard(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(message, fontSize = 13.sp, color = UthTextPrimary)
        TextButton(onClick = onRetry) {
            Text("Thử lại", color = UthBlue, fontWeight = FontWeight.SemiBold)
        }
    }
}
