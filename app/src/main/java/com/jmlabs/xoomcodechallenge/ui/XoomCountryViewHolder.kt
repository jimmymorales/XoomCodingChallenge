package com.jmlabs.xoomcodechallenge.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jmlabs.xoomcodechallenge.GlideRequests
import com.jmlabs.xoomcodechallenge.R
import com.jmlabs.xoomcodechallenge.vo.XoomCountry

/**
 * A RecyclerView ViewHolder that displays a xoom country.
 */
class XoomCountryViewHolder(
    view: View, private val glide: GlideRequests,
    private val updateCallback: (country: XoomCountry) -> Unit
) : RecyclerView.ViewHolder(view) {

    private val flag: ImageView = view.findViewById(R.id.flag)
    private val name: TextView = view.findViewById(R.id.country_title)
    private val star: ImageView = view.findViewById(R.id.star)
    private var country: XoomCountry? = null

    init {
        star.setOnClickListener {
            country?.favorite = country?.favorite?.not() ?: false
            updateCallback(country!!)
            bindFav(country?.favorite == true)
        }
    }

    fun bind(country: XoomCountry?) {
        this.country = country
        name.text = country?.name ?: "loading"
        if (country?.code?.any() == true) {
            glide.load(
                COUNTRY_FLAGS_URL_HOST +
                        country.code.toLowerCase() +
                        "/$COUNTRY_FLAG_TYPE/$COUNTRY_FLAG_SIZE.png"
            )
                .centerCrop()
                .placeholder(R.drawable.placeholder_flag)
                .into(flag)
        } else {
            glide.clear(flag)
            flag.setImageResource(R.drawable.placeholder_flag)
        }

        bindFav(country?.favorite == true)
    }

    private fun bindFav(isFavorite: Boolean) {
        star.setImageResource(
            if (isFavorite) {
                R.drawable.ic_star_teal_24dp
            } else {
                R.drawable.ic_star_border_teal_24dp
            }
        )
    }

    companion object {
        private const val COUNTRY_FLAGS_URL_HOST = "https://www.countryflags.io/"
        private const val COUNTRY_FLAG_TYPE = "flat"
        private const val COUNTRY_FLAG_SIZE = "64"

        fun create(
            parent: ViewGroup, glide: GlideRequests,
            updateCallback: (country: XoomCountry) -> Unit
        ): XoomCountryViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.xoom_country_item, parent, false)
            return XoomCountryViewHolder(view, glide, updateCallback)
        }
    }
}