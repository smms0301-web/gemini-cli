package com.mobiapp.util

import android.content.Context
import android.net.Uri
import com.mobiapp.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object BackupManager {

    private val DATE_FMT = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())

    suspend fun exportBackup(context: Context): File? = withContext(Dispatchers.IO) {
        try {
            val dbPath = context.getDatabasePath(AppDatabase.DATABASE_NAME)
            if (!dbPath.exists()) return@withContext null

            val dir = File(context.getExternalFilesDir(null), "backups").apply { mkdirs() }
            val dest = File(dir, "mobiapp_backup_${DATE_FMT.format(Date())}.db")

            FileInputStream(dbPath).use { input ->
                FileOutputStream(dest).use { output -> input.copyTo(output) }
            }
            // Also copy WAL if exists
            File("${dbPath.absolutePath}-wal").takeIf { it.exists() }?.let {
                FileInputStream(it).use { i -> FileOutputStream(File("${dest.absolutePath}-wal")).use { o -> i.copyTo(o) } }
            }
            dest
        } catch (e: Exception) { null }
    }

    suspend fun importBackup(context: Context, uri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            val dbPath = context.getDatabasePath(AppDatabase.DATABASE_NAME)
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(dbPath).use { output -> input.copyTo(output) }
            } ?: return@withContext false
            true
        } catch (e: Exception) { false }
    }
}
