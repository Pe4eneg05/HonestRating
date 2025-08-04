package com.pechenegmobilecompanyltd.honestrating.utils

import android.content.Context
import androidx.core.content.edit

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("session_prefs", Context.MODE_PRIVATE)
    fun saveLogin() = prefs.edit { putBoolean("isLoggedIn", true) }
    fun logOut() = prefs.edit { putBoolean("isLoggedIn", false) }
    fun isLoggedIn() = prefs.getBoolean("isLoggedIn", false)
}