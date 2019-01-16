package com.jmlabs.xoomcodechallenge

import android.app.Application
import android.content.Context
import androidx.annotation.VisibleForTesting
import com.jmlabs.xoomcodechallenge.api.XoomApi
import com.jmlabs.xoomcodechallenge.db.XoomCountriesDb
import com.jmlabs.xoomcodechallenge.repository.XoomCountriesRepository
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Super simplified service locator implementation to allow us to replace default implementations
 * for testing.
 */
interface ServiceLocator {
    companion object {
        private val LOCK = Any()
        private var instance: ServiceLocator? = null
        fun instance(context: Context): ServiceLocator {
            synchronized(LOCK) {
                if (instance == null) {
                    instance = DefaultServiceLocator(
                        app = context.applicationContext as Application)
                }
                return instance!!
            }
        }

        /**
         * Allows tests to replace the default implementations.
         */
        @VisibleForTesting
        fun swap(locator: ServiceLocator) {
            instance = locator
        }
    }

    fun getRepository(): XoomCountriesRepository

    fun getXoomApi(): XoomApi

    fun getNetworkExecutor(): Executor

    fun getDiskIOExecutor(): Executor

}

/**
 * default implementation of ServiceLocator that uses production endpoints.
 */
open class DefaultServiceLocator(val app: Application) : ServiceLocator {

    // thread pool used for disk access
    @Suppress("PrivatePropertyName")
    private val DISK_IO = Executors.newSingleThreadExecutor()

    // thread pool used for network requests
    @Suppress("PrivatePropertyName")
    private val NETWORK_IO = Executors.newFixedThreadPool(5)


    private val db by lazy {
        XoomCountriesDb.create(app)
    }

    private val api by lazy {
        XoomApi.create()
    }

    override fun getRepository(): XoomCountriesRepository {
        return XoomCountriesRepository(
                db = db,
                xoomApi = getXoomApi())
    }

    override fun getXoomApi(): XoomApi = api

    override fun getNetworkExecutor(): Executor = NETWORK_IO

    override fun getDiskIOExecutor(): Executor = DISK_IO
}