package com.example.uthtodo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AppLogo(
    size: Dp,
    background: Color,
    primary: Color,
    secondary: Color
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.25f))
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(size * 0.12f)) {
            LogoRow(size, filled = true, color = primary)
            LogoRow(size, filled = false, color = secondary)
        }
    }
}

@Composable
private fun LogoRow(size: Dp, filled: Boolean, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(size * 0.08f)
    ) {
        Box(
            modifier = Modifier
                .size(size * 0.2f)
                .clip(CircleShape)
                .then(
                    if (filled) Modifier.background(color)
                    else Modifier.border(1.5.dp, color, CircleShape)
                )
        )
        Box(
            modifier = Modifier
                .size(width = size * 0.34f, height = size * 0.09f)
                .clip(RoundedCornerShape(50))
                .background(color)
        )
    }
}