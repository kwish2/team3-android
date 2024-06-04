package com.wafflestudio.wafflestagram.ui.like

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wafflestudio.wafflestagram.R
import com.wafflestudio.wafflestagram.databinding.ItemLikeBinding
import com.wafflestudio.wafflestagram.databinding.ItemSearchBinding
import com.wafflestudio.wafflestagram.model.Follow
import com.wafflestudio.wafflestagram.model.User
import com.wafflestudio.wafflestagram.ui.detail.DetailUserActivity

class LikeAdapter(val likeInterface: LikeInterface) :RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var likes: List<User> = listOf()
    private var myFollowingList: List<Follow> = listOf()
    private lateinit var currUser: User

    inner class LikeViewHolder(val binding: ItemLikeBinding) : RecyclerView.ViewHolder(binding.root)
    inner class SearchViewHolder(val binding: ItemSearchBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            VIEW_TYPE_LIKE ->{
                val binding = ItemLikeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                LikeViewHolder(binding)
            }
            VIEW_TYPE_SEARCH ->{
                val binding = ItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                SearchViewHolder(binding)
            }
            else -> throw IllegalStateException("viewType must be 0 or 1")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is LikeViewHolder){
            val data = likes[position]
            holder.binding.apply {
                textUsername.text = data.username
                if(data.name.isNullOrBlank()){
                    textName.visibility = View.GONE
                }else{
                    textName.visibility = View.VISIBLE
                    textName.text = data.name
                }
                Glide.with(holder.itemView.context).load(data.profilePhotoURL).centerCrop().into(holder.binding.imageUserProfile)
                if(data == currUser){
                    buttonFollow.visibility = View.GONE
                }else{
                    buttonFollow.visibility = View.VISIBLE
                }
                if(myFollowingList.contains(Follow(user = data))){
                    buttonFollow.isSelected = true
                    buttonFollow.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
                    buttonFollow.text = "팔로잉"
                }else{
                    buttonFollow.isSelected = false
                    buttonFollow.setTextColor(Color.parseColor("#FFFFFFFF"))
                    buttonFollow.text = "팔로우"
                }

                buttonFollow.setOnClickListener{
                    if(buttonFollow.isSelected){
                        buttonFollow.isSelected = false
                        buttonFollow.setTextColor(Color.parseColor("#FFFFFFFF"))
                        buttonFollow.text = "팔로우"
                        //언팔로우
                        likeInterface.unfollow(data.id.toInt())
                    }else{
                        buttonFollow.isSelected = true
                        buttonFollow.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
                        buttonFollow.text = "팔로잉"
                        likeInterface.follow(data.id.toInt())
                    }
                }
            }
            holder.itemView.setOnClickListener {
                // 유저 정보
                val intent = Intent(holder.itemView.context, DetailUserActivity::class.java)
                intent.putExtra("id", data.id.toInt())
                ContextCompat.startActivity(holder.itemView.context,intent, null)
            }
        }
    }

    override fun getItemCount(): Int {
        return likes.size
    }

    override fun getItemViewType(position: Int): Int {
        return VIEW_TYPE_LIKE
    }

    fun updateData(likes : List<User>){
        this.likes = likes
        notifyDataSetChanged()
    }

    fun updateUser(user: User){
        this.currUser = user
    }

    fun updateFollowing(following : List<Follow>){
        this.myFollowingList = following
        notifyDataSetChanged()
    }

    companion object{
        const val VIEW_TYPE_LIKE = 0
        const val VIEW_TYPE_SEARCH = 1
    }

}