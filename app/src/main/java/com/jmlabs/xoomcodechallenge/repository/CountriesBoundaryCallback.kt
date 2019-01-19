package com.jmlabs.xoomcodechallenge.repository

import androidx.annotation.MainThread
import androidx.paging.PagedList
import androidx.paging.PagingRequestHelper
import com.jmlabs.xoomcodechallenge.api.XoomApi
import com.jmlabs.xoomcodechallenge.util.createStatusLiveData
import com.jmlabs.xoomcodechallenge.vo.XoomCountry
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor

/**
 * This boundary callback gets notified when user reaches to the edges of the list such that the
 * database cannot provide any more data.
 * <p>
 * The boundary callback might be called multiple times for the same direction so it does its own
 * rate limiting using the PagingRequestHelper class.
 */
class CountriesBoundaryCallback(
    private val webservice: XoomApi,
    private val handleResponse: (XoomApi.ListingResponse?) -> Unit,
    private val ioExecutor: Executor,
    private val networkPageSize: Int
) : PagedList.BoundaryCallback<XoomCountry>() {

    val helper = PagingRequestHelper(ioExecutor)
    val networkState = helper.createStatusLiveData()

    /**
     * Database returned 0 items. We should query the backend for more items.
     */
    @MainThread
    override fun onZeroItemsLoaded() {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) {
            webservice.getCountries(
                pageSize = networkPageSize,
                page = 1
            )
                .enqueue(createWebserviceCallback(it))
        }
    }

    /**
     * User reached to the end of the list.
     */
    @MainThread
    override fun onItemAtEndLoaded(itemAtEnd: XoomCountry) {
        if (itemAtEnd.nextPage == -1) return

        helper.runIfNotRunning(PagingRequestHelper.RequestType.AFTER) {
            webservice.getCountries(
                pageSize = networkPageSize,
                page = itemAtEnd.nextPage
            )
                .enqueue(createWebserviceCallback(it))
        }
    }

    /**
     * every time it gets new items, boundary callback simply inserts them into the database and
     * paging library takes care of refreshing the list if necessary.
     */
    private fun insertItemsIntoDb(
        response: Response<XoomApi.ListingResponse>,
        it: PagingRequestHelper.Request.Callback
    ) {
        ioExecutor.execute {
            handleResponse(response.body())
            it.recordSuccess()
        }
    }

    override fun onItemAtFrontLoaded(itemAtFront: XoomCountry) {
        // ignored, since we only ever append to what's in the DB
    }

    private fun createWebserviceCallback(it: PagingRequestHelper.Request.Callback)
            : Callback<XoomApi.ListingResponse> {
        return object : Callback<XoomApi.ListingResponse> {
            override fun onFailure(
                call: Call<XoomApi.ListingResponse>,
                t: Throwable
            ) {
                it.recordFailure(t)
            }

            override fun onResponse(
                call: Call<XoomApi.ListingResponse>,
                response: Response<XoomApi.ListingResponse>
            ) {
                insertItemsIntoDb(response, it)
            }
        }
    }
}