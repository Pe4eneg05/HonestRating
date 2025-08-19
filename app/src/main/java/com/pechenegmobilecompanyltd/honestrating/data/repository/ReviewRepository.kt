package com.pechenegmobilecompanyltd.honestrating.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.pechenegmobilecompanyltd.honestrating.data.model.Review
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ReviewRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val reviewsCollection = firestore.collection("reviews")

    fun getReviewsByCompanyInn(companyInn: String): Flow<List<Review>> = callbackFlow {
        val listenerRegistration = reviewsCollection.whereEqualTo("companyId", companyInn.toIntOrNull() ?: 0)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                val reviews = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Review::class.java)
                } ?: emptyList()
                trySend(reviews).isSuccess
            }
        awaitClose { listenerRegistration.remove() }
    }

    suspend fun addReview(review: Review) {
        reviewsCollection.add(review).await()
    }
}