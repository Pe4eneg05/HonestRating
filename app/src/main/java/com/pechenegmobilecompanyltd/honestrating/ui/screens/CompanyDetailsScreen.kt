package com.pechenegmobilecompanyltd.honestrating.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.pechenegmobilecompanyltd.honestrating.data.model.Company
import kotlinx.coroutines.tasks.await

@Composable
fun CompanyDetailsScreen(navController: NavController, companyId: String) {
    var company by remember { mutableStateOf<Company?>(null) }
    val firestore = FirebaseFirestore.getInstance()

    LaunchedEffect(companyId) {
        try {
            val snapshot = firestore.collection("companies").whereEqualTo("id", companyId).get().await()
            company = snapshot.documents.firstOrNull()?.toObject(Company::class.java)
        } catch (e: Exception) {
            // Обработка ошибки
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Company Details Screen")
        company?.let {
            Text("Name: ${it.name}")
            Text("Address: ${it.address}")
            Text("Industry: ${it.industry}")
            Text("Rating: ${it.averageRating}")
        } ?: Text("Loading or not found...")
    }
}