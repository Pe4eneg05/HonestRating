package com.pechenegmobilecompanyltd.honestrating.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pechenegmobilecompanyltd.honestrating.data.model.Company
import com.pechenegmobilecompanyltd.honestrating.data.repository.CompanyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: CompanyRepository) : ViewModel() {
    private val _companies = MutableStateFlow<List<Company>>(emptyList())
    val companies: StateFlow<List<Company>> get() = _companies

    init {
        loadCompanies()
    }

    private fun loadCompanies() {
        viewModelScope.launch {
            repository.getAllCompanies().collect { companies ->
                _companies.value = companies
            }
        }
    }

    fun addCompany(company: Company) {
        viewModelScope.launch {
            repository.insertCompany(company)
        }
    }
}