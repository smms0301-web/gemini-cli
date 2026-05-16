package com.mobiapp.util

import android.content.Context
import java.io.File

object BackupManager {

    fun export(context: Context): File? {
        val dbFile = context.getDatabasePath("mobi_app.db")
        if (!dbFile.exists()) return null
        val backupDir = File(context.getExternalFilesDir(null), "Backup").also { it.mkdirs() }
        val dest = File(backupDir, "mobi_app_backup_${System.currentTimeMillis()}.db")
        dbFile.copyTo(dest, overwrite = true)
        return dest
    }

    fun import(context: Context, sourceFile: File): Boolean {
        return try {
            val dbFile = context.getDatabasePath("mobi_app.db")
            sourceFile.copyTo(dbFile, overwrite = true)
            true
        } catch (e: Exception) {
            false
        }
    }
}
