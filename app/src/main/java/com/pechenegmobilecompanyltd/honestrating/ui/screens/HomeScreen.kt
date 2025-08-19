package com.pechenegmobilecompanyltd.honestrating.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.pechenegmobilecompanyltd.honestrating.ui.viewmodel.HomeViewModel
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel) {
    val companies by viewModel.companies.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }
    var selectedCity by remember { mutableStateOf<String?>(null) }
    var selectedIndustry by remember { mutableStateOf<String?>(null) }
    val cities = listOf("Москва", "Санкт-Петербург", "Новосибирск")
    val industries = listOf("IT", "Retail", "Производство")

    // Тестовые данные для компаний (добавляются в Firestore)
//    val testCompanies = listOf(
//        mapOf(
//            "inn" to "1234567890",
//            "name" to "Тестовая компания Новосиб",
//            "address" to "Новосибирск",
//            "industry" to "IT",
//            "description" to "Описание"
//        ),
//        mapOf(
//            "inn" to "0987654321",
//            "name" to "Тестовая компания Москва",
//            "address" to "Москва",
//            "industry" to "Retail",
//            "description" to "Описание"
//        ),
//        mapOf(
//            "inn" to "1122334455",
//            "name" to "Тестовая компания Санкт-Петербург",
//            "address" to "Санкт-Петербург",
//            "industry" to "Производство",
//            "description" to "Описание"
//        )
//    )

    LaunchedEffect(Unit) {
        viewModel.loadCompanies()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Компании", color = Color.Black) }
            )
        }
    ) { paddingValues ->
        Crossfade(targetState = isLoading, label = "HomeCrossfade") { loaded ->
            if (!loaded) {
                HomeShimmerPlaceholder()
            } else if (error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Ошибка: $error", color = Color.Red)
                }
            } else {
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { query ->
                            searchQuery = query
                            viewModel.setSearchQuery(query)
                        },
                        label = { Text("Поиск по названию") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Search,
                                contentDescription = null,
                                tint = Color(0xFF2196F3)
                            )
                        },
                        singleLine = true
                    )
                    Button(
                        onClick = { showFilters = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF9800),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Фильтры", fontSize = 16.sp)
                    }
                    LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
                        items(companies) { company ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                                    .clickable { navController.navigate("company/${company.inn}") },
                                shape = RoundedCornerShape(8.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(company.name, fontSize = 18.sp, color = Color(0xFF2196F3))
                                    Text("ИНН: ${company.inn}", fontSize = 14.sp)
                                    Text("Адрес: ${company.address}", fontSize = 14.sp)
                                    Text("Отрасль: ${company.industry}", fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
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
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Город: ",
                        modifier = Modifier.padding(end = 8.dp),
                        color = Color(0xFF2196F3)
                    )
                    Box {
                        Text(
                            text = selectedCity ?: "Все",
                            modifier = Modifier
                                .clickable { expandedCity = true }
                                .padding(8.dp),
                            color = Color.Black
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
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Отрасль: ",
                        modifier = Modifier.padding(end = 8.dp),
                        color = Color(0xFF2196F3)
                    )
                    Box {
                        Text(
                            text = selectedIndustry ?: "Все",
                            modifier = Modifier
                                .clickable { expandedIndustry = true }
                                .padding(8.dp),
                            color = Color.Black
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
                Button(
                    onClick = { showFilters = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF9800),
                        contentColor = Color.White
                    )
                ) {
                    Text("Применить", fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun HomeShimmerPlaceholder() {
    val shimmerInstance = rememberShimmer(shimmerBounds = ShimmerBounds.View)
    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Поиск по названию") },
            modifier = Modifier
                .fillMaxWidth()
                .shimmer(shimmerInstance)
                .background(Color.LightGray.copy(alpha = 0.36f)),
            enabled = false,
            leadingIcon = {
                Icon(
                    Icons.Filled.Search,
                    contentDescription = null,
                    tint = Color(0xFF2196F3).copy(alpha = 0.36f)
                )
            },
            singleLine = true
        )
        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .shimmer(shimmerInstance)
                .background(Color.LightGray.copy(alpha = 0.36f)),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF9800).copy(alpha = 0.36f),
                contentColor = Color.White.copy(alpha = 0.36f)
            ),
            enabled = false
        ) {
            Text("Фильтры", fontSize = 16.sp, color = Color.White.copy(alpha = 0.36f))
        }
        LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
            items((1..3).toList()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .shimmer(shimmerInstance),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                                .background(Color.LightGray.copy(alpha = 0.36f))
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(14.dp)
                                .background(Color.LightGray.copy(alpha = 0.36f))
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(14.dp)
                                .background(Color.LightGray.copy(alpha = 0.36f))
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(14.dp)
                                .background(Color.LightGray.copy(alpha = 0.36f))
                        )
                    }
                }
            }
        }
    }
}