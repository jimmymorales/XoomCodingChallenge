package com.jmlabs.xoomcodechallenge.repository

import com.jmlabs.xoomcodechallenge.api.XoomApi
import com.jmlabs.xoomcodechallenge.vo.XoomCountry
import retrofit2.Call
import retrofit2.mock.Calls
import java.io.IOException

/**
 * implements the XoomApi with controllable requests
 */
class FakeXoomApi : XoomApi {

    // countries
    private val model = mutableListOf<XoomCountry>()
    var failureMsg: String? = null
    fun addCountry(country: XoomCountry) {
        model.add(country)
    }

    fun clear() {
        model.clear()
    }

    private fun findCountries(
        pageSize: Int,
        page: Int? = null
    ): List<XoomCountry> {
        if (page == null) {
            return model.subList(0, Math.min(model.size, pageSize))
        }
        val index = model.indexOfLast { it.nextPage == page }
        if (index == -1) {
            return emptyList()
        }
        val startPos = index + 1
        return model.subList(startPos, Math.min(model.size, startPos + model.size))
    }

    override fun getCountries(pageSize: Int, page: Int): Call<XoomApi.ListingResponse> {
        failureMsg?.let {
            return Calls.failure(IOException(it))
        }

        val response = XoomApi.ListingResponse(findCountries(pageSize, page), listOf())
        return Calls.response(response)
    }
}