package com.wafflestudio.wafflestagram.ui.post

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wafflestudio.wafflestagram.databinding.ItemUserTagBinding
import com.wafflestudio.wafflestagram.model.User

class AddUserTagAdapter :RecyclerView.Adapter<AddUserTagAdapter.UserTagViewHolder>(){

    private var users : List<User> = listOf()

    inner class UserTagViewHolder(val binding: ItemUserTagBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserTagViewHolder {
        val binding = ItemUserTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserTagViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserTagViewHolder, position: Int) {
        val data = users[position]

        holder.apply {
            Glide.with(itemView.context).load(data.profilePhotoURL).centerCrop().into(binding.imageUserProfile)
            binding.textUsername.text = data.username

            binding.buttonCancel.setOnClickListener {
                onClickedListener.onClicked(position)
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

    interface ButtonClickListener{
        fun onClicked(position: Int)
    }

    private lateinit var onClickedListener: ButtonClickListener

    fun setOnClickedListener(listener: ButtonClickListener){
        onClickedListener = listener
    }
}