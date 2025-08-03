package com.pechenegmobilecompanyltd.honestrating

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pechenegmobilecompanyltd.honestrating.navigation.AppNavigation
import com.pechenegmobilecompanyltd.honestrating.ui.theme.HonestRatingTheme
import com.pechenegmobilecompanyltd.honestrating.utils.SessionManager

class MainActivity : ComponentActivity() {

    private lateinit var analytics: FirebaseAnalytics
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализация Firebase Analytics
        analytics = Firebase.analytics

        // Инициализация Firebase Auth и Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Инициализация SessionManager
        sessionManager = SessionManager(applicationContext)

        setContent {
            HonestRatingTheme {
                Surface {
                    val navController = rememberNavController()
                    // Передаём все зависимости в навигацию
                    AppNavigation(
                        navController = navController,
                        auth = auth,
                        firestore = firestore,
                        sessionManager = sessionManager
                    )
                }
            }
        }
    }
}