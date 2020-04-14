package com.khatm.client.models

import androidx.room.*
import com.squareup.moshi.Json

@Entity(tableName = "settings")
data class SettingsModel(
    @PrimaryKey val version: Int,
    val constants: Constants,
    val features: Features
)

data class Constants(
    @field:Json(name = "book_types") val books: Map<String, Int>,
    val platforms: Map<String, Int>
)

class Features(
)