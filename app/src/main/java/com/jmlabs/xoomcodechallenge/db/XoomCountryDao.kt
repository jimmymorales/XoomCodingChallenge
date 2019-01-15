package com.jmlabs.xoomcodechallenge.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jmlabs.xoomcodechallenge.vo.XoomCountry

@Dao
interface XoomCountryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(posts : List<XoomCountry>)

    @Query("SELECT * FROM countries")
    fun countries()
}
