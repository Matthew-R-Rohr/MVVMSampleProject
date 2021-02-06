package com.fct.mvvm.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fct.mvvm.data.LaunchEntity
import com.fct.mvvm.data.database.converter.StringListConverter

@Database(
    entities = [
        LaunchEntity::class
    ],
    version = 1
)
@TypeConverters(
    StringListConverter::class
)
abstract class SpaceXDatabase : RoomDatabase() {

    abstract fun launchDao(): LaunchDao

    companion object {

        // singleton prevents multiple instances of database opening at the same time.
        @Volatile
        private var INSTANCE: SpaceXDatabase? = null

        fun getDatabase(context: Context): SpaceXDatabase {
            // if the INSTANCE is not null, then return it, if it is, then create the database
            return INSTANCE ?: synchronized(this) {

                // keeping things simple using an in memory version of ROOM which allows:
                // 1. leveraging SQL queries on the data
                // 2. no schema update issues
                // 3. ensures user sees fresh api data on every app start
                val instance = Room.inMemoryDatabaseBuilder(
                    context.applicationContext,
                    SpaceXDatabase::class.java
                ).build()
                INSTANCE = instance

                // return instance
                instance
            }
        }
    }
}