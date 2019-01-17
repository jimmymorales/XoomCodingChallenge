package com.jmlabs.xoomcodechallenge.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jmlabs.xoomcodechallenge.GlideRequests
import com.jmlabs.xoomcodechallenge.R
import com.jmlabs.xoomcodechallenge.vo.XoomCountry

/**
 * A RecyclerView ViewHolder that displays a xoom country.
 */
class XoomCountryViewHolder(view: View, private val glide: GlideRequests) : RecyclerView.ViewHolder(view) {
    private val flag: ImageView = view.findViewById(R.id.flag)
    private val name: TextView = view.findViewById(R.id.country_title)
    private val star: ImageView = view.findViewById(R.id.star)
    private var country: XoomCountry? = null

    fun bind(country: XoomCountry?) {
        this.country = country
        name.text = country?.name ?: "loading"
        if (country?.code?.any() == true) {
            glide.load("https://www.countryflags.io/${country.code.toLowerCase()}/flat/64.png")
                .centerCrop()
                .placeholder(R.drawable.placeholder_flag)
                .into(flag)
        } else {
            glide.clear(flag)
            flag.setImageResource(R.drawable.placeholder_flag)
        }

        star.setImageResource(
            if (country?.favorite == true) {
                R.drawable.ic_star_teal_24dp
            } else {
                R.drawable.ic_star_border_teal_24dp
            }
        )
    }

    companion object {
        fun create(parent: ViewGroup, glide: GlideRequests): XoomCountryViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.xoom_country_item, parent, false)
            return XoomCountryViewHolder(view, glide)
        }
    }
}