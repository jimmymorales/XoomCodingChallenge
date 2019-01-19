package com.jmlabs.xoomcodechallenge.repository

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.toLiveData
import com.jmlabs.xoomcodechallenge.api.XoomApi
import com.jmlabs.xoomcodechallenge.db.XoomCountriesDb
import com.jmlabs.xoomcodechallenge.vo.XoomCountry
import okhttp3.HttpUrl
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor

class XoomCountriesRepository(
    val db: XoomCountriesDb,
    private val xoomApi: XoomApi,
    private val ioExecutor: Executor,
    private val networkPageSize: Int = DEFAULT_NETWORK_PAGE_SIZE
) {

    companion object {
        private const val DEFAULT_NETWORK_PAGE_SIZE = 30
    }

    /**
     * Inserts the response into the database.
     */
    private fun insertResultIntoDb(body: XoomApi.ListingResponse?) {
        val href =
            body?.links?.find { it.rel == "next" }?.href
        val nextPage = if (href != null) {
            val url = HttpUrl.parse("http://example.com$href")
            url?.queryParameter("page")?.toInt()
        } else {
            null
        }
        body?.items?.let { countries ->
            val countriesWithNextPage = countries.map {
                it.nextPage = nextPage ?: -1
                it.hasDisbursementOptions = it.disbursementOptions?.any() ?: false
                it
            }
            db.runInTransaction {
                db.countriesDao().insert(countriesWithNextPage)
            }
        }
    }

    /**
     * When refresh is called, we simply run a fresh network request and when it arrives.
     * <p>
     * Since the PagedList already uses a database bound data source, it will automatically be
     * updated after the database transaction is finished.
     */
    @MainThread
    private fun refresh(): LiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING
        xoomApi.getCountries(networkPageSize, 1).enqueue(
            object : Callback<XoomApi.ListingResponse> {
                override fun onFailure(call: Call<XoomApi.ListingResponse>, t: Throwable) {
                    // retrofit calls this on main thread so safe to call set value
                    networkState.value = NetworkState.error(t.message)
                }

                override fun onResponse(
                    call: Call<XoomApi.ListingResponse>,
                    response: Response<XoomApi.ListingResponse>
                ) {
                    ioExecutor.execute {
                        insertResultIntoDb(response.body())
                        // since we are in bg thread now, post the result.
                        networkState.postValue(NetworkState.LOADED)
                    }
                }
            }
        )
        return networkState
    }

    /**
     * Returns a Listing.
     */
    @MainThread
    fun getCountryListing(pageSize: Int): Listing<XoomCountry> {
        // create a boundary callback which will observe when the user reaches to the edges of
        // the list and update the database with extra data.
        val boundaryCallback = CountriesBoundaryCallback(
            webservice = xoomApi,
            handleResponse = this::insertResultIntoDb,
            ioExecutor = ioExecutor,
            networkPageSize = networkPageSize
        )
        // we are using a mutable live data to trigger refresh requests which eventually calls
        // refresh method and gets a new live data. Each refresh request by the user becomes a newly
        // dispatched data in refreshTrigger
        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = Transformations.switchMap(refreshTrigger) {
            refresh()
        }

        val livePagedList = db.countriesDao().countriesWithDisbursementOptions().toLiveData(
            pageSize = pageSize,
            boundaryCallback = boundaryCallback
        )

        return Listing(
            pagedList = livePagedList,
            networkState = boundaryCallback.networkState,
            retry = {
                boundaryCallback.helper.retryAllFailed()
            },
            refresh = {
                refreshTrigger.value = null
            },
            refreshState = refreshState
        )
    }

    fun updateFav(country: XoomCountry) = ioExecutor.execute {
        db.countriesDao().updateFav(country)
    }
}