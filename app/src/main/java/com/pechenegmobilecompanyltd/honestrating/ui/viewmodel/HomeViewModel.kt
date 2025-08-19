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
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    private val _searchQuery = MutableStateFlow("")
    private val _cityFilter = MutableStateFlow<String?>(null)
    private val _industryFilter = MutableStateFlow<String?>(null)
    private val _selectedCompany = MutableStateFlow<Company?>(null)
    private val _reviews = MutableStateFlow<List<Review>>(emptyList())

    init {
        loadCompanies()
        observeFilters()
    }

    fun loadCompanies() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                companyRepository.getAllCompanies().collectLatest { companies ->
                    _companies.value = applyFilters(companies)
                }
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки данных: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
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

    fun selectCompany(company: Company) {
        _selectedCompany.value = company
        loadReviewsForCompany(company.inn)
    }

    private fun loadReviewsForCompany(companyInn: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                reviewRepository.getReviewsByCompanyInn(companyInn).collectLatest { reviews ->
                    _reviews.value = reviews
                    updateCompanyAverageRating(companyInn)
                }
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки отзывов: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addReview(review: Review) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                reviewRepository.addReview(review)
                loadReviewsForCompany(review.companyId.toString())
            } catch (e: Exception) {
                _error.value = "Ошибка добавления отзыва: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun updateCompanyAverageRating(companyInn: String) {
        val reviews = _reviews.value
        if (reviews.isNotEmpty()) {
            val averageRating = reviews.map { it.rating }.average().toFloat()
            companyRepository.updateAverageRating(companyInn, averageRating)
            loadCompanies() // Перезагрузка компаний для обновления рейтинга
        }
    }

    fun getSelectedCompany(): StateFlow<Company?> = _selectedCompany
    fun getReviews(): StateFlow<List<Review>> = _reviews
}