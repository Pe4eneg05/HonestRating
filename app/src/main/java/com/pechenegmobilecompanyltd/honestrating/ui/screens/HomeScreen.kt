package com.pechenegmobilecompanyltd.honestrating.ui.screens

import android.annotation.SuppressLint
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.Outbound
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.pechenegmobilecompanyltd.honestrating.utils.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavController) {

    val sessionManager = SessionManager(navController.context)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Главная") },
                actions = {
                    IconButton(onClick = {
                        Firebase.auth.signOut()
                        sessionManager.logOut()
                        navController.navigate("auth") {
                            popUpTo("home") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, "Выйти")
                    }
                    IconButton(onClick = {
                        navController.navigate("profile") {}
                    }) {
                        Icon(Icons.AutoMirrored.Outlined.Outbound, "Профиль")
                    }
                }
            )
        }
    ) {
        // Контент главного экрана
    }
}