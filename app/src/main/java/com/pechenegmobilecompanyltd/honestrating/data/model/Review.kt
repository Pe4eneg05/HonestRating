package com.pechenegmobilecompanyltd.honestrating.data.model

import com.google.firebase.firestore.PropertyName
import java.util.Date

data class Review(
    @PropertyName("companyId") val companyId: Int,
    @PropertyName("userId") val userId: String,
    @PropertyName("text") val text: String,
    @PropertyName("rating") val rating: Float,
    @PropertyName("date") val date: Date,
    @PropertyName("categories") val categories: Map<String, Int>,
    @PropertyName("isAnonymous") val isAnonymous: Boolean = false
)