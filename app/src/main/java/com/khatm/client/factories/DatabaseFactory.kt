package com.khatm.client.factories

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.khatm.client.models.SettingsModel
import com.khatm.client.models.UserModel
import com.khatm.client.repositories.UserDao
import com.khatm.client.repositories.SettingsDao


@Database(entities = [UserModel::class, SettingsModel::class], version = 1, exportSchema = false)
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