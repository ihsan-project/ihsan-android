package com.khatm.client.factories

import android.content.Context
import androidx.room.*
import com.khatm.client.domain.models.*
import com.khatm.client.domain.repositories.BooksDao
import com.khatm.client.domain.repositories.SettingsDao
import com.khatm.client.domain.repositories.UserDao
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi


@Database(
    entities = [
        UserModel::class,
        SettingsModel::class,
        BookModel::class
    ],
    version = 1,
    exportSchema = false)
@TypeConverters(
    value = [
        BasicConverters::class,
        SettingsConverters::class
    ]
)
abstract class DatabaseFactory : RoomDatabase() {

    /* Configuration for DAOs */
    abstract fun userDao(): UserDao
    abstract fun settingsDao(): SettingsDao
    abstract fun booksDao(): BooksDao

    /* Internal Workings */
    companion object {

        @Volatile
        private var INSTANCE: DatabaseFactory? = null

        internal fun getDatabase(context: Context): DatabaseFactory? {
            if (INSTANCE == null) {
                synchronized(DatabaseFactory::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            DatabaseFactory::class.java, "khatm.db"
                        )
                            .build()
                    }
                }
            }
            return INSTANCE
        }
    }
}


class BasicConverters {
    // When adding to this, don't worry so much about the method naming,
    // It's the method parameter signature that matters.

    val mapStringIntAdapter: JsonAdapter<Map<String, Int>?> = Moshi.Builder().build().adapter<Map<String, Int>?>(Map::class.java)
    @TypeConverter
    fun stringToMapStringInt(value: String): Map<String, Int>? { return mapStringIntAdapter.fromJson(value) }
    @TypeConverter
    fun mapStringIntToString(value: Map<String, Int>?): String { return mapStringIntAdapter.toJson(value) }

    val mapStringStringAdapter: JsonAdapter<Map<String, String>?> = Moshi.Builder().build().adapter<Map<String, String>?>(Map::class.java)
    @TypeConverter
    fun stringToMapStringString(value: String): Map<String, String>? { return mapStringStringAdapter.fromJson(value) }
    @TypeConverter
    fun mapStringStringToString(value: Map<String, String>?): String { return mapStringStringAdapter.toJson(value) }

    val listStringAdapter: JsonAdapter<List<String>?> = Moshi.Builder().build().adapter<List<String>?>(Map::class.java)
    @TypeConverter
    fun stringToListString(value: String): List<String>? { return listStringAdapter.fromJson(value) }
    @TypeConverter
    fun listStringToString(value: List<String>?): String { return listStringAdapter.toJson(value) }

    val listIntAdapter: JsonAdapter<List<Int>?> = Moshi.Builder().build().adapter<List<Int>?>(Map::class.java)
    @TypeConverter
    fun stringToListInt(value: String): List<Int>? { return listIntAdapter.fromJson(value) }
    @TypeConverter
    fun listIntToString(value: List<Int>?): String { return listIntAdapter.toJson(value) }
}