package com.khatm.client.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

data class Books(
    val results: List<BookModel>,
    val meta: PaginationMetaData
)

data class PaginationMetaData(
    val count: Int,
    val pageCount: Int,
    val totalCount: Int
)

@Entity(tableName = "books")
data class BookModel(
    @PrimaryKey val id: Int,
    @field:Json(name = "slug_id") val slug: String,
    val title: String,
    val type: Int
)