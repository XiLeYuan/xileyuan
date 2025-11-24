package com.xly.business.login.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.xly.R

class LifePhotoAdapter4(
    private val photos: MutableList<String>,
    private val onAddClick: () -> Unit,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_ADD = 0
        private const val TYPE_IMAGE = 1
        private const val MAX_PHOTO = 4
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == photos.size && photos.size < MAX_PHOTO) TYPE_ADD else TYPE_IMAGE
    }

    override fun getItemCount(): Int {
        return if (photos.size < MAX_PHOTO) photos.size + 1 else MAX_PHOTO
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_ADD) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_life_photo_add, parent, false)
            AddViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_life_photo_image, parent, false)
            ImageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AddViewHolder) {
            holder.ivAdd.setOnClickListener { onAddClick() }
        } else if (holder is ImageViewHolder) {
            val photoPath = photos[position]
            Glide.with(holder.ivPhoto.context)
                .load(photoPath)
                .placeholder(R.drawable.bg_avatar_selector)
                .into(holder.ivPhoto)
            holder.btnDelete.setOnClickListener { onDeleteClick(position) }
        }
    }

    class AddViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivAdd: ImageView = view.findViewById(R.id.ivAdd)
    }

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPhoto: ShapeableImageView = view.findViewById(R.id.ivPhoto)
        val btnDelete: ImageView = view.findViewById(R.id.btnDelete)
    }
}

