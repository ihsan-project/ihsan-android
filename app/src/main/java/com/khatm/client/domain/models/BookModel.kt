package com.khatm.client.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

data class Books(
    val results: List<BookModel>
)

@Entity(tableName = "books")
data class BookModel(
    @PrimaryKey val id: Int,
    @field:Json(name = "slug_id") val slug: String,
    val title: String,
    val type: Int
)