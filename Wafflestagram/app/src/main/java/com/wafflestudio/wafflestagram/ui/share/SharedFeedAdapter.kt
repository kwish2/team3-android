package com.wafflestudio.wafflestagram.ui.share

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aghajari.zoomhelper.ZoomHelper
import com.bumptech.glide.Glide
import com.wafflestudio.wafflestagram.databinding.PagerItemImageBinding
import com.wafflestudio.wafflestagram.model.Photo

class SharedFeedAdapter: RecyclerView.Adapter<SharedFeedAdapter.ImageViewHolder>() {

    private var photos: List<Photo> = listOf()

    inner class ImageViewHolder(val binding: PagerItemImageBinding): RecyclerView.ViewHolder(binding.root)

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
        var doubleClickTime = 0L
        holder.itemView.setOnClickListener {
            if(System.currentTimeMillis() - doubleClickTime < 500){
                onClickedListener.onClicked(0,0)
                doubleClickTime = 0L
            }else{
                doubleClickTime = System.currentTimeMillis()
            }
        }
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    fun updateData(photos: List<Photo>){
        this.photos = photos
    }

    interface ButtonClickListener{
        fun onClicked(id: Int, position: Int)
    }

    private lateinit var onClickedListener: ButtonClickListener

    fun setOnClickedListener(listener: ButtonClickListener){
        onClickedListener = listener
    }
}