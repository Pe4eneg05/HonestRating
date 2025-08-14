package com.pechenegmobilecompanyltd.honestrating.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pechenegmobilecompanyltd.honestrating.data.model.Company
import com.pechenegmobilecompanyltd.honestrating.data.repository.CompanyRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: CompanyRepository) : ViewModel() {
    private val _companies = MutableStateFlow<List<Company>>(emptyList())
    val companies: StateFlow<List<Company>> get() = _companies

    private val _searchQuery = MutableStateFlow("")
    private val _cityFilter = MutableStateFlow<String?>(null)
    private val _industryFilter = MutableStateFlow<String?>(null)

    init {
        loadCompanies()
        observeFilters()
    }

    private fun loadCompanies() {
        viewModelScope.launch {
            repository.getAllCompanies().collectLatest { companies ->
                _companies.value = applyFilters(companies)
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeFilters() {
        viewModelScope.launch {
            combine(
                repository.getAllCompanies().debounce(300),
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
                    (industry == null || company.description.contains(industry, ignoreCase = true))
        }
    }

    fun addCompanies(companies: List<Company>) {
        viewModelScope.launch {
            companies.forEach {
                repository.insertCompany(it)
            }
        }
    }
}