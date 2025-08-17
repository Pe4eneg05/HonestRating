package com.pechenegmobilecompanyltd.honestrating.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.pechenegmobilecompanyltd.honestrating.data.dao.CompanyDao
import com.pechenegmobilecompanyltd.honestrating.data.model.Company
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class CompanyRepository(private val companyDao: CompanyDao) {
    private val firestore = FirebaseFirestore.getInstance()
    private val companiesCollection = firestore.collection("companies")

    suspend fun insertCompany(company: Company) = companyDao.insertCompany(company)

    suspend fun updateCompanyByInn(
        inn: String,
        name: String,
        address: String,
        industry: String,
        description: String,
        averageRating: Float
    ) = companyDao.updateCompanyByInn(inn, name, address, industry, description, averageRating)

    suspend fun getCompanyByInn(inn: String): Company? = companyDao.getCompanyByInn(inn)

    fun getAllCompanies(): Flow<List<Company>> = companyDao.getAllCompanies()

    suspend fun updateAverageRating(companyId: Int, averageRating: Float) = companyDao.updateAverageRating(companyId, averageRating)

    suspend fun getCompanyById(id: Int): Company? = companyDao.getCompanyById(id)

    // Новые методы для работы с Firestore
    fun getAllCompaniesFromFirestore(): Flow<List<Company>> {
        return callbackFlow {
            val listenerRegistration = companiesCollection.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                val companies = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Company::class.java)?.copy(id = doc.id.toIntOrNull() ?: 0)
                } ?: emptyList()
                trySend(companies).isSuccess
            }
            awaitClose { listenerRegistration.remove() }
        }
    }

    suspend fun addOrUpdateCompanyInFirestore(company: Company) {
        companiesCollection.document(company.inn).set(company).await()
        // Синхронизация с локальной базой
        if (companyDao.getCompanyByInn(company.inn) == null) {
            companyDao.insertCompany(company)
        } else {
            companyDao.updateCompanyByInn(
                inn = company.inn,
                name = company.name,
                address = company.address,
                industry = company.industry,
                description = company.description,
                averageRating = company.averageRating
            )
        }
    }

    suspend fun getCompanyByIdFromFirestore(id: Int): Company? {
        val snapshot = companiesCollection.whereEqualTo("id", id).get().await()
        return snapshot.documents.firstOrNull()?.toObject(Company::class.java)?.copy(id = id)
    }

    suspend fun updateAverageRatingInFirestore(companyId: Int, averageRating: Float) {
        val company = getCompanyByIdFromFirestore(companyId)
        company?.let {
            companiesCollection.document(it.inn).update("averageRating", averageRating).await()
            // Синхронизация с локальной базой
            companyDao.updateAverageRating(companyId, averageRating)
        }
    }
}