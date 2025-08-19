package com.pechenegmobilecompanyltd.honestrating

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.pechenegmobilecompanyltd.honestrating.data.repository.CompanyRepository
import com.pechenegmobilecompanyltd.honestrating.data.repository.ReviewRepository
import com.pechenegmobilecompanyltd.honestrating.ui.screens.CompanyDetailsScreen
import com.pechenegmobilecompanyltd.honestrating.ui.screens.HomeScreen
import com.pechenegmobilecompanyltd.honestrating.ui.screens.LoginScreen
import com.pechenegmobilecompanyltd.honestrating.ui.screens.NoInternetScreen
import com.pechenegmobilecompanyltd.honestrating.ui.screens.ProfileScreen
import com.pechenegmobilecompanyltd.honestrating.ui.theme.HonestRatingTheme
import com.pechenegmobilecompanyltd.honestrating.ui.viewmodel.HomeViewModel
import com.pechenegmobilecompanyltd.honestrating.ui.viewmodel.HomeViewModelFactory
import com.pechenegmobilecompanyltd.honestrating.utils.internetConnectionState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val companyRepository = CompanyRepository()
        val reviewRepository = ReviewRepository()
        val viewModelFactory = HomeViewModelFactory(companyRepository, reviewRepository)

        setContent {
            HonestRatingTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    val auth = FirebaseAuth.getInstance()
                    val isConnected by internetConnectionState()
                    val startDestination = if (auth.currentUser != null) "home" else "login"

                    if (isConnected) {
                        Scaffold(
                            bottomBar = {
                                NavigationBar {
                                    NavigationBarItem(
                                        icon = {
                                            Icon(
                                                Icons.Default.Home,
                                                contentDescription = "Home"
                                            )
                                        },
                                        label = { Text("Home") },
                                        selected = navController.currentDestination?.route == "home",
                                        onClick = {
                                            navController.navigate("home") {
                                                popUpTo("home") { saveState = true }
                                                launchSingleTop = true
                                            }
                                        }
                                    )
                                    NavigationBarItem(
                                        icon = {
                                            Icon(
                                                Icons.Default.Person,
                                                contentDescription = "Profile"
                                            )
                                        },
                                        label = { Text("Profile") },
                                        selected = navController.currentDestination?.route == "profile",
                                        onClick = {
                                            navController.navigate("profile") {
                                                popUpTo("profile") { saveState = true }
                                                launchSingleTop = true
                                            }
                                        }
                                    )
                                }
                            }
                        ) { paddingValues ->
                            NavHost(
                                navController,
                                startDestination = startDestination,
                                Modifier.padding(paddingValues)
                            ) {
                                composable("login") { LoginScreen(navController) }
                                composable("home") {
                                    val viewModel: HomeViewModel =
                                        viewModel(factory = viewModelFactory)
                                    HomeScreen(navController, viewModel)
                                }
                                composable("profile") { ProfileScreen(navController) }
                                composable("company/{companyInn}") { backStackEntry ->
                                    val companyInn =
                                        backStackEntry.arguments?.getString("companyInn") ?: ""
                                    CompanyDetailsScreen(navController, companyInn)
                                }
                            }
                        }
                    } else {
                        NoInternetScreen {
                            val currentIsConnected by internetConnectionState()
                            if (currentIsConnected) {
                                Button(onClick = {
                                    navController.navigate(startDestination) {
                                        popUpTo(0)
                                    }
                                }) {
                                    Text("Повторить подключение")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}