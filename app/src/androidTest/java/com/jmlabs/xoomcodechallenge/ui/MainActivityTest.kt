package com.jmlabs.xoomcodechallenge.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.recyclerview.widget.RecyclerView
import androidx.test.InstrumentationRegistry
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.jmlabs.xoomcodechallenge.DefaultServiceLocator
import com.jmlabs.xoomcodechallenge.ServiceLocator
import com.jmlabs.xoomcodechallenge.api.XoomApi
import com.jmlabs.xoomcodechallenge.repository.CountryFactory
import com.jmlabs.xoomcodechallenge.repository.FakeXoomApi
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * Simple sanity test to ensure data is displayed
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class RedditActivityTest {

    @get:Rule
    var testRule = CountingTaskExecutorRule()

    @get:Rule
    var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    private val countriesFactory = CountryFactory()
    @Before
    fun init() {
        val fakeApi = FakeXoomApi()
        fakeApi.addCountry(countriesFactory.createXoomCountry())
        fakeApi.addCountry(countriesFactory.createXoomCountry())
        fakeApi.addCountry(countriesFactory.createXoomCountry())
        val app = ApplicationProvider.getApplicationContext<Context>() as Application
        // use a controlled service locator w/ fake API
        ServiceLocator.swap(
            object : DefaultServiceLocator(
                app = app,
                useInMemoryDb = true
            ) {
                override fun getXoomApi(): XoomApi = fakeApi
            }
        )
    }

    @Test
    @Throws(InterruptedException::class, TimeoutException::class)
    fun showSomeResults() {
        val recyclerView = activity.findViewById<RecyclerView>(R.id.list)
        MatcherAssert.assertThat(recyclerView.adapter, CoreMatchers.notNullValue())
        waitForAdapterChange(recyclerView)
        MatcherAssert.assertThat(recyclerView.adapter?.itemCount, CoreMatchers.`is`(3))
    }

    private fun waitForAdapterChange(recyclerView: RecyclerView) {
        val latch = CountDownLatch(1)
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            recyclerView.adapter?.registerAdapterDataObserver(
                object : RecyclerView.AdapterDataObserver() {
                    override fun onChanged() {
                        latch.countDown()
                    }

                    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                        latch.countDown()
                    }
                })
        }
        testRule.drainTasks(1, TimeUnit.SECONDS)
        if (recyclerView.adapter?.itemCount ?: 0 > 0) {
            return
        }
        MatcherAssert.assertThat(latch.await(10, TimeUnit.SECONDS), CoreMatchers.`is`(true))
    }
}