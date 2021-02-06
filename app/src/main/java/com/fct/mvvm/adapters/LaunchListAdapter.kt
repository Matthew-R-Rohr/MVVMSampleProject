package com.fct.mvvm.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.fct.mvvm.data.LaunchDiffCallback
import com.fct.mvvm.data.LaunchEntity
import com.fct.mvvm.databinding.LaunchListItemBinding
import com.fct.mvvm.views.viewholder.LaunchItemViewHolder

/**
 * Adapter for [LaunchEntity] list
 */
class LaunchListAdapter(
    private val clickListener: (entity: LaunchEntity) -> Unit = {}
) : ListAdapter<LaunchEntity, LaunchItemViewHolder>(LaunchDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LaunchItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LaunchListItemBinding.inflate(inflater, parent, false)
        return LaunchItemViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(holder: LaunchItemViewHolder, position: Int) {
        onBindViewHolder(holder, position, emptyList())
    }

    override fun onBindViewHolder(
        viewHolder: LaunchItemViewHolder,
        position: Int,
        list: List<Any>
    ) {
        viewHolder.bind(getItem(position))
    }

    override fun onViewRecycled(holder: LaunchItemViewHolder) {
        holder.unbind()
    }
}