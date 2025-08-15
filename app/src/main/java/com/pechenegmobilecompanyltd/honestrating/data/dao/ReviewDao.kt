package com.pechenegmobilecompanyltd.honestrating.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pechenegmobilecompanyltd.honestrating.data.model.Review
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(review: Review)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reviews: List<Review>)

    @Query("SELECT * FROM reviews WHERE companyId = :companyId")
    fun getReviewsByCompanyId(companyId: Int): Flow<List<Review>>

    @Query("SELECT * FROM reviews WHERE id = :id")
    fun getReviewById(id: Int): Flow<Review?>
}