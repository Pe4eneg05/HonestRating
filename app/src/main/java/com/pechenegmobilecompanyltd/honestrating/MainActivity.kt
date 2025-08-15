package com.pechenegmobilecompanyltd.honestrating

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.pechenegmobilecompanyltd.honestrating.ui.theme.HonestRatingTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pechenegmobilecompanyltd.honestrating.ui.screens.LoginScreen
import com.pechenegmobilecompanyltd.honestrating.data.database.HonestRatingDatabase
import com.google.firebase.auth.FirebaseAuth
import com.pechenegmobilecompanyltd.honestrating.ui.screens.CompanyDetailsScreen
import com.pechenegmobilecompanyltd.honestrating.ui.screens.HomeScreen
import com.pechenegmobilecompanyltd.honestrating.ui.screens.ProfileScreen

class MainActivity : ComponentActivity() {
    private lateinit var db: HonestRatingDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = HonestRatingDatabase.getDatabase(this)
        setContent {
            HonestRatingTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    val auth = FirebaseAuth.getInstance()
                    val startDestination = if (auth.currentUser != null) "home" else "login"

                    NavHost(navController, startDestination = startDestination) {
                        composable("login") { LoginScreen(navController) }
                        composable("home") { HomeScreen(navController, db) }
                        composable("profile") { ProfileScreen(navController) }
                        composable("company/{companyId}") { backStackEntry ->
                            val companyId = backStackEntry.arguments?.getString("companyId")?.toInt() ?: 0
                            CompanyDetailsScreen(navController, companyId)
                        }
                    }
                }
            }
        }
    }
}