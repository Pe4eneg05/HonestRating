package com.pechenegmobilecompanyltd.honestrating.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
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
fun AuthScreen(
    navController: NavHostController,
    auth: FirebaseAuth
) {
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
    val authCore = FirebaseAuthManager.auth

    // Проверка текущего пользователя
    LaunchedEffect(Unit) {
        if (authCore.currentUser != null) {
            navController.navigate("companies") {
                popUpTo("auth") { inclusive = true }
            }
        }
    }

    // === ФОН ===
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.background
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(22.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(8.dp),
            shape = RoundedCornerShape(20)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isLoginMode) stringResource(R.string.login_txt)
                    else stringResource(R.string.reg_txt),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(24.dp))

                /// Email field
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = false
                    },
                    label = { Text(stringResource(R.string.email_hint)) },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    isError = emailError,
                    supportingText = {
                        if (emailError) {
                            Text(emailErrorMessage)
                        }
                    },
                    singleLine = true, // Запрет переноса на новую строку
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp), // Фиксированная минимальная высота
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { passwordFocusRequester.requestFocus() }
                    )
                )

                Spacer(Modifier.height(12.dp))

                // Password field
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = false
                    },
                    label = { Text(stringResource(R.string.password_hint)) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true, // Запрет переноса на новую строку
                    isError = passwordError,
                    supportingText = {
                        if (passwordError) {
                            Text(stringResource(R.string.password_error))
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp) // Фиксированная минимальная высота
                        .focusRequester(passwordFocusRequester),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    )
                )

                Spacer(Modifier.height(18.dp))

                // Login/Register button
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        // Валидация
                        if (!isValidEmail(email)) {
                            emailError = true
                            emailErrorMessage = emailErrorText
                            return@Button
                        }
                        if (isValidCorporateEmail(email)) {
                            emailError = true
                            emailErrorMessage = corporateErrorText
                            return@Button
                        }
                        if (!isValidPassword(password)) {
                            passwordError = true
                            return@Button
                        }
                        isLoading = true
                        //Авторизация
                        if (isLoginMode) {
                            auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    isLoading = false
                                    if (task.isSuccessful) {
                                        navController.navigate("companies") {
                                            popUpTo("auth") { inclusive = true }
                                        }
                                    } else {
                                        emailError = true
                                        emailErrorMessage = task.exception?.message ?: "Ошибка входа"
                                    }
                                }
                        } else {
                            //Регистрация
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    isLoading = false
                                    if (task.isSuccessful) {
                                        createUserProfile(auth.currentUser?.uid, email)
                                        navController.navigate("verify") {
                                            popUpTo("auth") { inclusive = true }
                                        }
                                    } else {
                                        emailError = true
                                        emailErrorMessage = task.exception?.message ?: "Ошибка регистрации"
                                    }
                                }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(22),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = if (isLoginMode) stringResource(R.string.login_button)
                            else stringResource(R.string.register_button_2),
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Switch login/register
                TextButton(
                    onClick = { isLoginMode = !isLoginMode },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = if (isLoginMode) stringResource(R.string.register_button)
                        else stringResource(R.string.login_button),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
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