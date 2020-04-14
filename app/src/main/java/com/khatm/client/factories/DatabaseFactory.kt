package com.khatm.client.factories

import android.content.Context
import androidx.room.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.khatm.client.models.Constants
import com.khatm.client.models.Features
import com.khatm.client.models.SettingsModel
import com.khatm.client.models.UserModel
import com.khatm.client.repositories.SettingsDao
import com.khatm.client.repositories.UserDao
import com.squareup.moshi.Moshi
import java.lang.reflect.Type


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
    // TODO: Replace this with Moshi so not using Gson anywhere

    @TypeConverter
    fun fromStringToMap(value: String?): Map<String, Int> {
        val mapType: Type = object : TypeToken<Map<String, Int>?>() {}.getType()
        return Gson().fromJson(value, mapType)
    }

    @TypeConverter
    fun fromMap(value: Map<String, Int>?): String {
        val gson = Gson()
        return gson.toJson(value)
    }


    @TypeConverter
    fun fromStringToConstants(value: String?): Constants {
        val mapType: Type = object : TypeToken<Constants?>() {}.getType()
        return Gson().fromJson(value, mapType)
    }

    @TypeConverter
    fun fromConstants(value: Constants?): String {
        val gson = Gson()
        return gson.toJson(value)
    }


    @TypeConverter
    fun fromStringToFeatures(value: String?): Features {
        val mapType: Type = object : TypeToken<Features?>() {}.getType()
        return Gson().fromJson(value, mapType)
    }

    @TypeConverter
    fun fromFeatures(value: Features?): String {
        val gson = Gson()
        return gson.toJson(value)
    }
}
