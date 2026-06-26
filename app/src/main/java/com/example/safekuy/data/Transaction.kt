package com.example.safekuy.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "pemasukan" or "pengeluaran"
    val category: String, // Kategori transaksi
    val amount: Double,
    val note: String,
    val date: String, // Format YYYY-MM-DD
    val emoji: String = "💰"
)
