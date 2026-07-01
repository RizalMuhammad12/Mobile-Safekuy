package com.example.safekuy.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_split")
data class DailySplit(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Long,
    val income: Double,
    val note: String?
)
