package com.pechenegmobilecompanyltd.honestrating.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pechenegmobilecompanyltd.honestrating.data.converters.Converters
import com.pechenegmobilecompanyltd.honestrating.data.model.Company
import com.pechenegmobilecompanyltd.honestrating.data.model.Review
import com.pechenegmobilecompanyltd.honestrating.data.dao.CompanyDao
import com.pechenegmobilecompanyltd.honestrating.data.dao.ReviewDao

@Database(entities = [Company::class, Review::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class HonestRatingDatabase : RoomDatabase() {
    abstract fun companyDao(): CompanyDao
    abstract fun reviewDao(): ReviewDao

    companion object {
        @Volatile
        private var INSTANCE: HonestRatingDatabase? = null

        fun getDatabase(context: Context): HonestRatingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HonestRatingDatabase::class.java,
                    "honest_rating_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}