package com.example.uthtodo.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uthtodo.model.OnboardingPage
import com.example.uthtodo.model.onboardingPages
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.ButtonDefaults
@Composable
fun OnboardingScreen(
    page: OnboardingPage,
    pageIndex: Int,
    pageCount: Int,
    buttonText: String,
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    val blue = Color(0xFF3B5BDB)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AppLogo(
                    size = 32.dp,
                    background = blue,
                    primary = Color.White,
                    secondary = Color.White.copy(alpha = 0.6f)
                )
                Row {
                    Text("UTH ", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Todo", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = blue)
                }
            }
            TextButton(
                onClick = onSkip,
                colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)
            ) {
                Text(text = "Bỏ qua")
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            page.illustration()
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = page.title,
            fontSize = 30.sp,
            lineHeight = 38.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = page.description,
            fontSize = 17.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 12.dp)
        )

        Row(
            modifier = Modifier.padding(top = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(pageCount) { index ->
                Box(
                    modifier = Modifier
                        .size(width = if (index == pageIndex) 24.dp else 8.dp, height = 8.dp)
                        .clip(CircleShape)
                        .background(if (index == pageIndex) blue else Color.LightGray)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onNext,
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = blue,
                contentColor = Color.White
            )
        ) {
            Text(text = buttonText, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}
@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    OnboardingScreen(
        page = onboardingPages[0],
        pageIndex = 0,
        pageCount = onboardingPages.size,
        buttonText = "Tiếp tục",
        onNext = {},
        onSkip = {}
    )
}

