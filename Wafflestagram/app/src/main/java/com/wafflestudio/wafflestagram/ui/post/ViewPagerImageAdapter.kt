package com.wafflestudio.wafflestagram.ui.post

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wafflestudio.wafflestagram.databinding.PagerItemImageBinding
import com.wafflestudio.wafflestagram.model.Photo

class ViewPagerImageAdapter : RecyclerView.Adapter<ViewPagerImageAdapter.ImageViewHolder>() {

    private var photos: List<Photo> = listOf()
    private var imageUris : List<String> = listOf()

    inner class ImageViewHolder(val binding: PagerItemImageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = PagerItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {

        if(position < photos.size){
            val data = photos[position]
            holder.apply {
                Glide.with(itemView.context).load(data.path).centerCrop().into(binding.imagePhoto)
            }
        }else{
            val data = imageUris[position]
            holder.binding.imagePhoto.setImageURI(Uri.parse(data))
        }


    }

    override fun getItemCount(): Int {
        return photos.size + imageUris.size
    }

    fun updatePhotos(photos: List<Photo>){
        this.photos = photos
        notifyDataSetChanged()
    }

    fun updateImageUris(imageUris : List<String>){
        this.imageUris = imageUris
        notifyDataSetChanged()
    }
}