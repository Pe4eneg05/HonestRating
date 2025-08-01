package com.pechenegmobilecompanyltd.honestrating

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.pechenegmobilecompanyltd.honestrating.utils.initFirebase

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initFirebase()
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
            private set
    }
}