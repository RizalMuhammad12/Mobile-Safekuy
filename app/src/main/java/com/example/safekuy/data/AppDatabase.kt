package com.example.safekuy.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.migration.Migration

@Database(
    entities = [
        Transaction::class, 
        DailySplit::class, 
        SplitCategoryItem::class, 
        SplitCategoryTemplate::class
    ], 
    version = 4, 
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun splitDao(): SplitDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Not needed here, just keeping signature if Room complains, but it's fine.
            }
        }
        
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE IF EXISTS `daily_split`")
                
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `daily_split` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`date` INTEGER NOT NULL, " +
                            "`income` REAL NOT NULL, " +
                            "`note` TEXT)"
                )
                
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `split_category_item` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`dailySplitId` INTEGER NOT NULL, " +
                            "`name` TEXT NOT NULL, " +
                            "`amount` REAL NOT NULL, " +
                            "`isSaved` INTEGER NOT NULL, " +
                            "`storageLocation` TEXT NOT NULL, " +
                            "FOREIGN KEY(`dailySplitId`) REFERENCES `daily_split`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )"
                )
                
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_split_category_item_dailySplitId` ON `split_category_item` (`dailySplitId`)")
                
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `split_category_template` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`name` TEXT NOT NULL, " +
                            "`percentage` INTEGER NOT NULL)"
                )
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "safekuy_database"
                )
                .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
