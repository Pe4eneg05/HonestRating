package com.pechenegmobilecompanyltd.honestrating

import android.app.Application
import com.pechenegmobilecompanyltd.honestrating.data.database.HonestRatingDatabase

class HonestRatingApplication : Application() {
    val database: HonestRatingDatabase by lazy {
        HonestRatingDatabase.getDatabase(this)
    }
}