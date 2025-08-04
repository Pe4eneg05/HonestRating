package com.pechenegmobilecompanyltd.honestrating.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import com.pechenegmobilecompanyltd.honestrating.R

@Composable
fun VerifyEmailScreen(
    auth: FirebaseAuth,
    onVerified: () -> Unit,
    navController: NavController? = null
) {
    val user = auth.currentUser
    var timerRunning by remember { mutableStateOf(false) }
    var timeLeft by remember { mutableStateOf(60) }

    if (navController != null) {
        BackHandler {
            navController.navigate("auth") {
                popUpTo("verify") { inclusive = true }
            }
        }
    }

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
                .padding(24.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(8.dp),
            shape = RoundedCornerShape(22)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Email,
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.CenterHorizontally),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(24.dp))
                Text(
                    text = stringResource(R.string.verify_email_title),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.verify_email_description),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
                Spacer(Modifier.height(32.dp))
                AnimatedContent(targetState = timerRunning) { running ->
                    Button(
                        onClick = {
                            user?.sendEmailVerification()
                            timerRunning = true
                            timeLeft = 60
                        },
                        enabled = !running,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp), // чуть ниже кнопка
                        shape = RoundedCornerShape(22),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            text = if (running)
                                stringResource(R.string.resend_on, timeLeft)
                            else
                                stringResource(R.string.send_letter)
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))

                Button(
                    onClick = {
                        user?.reload()?.addOnSuccessListener {
                            if (user?.isEmailVerified == true) {
                                onVerified()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    shape = RoundedCornerShape(22),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text(stringResource(R.string.verify_done))
                }

            }
        }
    }

    LaunchedEffect(timerRunning) {
        if (timerRunning) {
            while (timeLeft > 0) {
                delay(1000)
                timeLeft -= 1
            }
            timerRunning = false
        }
    }
}