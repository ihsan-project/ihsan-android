package com.khatm.client.factories

import android.content.Context
import androidx.room.*
import com.khatm.client.models.Constants
import com.khatm.client.models.Features
import com.khatm.client.models.SettingsModel
import com.khatm.client.models.UserModel
import com.khatm.client.repositories.SettingsDao
import com.khatm.client.repositories.UserDao
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi


@Database(entities = [UserModel::class, SettingsModel::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
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

class Converters {
    @TypeConverter
    fun fromStringToMap(value: String?): Map<String, Int>? {
        val moshi = Moshi.Builder().build()
        val jsonAdapter: JsonAdapter<Map<String, Int>> = moshi.adapter<Map<String, Int>>(Map::class.java)

        return jsonAdapter.fromJson(value)
    }

    @TypeConverter
    fun fromMap(value: Map<String, Int>?): String {
        val moshi = Moshi.Builder().build()
        val jsonAdapter: JsonAdapter<Map<String, Int>?> = moshi.adapter<Map<String, Int>?>(Map::class.java)

        return jsonAdapter.toJson(value)
    }


    @TypeConverter
    fun fromStringToConstants(value: String?): Constants? {
        val moshi = Moshi.Builder().build()
        val jsonAdapter: JsonAdapter<Constants?> = moshi.adapter<Constants?>(Constants::class.java)

        return jsonAdapter.fromJson(value)
    }

    @TypeConverter
    fun fromConstants(value: Constants?): String {
        val moshi = Moshi.Builder().build()
        val jsonAdapter: JsonAdapter<Constants?> = moshi.adapter<Constants?>(Constants::class.java)

        return jsonAdapter.toJson(value)
    }


    @TypeConverter
    fun fromStringToFeatures(value: String?): Features? {
        val moshi = Moshi.Builder().build()
        val jsonAdapter: JsonAdapter<Features?> = moshi.adapter<Features?>(Features::class.java)

        return jsonAdapter.fromJson(value)
    }

    @TypeConverter
    fun fromFeatures(value: Features?): String {
        val moshi = Moshi.Builder().build()
        val jsonAdapter: JsonAdapter<Features?> = moshi.adapter<Features?>(Features::class.java)

        return jsonAdapter.toJson(value)
    }
}
