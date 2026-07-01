package com.example.safekuy.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "split_category_item",
    foreignKeys = [
        ForeignKey(
            entity = DailySplit::class,
            parentColumns = ["id"],
            childColumns = ["dailySplitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("dailySplitId")]
)
data class SplitCategoryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dailySplitId: Long,
    val name: String,
    val amount: Double,
    val isSaved: Boolean = false,
    val storageLocation: String = "Tunai"
)
