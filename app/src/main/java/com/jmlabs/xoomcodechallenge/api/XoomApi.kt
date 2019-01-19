package com.jmlabs.xoomcodechallenge.api

import android.util.Log
import com.google.gson.annotations.SerializedName
import com.jmlabs.xoomcodechallenge.vo.XoomCountry
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface XoomApi {
    @GET("/catalog/v2/countries")
    fun getCountries(
        @Query("page_size") pageSize: Int,
        @Query("page") page: Int): Call<ListingResponse>

    class ListingResponse(
        val items: List<XoomCountry>,
        val links: List<LinkResponse>
    )

    class LinkResponse(
        val rel: String,
        val href: String
    )

    class DisbursementOption(
        val id: String,
        @SerializedName("disbursement_type")
        val type: String)

    companion object {
        private const val BASE_URL = "https://mobile.xoom.com"
        fun create(): XoomApi = create(HttpUrl.parse(BASE_URL)!!)
        fun create(httpUrl: HttpUrl): XoomApi {
            val logger = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
                Log.d("API", it)
            })
            logger.level = HttpLoggingInterceptor.Level.BASIC

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()
            return Retrofit.Builder()
                .baseUrl(httpUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(XoomApi::class.java)
        }
    }
}