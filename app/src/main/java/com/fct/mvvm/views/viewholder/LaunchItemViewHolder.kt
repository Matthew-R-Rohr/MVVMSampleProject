package com.fct.mvvm.views.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.fct.mvvm.R
import com.fct.mvvm.data.LaunchEntity
import com.fct.mvvm.data.generateSuccessMessage
import com.fct.mvvm.databinding.LaunchListItemBinding
import com.fct.mvvm.extensions.toDateFromUnix
import com.fct.mvvm.extensions.toMonthDayYearString
import com.squareup.picasso.Picasso

/**
 * ViewHolder for the LaunchListAdapter
 */
class LaunchItemViewHolder(
    val binding: LaunchListItemBinding,
    val clickListener: (entity: LaunchEntity) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(entity: LaunchEntity) {

        // mission patch
        with(binding.imgPatch) {
            Picasso.get()
                .load(entity.missionPatchSmall)
                .placeholder(R.mipmap.placeholder)
                .error(R.mipmap.placeholder)
                .into(binding.imgPatch)
            contentDescription = entity.name
        }

        // name
        binding.txtName.text = entity.name

        // photo count
        binding.txtPhotoCount.text = (entity.flickrImages?.size ?: 0).toString()

        // success?
        binding.txtSuccess.text = entity.generateSuccessMessage(itemView.context)

        // date
        val date = entity.dateUnix.toDateFromUnix()
        binding.txtDate.text = date.toMonthDayYearString()

        // click listeners
        itemView.setOnClickListener {
            clickListener(entity)
        }
    }

    fun unbind() {
        itemView.setOnLongClickListener(null)
    }
}