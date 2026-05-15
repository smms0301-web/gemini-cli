package com.mobiapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "processes")
data class ProcessEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String,
    val siteTag: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
