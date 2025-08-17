package com.pechenegmobilecompanyltd.honestrating.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pechenegmobilecompanyltd.honestrating.data.dao.CompanyDao
import com.pechenegmobilecompanyltd.honestrating.data.dao.ReviewDao
import com.pechenegmobilecompanyltd.honestrating.data.model.Company
import com.pechenegmobilecompanyltd.honestrating.data.model.Review
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

class HomeViewModel(
    private val companyRepository: CompanyDao,
    private val reviewRepository: ReviewDao
) : ViewModel() {
    private val _companies = MutableStateFlow<List<Company>>(emptyList())
    val companies: StateFlow<List<Company>> get() = _companies

    private val _searchQuery = MutableStateFlow("")
    private val _cityFilter = MutableStateFlow<String?>(null)
    private val _industryFilter = MutableStateFlow<String?>(null)
    private val _selectedCompany = MutableStateFlow<Company?>(null)
    private val _reviews = MutableStateFlow<List<Review>>(emptyList())

    init {
        loadCompaniesFromFirestore()
        observeFilters()
    }

    private fun loadCompaniesFromFirestore() {
        viewModelScope.launch {
            companyRepository.getAllCompaniesFromFirestore().collectLatest { companies ->
                _companies.value = applyFilters(companies)
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeFilters() {
        viewModelScope.launch {
            combine(
                companyRepository.getAllCompaniesFromFirestore().debounce(300),
                _searchQuery.debounce(300),
                _cityFilter.debounce(300),
                _industryFilter.debounce(300)
            ) { companies, query, city, industry ->
                applyFilters(companies, query, city, industry)
            }.collectLatest { filteredCompanies ->
                _companies.value = filteredCompanies
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCityFilter(city: String?) {
        _cityFilter.value = city
    }

    fun setIndustryFilter(industry: String?) {
        _industryFilter.value = industry
    }

    private fun applyFilters(
        companies: List<Company>,
        query: String = _searchQuery.value,
        city: String? = _cityFilter.value,
        industry: String? = _industryFilter.value
    ): List<Company> {
        return companies.filter { company ->
            (query.isEmpty() || company.name.contains(query, ignoreCase = true)) &&
                    (city == null || company.address.contains(city, ignoreCase = true)) &&
                    (industry == null || company.industry.contains(industry, ignoreCase = true))
        }
    }

    fun addOrUpdateCompanies(companies: List<Company>) {
        viewModelScope.launch {
            companies.forEach { company ->
                companyRepository.addOrUpdateCompanyInFirestore(company)
            }
        }
    }

    fun selectCompany(company: Company) {
        _selectedCompany.value = company
        loadReviewsForCompany(company.id ?: 0)
    }

    private fun loadReviewsForCompany(companyId: Int) {
        viewModelScope.launch {
            reviewRepository.getReviewsByCompanyIdFromFirestore(companyId).collectLatest { reviews ->
                _reviews.value = reviews
                updateCompanyAverageRating(companyId)
            }
        }
    }

    fun addReview(review: Review) {
        viewModelScope.launch {
            reviewRepository.addReviewToFirestore(review)
            loadReviewsForCompany(review.companyId)
        }
    }

    private suspend fun updateCompanyAverageRating(companyId: Int) {
        val reviews = _reviews.value
        if (reviews.isNotEmpty()) {
            val averageRating = reviews.map { it.rating }.average().toFloat()
            companyRepository.updateAverageRatingInFirestore(companyId, averageRating)
            val updatedCompany = companyRepository.getCompanyByIdFromFirestore(companyId)
            updatedCompany?.let { company ->
                _companies.value = _companies.value.map { if (it.id == company.id) company else it }
            }
        }
    }

    fun getSelectedCompany(): StateFlow<Company?> = _selectedCompany
    fun getReviews(): StateFlow<List<Review>> = _reviews
}