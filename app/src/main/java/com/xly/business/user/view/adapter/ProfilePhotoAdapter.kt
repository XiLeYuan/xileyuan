package com.xly.business.user.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xly.R

class ProfilePhotoAdapter(
    private val photos: MutableList<Int>,
    private val maxPhotos: Int = 6,
    private val onAddClick: () -> Unit,
    private val onPhotoClick: (Int, Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_ADD = 0
        private const val TYPE_PHOTO = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == photos.size && photos.size < maxPhotos) {
            TYPE_ADD
        } else {
            TYPE_PHOTO
        }
    }

    override fun getItemCount(): Int {
        return if (photos.size < maxPhotos) photos.size + 1 else maxPhotos
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_ADD) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_profile_photo_add, parent, false)
            AddPhotoViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_profile_photo, parent, false)
            PhotoViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AddPhotoViewHolder) {
            holder.itemView.setOnClickListener { onAddClick() }
        } else if (holder is PhotoViewHolder) {
            val photoResId = photos[position]
            Glide.with(holder.itemView.context)
                .load(photoResId)
                .centerCrop()
                .into(holder.ivPhoto)
            holder.itemView.setOnClickListener { onPhotoClick(position, photoResId) }
        }
    }

    fun updatePhotos(newPhotos: List<Int>) {
        photos.clear()
        photos.addAll(newPhotos)
        notifyDataSetChanged()
    }

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPhoto: ImageView = itemView.findViewById(R.id.ivPhoto)
    }

    class AddPhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivAdd: ImageView = itemView.findViewById(R.id.ivAdd)
    }
}


