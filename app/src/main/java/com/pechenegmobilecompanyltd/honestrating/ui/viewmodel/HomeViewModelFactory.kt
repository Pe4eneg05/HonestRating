package com.pechenegmobilecompanyltd.honestrating.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pechenegmobilecompanyltd.honestrating.data.dao.CompanyDao
import com.pechenegmobilecompanyltd.honestrating.data.dao.ReviewDao

class HomeViewModelFactory(
    private val companyDao: CompanyDao,
    private val reviewDao: ReviewDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(companyDao, reviewDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}