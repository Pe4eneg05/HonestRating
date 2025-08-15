package com.pechenegmobilecompanyltd.honestrating.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.pechenegmobilecompanyltd.honestrating.data.model.Company
import kotlinx.coroutines.flow.Flow

@Dao
interface CompanyDao {
    @Insert
    suspend fun insertCompany(company: Company)

    @Update
    suspend fun updateCompany(company: Company)

    @Query("SELECT * FROM companies WHERE inn = :inn LIMIT 1")
    suspend fun getCompanyByInn(inn: String): Company?

    @Query("SELECT * FROM companies WHERE id = :id LIMIT 1")
    suspend fun getCompanyById(id: Int): Company?

    @Query("SELECT * FROM companies")
    fun getAllCompanies(): Flow<List<Company>>

    @Query("UPDATE companies SET name = :name, address = :address, industry = :industry, description = :description, averageRating = :averageRating WHERE inn = :inn")
    suspend fun updateCompanyByInn(
        inn: String,
        name: String,
        address: String,
        industry: String,
        description: String,
        averageRating: Float
    )

    @Query("UPDATE companies SET averageRating = :averageRating WHERE id = :companyId")
    suspend fun updateAverageRating(companyId: Int, averageRating: Float)
}