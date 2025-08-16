package com.pechenegmobilecompanyltd.honestrating.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pechenegmobilecompanyltd.honestrating.data.database.HonestRatingDatabase

@Composable
fun HomeScreen(navController: NavController, database: HonestRatingDatabase) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Home Screen")
        Button(onClick = { navController.navigate("profile") }) {
            Text("Перейти в профиль")
        }
    }
}