package com.pechenegmobilecompanyltd.honestrating.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pechenegmobilecompanyltd.honestrating.model.Company
import com.pechenegmobilecompanyltd.honestrating.model.mockCompanies

@Composable
fun CompanyListScreen(
    onCompanyClick: (Company) -> Unit
) {
    var search by remember { mutableStateOf("") }

    val filtered by remember(search) {
        derivedStateOf {
            if (search.isEmpty()) mockCompanies
            else {
                val query = search.lowercase()
                mockCompanies.filter {
                    it.name.lowercase().contains(query) ||
                            it.industry.lowercase().contains(query)
                }
            }
        }
    }

    val industries by remember(filtered) {
        derivedStateOf {
            filtered.groupByTo(LinkedHashMap()) { it.industry }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            SearchBar(
                query = search,
                onQueryChange = { search = it },
                modifier = Modifier.padding(12.dp)
            )

            OptimizedCompanyList(
                industries = industries,
                onCompanyClick = onCompanyClick
            )
        }
    }
}

@Composable
private fun OptimizedCompanyList(
    industries: Map<String, List<Company>>,
    onCompanyClick: (Company) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        industries.forEach { (industry, companies) ->
            stickyHeader(key = "header_$industry") {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = industry,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 16.dp),
                        maxLines = 1
                    )
                }
            }

            items(
                items = companies,
                key = { it.id },
                contentType = { "COMPANY_ITEM" }
            ) { company ->
                CompanyCard(
                    company = company,
                    onClick = { onCompanyClick(company) },
                    // Убрали animateItemPlacement, так как он требует Compose 1.3+ и дополнительной настройки
                    modifier = Modifier
                )
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        shape = CircleShape,
        leadingIcon = { Icon(Icons.Filled.Search, null) },
        placeholder = { Text("Поиск компании...") },
        singleLine = true,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}