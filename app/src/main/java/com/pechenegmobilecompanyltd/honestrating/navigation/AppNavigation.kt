package com.pechenegmobilecompanyltd.honestrating.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pechenegmobilecompanyltd.honestrating.ui.AuthViewModel
import com.pechenegmobilecompanyltd.honestrating.ui.screens.AuthScreen
import com.pechenegmobilecompanyltd.honestrating.ui.screens.HomeScreen
import com.pechenegmobilecompanyltd.honestrating.ui.screens.OnboardingScreen
import com.pechenegmobilecompanyltd.honestrating.ui.screens.SplashScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthViewModel.AuthState.Authenticated -> {
                navController.navigate("home") {
                    popUpTo("splash") { inclusive = true }
                }
            }
            is AuthViewModel.AuthState.Unauthenticated -> {
                navController.navigate("auth") {
                    popUpTo("splash") { inclusive = true }
                }
            }
            else -> { /* Loading */ }
        }
    }

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
            AuthScreen(navController)
        }
        composable("home") {
            HomeScreen(navController)
        }
    }
}