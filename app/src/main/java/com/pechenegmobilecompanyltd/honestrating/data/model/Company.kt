package com.pechenegmobilecompanyltd.honestrating.data.model

import com.google.firebase.firestore.PropertyName

data class Company(
    @PropertyName("inn") val inn: String = "",
    @PropertyName("name") val name: String = "",
    @PropertyName("address") val address: String = "",
    @PropertyName("industry") val industry: String = "",
    @PropertyName("description") val description: String = "",
    @PropertyName("averageRating") val averageRating: Float = 0.0f
)