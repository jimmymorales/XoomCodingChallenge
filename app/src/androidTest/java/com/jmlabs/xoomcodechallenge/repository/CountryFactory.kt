package com.jmlabs.xoomcodechallenge.repository

import com.jmlabs.xoomcodechallenge.vo.XoomCountry
import java.util.concurrent.atomic.AtomicInteger

class CountryFactory {
    private val counter = AtomicInteger(0)
    fun createXoomCountry() : XoomCountry {
        val id = counter.incrementAndGet()
        return XoomCountry(
            code = id.toString(),
            name = "name_$id",
            favorite = false)
    }
}