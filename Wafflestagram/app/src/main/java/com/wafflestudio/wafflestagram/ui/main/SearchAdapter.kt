package com.wafflestudio.wafflestagram.ui.main

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
import com.wafflestudio.wafflestagram.model.Follow
import com.wafflestudio.wafflestagram.model.User
import com.wafflestudio.wafflestagram.ui.detail.DetailUserActivity

class SearchAdapter(val searchInterface: SearchInterface) :RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var users: List<User> = listOf()
    private var myFollowingList: List<Follow> = listOf()
    private lateinit var currUser: User

    inner class UserViewHolder(val binding: ItemLikeBinding) :RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemLikeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = users[position]
        if(holder is UserViewHolder){
            holder.binding.apply {
                textUsername.text = data.username
                if(data.name.isNullOrBlank()){
                    textName.visibility = View.GONE
                }else{
                    textName.visibility = View.VISIBLE
                    textName.text = data.name
                }
                Glide.with(holder.itemView.context).load(data.profilePhotoURL).centerCrop().into(holder.binding.imageUserProfile)
                //팔로우 확인 로직
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
                        searchInterface.unfollow(data.id.toInt())
                    }else{
                        buttonFollow.isSelected = true
                        buttonFollow.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
                        buttonFollow.text = "팔로잉"
                        searchInterface.follow(data.id.toInt())
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
        return users.size
    }

    fun updateData(users : List<User>){
        this.users = users
        notifyDataSetChanged()
    }

    fun deleteData(){
        this.users = listOf()
        notifyDataSetChanged()
    }


    fun updateFollowing(following : List<Follow>){
        this.myFollowingList = following
        notifyDataSetChanged()
    }

    fun updateUser(user: User){
        this.currUser = user
    }
}