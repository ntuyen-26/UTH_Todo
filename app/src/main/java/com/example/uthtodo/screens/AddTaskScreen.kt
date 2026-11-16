package com.example.uthtodo.screens

import android.app.TimePickerDialog
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uthtodo.model.AddTaskViewModel
import com.example.uthtodo.model.DayChoice
import com.example.uthtodo.model.Priority
import com.example.uthtodo.model.TodoCategory
import com.example.uthtodo.ui.theme.UthBackground
import com.example.uthtodo.ui.theme.UthBlue
import com.example.uthtodo.ui.theme.UthBlueLight
import com.example.uthtodo.ui.theme.UthBorder
import com.example.uthtodo.ui.theme.UthGreen
import com.example.uthtodo.ui.theme.UthTextPrimary
import com.example.uthtodo.ui.theme.UthTextSecondary

/** View: màn "Việc mới" (MVVM), mọi dữ liệu và logic nằm ở AddTaskViewModel. */
@Composable
fun AddTaskScreen(onClose: () -> Unit, viewModel: AddTaskViewModel = viewModel()) {
    val context = LocalContext.current
    val s by viewModel.state.collectAsState()

    val save = {
        viewModel.save(
            onSuccess = {
                Toast.makeText(context, "Đã thêm công việc", Toast.LENGTH_SHORT).show()
                onClose()
            },
            onError = { msg -> Toast.makeText(context, msg, Toast.LENGTH_LONG).show() }
        )
    }

    Column(
        Modifier.fillMaxSize().background(UthBackground).statusBarsPadding().imePadding()
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
            Arrangement.SpaceBetween, Alignment.CenterVertically
        ) {
            Text("Hủy", color = UthBlue, fontSize = 16.sp, modifier = Modifier.clickable(onClick = onClose))
            Text("Việc mới", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = UthTextPrimary)
            Text(
                "Lưu", fontSize = 16.sp,
                color = if (s.canSave) UthBlue else UthTextSecondary,
                modifier = Modifier.clickable(enabled = s.canSave) { save() }
            )
        }

        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)
        ) {
            Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.White)) {
                FieldBlock("Tên công việc", s.title, "Ví dụ: Ôn chương 4 – Giải thuật", viewModel::onTitle)
                Box(Modifier.fillMaxWidth().height(1.dp).background(UthBackground))
                FieldBlock("Ghi chú", s.note, "Thêm ghi chú (không bắt buộc)", viewModel::onNote, minHeight = 64)
            }

            Label("Nhóm")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TodoCategory.values().forEach { c ->
                    Choice(c.label, s.category == c, Modifier.weight(1f)) { viewModel.onCategory(c) }
                }
            }

            Label("Ngày")
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                DayChoice.values().forEach { d ->
                    Choice(d.label, s.day == d, Modifier.weight(1f)) { viewModel.onDay(d) }
                }
            }

            Spacer(Modifier.height(16.dp))
            Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.White)) {
                Row(Modifier.fillMaxWidth().padding(14.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.AccessTime, null, tint = UthTextSecondary, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.size(10.dp))
                        Text("Giờ", color = UthTextPrimary, fontWeight = FontWeight.Medium)
                    }
                    Text(
                        s.time, color = UthTextPrimary,
                        modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(UthBackground)
                            .clickable {
                                val (h, m) = s.time.split(":").let { (it.getOrNull(0)?.toIntOrNull() ?: 19) to (it.getOrNull(1)?.toIntOrNull() ?: 0) }
                                TimePickerDialog(context, { _, hh, mm -> viewModel.onTime("%02d:%02d".format(hh, mm)) }, h, m, true).show()
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
                Box(Modifier.fillMaxWidth().height(1.dp).background(UthBackground))
                Row(Modifier.fillMaxWidth().padding(14.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Notifications, null, tint = UthTextSecondary, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.size(10.dp))
                        Column {
                            Text("Nhắc tôi", color = UthTextPrimary, fontWeight = FontWeight.Medium)
                            Text("Trước 15 phút", color = UthTextSecondary, fontSize = 12.sp)
                        }
                    }
                    Switch(
                        checked = s.remind, onCheckedChange = viewModel::onRemind,
                        colors = SwitchDefaults.colors(checkedTrackColor = UthGreen, checkedThumbColor = Color.White)
                    )
                }
            }

            Label("Mức ưu tiên")
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Color(0xFFE9EBF0)).padding(4.dp)
            ) {
                Priority.values().forEach { p ->
                    val on = s.priority == p
                    Text(
                        p.label, textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = if (on) UthTextPrimary else UthTextSecondary,
                        fontWeight = if (on) FontWeight.SemiBold else FontWeight.Normal,
                        modifier = Modifier.weight(1f).clip(RoundedCornerShape(11.dp))
                            .background(if (on) Color.White else Color.Transparent)
                            .clickable { viewModel.onPriority(p) }.padding(vertical = 11.dp)
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
        }

        Box(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp).navigationBarsPadding()
                .height(52.dp).clip(RoundedCornerShape(14.dp))
                .background(if (s.canSave) UthBlue else Color(0xFFE3E5EA))
                .clickable(enabled = s.canSave) { save() },
            Alignment.Center
        ) {
            Text(
                if (s.saving) "Đang lưu..." else "Lưu công việc",
                color = if (s.canSave) Color.White else UthTextSecondary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun Label(text: String) {
    Text(text, color = UthTextSecondary, fontSize = 14.sp, fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(top = 20.dp, bottom = 8.dp))
}

@Composable
private fun Choice(text: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier.clip(RoundedCornerShape(14.dp))
            .background(if (selected) UthBlueLight else Color.White)
            .border(1.5.dp, if (selected) UthBlue else UthBorder, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick).padding(vertical = 14.dp),
        Alignment.Center
    ) {
        Text(text, color = if (selected) UthBlue else UthTextPrimary, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun FieldBlock(label: String, value: String, hint: String, onChange: (String) -> Unit, minHeight: Int = 0) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 6.dp)) {
        Text(label, color = UthTextSecondary, fontSize = 12.sp, modifier = Modifier.padding(start = 10.dp, top = 6.dp))
        TextField(
            value = value, onValueChange = onChange,
            placeholder = { Text(hint, color = UthTextSecondary) },
            modifier = Modifier.fillMaxWidth().heightIn(min = minHeight.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}
