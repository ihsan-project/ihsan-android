package com.khatm.client.models

import androidx.room.*
import com.squareup.moshi.Json

@Entity(tableName = "settings")
data class SettingsModel(
    @PrimaryKey val version: Int,
    @field:Json(name = "bookTypes") val books: Map<String, Int>,
    val platforms: Map<String, Int>
)