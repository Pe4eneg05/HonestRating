package com.pechenegmobilecompanyltd.honestrating.data.repository

import com.pechenegmobilecompanyltd.honestrating.data.dao.CompanyDao
import com.pechenegmobilecompanyltd.honestrating.data.model.Company
import kotlinx.coroutines.flow.Flow

class CompanyRepository(private val companyDao: CompanyDao) {
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

    suspend fun updateAverageRating(companyId: Int, averageRating: Float) =
        companyDao.updateAverageRating(companyId, averageRating)

    suspend fun getCompanyById(id: Int): Company? = companyDao.getCompanyById(id)
}