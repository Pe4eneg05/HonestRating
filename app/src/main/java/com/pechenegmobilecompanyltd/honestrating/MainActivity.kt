package com.pechenegmobilecompanyltd.honestrating

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.pechenegmobilecompanyltd.honestrating.navigation.AppNavigation
import com.pechenegmobilecompanyltd.honestrating.ui.theme.HonestRatingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HonestRatingTheme {
                AppNavigation()
            }
        }
    }
}