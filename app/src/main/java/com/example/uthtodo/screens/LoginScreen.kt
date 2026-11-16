package com.example.uthtodo.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uthtodo.auth.AuthHelper
import com.example.uthtodo.auth.authErrorMessage
import com.example.uthtodo.ui.theme.UthBlue
import com.example.uthtodo.ui.theme.UthBorder
import com.example.uthtodo.ui.theme.UthTextPrimary
import com.example.uthtodo.ui.theme.UthTextSecondary
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onLoggedIn: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(false) }

    fun fail(e: Exception) {
        Toast.makeText(context, authErrorMessage(e), Toast.LENGTH_LONG).show()
    }

    // Chạy một kiểu đăng nhập: thành công -> Toast + sang trang chủ, lỗi -> Toast báo lỗi
    fun login(action: suspend () -> Unit) {
        if (loading) return
        loading = true
        scope.launch {
            try {
                action()
                Toast.makeText(context, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                onLoggedIn()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                fail(e)
            } finally {
                loading = false
            }
        }
    }

    // Hộp chọn tài khoản Google trả kết quả về đây
    val googleLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        login { AuthHelper.finishGoogleSignIn(result.data) }
    }

    LoginContent(
        loading = loading,
        onGoogle = {
            if (!loading) googleLauncher.launch(AuthHelper.googleSignInIntent(context))
        },
        onGuest = { login { AuthHelper.signInAnonymously() } }
    )
}

@Composable
private fun LoginContent(loading: Boolean, onGoogle: () -> Unit, onGuest: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Spacer(Modifier.weight(1.3f))

        AppLogo(
            size = 64.dp,
            background = UthBlue,
            primary = Color.White,
            secondary = Color.White.copy(alpha = 0.7f)
        )
        Text(
            text = "Chào mừng đến với\nUTH Todo",
            fontSize = 26.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.Bold,
            color = UthTextPrimary,
            modifier = Modifier.padding(top = 20.dp)
        )
        Text(
            text = "Đăng nhập để lưu công việc và đồng bộ trên mọi thiết bị của bạn.",
            fontSize = 14.sp,
            color = UthTextSecondary,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(Modifier.weight(1f))

        OutlinedButton(
            onClick = onGoogle,
            enabled = !loading,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, UthBorder),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = UthTextPrimary)
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = UthBlue
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .border(1.dp, UthBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("G", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = UthTextSecondary)
                }
            }
            Text(
                text = "Tiếp tục với Google",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 10.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HorizontalDivider(Modifier.weight(1f), color = UthBorder)
            Text("hoặc", fontSize = 12.sp, color = UthTextSecondary)
            HorizontalDivider(Modifier.weight(1f), color = UthBorder)
        }

        TextButton(
            onClick = onGuest,
            enabled = !loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Dùng thử không cần tài khoản",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = UthBlue
            )
        }

        Text(
            text = "Khi tiếp tục, bạn đồng ý với Điều khoản sử dụng và Chính sách quyền riêng tư của UTH Todo.",
            fontSize = 11.sp,
            lineHeight = 16.sp,
            color = UthTextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginPreview() {
    LoginContent(loading = false, onGoogle = {}, onGuest = {})
}
