package com.pechenegmobilecompanyltd.honestrating.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.pechenegmobilecompanyltd.honestrating.utils.FirebaseAuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuthManager.auth

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            auth.addAuthStateListener { firebaseAuth ->
                _authState.value = if (firebaseAuth.currentUser != null) {
                    AuthState.Authenticated(firebaseAuth.currentUser!!.uid)
                } else {
                    AuthState.Unauthenticated
                }
            }
        }
    }

    sealed class AuthState {
        data object Loading : AuthState()
        data class Authenticated(val userId: String) : AuthState()
        data object Unauthenticated : AuthState()
    }
}