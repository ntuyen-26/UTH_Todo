package com.example.uthtodo.model

import androidx.compose.runtime.Composable

data class OnboardingPage(
    val title: String,
    val description: String,
    val illustration: @Composable () -> Unit
)