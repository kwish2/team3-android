package com.wafflestudio.wafflestagram.ui.comment

import android.content.Intent
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.wafflestudio.wafflestagram.databinding.ItemCommentBinding
import com.wafflestudio.wafflestagram.databinding.ItemContentBinding
import com.wafflestudio.wafflestagram.databinding.ItemReplyBinding
import com.wafflestudio.wafflestagram.model.Comment
import com.wafflestudio.wafflestagram.model.Feed
import com.wafflestudio.wafflestagram.model.User
import com.wafflestudio.wafflestagram.ui.detail.DetailUserActivity
import pokercc.android.expandablerecyclerview.ExpandableAdapter
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class CommentAdapter(val commentInterface: CommentInterface) : ExpandableAdapter<ExpandableAdapter.ViewHolder>() {

    private var comments: List<Comment> = listOf()
    private var feed: Feed? = null
    private lateinit var currUser: User

    inner class CommentViewHolder(val binding: ItemCommentBinding) : ExpandableAdapter.ViewHolder(binding.root)
    inner class ContentViewHolder(val binding: ItemContentBinding) : ExpandableAdapter.ViewHolder(binding.root)
    inner class ReplyViewHolder(val binding: ItemReplyBinding) : ExpandableAdapter.ViewHolder(binding.root)

    /*
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            VIEW_TYPE_CONTENT ->{
                val binding = ItemContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ContentViewHolder(binding)
            }

            VIEW_TYPE_COMMENT ->{
                val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CommentViewHolder(binding)
            }
            else -> throw IllegalStateException("viewType must be 0 or 1")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(position == 0){
            val data = feed!!
            if(holder is ContentViewHolder){
                holder.binding.apply {
                    val spannable = SpannableStringBuilder(data.author?.username)
                    spannable.setSpan(StyleSpan(Typeface.BOLD), 0, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannable.setSpan(object : ClickableSpan(){
                        override fun updateDrawState(ds: TextPaint) {
                            ds.isUnderlineText = false
                        }
                        override fun onClick(p0: View) {
                            // user page
                            val intent = Intent(holder.itemView.context, DetailUserActivity::class.java)
                            intent.putExtra("id", data.author?.id?.toInt())
                            ContextCompat.startActivity(holder.itemView.context,intent, null)
                        }
                    }, 0, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannable.append(" " + data.content)
                    textContent.linksClickable = true
                    textContent.isClickable = true
                    textContent.movementMethod = LinkMovementMethod.getInstance()
                    textContent.text = spannable

                    textTime.text = getBetween(data.createdAt!!.plusHours(9), ZonedDateTime.now(ZoneId.of("Asia/Seoul")))

                    //프로필 사진
                    Glide.with(holder.itemView.context).load(data.author?.profilePhotoURL).centerCrop().into(holder.binding.imageUserProfile)

                    imageUserProfile.setOnClickListener {
                        val intent = Intent(holder.itemView.context, DetailUserActivity::class.java)
                        intent.putExtra("id", data.author?.id?.toInt())
                        ContextCompat.startActivity(holder.itemView.context,intent, null)
                    }
                }
            }
        }else{
            val data = comments[position-1]
            if(holder is CommentViewHolder){
                holder.binding.apply {
                    val spannable = SpannableStringBuilder(data.writer.username)
                    spannable.setSpan(StyleSpan(Typeface.BOLD), 0, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannable.setSpan(object : ClickableSpan(){
                        override fun updateDrawState(ds: TextPaint) {
                            ds.isUnderlineText = false
                        }
                        override fun onClick(p0: View) {
                            // user page
                            val intent = Intent(holder.itemView.context, DetailUserActivity::class.java)
                            intent.putExtra("id", data.writer.id.toInt())
                            ContextCompat.startActivity(holder.itemView.context,intent, null)
                        }
                    }, 0, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannable.append(" " + data.text)
                    textContent.linksClickable = true
                    textContent.isClickable = true
                    textContent.movementMethod = LinkMovementMethod.getInstance()
                    textContent.text = spannable

                    textTime.text = getBetween(data.createdAt!!.plusHours(9), ZonedDateTime.now(ZoneId.of("Asia/Seoul")))
                    //textReplyNumber.text = data.replies.size.toString()

                    imageUserProfile.setOnClickListener {
                        val intent = Intent(holder.itemView.context, DetailUserActivity::class.java)
                        intent.putExtra("id", data.writer.id.toInt())
                        ContextCompat.startActivity(holder.itemView.context,intent, null)
                    }
                }
                Glide.with(holder.itemView.context).load(data.writer.profilePhotoURL).centerCrop().into(holder.binding.imageUserProfile)
            }
        }



    }

    override fun getItemCount(): Int {
        return if(feed == null){
            0
        }else{
            comments.size + 1
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if(position == 0){
            VIEW_TYPE_CONTENT
        }else{
            VIEW_TYPE_COMMENT
        }
    }

     */

    fun updateData(feed: Feed){
        this.feed = feed
        this.comments = feed.comments
        this.notifyDataSetChanged()
    }

    fun updateUser(user: User){
        this.currUser = user
        this.notifyDataSetChanged()
    }

    private fun getBetween(time: LocalDateTime, time2: ZonedDateTime) : String{
        for(timeFormat in listOf(ChronoUnit.SECONDS, ChronoUnit.MINUTES, ChronoUnit.HOURS, ChronoUnit.DAYS)){
            val between = timeFormat.between(time, time2)
            when(timeFormat){
                ChronoUnit.SECONDS -> if(between < 60) return between.toString() + "초"
                ChronoUnit.MINUTES -> if(between < 60) return between.toString() + "분"
                ChronoUnit.HOURS -> if(between < 24) return between.toString() + "시간"
                ChronoUnit.DAYS -> if(between < 7) return between.toString() + "일"
                else -> {}
            }
        }
        return time.format(DateTimeFormatter.ofPattern( "MM월 dd일"))
    }

     companion object{
         const val VIEW_TYPE_CONTENT = 2
         const val VIEW_TYPE_COMMENT = 1
         const val VIEW_TYPE_REPLY = 0
     }

    override fun onGroupViewHolderExpandChange(
        holder: ViewHolder,
        groupPosition: Int,
        animDuration: Long,
        expand: Boolean
    ) {
        if(holder is CommentViewHolder){
            holder.binding.apply {
                if(expand){
                    buttonViewReply.visibility = View.GONE
                    buttonViewReplyClose.visibility = View.VISIBLE
                }else{
                    buttonViewReply.visibility = View.VISIBLE
                    buttonViewReplyClose.visibility = View.GONE
                }

            }
        }
    }

    override fun getChildCount(groupPosition: Int): Int {
        return comments[groupPosition-1].replies.size
    }

    override fun getGroupCount(): Int {
        return if(feed == null){
            0
        }else{
            comments.size + 1
        }
    }

    override fun onBindChildViewHolder(
        holder: ViewHolder,
        groupPosition: Int,
        childPosition: Int,
        payloads: List<Any>
    ) {
        val data = comments[groupPosition-1].replies[childPosition]
        if(holder is ReplyViewHolder){
            holder.binding.apply {
                val spannable = SpannableStringBuilder(data.writer.username)
                spannable.setSpan(
                    StyleSpan(Typeface.BOLD),
                    0,
                    spannable.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannable.setSpan(object : ClickableSpan() {
                    override fun updateDrawState(ds: TextPaint) {
                        ds.isUnderlineText = false
                    }

                    override fun onClick(p0: View) {
                        // user page
                        val intent =
                            Intent(holder.itemView.context, DetailUserActivity::class.java)
                        intent.putExtra("id", data.writer.id.toInt())
                        ContextCompat.startActivity(holder.itemView.context, intent, null)
                    }
                }, 0, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannable.append(" " + data.text)
                textContent.linksClickable = true
                textContent.isClickable = true
                textContent.movementMethod = LinkMovementMethod.getInstance()
                textContent.text = spannable

                textTime.text = getBetween(
                    data.createdAt!!.plusHours(9),
                    ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
                )
                //textReplyNumber.text = data.replies.size.toString()

                imageUserProfile.setOnClickListener {
                    val intent = Intent(holder.itemView.context, DetailUserActivity::class.java)
                    intent.putExtra("id", data.writer.id.toInt())
                    ContextCompat.startActivity(holder.itemView.context, intent, null)
                }

                if(data.writer == currUser){
                    buttonDelete.visibility = View.VISIBLE
                    buttonDelete.isEnabled = true
                }else{
                    buttonDelete.visibility = View.INVISIBLE
                    buttonDelete.isEnabled = false
                }
                buttonDelete.setOnClickListener {
                    commentInterface.deleteReply(data.id.toInt(), groupPosition)
                }
            }
            Glide.with(holder.itemView.context).load(data.writer.profilePhotoURL).centerCrop()
                .into(holder.binding.imageUserProfile)
        }

    }

    override fun onBindGroupViewHolder(
        holder: ViewHolder,
        groupPosition: Int,
        expand: Boolean,
        payloads: List<Any>
    ) {
        if (groupPosition == 0) {
            val data = feed!!
            if (holder is ContentViewHolder) {
                holder.binding.apply {
                    val spannable = SpannableStringBuilder(data.author?.username)
                    spannable.setSpan(
                        StyleSpan(Typeface.BOLD),
                        0,
                        spannable.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    spannable.setSpan(object : ClickableSpan() {
                        override fun updateDrawState(ds: TextPaint) {
                            ds.isUnderlineText = false
                        }

                        override fun onClick(p0: View) {
                            // user page
                            val intent =
                                Intent(holder.itemView.context, DetailUserActivity::class.java)
                            intent.putExtra("id", data.author?.id?.toInt())
                            ContextCompat.startActivity(holder.itemView.context, intent, null)
                        }
                    }, 0, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannable.append(" " + data.content)
                    textContent.linksClickable = true
                    textContent.isClickable = true
                    textContent.movementMethod = LinkMovementMethod.getInstance()
                    textContent.text = spannable

                    textTime.text = getBetween(
                        data.createdAt!!.plusHours(9), ZonedDateTime.now(
                            ZoneId.of("Asia/Seoul")
                        )
                    )

                    //프로필 사진
                    Glide.with(holder.itemView.context).load(data.author?.profilePhotoURL)
                        .centerCrop().into(holder.binding.imageUserProfile)

                    imageUserProfile.setOnClickListener {
                        val intent = Intent(holder.itemView.context, DetailUserActivity::class.java)
                        intent.putExtra("id", data.author?.id?.toInt())
                        ContextCompat.startActivity(holder.itemView.context, intent, null)
                    }
                    // library
                    holder.itemView.setOnClickListener {
                    }
                }
            }
        } else {
            val data = comments[groupPosition - 1]
            if (holder is CommentViewHolder) {
                holder.binding.apply {
                    val spannable = SpannableStringBuilder(data.writer.username)
                    spannable.setSpan(
                        StyleSpan(Typeface.BOLD),
                        0,
                        spannable.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    spannable.setSpan(object : ClickableSpan() {
                        override fun updateDrawState(ds: TextPaint) {
                            ds.isUnderlineText = false
                        }

                        override fun onClick(p0: View) {
                            // user page
                            val intent =
                                Intent(holder.itemView.context, DetailUserActivity::class.java)
                            intent.putExtra("id", data.writer.id.toInt())
                            ContextCompat.startActivity(holder.itemView.context, intent, null)
                        }
                    }, 0, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannable.append(" " + data.text)
                    textContent.linksClickable = true
                    textContent.isClickable = true
                    textContent.movementMethod = LinkMovementMethod.getInstance()
                    textContent.text = spannable

                    textTime.text = getBetween(
                        data.createdAt!!.plusHours(9),
                        ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
                    )
                    //textReplyNumber.text = data.replies.size.toString()

                    imageUserProfile.setOnClickListener {
                        val intent = Intent(holder.itemView.context, DetailUserActivity::class.java)
                        intent.putExtra("id", data.writer.id.toInt())
                        ContextCompat.startActivity(holder.itemView.context, intent, null)
                    }



                    if(data.replies.isEmpty()){
                        buttonViewReply.visibility = View.GONE
                        buttonViewReplyClose.visibility = View.GONE
                    }else{
                        if(expand){
                            buttonViewReply.visibility = View.GONE
                            buttonViewReplyClose.visibility = View.VISIBLE
                        }else{
                            buttonViewReply.visibility = View.VISIBLE
                            buttonViewReplyClose.visibility = View.GONE
                        }
                    }

                    buttonViewReply.setOnClickListener {
                        expandGroup(groupPosition, false)
                    }

                    buttonViewReplyClose.setOnClickListener {
                        collapseGroup(groupPosition, false)
                    }

                    if(data.writer == currUser){
                        buttonDelete.visibility = View.VISIBLE
                        buttonDelete.isEnabled = true
                    }else{
                        buttonDelete.visibility = View.INVISIBLE
                        buttonDelete.isEnabled = false
                    }
                    buttonDelete.setOnClickListener {
                        commentInterface.deleteComment(data.id.toInt(), groupPosition)
                    }

                    textReply.setOnClickListener {
                        commentInterface.addReply(data, groupPosition)
                    }

                    textReplyNumber.text = data.replies.size.toString()

                }
                Glide.with(holder.itemView.context).load(data.writer.profilePhotoURL).centerCrop()
                    .into(holder.binding.imageUserProfile)

                holder.itemView.setOnClickListener {
                }
            }
        }
    }

    override fun onCreateChildViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemReplyBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ReplyViewHolder(binding)
    }

    override fun onCreateGroupViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        return when(viewType){
            VIEW_TYPE_CONTENT ->{
                val binding = ItemContentBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
                ContentViewHolder(binding)
            }

            VIEW_TYPE_COMMENT ->{
                val binding = ItemCommentBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
                CommentViewHolder(binding)
            }
            else -> throw IllegalStateException("viewType must be 0 or 1")
        }
    }

    override fun getGroupItemViewType(groupPosition: Int): Int {
        return if(groupPosition == 0){
            VIEW_TYPE_CONTENT
        }else{
            VIEW_TYPE_COMMENT
        }
    }

    override fun getChildItemViewType(groupPosition: Int, childPosition: Int): Int {
        return VIEW_TYPE_REPLY
    }

}