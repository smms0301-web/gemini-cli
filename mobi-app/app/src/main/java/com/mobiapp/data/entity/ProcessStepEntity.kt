package com.mobiapp.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "process_steps",
    foreignKeys = [ForeignKey(
        entity = ProcessEntity::class,
        parentColumns = ["id"],
        childColumns = ["processId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class ProcessStepEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val processId: Long,
    val stepNumber: Int,
    val title: String,
    val description: String = "",
    val voiceNotePath: String? = null,
    val imagePaths: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
