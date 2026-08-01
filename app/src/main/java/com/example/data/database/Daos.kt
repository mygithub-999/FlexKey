package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDictionaryDao {
    @Query("SELECT * FROM user_dictionary ORDER BY frequency DESC, word ASC")
    fun getAllWords(): Flow<List<UserDictionaryEntity>>

    @Query("SELECT * FROM user_dictionary WHERE word LIKE :query || '%' OR shortcut LIKE :query || '%' LIMIT 10")
    suspend fun searchWords(query: String): List<UserDictionaryEntity>

    @Query("SELECT * FROM user_dictionary WHERE shortcut = :shortcut LIMIT 1")
    suspend fun findByShortcut(shortcut: String): UserDictionaryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(entity: UserDictionaryEntity): Long

    @Query("DELETE FROM user_dictionary WHERE id = :id")
    suspend fun deleteWord(id: Long)
}

@Dao
interface ClipboardDao {
    @Query("SELECT * FROM clipboard_history ORDER BY isPinned DESC, timestamp DESC")
    fun getAllClips(): Flow<List<ClipboardEntity>>

    @Query("SELECT * FROM clipboard_history WHERE isPinned = 1 ORDER BY timestamp DESC")
    fun getPinnedClips(): Flow<List<ClipboardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClip(clip: ClipboardEntity): Long

    @Update
    suspend fun updateClip(clip: ClipboardEntity)

    @Query("DELETE FROM clipboard_history WHERE id = :id")
    suspend fun deleteClip(id: Long)

    @Query("DELETE FROM clipboard_history WHERE isPinned = 0")
    suspend fun clearUnpinnedClips()
}
