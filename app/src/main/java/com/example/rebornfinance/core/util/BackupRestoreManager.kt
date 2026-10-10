package com.example.rebornfinance.core.util

import android.content.Context
import android.os.Environment
import com.example.rebornfinance.data.local.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object BackupRestoreManager {

    suspend fun exportBackupJson(context: Context, database: AppDatabase): File? = withContext(Dispatchers.IO) {
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "RebornFinance_Backup.json")
        try {
            // Simple backup summary format
            val json = "{ \"version\": 5, \"appName\": \"RebornFinance\", \"exportDate\": ${System.currentTimeMillis()} }"
            FileOutputStream(file).use { it.write(json.toByteArray(Charsets.UTF_8)) }
            file
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}
