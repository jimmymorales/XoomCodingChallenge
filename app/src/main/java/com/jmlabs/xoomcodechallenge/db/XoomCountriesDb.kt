package com.jmlabs.xoomcodechallenge.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jmlabs.xoomcodechallenge.vo.XoomCountry

/**
 * Database schema used by the XoomCountriesRepository
 */
@Database(
    entities = [XoomCountry::class],
    version = 1,
    exportSchema = false
)
abstract class XoomCountriesDb : RoomDatabase() {
    companion object {
        fun create(context: Context, useInMemoryDb: Boolean): XoomCountriesDb {
            val databaseBuilder = if (useInMemoryDb) {
                Room.inMemoryDatabaseBuilder(context, XoomCountriesDb::class.java)
            } else {
                Room.databaseBuilder(context, XoomCountriesDb::class.java, "xoom.db")
            }

            return databaseBuilder.fallbackToDestructiveMigration()
                .build()
        }
    }

    abstract fun countriesDao(): XoomCountryDao
}
