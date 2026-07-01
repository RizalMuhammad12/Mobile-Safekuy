package com.example.safekuy.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "split_category_template")
data class SplitCategoryTemplate(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val percentage: Int
)
