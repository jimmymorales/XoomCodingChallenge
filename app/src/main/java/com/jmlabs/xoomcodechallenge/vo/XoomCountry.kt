package com.jmlabs.xoomcodechallenge.vo

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "countries"
)
data class XoomCountry(
    @PrimaryKey
    @SerializedName("code")
    val code: String,
    @SerializedName("name")
    val name: String,
    val favorite: Boolean,
    var nextPage: Int = -1)