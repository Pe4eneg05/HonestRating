package com.pechenegmobilecompanyltd.honestrating.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val scope = rememberCoroutineScope()
    val userId = auth.currentUser?.uid ?: ""
    var userData by remember { mutableStateOf<Map<String, Any>?>(null) }
    var isEditing by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf(TextFieldValue("")) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isDataLoaded by remember { mutableStateOf(false) }

    // Загрузка данных Firestore (и активация shimmer)
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            isDataLoaded = false
            try {
                val document = firestore.collection("users").document(userId).get().await()
                userData = document.data
                name = TextFieldValue(userData?.get("name") as? String ?: "Не указано")
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Ошибка загрузки данных: ${e.message}"
                name = TextFieldValue("Не указано")
            } finally {
                isDataLoaded = true
            }
        } else {
            errorMessage = "Пользователь не аутентифицирован"
            name = TextFieldValue("Не указано")
            isDataLoaded = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Профиль", color = Color.Black) }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Crossfade(targetState = isDataLoaded, label = "ProfileCrossfade") { loaded ->
                    if (!loaded) {
                        ProfileShimmerPlaceholder()
                    } else {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Email: ${auth.currentUser?.email ?: "Неизвестно"}", fontSize = 16.sp)
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Имя: ", fontSize = 16.sp)
                                if (isEditing) {
                                    OutlinedTextField(
                                        value = name,
                                        onValueChange = { name = it },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )
                                    IconButton(onClick = {
                                        scope.launch {
                                            try {
                                                if (userId.isNotEmpty()) {
                                                    val document = firestore.collection("users").document(userId).get().await()
                                                    if (!document.exists()) {
                                                        firestore.collection("users").document(userId)
                                                            .set(mapOf("name" to name.text, "email" to (auth.currentUser?.email ?: "")))
                                                            .await()
                                                    } else {
                                                        firestore.collection("users").document(userId).update("name", name.text).await()
                                                    }
                                                    userData = userData?.toMutableMap()?.apply { this["name"] = name.text }
                                                    isEditing = false
                                                    errorMessage = null
                                                }
                                            } catch (e: Exception) {
                                                errorMessage = "Ошибка сохранения: ${e.message}"
                                            }
                                        }
                                    }) {
                                        Icon(
                                            imageVector = Icons.Filled.Edit,
                                            contentDescription = "Сохранить",
                                            tint = Color(0xFF2196F3)
                                        )
                                    }
                                } else {
                                    Text(name.text, fontSize = 16.sp)
                                    IconButton(onClick = { isEditing = true }) {
                                        Icon(
                                            imageVector = Icons.Filled.Edit,
                                            contentDescription = "Редактировать",
                                            tint = Color(0xFF2196F3)
                                        )
                                    }
                                }
                            }
                            Button(
                                onClick = {
                                    auth.signOut()
                                    navController.navigate("login") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800), contentColor = Color.White)
                            ) {
                                Text("Выйти", fontSize = 16.sp)
                            }
                            if (errorMessage != null) {
                                Text(text = errorMessage!!, color = Color(0xFFD32F2F), modifier = Modifier.padding(top = 8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileShimmerPlaceholder() {
    val shimmerInstance = rememberShimmer(shimmerBounds = ShimmerBounds.View)
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .shimmer(shimmerInstance)
                .background(Color.LightGray.copy(alpha = 0.36f))
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(16.dp)
                    .shimmer(shimmerInstance)
                    .background(Color.LightGray.copy(alpha = 0.36f))
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .shimmer(shimmerInstance)
                    .background(Color.LightGray.copy(alpha = 0.36f))
            )
        }
        Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800).copy(alpha = 0.2f), contentColor = Color.White.copy(alpha = 0.2f)),
            enabled = false
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .shimmer(shimmerInstance)
                    .background(Color.LightGray.copy(alpha = 0.36f))
            )
        }
    }
}