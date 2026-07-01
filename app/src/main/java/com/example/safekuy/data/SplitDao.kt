package com.example.safekuy.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface SplitDao {
    // --- DailySplit ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSplit(split: DailySplit): Long

    @Query("DELETE FROM daily_split WHERE id = :id")
    suspend fun deleteDailySplit(id: Long)

    @Query("SELECT * FROM daily_split ORDER BY date DESC")
    fun getAllSplits(): Flow<List<DailySplit>>
    
    @Query("SELECT SUM(income) FROM daily_split WHERE date >= :startTimestamp AND date <= :endTimestamp")
    fun getTotalIncome(startTimestamp: Long, endTimestamp: Long): Flow<Double?>
    
    // --- SplitCategoryItem ---
    @Insert
    suspend fun insertCategoryItems(items: List<SplitCategoryItem>)
    
    @Update
    suspend fun updateCategoryItem(item: SplitCategoryItem)

    @Query("SELECT * FROM split_category_item WHERE dailySplitId = :splitId")
    suspend fun getItemsForSplit(splitId: Long): List<SplitCategoryItem>

    @Query("SELECT * FROM split_category_item WHERE dailySplitId = :splitId")
    fun observeItemsForSplit(splitId: Long): Flow<List<SplitCategoryItem>>
    
    // To get stats per category dynamically
    @Query("""
        SELECT name, SUM(amount) as total 
        FROM split_category_item 
        INNER JOIN daily_split ON split_category_item.dailySplitId = daily_split.id
        WHERE date >= :startTimestamp AND date <= :endTimestamp
        GROUP BY name
    """)
    fun getCategoryStats(startTimestamp: Long, endTimestamp: Long): Flow<List<CategoryStat>>
    
    // --- SplitCategoryTemplate ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: SplitCategoryTemplate)
    
    @Update
    suspend fun updateTemplate(template: SplitCategoryTemplate)
    
    @Query("DELETE FROM split_category_template WHERE id = :id")
    suspend fun deleteTemplate(id: Long)
    
    @Query("SELECT * FROM split_category_template ORDER BY id ASC")
    fun getAllTemplates(): Flow<List<SplitCategoryTemplate>>

    @Query("SELECT COUNT(*) FROM split_category_template")
    suspend fun getTemplateCount(): Int
}

data class CategoryStat(
    val name: String,
    val total: Double
)
