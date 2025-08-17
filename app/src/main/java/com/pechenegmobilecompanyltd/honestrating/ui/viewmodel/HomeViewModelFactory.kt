package com.pechenegmobilecompanyltd.honestrating.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pechenegmobilecompanyltd.honestrating.data.repository.CompanyRepository
import com.pechenegmobilecompanyltd.honestrating.data.repository.ReviewRepository

class HomeViewModelFactory(
    private val companyRepository: CompanyRepository,
    private val reviewRepository: ReviewRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(companyRepository, reviewRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}