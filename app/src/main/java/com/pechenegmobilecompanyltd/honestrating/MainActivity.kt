package com.pechenegmobilecompanyltd.honestrating

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.pechenegmobilecompanyltd.honestrating.navigation.AppNavigation
import com.pechenegmobilecompanyltd.honestrating.ui.theme.HonestRatingTheme

class MainActivity : ComponentActivity() {

    private lateinit var analytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analytics = Firebase.analytics
        setContent {
            HonestRatingTheme {
                AppNavigation()
            }
        }
    }
}