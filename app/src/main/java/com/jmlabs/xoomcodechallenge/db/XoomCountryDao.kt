package com.jmlabs.xoomcodechallenge.db

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jmlabs.xoomcodechallenge.vo.XoomCountry

@Dao
interface XoomCountryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(posts : List<XoomCountry>)

    @Query("SELECT * FROM countries")
    fun countries() : DataSource.Factory<Int, XoomCountry>

    // TODO: Fix query to show favs first
    // TODO: Add Disbursement options to entity and filter by disbursement options
    @Query("SELECT * FROM countries")
    fun countriesWithDisbursementOptions() : DataSource.Factory<Int, XoomCountry>

    // TODO: Add update fav and add click listener to start

}
