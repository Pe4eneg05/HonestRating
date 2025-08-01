package com.pechenegmobilecompanyltd.honestrating.utils

import android.annotation.SuppressLint
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.pechenegmobilecompanyltd.honestrating.MyApplication

fun initFirebase() {
    FirebaseApp.initializeApp(MyApplication.context)
}

object FirebaseAuthManager {
    val auth: FirebaseAuth = Firebase.auth
    @SuppressLint("StaticFieldLeak")
    val firestore: FirebaseFirestore = Firebase.firestore
}