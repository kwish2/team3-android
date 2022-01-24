package com.wafflestudio.wafflestagram.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wafflestudio.wafflestagram.databinding.ItemLoadingBinding
import com.wafflestudio.wafflestagram.databinding.ItemUserPhotoBinding
import com.wafflestudio.wafflestagram.model.Feed
import timber.log.Timber

// OnSelectClickListener의 param: feed의 id
class UserPhotoAdapter(private val onSelectClickListener: (Int) -> (Unit)) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var photos: MutableList<Feed> = mutableListOf()
    private lateinit var context: Context

    inner class UserPhotoViewHolder(val binding: ItemUserPhotoBinding): RecyclerView.ViewHolder(binding.root)
    inner class LoadingViewHolder(val binding: ItemLoadingBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            VIEW_TYPE_FEED ->{
                val binding = ItemUserPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                UserPhotoViewHolder(binding)
            }
            VIEW_TYPE_LOADING ->{
                val binding = ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                LoadingViewHolder(binding)
            }
            else -> throw IllegalStateException("viewType must be 0 or 1")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = photos[position]

        if(holder is UserPhotoViewHolder){
            // 각 feed의 첫 사진 띄우기
            try {
                Glide.with(holder.itemView.context)
                    .load(data.photos[0].path)
                    .centerCrop()
                    .into(holder.binding.userPhoto)
            } catch(e: Throwable) {
                Timber.e(e)
            }
            holder.itemView.setOnClickListener{
                onSelectClickListener(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    override fun getItemViewType(position: Int): Int {
        return when(photos[position].id.toInt()){
            NULL -> VIEW_TYPE_LOADING
            else -> VIEW_TYPE_FEED
        }
    }

    fun updatePhotos(photos: List<Feed>, context: Context){
        this.photos = photos.toMutableList()
        this.context = context
        this.notifyDataSetChanged()
    }

    fun addPhotos(photos: List<Feed>){
        this.photos.addAll(photos.toMutableList())
    }

    fun clearPhotos(){
        this.photos = mutableListOf()
        notifyDataSetChanged()
    }

    fun deleteLoading(){
        this.photos.removeAt(photos.lastIndex)
    }

    fun addLoading(){
        this.photos.add(Feed((NULL.toLong())))
    }

    companion object{
        const val VIEW_TYPE_FEED = 0
        const val VIEW_TYPE_LOADING = 1
        const val NULL = -1
    }
}