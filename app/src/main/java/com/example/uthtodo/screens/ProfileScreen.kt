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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uthtodo.auth.AuthHelper
import com.example.uthtodo.auth.displayLabel
import com.example.uthtodo.auth.initialsOf
import com.example.uthtodo.auth.isGoogleLinked
import com.example.uthtodo.model.TodoStore
import com.example.uthtodo.ui.theme.UthBackground
import com.example.uthtodo.ui.theme.UthBlue
import com.example.uthtodo.ui.theme.UthBlueLight
import com.example.uthtodo.ui.theme.UthBorder
import com.example.uthtodo.ui.theme.UthGreen
import com.example.uthtodo.ui.theme.UthGreenBg
import com.example.uthtodo.ui.theme.UthTextPrimary
import com.example.uthtodo.ui.theme.UthTextSecondary
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun ProfileScreen(onBack: () -> Unit, onLoggedOut: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val user = AuthHelper.currentUser
    val name = user.displayLabel()
    val googleLinked = user.isGoogleLinked()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(UthBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Thanh tiêu đề: < Cài đặt | Hồ sơ
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clickable(onClick = onBack),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Quay lại", tint = UthBlue, modifier = Modifier.size(22.dp))
                Text("Cài đặt", fontSize = 14.sp, color = UthBlue)
            }
            Text(
                "Hồ sơ",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = UthTextPrimary,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(UthBlueLight),
                contentAlignment = Alignment.Center
            ) {
                Text(initialsOf(name), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = UthBlue)
            }
            Text(name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = UthTextPrimary, modifier = Modifier.padding(top = 12.dp))
            Text(
                text = user?.email?.takeIf { it.isNotBlank() } ?: "Chưa có email",
                fontSize = 13.sp,
                color = UthTextSecondary,
                modifier = Modifier.padding(top = 2.dp)
            )

            // Nhãn trạng thái liên kết
            Row(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (googleLinked) UthGreenBg else UthBorder)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (googleLinked) {
                    Icon(Icons.Filled.Check, contentDescription = null, tint = UthGreen, modifier = Modifier.size(12.dp))
                    Spacer(Modifier.width(4.dp))
                }
                Text(
                    text = if (googleLinked) "Đã liên kết tài khoản Google" else "Tài khoản dùng thử",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (googleLinked) UthGreen else UthTextSecondary
                )
            }

            // 3 ô thống kê
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(Modifier.weight(1f), "${TodoStore.doneCount}", "Việc đã xong")
                StatCard(Modifier.weight(1f), "${TodoStore.pendingCount}", "Đang chờ")
                StatCard(Modifier.weight(1f), "${TodoStore.percent}%", "Hoàn thành")
            }

            WeeklyChart(modifier = Modifier.padding(top = 12.dp))
        }

        // Nút đăng xuất
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White)
                .clickable {
                    scope.launch {
                        AuthHelper.signOut(context)
                        Toast.makeText(context, "Đã đăng xuất", Toast.LENGTH_SHORT).show()
                        onLoggedOut()
                    }
                }
                .padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = null, tint = UthBlue, modifier = Modifier.size(18.dp))
            Text("Đăng xuất", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = UthBlue, modifier = Modifier.padding(start = 8.dp))
        }
    }
}

@Composable
private fun StatCard(modifier: Modifier, value: String, label: String) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .padding(12.dp)
    ) {
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = UthTextPrimary)
        Text(label, fontSize = 11.sp, color = UthTextSecondary)
    }
}

@Composable
private fun WeeklyChart(modifier: Modifier = Modifier) {
    val labels = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
    // Thứ trong tuần của hôm nay: T2 = 0 ... CN = 6
    val today = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 5) % 7
    // Dữ liệu mẫu cho các ngày đã qua; hôm nay lấy theo số việc đã xong thật
    val pastSample = listOf(3, 2, 4, 1, 3, 2, 3)
    val values = labels.indices.map { i ->
        when {
            i < today -> pastSample[i]
            i == today -> TodoStore.doneCount
            else -> 0
        }
    }
    val maxValue = (values.maxOrNull() ?: 1).coerceAtLeast(1)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .padding(14.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Tuần này", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = UthTextPrimary)
            Text("Việc xong mỗi ngày", fontSize = 11.sp, color = UthTextSecondary)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            labels.forEachIndexed { i, label ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.height(64.dp), contentAlignment = Alignment.BottomCenter) {
                        val barHeight = if (values[i] == 0) 2.dp else (64.dp * values[i] / maxValue)
                        Box(
                            modifier = Modifier
                                .width(14.dp)
                                .height(barHeight)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    when {
                                        values[i] == 0 -> UthBorder
                                        i == today -> UthBlue
                                        else -> Color(0xFFBAC8FF)
                                    }
                                )
                        )
                    }
                    Text(label, fontSize = 10.sp, color = UthTextSecondary, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 6.dp))
                }
            }
        }
    }
}
