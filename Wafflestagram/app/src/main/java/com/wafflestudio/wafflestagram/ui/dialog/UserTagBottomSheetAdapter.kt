package com.wafflestudio.wafflestagram.ui.dialog

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wafflestudio.wafflestagram.databinding.ItemLikeBinding
import com.wafflestudio.wafflestagram.model.User
import com.wafflestudio.wafflestagram.ui.detail.DetailUserActivity

class UserTagBottomSheetAdapter : RecyclerView.Adapter<UserTagBottomSheetAdapter.UserTagViewHolder>() {

    private var userTags: List<User> = listOf()

    inner class UserTagViewHolder(val binding: ItemLikeBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserTagViewHolder {
        val binding = ItemLikeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserTagViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserTagViewHolder, position: Int) {
        val data = userTags[position]

        holder.binding.apply {
            Glide.with(holder.itemView.context).load(data!!.profilePhotoURL).centerCrop().into(imageUserProfile)
            textUsername.text = data.username
            buttonFollow.visibility = View.GONE
            if(data.name.isNullOrBlank()){
                textName.visibility = View.GONE
            }else{
                textName.visibility = View.VISIBLE
                textName.text = data.name
            }
        }
        holder.itemView.setOnClickListener {
            holder.itemView.setOnClickListener {
                val intent = Intent(holder.itemView.context, DetailUserActivity::class.java)
                intent.putExtra("id", data!!.id.toInt())
                ContextCompat.startActivity(holder.itemView.context, intent, null)
            }
        }
    }

    override fun getItemCount(): Int {
        return userTags.size
    }

    fun updateData(userTags : List<User>){
        this.userTags = userTags
        notifyDataSetChanged()
    }

}