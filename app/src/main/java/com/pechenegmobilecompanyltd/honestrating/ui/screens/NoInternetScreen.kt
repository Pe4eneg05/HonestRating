package com.pechenegmobilecompanyltd.honestrating.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NoInternetScreen(onRetry: @Composable () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Нет подключения к интернету", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Проверьте соединение и попробуйте снова", fontSize = 16.sp)
        Spacer(modifier = Modifier.height(24.dp))
        onRetry()
    }
}