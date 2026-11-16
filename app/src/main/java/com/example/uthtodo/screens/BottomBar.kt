package com.example.uthtodo.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uthtodo.ui.theme.UthBlue
import com.example.uthtodo.ui.theme.UthTextSecondary

enum class NavTab { HOME, LIST, NOTIFICATIONS, SETTINGS }

/** Thanh điều hướng dưới cùng dùng chung cho các màn chính. */
@Composable
fun AppBottomBar(
    selected: NavTab,
    onHome: () -> Unit,
    onList: () -> Unit,
    onNotifications: () -> Unit,
    onSettings: () -> Unit,
    onAdd: () -> Unit
) {
    val context = LocalContext.current
    val soon: () -> Unit = {
        Toast.makeText(context, "Tính năng sắp ra mắt", Toast.LENGTH_SHORT).show()
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .navigationBarsPadding()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            NavItem(Icons.Outlined.Home, "Trang chủ", selected == NavTab.HOME, Modifier.weight(1f), onHome)
            NavItem(Icons.AutoMirrored.Outlined.List, "Danh sách", selected == NavTab.LIST, Modifier.weight(1f), onList)
            Spacer(Modifier.weight(1f))
            NavItem(Icons.Outlined.Notifications, "Thông báo", selected == NavTab.NOTIFICATIONS, Modifier.weight(1f), onNotifications)
            NavItem(Icons.Outlined.Tune, "Cài đặt", selected == NavTab.SETTINGS, Modifier.weight(1f), onSettings)
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-18).dp)
                .size(52.dp)
                .clip(CircleShape)
                .background(UthBlue)
                .clickable(onClick = onAdd),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Thêm việc", tint = Color.White, modifier = Modifier.size(26.dp))
        }
    }
}

@Composable
private fun NavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val color = if (selected) UthBlue else UthTextSecondary
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(22.dp))
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = color, modifier = Modifier.padding(top = 2.dp))
    }
}
