package com.example.uthtodo.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uthtodo.model.NotificationItem
import com.example.uthtodo.model.NotificationType
import com.example.uthtodo.model.NotificationViewModel
import com.example.uthtodo.ui.theme.UthBackground
import com.example.uthtodo.ui.theme.UthBlue
import com.example.uthtodo.ui.theme.UthBlueLight
import com.example.uthtodo.ui.theme.UthGreen
import com.example.uthtodo.ui.theme.UthGreenBg
import com.example.uthtodo.ui.theme.UthTextPrimary
import com.example.uthtodo.ui.theme.UthTextSecondary

private val RedFg = Color(0xFFE5484D)
private val RedBg = Color(0xFFFDECEC)

/** View: chỉ vẽ những gì ViewModel cung cấp. */
@Composable
fun NotificationScreen(
    onHome: () -> Unit,
    onList: () -> Unit,
    onSettings: () -> Unit,
    onAdd: () -> Unit,
    viewModel: NotificationViewModel = viewModel()
) {
    val context = LocalContext.current
    val items by viewModel.items.collectAsState()
    val soon = { Toast.makeText(context, "Tính năng sắp ra mắt", Toast.LENGTH_SHORT).show() }

    Column(Modifier.fillMaxSize().background(UthBackground).statusBarsPadding()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
            Arrangement.SpaceBetween, Alignment.CenterVertically
        ) {
            Text("Thông báo", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = UthTextPrimary)
            if (items.isNotEmpty()) {
                Text(
                    "Đánh dấu đã đọc", color = UthBlue, fontSize = 14.sp, fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { viewModel.markAllRead() }
                )
            }
        }

        Box(Modifier.weight(1f).fillMaxWidth()) {
            if (items.isEmpty()) EmptyView(onAdd = onAdd, onSettings = soon)
            else LazyColumn(Modifier.padding(horizontal = 16.dp)) {
                item {
                    Column(Modifier.clip(RoundedCornerShape(16.dp)).background(Color.White)) {
                        items.forEachIndexed { i, n ->
                            NotificationRow(n)
                            if (i < items.lastIndex) Box(Modifier.fillMaxWidth().height(1.dp).background(UthBackground))
                        }
                    }
                }
            }
        }

        AppBottomBar(
            selected = NavTab.NOTIFICATIONS,
            onHome = onHome,
            onList = onList,
            onNotifications = {},
            onSettings = onSettings,
            onAdd = onAdd
        )
    }
}

@Composable
private fun NotificationRow(n: NotificationItem) {
    val (icon, fg, bg) = when (n.type) {
        NotificationType.DUE_SOON, NotificationType.REMINDER -> Triple(Icons.Outlined.Alarm, UthBlue, UthBlueLight)
        NotificationType.OVERDUE -> Triple(Icons.Outlined.WarningAmber, RedFg, RedBg)
        NotificationType.GOOD_JOB -> Triple(Icons.Outlined.Check, UthGreen, UthGreenBg)
    }
    Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.Top) {
        IconBadge(icon, fg, bg)
        Spacer(Modifier.size(12.dp))
        Column(Modifier.weight(1f)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text(n.title, fontWeight = FontWeight.SemiBold, color = UthTextPrimary, fontSize = 15.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(n.time, color = UthTextSecondary, fontSize = 12.sp)
                    if (!n.read) {
                        Spacer(Modifier.size(8.dp))
                        Box(Modifier.size(8.dp).clip(CircleShape).background(UthBlue))
                    }
                }
            }
            Text(n.message, color = UthTextSecondary, fontSize = 13.sp, modifier = Modifier.padding(top = 2.dp))
        }
    }
}

@Composable
private fun IconBadge(icon: ImageVector, fg: Color, bg: Color) {
    Box(Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(bg), Alignment.Center) {
        Icon(icon, null, tint = fg, modifier = Modifier.size(22.dp))
    }
}

/** emptyView: hiện khi danh sách thông báo rỗng. */
@Composable
private fun EmptyView(onAdd: () -> Unit, onSettings: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(Modifier.size(120.dp).clip(CircleShape).background(UthBlueLight), Alignment.Center) {
            Box(Modifier.size(80.dp).clip(CircleShape).background(Color.White), Alignment.Center) {
                Icon(Icons.Outlined.Notifications, null, tint = UthBlue, modifier = Modifier.size(36.dp))
            }
            Box(
                Modifier.align(Alignment.BottomEnd).padding(14.dp).size(26.dp).clip(CircleShape).background(UthGreen),
                Alignment.Center
            ) { Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(16.dp)) }
        }
        Spacer(Modifier.height(24.dp))
        Text("Chưa có thông báo nào", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = UthTextPrimary)
        Spacer(Modifier.height(8.dp))
        Text(
            "Khi có việc sắp đến hạn, quá hạn hoặc lời nhắc, bạn sẽ thấy chúng ở đây.",
            color = UthTextSecondary, fontSize = 14.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onAdd,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = UthBlue)
        ) {
            Icon(Icons.Filled.Add, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.size(6.dp))
            Text("Thêm việc cần làm")
        }
        TextButton(onClick = onSettings) { Text("Cài đặt nhắc việc", color = UthBlue) }
    }
}
