package com.khatm.client.factories

import android.content.Context
import androidx.room.*
import com.khatm.client.models.*
import com.khatm.client.repositories.SettingsDao
import com.khatm.client.repositories.UserDao
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi


@Database(entities = [UserModel::class, SettingsModel::class], version = 1, exportSchema = false)
@TypeConverters(BaseConverters::class, SettingsConverters::class)
abstract class DatabaseFactory : RoomDatabase() {

    /* Configuration for DAOs */
    abstract fun userDao(): UserDao
    abstract fun settingsDao(): SettingsDao


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


class BaseConverters {
    val mapStringIntAdapter: JsonAdapter<Map<String, Int>?> = Moshi.Builder().build().adapter<Map<String, Int>?>(Map::class.java)

    @TypeConverter
    fun fromStringToMap(value: String?): Map<String, Int>? { return mapStringIntAdapter.fromJson(value) }
    @TypeConverter
    fun fromMap(value: Map<String, Int>?): String { return mapStringIntAdapter.toJson(value) }
}