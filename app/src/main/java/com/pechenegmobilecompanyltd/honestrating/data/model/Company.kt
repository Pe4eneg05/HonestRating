package com.pechenegmobilecompanyltd.honestrating.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "companies")
data class Company(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val inn: String,
    val name: String,
    val address: String,
    val industry: String,
    val description: String,
    val averageRating: Float = 0.0f
)