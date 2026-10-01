package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarked_schemes")
data class SchemeBookmarkEntity(
    @PrimaryKey
    val schemeId: String,
    val schemeTitle: String,
    val benefitHighlight: String,
    val category: String,
    val savedTimestamp: Long = System.currentTimeMillis(),
    val checkedDocumentsJson: String = "", // Comma-delimited list of checked docs
    val isApplied: Boolean = false,
    val userNote: String = ""
)
