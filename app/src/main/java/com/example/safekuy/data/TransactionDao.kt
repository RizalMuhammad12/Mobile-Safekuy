package com.example.safekuy.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY date DESC, id DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE date = :date ORDER BY id DESC")
    fun getTransactionsByDate(date: String): Flow<List<Transaction>>

    @Insert
    suspend fun insert(transaction: Transaction)

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)
    
    @Query("DELETE FROM transactions")
    suspend fun deleteAll()
    
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'pemasukan' AND date = :date")
    fun getTotalPemasukanByDate(date: String): Flow<Double?>
    
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'pengeluaran' AND date = :date")
    fun getTotalPengeluaranByDate(date: String): Flow<Double?>
}
