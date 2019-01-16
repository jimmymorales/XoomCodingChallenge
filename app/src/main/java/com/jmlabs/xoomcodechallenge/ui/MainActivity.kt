package com.jmlabs.xoomcodechallenge.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import com.jmlabs.xoomcodechallenge.GlideApp
import com.jmlabs.xoomcodechallenge.R
import com.jmlabs.xoomcodechallenge.ServiceLocator
import com.jmlabs.xoomcodechallenge.repository.NetworkState
import com.jmlabs.xoomcodechallenge.vo.XoomCountry
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var model: CountriesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        model = getViewModel()

        initAdapter()
        initSwipeToRefresh()

    }

    private fun getViewModel(): CountriesViewModel {
        return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val repo = ServiceLocator.instance(this@MainActivity)
                    .getRepository()
                @Suppress("UNCHECKED_CAST")
                return CountriesViewModel(repo) as T
            }
        })[CountriesViewModel::class.java]
    }

    private fun initAdapter() {
        val glide = GlideApp.with(this)
        val adapter = CountriesAdapter(glide) {
            model.retry()
        }
        list.adapter = adapter
        model.countries.observe(this, Observer<PagedList<XoomCountry>> {
            adapter.submitList(it)
        })
        model.networkState.observe(this, Observer {
            adapter.setNetworkState(it)
        })
    }

    private fun initSwipeToRefresh() {
        model.refreshState.observe(this, Observer {
            swipe_refresh.isRefreshing = it == NetworkState.LOADING
        })
        swipe_refresh.setOnRefreshListener {
            model.refresh()
        }
    }
}
