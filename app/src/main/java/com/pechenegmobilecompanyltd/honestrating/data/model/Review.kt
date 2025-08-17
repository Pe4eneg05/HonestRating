package com.pechenegmobilecompanyltd.honestrating.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "reviews",
    foreignKeys = [
        ForeignKey(
            entity = Company::class,
            parentColumns = ["id"],
            childColumns = ["companyId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["companyId"])]
)
data class Review(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ForeignKey(entity = Company::class, parentColumns = ["id"], childColumns = ["companyId"]) val companyId: Int,
    val userId: String,
    val text: String,
    val rating: Float,
    val date: Date,
    val categories: Map<String, Int>, // Сериализуется в JSON через TypeConverter
    val isAnonymous: Boolean = false
)