package com.xly.business.user.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.xly.R

class ThumbnailAdapter(
    private val imageResources: List<Int>,
    private val onThumbnailClick: (Int) -> Unit
) : RecyclerView.Adapter<ThumbnailAdapter.ThumbnailViewHolder>() {

    var selectedPosition: Int = 0
        set(value) {
            val oldPosition = field
            field = value
            notifyItemChanged(oldPosition)
            notifyItemChanged(value)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThumbnailViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_thumbnail, parent, false)
        return ThumbnailViewHolder(view)
    }

    override fun onBindViewHolder(holder: ThumbnailViewHolder, position: Int) {
        holder.bind(imageResources[position], position == selectedPosition)
    }

    override fun getItemCount(): Int = imageResources.size

    inner class ThumbnailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val thumbnailImage: ImageView = itemView.findViewById(R.id.thumbnail_image)

        fun bind(imageResource: Int, isSelected: Boolean) {
            Glide.with(itemView)
                .load(imageResource)
                .apply(RequestOptions.centerCropTransform())
                .into(thumbnailImage)

            itemView.isSelected = isSelected

            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    selectedPosition = position
                    onThumbnailClick(position)
                }
            }
        }
    }
}

