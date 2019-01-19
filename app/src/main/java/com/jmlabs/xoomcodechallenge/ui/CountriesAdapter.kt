package com.jmlabs.xoomcodechallenge.ui

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.jmlabs.xoomcodechallenge.GlideRequests
import com.jmlabs.xoomcodechallenge.R
import com.jmlabs.xoomcodechallenge.repository.NetworkState
import com.jmlabs.xoomcodechallenge.vo.XoomCountry

/**
 * A simple adapter implementation that shows Xoom's countries with active disbursement options.
 */
class CountriesAdapter(
    private val glide: GlideRequests,
    private val retryCallback: () -> Unit,
    private val updateCallback: (it: XoomCountry) -> Unit)
    : PagedListAdapter<XoomCountry, RecyclerView.ViewHolder>(COUNTRY_COMPARATOR) {

    private var networkState: NetworkState? = null

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.xoom_country_item -> (holder as XoomCountryViewHolder).bind(getItem(position))
            R.layout.network_state_item -> (holder as NetworkStateItemViewHolder).bindTo(
                networkState)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.xoom_country_item -> XoomCountryViewHolder.create(parent, glide, updateCallback)
            R.layout.network_state_item -> NetworkStateItemViewHolder.create(parent, retryCallback)
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

    private fun hasExtraRow() = networkState != null && networkState != NetworkState.LOADED

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            R.layout.network_state_item
        } else {
            R.layout.xoom_country_item
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    fun setNetworkState(newNetworkState: NetworkState?) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }

    companion object {
        val COUNTRY_COMPARATOR = object : DiffUtil.ItemCallback<XoomCountry>() {
            override fun areContentsTheSame(oldItem: XoomCountry, newItem: XoomCountry): Boolean =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: XoomCountry, newItem: XoomCountry): Boolean =
                oldItem.name == newItem.name
        }
    }
}
