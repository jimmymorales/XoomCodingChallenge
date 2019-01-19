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
        fun create(context: Context): XoomCountriesDb {
            return Room.databaseBuilder(context, XoomCountriesDb::class.java, "xoom.db")
                .fallbackToDestructiveMigration()
                .build()
        }
    }

    abstract fun countriesDao(): XoomCountryDao
}
