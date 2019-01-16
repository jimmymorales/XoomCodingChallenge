package com.jmlabs.xoomcodechallenge.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.jmlabs.xoomcodechallenge.repository.Listing
import com.jmlabs.xoomcodechallenge.repository.XoomCountriesRepository
import com.jmlabs.xoomcodechallenge.vo.XoomCountry

class CountriesViewModel(private val repository: XoomCountriesRepository) : ViewModel() {

    private val repoResult = MutableLiveData<Listing<XoomCountry>>().apply {
        value = repository.countries(30)
    }
    val countries = Transformations.switchMap(repoResult) { it.pagedList }!!
    val networkState = Transformations.switchMap(repoResult) { it.networkState }!!
    val refreshState = Transformations.switchMap(repoResult) { it.refreshState }!!

    fun refresh() {
        repoResult.value?.refresh?.invoke()
    }

    fun retry() {
        val listing = repoResult.value
        listing?.retry?.invoke()
    }
}