package com.mobiapp.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "process_steps",
    foreignKeys = [ForeignKey(
        entity = ProcessEntity::class,
        parentColumns = ["id"],
        childColumns = ["processId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("processId")]
)
data class ProcessStepEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val processId: Long,
    val stepNumber: Int,
    val title: String,
    val note: String = "",
    val voiceNotePath: String = "",
    val imagePaths: String = "", // JSON array of paths
    val isDone: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
