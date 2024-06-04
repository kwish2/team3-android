package com.wafflestudio.wafflestagram.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wafflestudio.wafflestagram.databinding.ItemLoadingBinding
import com.wafflestudio.wafflestagram.databinding.ItemUserFeedBinding
import com.wafflestudio.wafflestagram.model.Feed

// OnSelectClickListener의 param: feed의 id
class UserFeedAdapter(private val onSelectClickListener: (Int) -> (Unit)) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var feeds: MutableList<Feed> = mutableListOf()
    private lateinit var context: Context

    inner class UserFeedViewHolder(val binding: ItemUserFeedBinding): RecyclerView.ViewHolder(binding.root)
    inner class LoadingViewHolder(val binding: ItemLoadingBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            VIEW_TYPE_FEED ->{
                val binding = ItemUserFeedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                UserFeedViewHolder(binding)
            }
            VIEW_TYPE_LOADING ->{
                val binding = ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                LoadingViewHolder(binding)
            }
            else -> throw IllegalStateException("viewType must be 0 or 1")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = feeds[position]

        if(holder is UserFeedViewHolder){
            holder.binding.apply {
                if(!data.photos.isNullOrEmpty()){
                    Glide.with(holder.itemView.context).load(data.photos.get(0).path).centerCrop().into(userPhoto)
                }
            }
            holder.itemView.setOnClickListener{
                onSelectClickListener(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return feeds.size
    }

    override fun getItemViewType(position: Int): Int {
        return when(feeds[position].id.toInt()){
            NULL -> VIEW_TYPE_LOADING
            else -> VIEW_TYPE_FEED
        }
    }

    fun updateData(feeds: MutableList<Feed>, context: Context){
        this.feeds = feeds
        this.context = context
        this.notifyDataSetChanged()
    }

    fun addData(feeds: MutableList<Feed>){
        this.feeds.addAll(feeds)
        this.notifyDataSetChanged()
    }

    fun clearData(){
        this.feeds = mutableListOf()
        notifyDataSetChanged()
    }

    fun deleteLoading(){
        this.feeds.removeAt(feeds.lastIndex)
    }

    fun addLoading(){
        this.feeds.add(Feed((NULL.toLong())))
    }

    companion object{
        const val VIEW_TYPE_FEED = 0
        const val VIEW_TYPE_LOADING = 1
        const val NULL = -1
    }
}