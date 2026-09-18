package com.statusking.ai.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.statusking.ai.model.StatusItem

@Entity(tableName = "statuses")
data class StatusEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val category: String,
    val tone: String,
    val language: String,
    val author: String = "StatusKing AI",
    val isFavorite: Boolean = false,
    val isCreation: Boolean = false,
    val creationImagePath: String? = null,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toStatusItem(): StatusItem = StatusItem(
        id = id,
        text = text,
        category = category,
        tone = tone,
        language = language,
        author = author,
        isFavorite = isFavorite,
        isCreation = isCreation,
        creationImagePath = creationImagePath,
        timestamp = timestamp
    )

    companion object {
        fun fromStatusItem(item: StatusItem): StatusEntity = StatusEntity(
            id = item.id,
            text = item.text,
            category = item.category,
            tone = item.tone,
            language = item.language,
            author = item.author,
            isFavorite = item.isFavorite,
            isCreation = item.isCreation,
            creationImagePath = item.creationImagePath,
            timestamp = item.timestamp
        )
    }
}
