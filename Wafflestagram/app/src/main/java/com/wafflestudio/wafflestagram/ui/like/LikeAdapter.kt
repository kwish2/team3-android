package com.wafflestudio.wafflestagram.ui.like

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.wafflestudio.wafflestagram.databinding.ItemLikeBinding
import com.wafflestudio.wafflestagram.databinding.ItemSearchBinding
import com.wafflestudio.wafflestagram.model.Like
import com.wafflestudio.wafflestagram.model.User
import com.wafflestudio.wafflestagram.ui.detail.DetailUserActivity

class LikeAdapter :RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var likes: List<User> = listOf()
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
        if(position > 0 && holder is LikeViewHolder){
            val data = likes[position - 1]
            holder.binding.apply {
                textUsername.text = data.username

                //팔로우 확인 로직
                if(LikeActivity().checkFollowing(data.id.toInt()).body()!!){
                    buttonFollow.isSelected = true
                }else{
                    buttonFollow.isSelected = false
                }

                buttonFollow.setOnClickListener{
                    if(buttonFollow.isSelected){
                        buttonFollow.isSelected = false
                        buttonFollow.setTextColor(Color.parseColor("#FFFFFFFF"))
                        buttonFollow.text = "팔로우"
                        //언팔로우
                        LikeActivity().unfollow(data.id.toInt())
                    }else{
                        buttonFollow.isSelected = true
                        buttonFollow.setTextColor(Color.parseColor("#FF000000"))
                        buttonFollow.text = "팔로잉"
                        LikeActivity().follow(data.id.toInt())
                    }
                }
            }
            holder.itemView.setOnClickListener {
                // 유저 정보
                val intent = Intent(holder.itemView.context, DetailUserActivity::class.java)
                intent.putExtra("id", data.id)
                ContextCompat.startActivity(holder.itemView.context,intent, null)
            }
        }
    }

    override fun getItemCount(): Int {
        return likes.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == 0){
            VIEW_TYPE_SEARCH
        }else{
            VIEW_TYPE_LIKE
        }
    }

    fun updateData(likes : List<User>){
        this.likes = likes
        notifyDataSetChanged()
    }

    fun updateUser(user: User){
        this.currUser = user
    }

    companion object{
        const val VIEW_TYPE_LIKE = 0
        const val VIEW_TYPE_SEARCH = 1
    }

}