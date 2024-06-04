package com.wafflestudio.wafflestagram.ui.post

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aghajari.zoomhelper.ZoomHelper
import com.bumptech.glide.Glide
import com.wafflestudio.wafflestagram.databinding.PagerItemImageBinding
import com.wafflestudio.wafflestagram.model.Photo

class EditPostAdapter : RecyclerView.Adapter<EditPostAdapter.ImageViewHolder>(){

    private var photos: List<Photo> = listOf()

    inner class ImageViewHolder(val binding: PagerItemImageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = PagerItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val data = photos[position]
        ZoomHelper.addZoomableView(holder.binding.imagePhoto)
        holder.apply {
            Glide.with(itemView.context).load(data.path).centerCrop().into(binding.imagePhoto)
        }
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    fun updateData(photos: List<Photo>){
        this.photos = photos
        notifyDataSetChanged()
    }
}