package com.pechenegmobilecompanyltd.honestrating.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pechenegmobilecompanyltd.honestrating.data.model.Company
import com.pechenegmobilecompanyltd.honestrating.data.model.Review
import com.pechenegmobilecompanyltd.honestrating.data.repository.CompanyRepository
import com.pechenegmobilecompanyltd.honestrating.data.repository.ReviewRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import java.util.Date

class HomeViewModel(
    private val companyRepository: CompanyRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {
    private val _companies = MutableStateFlow<List<Company>>(emptyList())
    val companies: StateFlow<List<Company>> get() = _companies

    private val _searchQuery = MutableStateFlow("")
    private val _cityFilter = MutableStateFlow<String?>(null)
    private val _industryFilter = MutableStateFlow<String?>(null)
    private val _selectedCompany = MutableStateFlow<Company?>(null)
    private val _reviews = MutableStateFlow<List<Review>>(emptyList())

    init {
        loadCompanies()
        observeFilters()
    }

    private fun loadCompanies() {
        viewModelScope.launch {
            companyRepository.getAllCompanies().collectLatest { companies ->
                _companies.value = applyFilters(companies)
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeFilters() {
        viewModelScope.launch {
            combine(
                companyRepository.getAllCompanies().debounce(300),
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
                val existingCompany = companyRepository.getCompanyByInn(company.inn)
                if (existingCompany != null) {
                    companyRepository.updateCompanyByInn(
                        inn = company.inn,
                        name = company.name,
                        address = company.address,
                        industry = company.industry,
                        description = company.description,
                        averageRating = company.averageRating
                    )
                } else {
                    companyRepository.insertCompany(company)
                }
            }
        }
    }

    fun selectCompany(company: Company) {
        _selectedCompany.value = company
        loadReviewsForCompany(company.id)
    }

    private fun loadReviewsForCompany(companyId: Int) {
        viewModelScope.launch {
            reviewRepository.getReviewsByCompanyId(companyId).collectLatest { reviews ->
                _reviews.value = reviews
                updateCompanyAverageRating(companyId)
            }
        }
    }

    fun addReview(review: Review) {
        viewModelScope.launch {
            reviewRepository.insert(review)
            loadReviewsForCompany(review.companyId) // Обновляем отзывы и рейтинг
        }
    }

    private suspend fun updateCompanyAverageRating(companyId: Int) {
        val reviews = _reviews.value
        if (reviews.isNotEmpty()) {
            val averageRating = reviews.map { it.rating }.average().toFloat()
            companyRepository.updateAverageRating(companyId, averageRating)
            // Обновляем список компаний с новым рейтингом
            val updatedCompany = companyRepository.getCompanyById(companyId)
            updatedCompany?.let { company ->
                _companies.value = _companies.value.map {
                    if (it.id == company.id) company else it
                }
            }
        }
    }

    fun getSelectedCompany(): StateFlow<Company?> = _selectedCompany
    fun getReviews(): StateFlow<List<Review>> = _reviews
}