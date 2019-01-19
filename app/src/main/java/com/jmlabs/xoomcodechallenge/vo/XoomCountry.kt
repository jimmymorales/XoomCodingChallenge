package com.jmlabs.xoomcodechallenge.vo

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.jmlabs.xoomcodechallenge.api.XoomApi

@Entity(
    tableName = "countries"
)
data class XoomCountry(
    @PrimaryKey
    val code: String,
    val name: String,
    var favorite: Boolean,
    var nextPage: Int = -1) {

    var hasDisbursementOptions = false

    @Ignore
    @SerializedName("disbursement_options")
    var disbursementOptions: List<XoomApi.DisbursementOption>? = null
}