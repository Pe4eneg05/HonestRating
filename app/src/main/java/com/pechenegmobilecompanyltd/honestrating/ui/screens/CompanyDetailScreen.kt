package com.pechenegmobilecompanyltd.honestrating.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pechenegmobilecompanyltd.honestrating.model.Company
import com.pechenegmobilecompanyltd.honestrating.model.mockCompanies

@Composable
fun CompanyDetailScreen(companyId: String?) {
    val company = remember(companyId) {
        mockCompanies.find { it.id == companyId }
    }

    if (company == null) {
        Text("Компания не найдена", Modifier.padding(24.dp))
        return
    }

    var tabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("Обзор", "Отзывы", "Зарплаты")

    // Collapsing header imitation (простая реализация)
    Column(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(210.dp)
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            // Лого (или инициал)
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .align(Alignment.BottomStart)
                    .padding(start = 32.dp, bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (company.logoRes != null) {
                    Image(
                        painterResource(company.logoRes),
                        null,
                        modifier = Modifier.size(60.dp)
                    )
                } else {
                    Text(
                        company.name.first().uppercase(),
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Black
                    )
                }
            }
            Text(
                text = company.name,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 150.dp, bottom = 32.dp)
            )
        }
        // TAB BAR (Material3)
        TabRow(selectedTabIndex = tabIndex) {
            tabTitles.forEachIndexed { i, title ->
                Tab(
                    selected = tabIndex == i,
                    onClick = { tabIndex = i },
                    text = { Text(title) }
                )
            }
        }

        // TAB CONTENT
        Crossfade(tabIndex) { i ->
            when (i) {
                0 -> CompanyOverviewTab(company)
                1 -> ReviewsStubTab() // Заглушка, реализуешь позже
                2 -> SalaryChartTab(company)
            }
        }
    }
}

@Composable
private fun CompanyOverviewTab(company: Company) {
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "О компании",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = company.about,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(24.dp))
        // Рейтинг компании
        Text(
            text = "Средний рейтинг",
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(Modifier.height(4.dp))
        AnimatedRatingBar(rating = company.rating)
        Spacer(Modifier.height(8.dp))
        Text(
            text = "${company.reviews} отзывов",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun ReviewsStubTab() {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(42.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "Раздел отзывов появится позже",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun SalaryChartTab(company: Company) {
    Column(Modifier.padding(24.dp)) {
        Text(
            "Статистика зарплат",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(18.dp))
        if (company.salaryStats.isEmpty()) {
            Text("Нет данных по зарплатам.")
        } else {
            LazyRow {
                items(company.salaryStats) { (title, value) ->
                    Card(
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .size(width = 120.dp, height = 100.dp),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(title, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "${value / 1000} тыс ₽",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}