package com.wafflestudio.wafflestagram.ui.post

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wafflestudio.wafflestagram.databinding.ItemLikeBinding
import com.wafflestudio.wafflestagram.model.User

class SearchAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private var users: List<User> = listOf()

    inner class UserViewHolder(val binding: ItemLikeBinding) : RecyclerView.ViewHolder(binding.root)
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
                buttonFollow.visibility = View.GONE
            }
            holder.itemView.setOnClickListener {
                // 유저 선택
                onClickedListener.onClicked(data)
            }

        }
    }

    interface ButtonClickListener{
        fun onClicked(user: User)
    }

    private lateinit var onClickedListener: ButtonClickListener

    fun setOnClickedListener(listener: ButtonClickListener){
        onClickedListener = listener
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


}