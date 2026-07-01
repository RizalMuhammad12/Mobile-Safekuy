package com.example.safekuy.viewmodel

import com.example.safekuy.BuildConfig
import com.example.safekuy.data.Transaction
import com.example.safekuy.data.TransactionDao
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject

class TransactionRepository(private val transactionDao: TransactionDao) {

    fun getTransactionsByDate(date: String): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByDate(date)
    }

    fun getTotalPemasukan(date: String): Flow<Double?> {
        return transactionDao.getTotalPemasukanByDate(date)
    }

    fun getTotalPengeluaran(date: String): Flow<Double?> {
        return transactionDao.getTotalPengeluaranByDate(date)
    }

    fun getTransactionsByMonth(monthPrefix: String): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByMonth("$monthPrefix%")
    }

    fun getTotalPemasukanByMonth(monthPrefix: String): Flow<Double?> {
        return transactionDao.getTotalPemasukanByMonth("$monthPrefix%")
    }

    fun getTotalPengeluaranByMonth(monthPrefix: String): Flow<Double?> {
        return transactionDao.getTotalPengeluaranByMonth("$monthPrefix%")
    }

    suspend fun insertManual(transaction: Transaction) {
        transactionDao.insert(transaction)
    }
    
    suspend fun delete(transaction: Transaction) {
        transactionDao.delete(transaction)
    }
    
    suspend fun deleteAll() {
        transactionDao.deleteAll()
    }

    suspend fun insertViaAi(text: String, date: String) {
        try {
            val generativeModel = GenerativeModel(
                modelName = "gemini-2.5-flash",
                apiKey = BuildConfig.API_KEY,
                generationConfig = com.google.ai.client.generativeai.type.generationConfig {
                    responseMimeType = "application/json"
                }
            )
            
            val prompt = """
                Ekstrak data transaksi keuangan dari kalimat berikut ke dalam format JSON.
                Tentukan apakah itu "pengeluaran" (misal: beli, bayar, tagihan) atau "pemasukan" (misal: gaji, dapat uang, jual).
                Berikan juga "category" singkat (misal: Makanan, Transportasi, Hiburan, Tagihan, Belanja, Gaji, dll) dan satu karakter "emoji" yang merepresentasikan transaksi tersebut.
                Format JSON:
                {
                    "type": "pemasukan" atau "pengeluaran",
                    "category": "<kategori>",
                    "amount": <angka>,
                    "note": "<deskripsi singkat>",
                    "emoji": "<satu karakter emoji>"
                }
                
                Kalimat: "$text"
            """.trimIndent()
            
            val response = generativeModel.generateContent(prompt)
            val jsonString = response.text?.trim() ?: throw Exception("Respons AI kosong")
            
            // Log response aslinya untuk debugging
            println("AI Response: $jsonString")
            
            val jsonObject = org.json.JSONObject(jsonString)
            val type = jsonObject.optString("type", "pengeluaran").lowercase()
            val category = jsonObject.optString("category", "Lainnya")
            val amount = jsonObject.optDouble("amount", 0.0)
            val note = jsonObject.optString("note", text)
            val parsedEmoji = jsonObject.optString("emoji", if (type == "pemasukan") "📈" else "💸")
            
            val transaction = Transaction(
                type = type,
                category = category,
                amount = amount,
                note = note,
                date = date,
                emoji = parsedEmoji
            )
            
            transactionDao.insert(transaction)
        } catch (e: org.json.JSONException) {
            throw Exception("Format respons AI tidak valid. Coba ulangi dengan kalimat lebih spesifik. Error: ${e.message}")
        } catch (e: Exception) {
            throw Exception("Detail Error: ${e.localizedMessage ?: e.message}")
        }
    }
}
