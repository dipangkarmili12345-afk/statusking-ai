package com.statusking.ai.repository

import com.statusking.ai.data.local.SampleData
import com.statusking.ai.data.local.StatusDao
import com.statusking.ai.data.local.StatusEntity
import com.statusking.ai.model.StatusItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StatusRepository(private val statusDao: StatusDao) {

    val allFavorites: Flow<List<StatusItem>> = statusDao.getFavoriteStatuses().map { list ->
        list.map { it.toStatusItem() }
    }

    val allCreations: Flow<List<StatusItem>> = statusDao.getCreations().map { list ->
        list.map { it.toStatusItem() }
    }

    val allStatuses: Flow<List<StatusItem>> = statusDao.getAllStatuses().map { list ->
        list.map { it.toStatusItem() }
    }

    suspend fun preloadIfEmpty() {
        val count = statusDao.count()
        if (count == 0) {
            val entities = SampleData.STARTER_STATUSES.map { StatusEntity.fromStatusItem(it) }
            statusDao.insertAll(entities)
        }
    }

    suspend fun toggleFavorite(status: StatusItem) {
        val newFav = !status.isFavorite
        if (status.id > 0) {
            statusDao.updateFavorite(status.id, newFav)
        } else {
            val existing = statusDao.isFavoriteText(status.text)
            if (existing) {
                statusDao.updateFavoriteByText(status.text, false)
            } else {
                statusDao.insertStatus(
                    StatusEntity(
                        text = status.text,
                        category = status.category,
                        tone = status.tone,
                        language = status.language,
                        author = status.author,
                        isFavorite = true,
                        isCreation = status.isCreation,
                        creationImagePath = status.creationImagePath
                    )
                )
            }
        }
    }

    suspend fun saveCreation(
        text: String,
        category: String,
        tone: String,
        language: String,
        imagePath: String?
    ): Long {
        return statusDao.insertStatus(
            StatusEntity(
                text = text,
                category = category,
                tone = tone,
                language = language,
                author = "You (StatusKing)",
                isFavorite = false,
                isCreation = true,
                creationImagePath = imagePath,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun deleteById(id: Long) {
        statusDao.deleteById(id)
    }

    suspend fun clearFavorites() {
        statusDao.clearFavorites()
    }

    suspend fun clearCreations() {
        statusDao.clearCreations()
    }
}
