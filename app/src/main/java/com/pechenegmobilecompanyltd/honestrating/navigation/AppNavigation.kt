package com.pechenegmobilecompanyltd.honestrating.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pechenegmobilecompanyltd.honestrating.ui.screens.OnboardingScreen
import com.pechenegmobilecompanyltd.honestrating.ui.screens.SplashScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(navController)
        }
        composable("onboarding") {
            OnboardingScreen(navController)
        }
        composable("auth") {
            OnboardingScreen(navController) // Пока заглушк
        }
    }
}