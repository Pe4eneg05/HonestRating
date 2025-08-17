package com.pechenegmobilecompanyltd.honestrating.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.PropertyName

@Entity(tableName = "companies")
data class Company(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @PropertyName("inn") val inn: String,
    @PropertyName("name") val name: String,
    @PropertyName("address") val address: String,
    @PropertyName("industry") val industry: String,
    @PropertyName("description") val description: String,
    @PropertyName("averageRating") val averageRating: Float = 0.0f
)