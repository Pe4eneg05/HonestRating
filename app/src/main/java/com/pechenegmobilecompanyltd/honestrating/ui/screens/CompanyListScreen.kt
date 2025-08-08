package com.pechenegmobilecompanyltd.honestrating.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.pechenegmobilecompanyltd.honestrating.model.Company
import com.pechenegmobilecompanyltd.honestrating.model.mockCompanies
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val SEARCH_DELAY = 300L

@Composable
fun CompanyListScreen(
    onCompanyClick: (Company) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()

    // Оптимизация: отложенная обработка ввода для избежания лишних вычислений
    val debouncedSearchQuery by remember(searchQuery) {
        derivedStateOf {
            debounce(searchQuery, SEARCH_DELAY)
        }
    }

    // Оптимизированная фильтрация с группировкой
    val industries by remember(debouncedSearchQuery) {
        derivedStateOf {
            val query = debouncedSearchQuery.lowercase()
            val filteredList = if (query.isEmpty()) {
                mockCompanies
            } else {
                mockCompanies.filter {
                    it.name.lowercase().contains(query) ||
                            it.industry.lowercase().contains(query)
                }
            }
            filteredList.groupByTo(LinkedHashMap()) { it.industry }
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column {
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onClear = {
                    searchQuery = ""
                    focusManager.clearFocus()
                },
                focusRequester = focusRequester,
                modifier = Modifier.padding(12.dp)

                        OptimizedCompanyList (
                        industries = industries,
                onCompanyClick = onCompanyClick
            )
        }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}

@Composable
private fun OptimizedCompanyList(
    industries: Map<String, List<Company>>,
    onCompanyClick: (Company) -> Unit
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
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
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .weight(1f)
                .clip(CircleShape)
                .focusRequester(focusRequester),
            shape = CircleShape,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = onClear) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Очистить"
                        )
                    }
                }
            },
            placeholder = { Text("Поиск компании...") },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
            )
        )

        IconButton(
            onClick = { /* Фильтры */ },
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = "Фильтры"
            )
        }
    }
}

// Оптимизация: отложенный ввод для уменьшения вычислений
@Composable
fun debounce(
    value: String,
    delayMillis: Long
): String {
    var debouncedValue by remember { mutableStateOf(value) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(value) {
        scope.launch {
            delay(delayMillis)
            debouncedValue = value
        }
    }

    return debouncedValue
}