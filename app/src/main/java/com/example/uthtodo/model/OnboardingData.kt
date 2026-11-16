package com.example.uthtodo.model

import com.example.uthtodo.screens.CategoryIllustration
import com.example.uthtodo.screens.ChecklistIllustration
import com.example.uthtodo.screens.ReminderIllustration

val onboardingPages = listOf(
    OnboardingPage(
        title = "Mọi việc, gọn trong một chỗ",
        description = "Ghi lại bài tập, việc nhà, lịch hẹn. Không còn quên việc nào.",
        illustration = { ChecklistIllustration() }
    ),
    OnboardingPage(
        title = "Chia theo Học tập và Cuộc sống",
        description = "Mỗi việc có nhóm riêng, nhìn là biết hôm nay cần lo gì.",
        illustration = { CategoryIllustration() }
    ),
    OnboardingPage(
        title = "Nhắc đúng lúc",
        description = "Bật thông báo để UTH Todo nhắc bạn trước hạn chót.",
        illustration = { ReminderIllustration() }
    )
)