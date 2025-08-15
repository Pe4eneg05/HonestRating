package com.pechenegmobilecompanyltd.honestrating.data.repository

import com.pechenegmobilecompanyltd.honestrating.data.dao.ReviewDao
import com.pechenegmobilecompanyltd.honestrating.data.model.Review
import kotlinx.coroutines.flow.Flow

class ReviewRepository(private val reviewDao: ReviewDao) {
    suspend fun insert(review: Review) = reviewDao.insert(review)

    fun getReviewsByCompanyId(companyId: Int): Flow<List<Review>> = reviewDao.getReviewsByCompanyId(companyId)

    fun getReviewById(id: Int): Flow<Review?> = reviewDao.getReviewById(id)
}