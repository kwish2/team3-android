package com.wafflestudio.wafflestagram.ui.follow

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

class FollowAdapter(val followInterface: FollowInterface) :RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var follows: List<Follow> = listOf()
    private var myFollowingList: List<Follow> = listOf()
    private lateinit var currUser: User

    inner class FollowViewHolder(val binding: ItemLikeBinding) : RecyclerView.ViewHolder(binding.root)
    inner class SearchViewHolder(val binding: ItemSearchBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            VIEW_TYPE_LIKE ->{
                val binding = ItemLikeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                FollowViewHolder(binding)
            }
            VIEW_TYPE_SEARCH ->{
                val binding = ItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                SearchViewHolder(binding)
            }
            else -> throw IllegalStateException("viewType must be 0 or 1")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is FollowViewHolder){
            val data = follows[position]
            holder.binding.apply {
                textUsername.text = data.user.username
                if(data.user.name.isNullOrBlank()){
                    textName.visibility = View.GONE
                }else{
                    textName.visibility = View.VISIBLE
                    textName.text = data.user.name
                }
                //팔로우 확인 로직
                Glide.with(holder.itemView.context).load(data.user.profilePhotoURL).centerCrop().into(holder.binding.imageUserProfile)
                if(data.user == currUser){
                    buttonFollow.visibility = View.GONE
                }else{
                    buttonFollow.visibility = View.VISIBLE
                }

                if(myFollowingList.contains(data)){
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
                        followInterface.unfollow(data.user.id.toInt())
                    }else{
                        buttonFollow.isSelected = true
                        buttonFollow.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
                        buttonFollow.text = "팔로잉"
                        followInterface.follow(data.user.id.toInt())
                    }
                }
            }
            holder.itemView.setOnClickListener {
                // 유저 정보
                val intent = Intent(holder.itemView.context, DetailUserActivity::class.java)
                intent.putExtra("id", data.user.id.toInt())
                ContextCompat.startActivity(holder.itemView.context,intent, null)
            }
        }
    }

    override fun getItemCount(): Int {
        return follows.size
    }

    override fun getItemViewType(position: Int): Int {
        return VIEW_TYPE_LIKE
    }

    fun updateData(follows : List<Follow>){
        this.follows = follows
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