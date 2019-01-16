package com.jmlabs.xoomcodechallenge.ui

import android.content.Intent
import android.net.Uri
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
 * A RecyclerView ViewHolder that displays a reddit post.
 */
class XoomCountryViewHolder(view: View, private val glide: GlideRequests) : RecyclerView.ViewHolder(view) {
    private val title: TextView = view.findViewById(R.id.title)
    private val subtitle: TextView = view.findViewById(R.id.subtitle)
    private val score: TextView = view.findViewById(R.id.score)
    private val thumbnail: ImageView = view.findViewById(R.id.thumbnail)
    private var country: XoomCountry? = null

    fun bind(country: XoomCountry?) {
        this.country = country
        title.text = country?.title ?: "loading"
        subtitle.text = itemView.context.resources.getString(
            R.string.post_subtitle,
            post?.author ?: "unknown"
        )
        score.text = "${post?.score ?: 0}"
        if (post?.thumbnail?.startsWith("http") == true) {
            thumbnail.visibility = View.VISIBLE
            glide.load(post.thumbnail)
                .centerCrop()
                .placeholder(R.drawable.ic_insert_photo_black_48dp)
                .into(thumbnail)
        } else {
            thumbnail.visibility = View.GONE
            glide.clear(thumbnail)
        }
    }

    companion object {
        fun create(parent: ViewGroup, glide: GlideRequests): XoomCountryViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.reddit_post_item, parent, false)
            return XoomCountryViewHolder(view, glide)
        }
    }
}