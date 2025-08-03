package com.pechenegmobilecompanyltd.honestrating.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pechenegmobilecompanyltd.honestrating.ui.screens.AuthScreen
import com.pechenegmobilecompanyltd.honestrating.ui.screens.HomeScreen
import com.pechenegmobilecompanyltd.honestrating.ui.screens.ProfileScreen
import com.pechenegmobilecompanyltd.honestrating.ui.screens.VerifyEmailScreen
import com.pechenegmobilecompanyltd.honestrating.utils.SessionManager

object Routes {
    const val SPLASH = "splash"
    const val AUTH = "auth"
    const val VERIFY = "verify"
    const val PROFILE = "profile"
    const val HOME = "home"
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    auth: FirebaseAuth,
    firestore: FirebaseFirestore,
    sessionManager: SessionManager
) {
    NavHost(navController = navController, startDestination = Routes.SPLASH) {

        composable(Routes.SPLASH) {
            LaunchedEffect(Unit) {
                val isLoggedIn = sessionManager.isLoggedIn()
                val isEmailVerified = auth.currentUser?.isEmailVerified == true
                if (isLoggedIn) {
                    if (isEmailVerified) {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Routes.VERIFY) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    }
                } else {
                    navController.navigate(Routes.AUTH) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            }
        }

        composable(Routes.AUTH) {
            // Здесь параметр navController передаём в AuthScreen
            AuthScreen(navController = navController, auth = auth)
        }

        composable(Routes.VERIFY) {
            VerifyEmailScreen(
                auth = auth,
                onVerified = {
                    sessionManager.saveLogin()
                    navController.navigate(Routes.PROFILE) {
                        popUpTo(Routes.VERIFY) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                auth = auth,
                firestore = firestore,
                onComplete = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.PROFILE) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(navController)
        }
    }
}
