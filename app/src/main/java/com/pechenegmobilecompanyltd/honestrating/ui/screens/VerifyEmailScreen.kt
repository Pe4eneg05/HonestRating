package com.pechenegmobilecompanyltd.honestrating.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds
import com.pechenegmobilecompanyltd.honestrating.R

@Composable
fun VerifyEmailScreen(
    auth: FirebaseAuth,
    onVerified: () -> Unit
) {
    val user = auth.currentUser
    var timerRunning by remember { mutableStateOf(false) }
    var timeLeft by remember { mutableIntStateOf(60) }

    LaunchedEffect(timerRunning) {
        if (timerRunning) {
            while (timeLeft > 0) {
                delay(1.seconds)
                timeLeft--
            }
            timerRunning = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.verify_email_title),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = {
                user?.sendEmailVerification()
                timeLeft = 60
                timerRunning = true
            },
            enabled = !timerRunning,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = if (!timerRunning)
                    stringResource(R.string.send_letter_button)
                else
                    stringResource(R.string.resend_in_button, timeLeft)
            )
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                user?.reload()?.addOnSuccessListener {
                    if (auth.currentUser?.isEmailVerified == true) {
                        onVerified()
                    }
                }
            }
        ) {
            Text(text = stringResource(R.string.check_verification_button))
        }
    }
}