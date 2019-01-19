package com.jmlabs.xoomcodechallenge.db

import androidx.paging.DataSource
import androidx.room.*
import com.jmlabs.xoomcodechallenge.XoomAppGlideModule
import com.jmlabs.xoomcodechallenge.vo.XoomCountry

@Dao
interface XoomCountryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(posts: List<XoomCountry>)

    @Query(
        """
            SELECT *
            FROM countries
            WHERE hasDisbursementOptions = 1
            ORDER BY favorite DESC, code
            """
    )
    fun countriesWithDisbursementOptions(): DataSource.Factory<Int, XoomCountry>

    @Update
    fun updateFav(country: XoomCountry)
}
