package com.pechenegmobilecompanyltd.honestrating.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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

    // Загрузка данных пользователя с обработкой ошибок
    LaunchedEffect(Unit) {
        if (userId.isNotEmpty()) {
            try {
                val snapshot = firestore.collection("users").document(userId).get().await()
                userData = snapshot.data
                name = TextFieldValue(userData?.get("name") as? String ?: "Не указано")
            } catch (e: Exception) {
                errorMessage = "Ошибка загрузки данных: ${e.message}"
            }
        } else {
            errorMessage = "Пользователь не аутентифицирован"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Профиль", color = Color.Black) }
            )
        }
    ) { paddingValues ->
        Card(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
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
                                    firestore.collection("users").document(userId).update("name", name.text)
                                    isEditing = false
                                    userData = userData?.toMutableMap()?.apply { this["name"] = name.text }
                                    errorMessage = null
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
                        Text(userData?.get("name") as? String ?: "Не указано", fontSize = 16.sp)
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