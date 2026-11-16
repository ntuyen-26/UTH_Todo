package com.example.uthtodo.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
private val Blue = Color(0xFF3B5BDB)
private val SoftBlue = Color(0xFFE9EDFB)
private val BarGray = Color(0xFFE3E6EE)

@Composable
private fun IllustrationFrame(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .size(width = 240.dp, height = 280.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(SoftBlue),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun WhiteCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .shadow(3.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(horizontal = 12.dp, vertical = 12.dp)
    ) {
        content()
    }
}

@Composable
private fun GrayBar(width: Dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(width)
            .height(5.dp)
            .clip(RoundedCornerShape(50))
            .background(BarGray)
    )
}

@Composable
private fun CheckCircle(checked: Boolean) {
    Box(
        modifier = Modifier
            .size(18.dp)
            .clip(CircleShape)
            .then(
                if (checked) Modifier.background(Blue)
                else Modifier.border(1.5.dp, Color(0xFFB8BDC9), CircleShape)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Canvas(modifier = Modifier.size(10.dp)) {
                val path = Path().apply {
                    moveTo(size.width * 0.1f, size.height * 0.55f)
                    lineTo(size.width * 0.4f, size.height * 0.85f)
                    lineTo(size.width * 0.9f, size.height * 0.2f)
                }
                drawPath(
                    path,
                    Color.White,
                    style = Stroke(width = 2.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }
        }
    }
}

@Composable
fun ChecklistIllustration() {
    IllustrationFrame {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            repeat(3) { i ->
                WhiteCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CheckCircle(checked = i == 0)
                        GrayBar(width = if (i == 2) 70.dp else 80.dp)
                    }
                }
            }
        }
    }
}

@Composable
private fun TagPill(text: String, textColor: Color, bgColor: Color) {
    Text(
        text = text,
        color = textColor,
        fontSize = 10.sp,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    )
}

@Composable
fun CategoryIllustration() {
    IllustrationFrame {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WhiteCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TagPill("Học tập", Blue, Color(0xFFE3E9FB))
                    GrayBar(width = 120.dp)
                }
            }
            WhiteCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TagPill("Cuộc sống", Color(0xFFD9730D), Color(0xFFFCE8D5))
                    GrayBar(width = 100.dp)
                }
            }
        }
    }
}

@Composable
private fun BellIcon() {
    Canvas(modifier = Modifier.size(28.dp)) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = 3f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val body = Path().apply {
            moveTo(w * 0.2f, h * 0.75f)
            lineTo(w * 0.8f, h * 0.75f)
            lineTo(w * 0.72f, h * 0.62f)
            lineTo(w * 0.72f, h * 0.42f)
            cubicTo(w * 0.72f, h * 0.22f, w * 0.28f, h * 0.22f, w * 0.28f, h * 0.42f)
            lineTo(w * 0.28f, h * 0.62f)
            close()
        }
        drawPath(body, Blue, style = stroke)
        drawLine(Blue, Offset(w * 0.5f, h * 0.15f), Offset(w * 0.5f, h * 0.22f), 3f, StrokeCap.Round)
        drawLine(Blue, Offset(w * 0.42f, h * 0.86f), Offset(w * 0.58f, h * 0.86f), 3f, StrokeCap.Round)
    }
}

@Composable
fun ReminderIllustration() {
    IllustrationFrame {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .shadow(3.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                BellIcon()
            }
            Text(
                text = "Ôn bài Toán lúc 19:00",
                fontSize = 11.sp,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .shadow(3.dp, RoundedCornerShape(10.dp))
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            )
        }
    }
}