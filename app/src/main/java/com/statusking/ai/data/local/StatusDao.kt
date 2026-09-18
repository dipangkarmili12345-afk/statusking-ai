package com.statusking.ai.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StatusDao {

    @Query("SELECT * FROM statuses ORDER BY timestamp DESC")
    fun getAllStatuses(): Flow<List<StatusEntity>>

    @Query("SELECT * FROM statuses WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavoriteStatuses(): Flow<List<StatusEntity>>

    @Query("SELECT * FROM statuses WHERE isCreation = 1 ORDER BY timestamp DESC")
    fun getCreations(): Flow<List<StatusEntity>>

    @Query("SELECT * FROM statuses WHERE category = :category ORDER BY timestamp DESC")
    fun getStatusesByCategory(category: String): Flow<List<StatusEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatus(status: StatusEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(statuses: List<StatusEntity>)

    @Query("UPDATE statuses SET isFavorite = :isFav WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFav: Boolean)

    @Query("UPDATE statuses SET isFavorite = :isFav WHERE text = :text")
    suspend fun updateFavoriteByText(text: String, isFav: Boolean)

    @Query("SELECT EXISTS(SELECT 1 FROM statuses WHERE isFavorite = 1 AND text = :text)")
    suspend fun isFavoriteText(text: String): Boolean

    @Query("DELETE FROM statuses WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM statuses WHERE isFavorite = 1 AND isCreation = 0")
    suspend fun clearFavorites()

    @Query("DELETE FROM statuses WHERE isCreation = 1")
    suspend fun clearCreations()

    @Query("SELECT COUNT(*) FROM statuses")
    suspend fun count(): Int
}
