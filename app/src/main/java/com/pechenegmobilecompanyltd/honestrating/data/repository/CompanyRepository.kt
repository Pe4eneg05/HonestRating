package com.pechenegmobilecompanyltd.honestrating.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.pechenegmobilecompanyltd.honestrating.data.model.Company
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class CompanyRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val companiesCollection = firestore.collection("companies")

    fun getAllCompanies(): Flow<List<Company>> = callbackFlow {
        val listenerRegistration = companiesCollection.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
                return@addSnapshotListener
            }
            val companies = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(Company::class.java)?.copy(inn = doc.id) // Убедимся, что inn берется из ID документа
            } ?: emptyList()
            trySend(companies).isSuccess
        }
        awaitClose { listenerRegistration.remove() }
    }

    suspend fun updateAverageRating(inn: String, averageRating: Float) {
        companiesCollection.document(inn).update("averageRating", averageRating).await()
    }
}