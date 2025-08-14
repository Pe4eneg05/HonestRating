package com.pechenegmobilecompanyltd.honestrating.ui.screens

import android.view.inputmethod.EditorInfo
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    // Состояние для UI
    var searchQuery by remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }
    var selectedCity by remember { mutableStateOf<String?>(null) }
    var selectedIndustry by remember { mutableStateOf<String?>(null) }
    // Тестовые данные для фильтров
    val cities = listOf("Москва", "Санкт-Петербург", "Новосибирск")
    val industries = listOf("IT", "Retail", "Производство")
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val testListCompanies = listOf<Company>(
        Company(
            name = "Тестовая компания Новосиб",
            inn = "1234567890",
            address = "Новосибирск",
            industry = "IT",
            description = "Описание"
        ),
        Company(
            name = "Тестовая компания Москва",
            inn = "1234567890",
            address = "Москва",
            industry = "Retail",
            description = "Описание"
        ),
        Company(
            name = "Тестовая компания Санкт-Петербург",
            inn = "1234567890",
            address = "Санкт-Петербург",
            industry = "Производство",
            description = "Описание"
        ),
    )

    LaunchedEffect(Unit) {
        // Инициализация (можно добавить тестовые данные позже)
        viewModel.addCompanies(testListCompanies)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Honest Rating") })
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // Поле поиска
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { query ->
                    searchQuery = query
                    viewModel.setSearchQuery(query)
                },
                label = { Text("Поиск по названию") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        viewModel.setSearchQuery(searchQuery)
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                )
            )
            // Кнопка для открытия фильтров
            Button(
                onClick = { showFilters = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Фильтры", fontSize = 16.sp)
            }
            // Список компаний
            Text(text = "Компании: ${companies.size}", modifier = Modifier.padding(16.dp))
            companies.forEach { company ->
                Text(text = company.name, modifier = Modifier.padding(8.dp))
            }
        }
    }

    // Модальное окно фильтров
    if (showFilters) {
        ModalBottomSheet(
            onDismissRequest = { showFilters = false },
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                var expandedCity by remember { mutableStateOf(false) }
                var expandedIndustry by remember { mutableStateOf(false) }
                // Фильтр по городу
                Row(
                    modifier = Modifier.padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically // Выравнивание по вертикали
                ) {
                    Text("Город: ", modifier = Modifier.padding(end = 8.dp))
                    Box {
                        Text(
                            text = selectedCity ?: "Все",
                            modifier = Modifier
                                .clickable { expandedCity = true }
                                .padding(8.dp)
                        )
                        DropdownMenu(
                            expanded = expandedCity,
                            onDismissRequest = { expandedCity = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Все") },
                                onClick = {
                                    selectedCity = null
                                    viewModel.setCityFilter(null)
                                    expandedCity = false
                                }
                            )
                            cities.forEach { city ->
                                DropdownMenuItem(
                                    text = { Text(city) },
                                    onClick = {
                                        selectedCity = city
                                        viewModel.setCityFilter(city)
                                        expandedCity = false
                                    }
                                )
                            }
                        }
                    }
                }
                // Фильтр по отрасли
                Row(
                    modifier = Modifier.padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically // Выравнивание по вертикали
                ) {
                    Text("Отрасль: ", modifier = Modifier.padding(end = 8.dp))
                    Box {
                        Text(
                            text = selectedIndustry ?: "Все",
                            modifier = Modifier
                                .clickable { expandedIndustry = true }
                                .padding(8.dp)
                        )
                        DropdownMenu(
                            expanded = expandedIndustry,
                            onDismissRequest = { expandedIndustry = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Все") },
                                onClick = {
                                    selectedIndustry = null
                                    viewModel.setIndustryFilter(null)
                                    expandedIndustry = false
                                }
                            )
                            industries.forEach { industry ->
                                DropdownMenuItem(
                                    text = { Text(industry) },
                                    onClick = {
                                        selectedIndustry = industry
                                        viewModel.setIndustryFilter(industry)
                                        expandedIndustry = false
                                    }
                                )
                            }
                        }
                    }
                }
                // Кнопка применения фильтров
                Button(
                    onClick = { showFilters = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Применить", fontSize = 16.sp)
                }
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