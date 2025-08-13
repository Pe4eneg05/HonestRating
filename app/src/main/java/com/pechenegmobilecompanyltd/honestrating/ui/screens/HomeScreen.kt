package com.pechenegmobilecompanyltd.honestrating.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pechenegmobilecompanyltd.honestrating.data.dao.CompanyDao
import com.pechenegmobilecompanyltd.honestrating.data.database.HonestRatingDatabase
import com.pechenegmobilecompanyltd.honestrating.data.model.Company
import com.pechenegmobilecompanyltd.honestrating.data.repository.CompanyRepository
import com.pechenegmobilecompanyltd.honestrating.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, database: HonestRatingDatabase) {
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(database.companyDao())
    )

    val companies by viewModel.companies.collectAsState()

    LaunchedEffect(Unit) {
        // Инициализация (можно добавить тестовые данные позже)
        viewModel.addCompany(
            Company(
                name = "Тестовая компания",
                inn = "1234567890",
                address = "Москва",
                description = "Описание"
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Honest Rating") })
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Text(text = "Компании: ${companies.size}", modifier = Modifier.padding(16.dp))
            companies.forEach { company ->
                Text(text = company.name, modifier = Modifier.padding(8.dp))
            }
        }
    }
}

// Фабрика для ViewModel (ручное создание)
class HomeViewModelFactory(private val companyDao: CompanyDao) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(CompanyRepository(companyDao)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}