package com.jmlabs.xoomcodechallenge.repository

import com.jmlabs.xoomcodechallenge.api.XoomApi
import com.jmlabs.xoomcodechallenge.db.XoomCountriesDb

class XoomCountriesRepository(
    val db: XoomCountriesDb,
    private val xoomApi: XoomApi,
    private val networkPageSize: Int = DEFAULT_NETWORK_PAGE_SIZE) {
        companion object {
            private const val DEFAULT_NETWORK_PAGE_SIZE = 10
        }
}
