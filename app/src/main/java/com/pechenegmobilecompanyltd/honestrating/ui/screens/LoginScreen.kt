package com.pechenegmobilecompanyltd.honestrating.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Patterns
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var consent by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showResetDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }
    var resetMessage by remember { mutableStateOf("") }
    var isRegistration by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Список доступных доменов
    val allowedDomains = listOf("gmail.com", "yandex.ru", "mail.ru")

    fun validateInput(): Boolean {
        if (email.isEmpty()) {
            errorMessage = "Введите E-mail"
            return false
        }
        if (password.isEmpty()) {
            errorMessage = "Введите пароль"
            return false
        }
        if (isRegistration && confirmPassword.isEmpty()) {
            errorMessage = "Введите подтверждение пароля"
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessage = "Неверный формат email"
            return false
        }
        val domain = email.substringAfterLast("@")
        if (domain !in allowedDomains) {
            errorMessage = "Несуществующий домен почты"
            return false
        }
        if (password.length < 6) {
            errorMessage = "Пароль должен быть минимум 6 символов"
            return false
        }
        if (isRegistration && password != confirmPassword) {
            errorMessage = "Пароли не совпадают"
            return false
        }
        return true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isRegistration) "Регистрация" else "Вход", color = Color.Black) }
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
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Пароль") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.RemoveRedEye else Icons.Filled.VisibilityOff,
                                contentDescription = "Показать пароль",
                                tint = Color(0xFF2196F3)
                            )
                        }
                    },
                    singleLine = true
                )
                AnimatedVisibility(visible = isRegistration) {
                    Column {
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Подтвердите пароль") },
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                    Icon(
                                        imageVector = if (confirmPasswordVisible) Icons.Filled.RemoveRedEye else Icons.Filled.VisibilityOff,
                                        contentDescription = "Показать пароль",
                                        tint = Color(0xFF2196F3)
                                    )
                                }
                            },
                            singleLine = true
                        )
                        Row(
                            modifier = Modifier.padding(top = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(checked = consent, onCheckedChange = { consent = it })
                            Text("Согласие на обработку персональных данных (ФЗ-152 РФ)", fontSize = 12.sp)
                        }
                    }
                }
                Button(
                    onClick = {
                        if (validateInput() && (!isRegistration || consent)) {
                            if (isRegistration) {
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
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800), contentColor = Color.White)
                ) {
                    Text(if (isRegistration) "Зарегистрироваться" else "Войти", fontSize = 16.sp)
                }
                TextButton(onClick = { isRegistration = !isRegistration }) {
                    Text(
                        if (isRegistration) "Уже есть аккаунт? Войти" else "Нет аккаунта? Зарегистрироваться",
                        fontSize = 14.sp,
                        color = Color(0xFF2196F3)
                    )
                }
                TextButton(onClick = { showResetDialog = true }) {
                    Text("Забыли пароль?", fontSize = 14.sp, color = Color(0xFF2196F3))
                }
                if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = Color(0xFFD32F2F), modifier = Modifier.padding(top = 8.dp))
                }
            }
        }
    }

    // Диалог восстановления пароля
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Восстановление пароля", color = Color(0xFF2196F3)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = resetEmail,
                        onValueChange = { resetEmail = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (resetMessage.isNotEmpty()) {
                        Text(text = resetMessage, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (resetEmail.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(resetEmail).matches()) {
                            auth.sendPasswordResetEmail(resetEmail)
                                .addOnCompleteListener { task ->
                                    resetMessage = if (task.isSuccessful) "Письмо отправлено" else task.exception?.message ?: "Ошибка"
                                    showResetDialog = false
                                }
                        } else {
                            resetMessage = "Введите корректный email"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800), contentColor = Color.White)
                ) {
                    Text("Отправить")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showResetDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3), contentColor = Color.White)
                ) {
                    Text("Отмена")
                }
            }
        )
    }
}