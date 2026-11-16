package com.example.uthtodo.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.uthtodo.auth.AuthHelper
import com.example.uthtodo.model.onboardingPages
import com.example.uthtodo.screens.AddTaskScreen
import com.example.uthtodo.screens.HomeScreen
import com.example.uthtodo.screens.DetailScreen
import com.example.uthtodo.screens.ListScreen
import com.example.uthtodo.screens.LoginScreen
import com.example.uthtodo.screens.NotificationScreen
import com.example.uthtodo.screens.OnboardingScreen
import com.example.uthtodo.screens.ProfileScreen
import com.example.uthtodo.screens.SplashScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val goAdd = { navController.navigate(Routes.ADD) { launchSingleTop = true } }

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(onFinished = {
                // Đã đăng nhập thì vào thẳng trang chủ, chưa thì xem onboarding như cũ
                val next = if (AuthHelper.currentUser != null) Routes.HOME
                else "${Routes.ONBOARDING}/0"
                navController.navigate(next) {
                    popUpTo(Routes.SPLASH) { inclusive = true }
                }
            })
        }

        composable(
            route = "${Routes.ONBOARDING}/{index}",
            arguments = listOf(navArgument("index") { type = NavType.IntType })
        ) { backStackEntry ->
            val index = backStackEntry.arguments?.getInt("index") ?: 0
            val isLast = index == onboardingPages.lastIndex

            // Hết onboarding: đã đăng nhập thì vào thẳng trang chủ, chưa thì sang màn đăng nhập
            val finishOnboarding = {
                val next = if (AuthHelper.currentUser != null) Routes.HOME else Routes.LOGIN
                navController.navigate(next) {
                    popUpTo(Routes.ONBOARDING + "/{index}") { inclusive = true }
                }
            }

            OnboardingScreen(
                page = onboardingPages[index],
                pageIndex = index,
                pageCount = onboardingPages.size,
                buttonText = if (isLast) "Bắt đầu ngay" else "Tiếp tục",
                onNext = {
                    if (isLast) finishOnboarding()
                    else navController.navigate("${Routes.ONBOARDING}/${index + 1}")
                },
                onSkip = { finishOnboarding() }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(onLoggedIn = {
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            })
        }

        composable(Routes.HOME) {
            HomeScreen(
                onOpenProfile = { navController.navigate(Routes.PROFILE) },
                onOpenList = { navController.navigate(Routes.LIST) { launchSingleTop = true } },
                onOpenNotifications = { navController.navigate(Routes.NOTIFICATIONS) { launchSingleTop = true } },
                onAdd = goAdd,
                onOpenTask = { id -> navController.navigate("${Routes.DETAIL}/$id") }
            )
        }

        composable(Routes.LIST) {
            ListScreen(
                onHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNotifications = { navController.navigate(Routes.NOTIFICATIONS) { launchSingleTop = true } },
                onSettings = { navController.navigate(Routes.PROFILE) { launchSingleTop = true } },
                onAdd = goAdd,
                onOpenTask = { id -> navController.navigate("${Routes.DETAIL}/$id") }
            )
        }

        composable(
            route = "${Routes.DETAIL}/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            DetailScreen(taskId = id, onBack = { navController.popBackStack() })
        }

        composable(Routes.NOTIFICATIONS) {
            NotificationScreen(
                onHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onList = { navController.navigate(Routes.LIST) { launchSingleTop = true } },
                onSettings = { navController.navigate(Routes.PROFILE) { launchSingleTop = true } },
                onAdd = goAdd
            )
        }

        composable(Routes.ADD) {
            AddTaskScreen(onClose = { navController.popBackStack() })
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onLoggedOut = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
