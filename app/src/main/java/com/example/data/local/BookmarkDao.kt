package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarked_schemes ORDER BY savedTimestamp DESC")
    fun getAllBookmarks(): Flow<List<SchemeBookmarkEntity>>

    @Query("SELECT * FROM bookmarked_schemes WHERE schemeId = :schemeId LIMIT 1")
    fun getBookmarkFlow(schemeId: String): Flow<SchemeBookmarkEntity?>

    @Query("SELECT * FROM bookmarked_schemes WHERE schemeId = :schemeId LIMIT 1")
    suspend fun getBookmark(schemeId: String): SchemeBookmarkEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: SchemeBookmarkEntity)

    @Query("DELETE FROM bookmarked_schemes WHERE schemeId = :schemeId")
    suspend fun deleteBookmark(schemeId: String)

    @Query("UPDATE bookmarked_schemes SET checkedDocumentsJson = :docsJson WHERE schemeId = :schemeId")
    suspend fun updateCheckedDocuments(schemeId: String, docsJson: String)

    @Query("UPDATE bookmarked_schemes SET isApplied = :applied WHERE schemeId = :schemeId")
    suspend fun updateAppliedStatus(schemeId: String, applied: Boolean)
}
