package com.pechenegmobilecompanyltd.honestrating.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pechenegmobilecompanyltd.honestrating.ui.screens.AuthScreen
import com.pechenegmobilecompanyltd.honestrating.ui.screens.CompanyDetailScreen
import com.pechenegmobilecompanyltd.honestrating.ui.screens.CompanyListScreen
import com.pechenegmobilecompanyltd.honestrating.ui.screens.OnboardingScreen
import com.pechenegmobilecompanyltd.honestrating.ui.screens.ProfileScreen
import com.pechenegmobilecompanyltd.honestrating.ui.screens.VerifyEmailScreen
import com.pechenegmobilecompanyltd.honestrating.utils.SessionManager

object Routes {
    const val ONBOARDING = "onboarding"
    const val AUTH = "auth"
    const val VERIFY = "verify"
    const val PROFILE = "profile"
    const val COMPANIES = "companies"
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    auth: FirebaseAuth,
    firestore: FirebaseFirestore,
    sessionManager: SessionManager
) {
    val startDestination = selectStartDestination(auth, sessionManager)

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.ONBOARDING) { OnboardingScreen(navController = navController) }
        composable(Routes.AUTH) { AuthScreen(navController = navController, auth = auth) }
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
                    navController.navigate(Routes.COMPANIES) {
                        popUpTo(Routes.PROFILE) { inclusive = true }
                    }
                }
            )
        }

        composable("companies") {
            CompanyListScreen(onCompanyClick = { company ->
                navController.navigate("company/${company.id}")
            })
        }
        composable(
            "company/{companyId}",
            arguments = listOf(navArgument("companyId") { type = NavType.StringType })
        ) { backStackEntry ->
            val companyId = backStackEntry.arguments?.getString("companyId")
            // Можно передать CompanyScreen(companyId) или показать заглушку
            CompanyDetailScreen(companyId = companyId)
        }
    }
}

fun selectStartDestination(auth: FirebaseAuth, sessionManager: SessionManager): String {
    val user = auth.currentUser
    return when {
        user == null -> Routes.ONBOARDING
        !user.isEmailVerified -> Routes.VERIFY
        sessionManager.isLoggedIn() -> Routes.COMPANIES
        else -> Routes.AUTH
    }
}
