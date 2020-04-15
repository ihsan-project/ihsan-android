package com.khatm.client.models

import androidx.room.*
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi

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


// Converters

class SettingsConverters {
    val constantAdapter: JsonAdapter<Constants?> = Moshi.Builder().build().adapter<Constants?>(Constants::class.java)
    val featureAdapter: JsonAdapter<Features?> = Moshi.Builder().build().adapter<Features?>(Features::class.java)

    @TypeConverter
    fun fromStringToConstants(value: String?): Constants? { return constantAdapter.fromJson(value) }
    @TypeConverter
    fun fromConstantsToString(value: Constants?): String { return constantAdapter.toJson(value) }

    @TypeConverter
    fun fromStringToFeatures(value: String?): Features? { return featureAdapter.fromJson(value) }
    @TypeConverter
    fun fromFeaturesToString(value: Features?): String { return featureAdapter.toJson(value) }
}