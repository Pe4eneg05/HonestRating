package com.pechenegmobilecompanyltd.honestrating.data.repository

import com.pechenegmobilecompanyltd.honestrating.data.dao.CompanyDao
import com.pechenegmobilecompanyltd.honestrating.data.model.Company
import kotlinx.coroutines.flow.Flow

class CompanyRepository(private val companyDao: CompanyDao) {
    suspend fun insertCompany(company: Company) {
        companyDao.insert(company)
    }

    fun getAllCompanies(): Flow<List<Company>> {
        return companyDao.getAllCompanies()
    }

    fun getCompanyById(id: Int): Flow<Company?> {
        return companyDao.getCompanyById(id)
    }
}