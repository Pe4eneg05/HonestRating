package com.pechenegmobilecompanyltd.honestrating.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pechenegmobilecompanyltd.honestrating.R
import com.pechenegmobilecompanyltd.honestrating.ui.components.AnimatedTextField
import com.pechenegmobilecompanyltd.honestrating.ui.theme.PrimaryColor
import com.pechenegmobilecompanyltd.honestrating.utils.FirebaseAuthManager
import com.pechenegmobilecompanyltd.honestrating.utils.isValidCorporateEmail
import com.pechenegmobilecompanyltd.honestrating.utils.isValidEmail
import com.pechenegmobilecompanyltd.honestrating.utils.isValidPassword
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(navController: NavController) {

    val emailErrorText = stringResource(R.string.email_error)
    val corporateErrorText = stringResource(R.string.corporate_email_error)

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var emailErrorMessage by remember { mutableStateOf("") }
    var isLoginMode by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val passwordFocusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()

    // Firebase Auth
    val auth = FirebaseAuthManager.auth

    // Проверка текущего пользователя
    LaunchedEffect(Unit) {
        if (auth.currentUser != null) {
            navController.navigate("home") {
                popUpTo("auth") { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (isLoginMode) stringResource(R.string.login_button)
                        else stringResource(R.string.register_button),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate("onboarding") {
                            popUpTo("auth") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Анимированное появление формы
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Поле email
                    AnimatedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = false
                        },
                        label = stringResource(R.string.email_hint),
                        icon = Icons.Default.Email,
                        isError = emailError,
                        errorMessage = emailErrorMessage
                    )

                    // Поле пароля
                    AnimatedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = false
                        },
                        label = stringResource(R.string.password_hint),
                        icon = Icons.Default.Lock,
                        isPassword = true,
                        isError = passwordError,
                        errorMessage = stringResource(R.string.password_error),
                        modifier = Modifier.focusRequester(passwordFocusRequester)
                    )

                    // Кнопка входа/регистрации
                    Button(
                        onClick = {
                            focusManager.clearFocus()

                            // Валидация email
                            if (!isValidEmail(email)) {
                                emailError = true
                                emailErrorMessage = emailErrorText
                                return@Button
                            }

                            // Проверка корпоративного email
                            if (isValidCorporateEmail(email)) {
                                emailError = true
                                emailErrorMessage = corporateErrorText
                                return@Button
                            }

                            // Валидация пароля
                            if (!isValidPassword(password)) {
                                passwordError = true
                                return@Button
                            }

                            // Запуск процесса аутентификации
                            isLoading = true
                            // Аутентификация через Firebase
                            if (isLoginMode) {
                                // Вход
                                auth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { task ->
                                        isLoading = false
                                        if (task.isSuccessful) {
                                            navController.navigate("home") {
                                                popUpTo("auth") { inclusive = true }
                                            }
                                        } else {
                                            emailError = true
                                            emailErrorMessage =
                                                task.exception?.message ?: "Ошибка входа"
                                        }
                                    }
                            } else {
                                // Регистрация
                                auth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { task ->
                                        isLoading = false
                                        if (task.isSuccessful) {
                                            // Создаем профиль пользователя в Firestore
                                            createUserProfile(auth.currentUser?.uid, email)
                                            navController.navigate("home") {
                                                popUpTo("auth") { inclusive = true }
                                            }
                                        } else {
                                            emailError = true
                                            emailErrorMessage =
                                                task.exception?.message ?: "Ошибка регистрации"
                                        }
                                    }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryColor,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                text = if (isLoginMode) stringResource(R.string.login_button)
                                else stringResource(R.string.register_button),
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Переключатель между входом и регистрацией
            TextButton(
                onClick = { isLoginMode = !isLoginMode },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = if (isLoginMode) stringResource(R.string.register_button)
                    else stringResource(R.string.login_button),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

private fun createUserProfile(uid: String?, email: String) {
    uid?.let {
        val userData = hashMapOf(
            "email" to email,
            "createdAt" to com.google.firebase.Timestamp.now(),
            "isHR" to false,
            "companyId" to ""
        )

        FirebaseAuthManager.firestore.collection("users")
            .document(uid)
            .set(userData)
            .addOnSuccessListener { /* Успех */ }
            .addOnFailureListener { /* Ошибка */ }
    }
}