package com.pechenegmobilecompanyltd.honestrating.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var consent by remember { mutableStateOf(false) } // Согласие на обработку данных
    var errorMessage by remember { mutableStateOf("") }
    var showResetDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }
    var resetMessage by remember { mutableStateOf("") }

    // Регистрация нового пользователя с сохранением в Firestore
    LaunchedEffect(auth.currentUser) {
        if (auth.currentUser != null && auth.currentUser?.isEmailVerified == true) {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Вход / Регистрация") }) }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            Row(modifier = Modifier.padding(top = 8.dp)) {
                Checkbox(checked = consent, onCheckedChange = { consent = it })
                Text("Согласие на обработку персональных данных (ФЗ-152 РФ)")
            }
            Button(
                onClick = {
                    if (consent) {
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val user = task.result?.user
                                    user?.sendEmailVerification()
                                    firestore.collection("users").document(user?.uid ?: "")
                                        .set(mapOf("email" to email, "name" to "Пользователь"))
                                    errorMessage = "Подтвердите email для активации"
                                } else {
                                    errorMessage = task.exception?.message ?: "Ошибка регистрации"
                                }
                            }
                    } else {
                        errorMessage = "Требуется согласие"
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text("Регистрация")
            }
            Button(
                onClick = {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = task.result?.user
                                if (user?.isEmailVerified == true) {
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                } else {
                                    errorMessage = "Подтвердите email. Повторить отправку?"
                                    user?.sendEmailVerification()
                                }
                            } else {
                                errorMessage = task.exception?.message ?: "Ошибка входа"
                            }
                        }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text("Вход")
            }
            TextButton(onClick = { showResetDialog = true }) {
                Text("Забыли пароль?")
            }
            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
            }
        }
    }

    // Диалог восстановления пароля
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Восстановление пароля") },
            text = {
                Column {
                    OutlinedTextField(
                        value = resetEmail,
                        onValueChange = { resetEmail = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (resetMessage.isNotEmpty()) {
                        Text(text = resetMessage)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        auth.sendPasswordResetEmail(resetEmail)
                            .addOnCompleteListener { task ->
                                resetMessage = if (task.isSuccessful) "Письмо отправлено" else task.exception?.message ?: "Ошибка"
                                showResetDialog = false
                            }
                    }
                ) {
                    Text("Отправить")
                }
            },
            dismissButton = {
                Button(onClick = { showResetDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}