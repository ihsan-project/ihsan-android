package com.khatm.client.models

import androidx.room.*

@Entity(tableName = "settings")
data class SettingsModel(
    @PrimaryKey val version: Int,
    @ColumnInfo val books: Map<String, Int>,
    @ColumnInfo val platforms: Map<String, Int>
)
