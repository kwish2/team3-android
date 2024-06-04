package com.wafflestudio.wafflestagram.ui.main

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.wafflestudio.wafflestagram.R
import com.wafflestudio.wafflestagram.databinding.ItemFeedBinding
import com.wafflestudio.wafflestagram.databinding.ItemLoadingBinding
import com.wafflestudio.wafflestagram.model.Feed
import com.wafflestudio.wafflestagram.model.User
import com.wafflestudio.wafflestagram.ui.comment.CommentActivity
import com.wafflestudio.wafflestagram.ui.detail.DetailUserActivity
import com.wafflestudio.wafflestagram.ui.dialog.FeedBottomSheetFragment
import com.wafflestudio.wafflestagram.ui.dialog.UserTagBottomSheetFragment
import com.wafflestudio.wafflestagram.ui.like.LikeActivity
import dagger.hilt.android.internal.managers.FragmentComponentManager
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class FeedAdapter(val feedInterface: FeedInterface) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var feeds: MutableList<Feed> = mutableListOf()
    private lateinit var currUser: User

    inner class LoadingViewHolder(val binding: ItemLoadingBinding) : RecyclerView.ViewHolder(binding.root)
    inner class FeedViewHolder(val binding: ItemFeedBinding) : RecyclerView.ViewHolder(binding.root){
        fun onViewAppear(){
            binding.numberIndicatorPager.alpha = 1.0f
            fadeAwayIndicatorWithDelay()
        }
        val animator2 = ObjectAnimator.ofFloat(binding.numberIndicatorPager, "alpha", 1f, 0f).apply {
            duration = 2000
            startDelay = 2000
        }
        private fun fadeAwayIndicatorWithDelay(){

            if(animator2.isStarted){
                animator2.cancel()
                animator2.start()
            }else{
                animator2.start()
            }
        }

        fun onViewAppearForTag(){
            binding.buttonUserTag.alpha = 1.0f
            fadeAwayIndicatorWithDelayForTag()
        }
        val animator = ObjectAnimator.ofFloat(binding.buttonUserTag, "alpha", 1f, 0f).apply {
            duration = 2000
            startDelay = 2000
        }
        private fun fadeAwayIndicatorWithDelayForTag(){

            if(animator.isStarted){
                animator.cancel()
                animator.start()
            }else{
                animator.start()
            }
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            VIEW_TYPE_FEED ->{
                val binding = ItemFeedBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                FeedViewHolder(binding)
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

        var imageAdapter = ViewPagerImageAdapter()

        when(holder){
            is FeedViewHolder ->{
                holder.binding.apply {
                    buttonUsername.text = data.author!!.username
                    val spannable = SpannableStringBuilder(data.author.username)
                    spannable.setSpan(StyleSpan(Typeface.BOLD), 0, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannable.setSpan(object : ClickableSpan(){
                        override fun updateDrawState(ds: TextPaint) {
                            ds.isUnderlineText = false
                        }
                        override fun onClick(p0: View) {
                            // user page
                            val intent = Intent(holder.itemView.context, DetailUserActivity::class.java)
                            intent.putExtra("id", data.author.id.toInt())
                            ContextCompat.startActivity(holder.itemView.context,intent, null)
                        }
                    }, 0, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannable.append(" " + data.content)

                    textContent.linksClickable = true
                    textContent.isClickable = true
                    textContent.movementMethod = LinkMovementMethod.getInstance()
                    textContent.text = spannable

                    imageAdapter.setOnClickedListener(object : ViewPagerImageAdapter.ButtonClickListener{
                        override fun onClicked(id: Int, position: Int) {
                            if(buttonLike.isSelected){
                            }else{
                                buttonLike.isSelected = true
                                feedInterface.like(data.id.toInt(), position)
                                textLike.text = (textLike.text.toString().toInt()+1).toString()
                            }
                            buttonLike.startAnimation(AnimationUtils.loadAnimation(holder.itemView.context, R.anim.heart))

                            imageViewHeart.alpha = 1.0f
                            if(imageViewHeart.drawable is AnimatedVectorDrawableCompat){
                                val avd = (imageViewHeart.drawable as AnimatedVectorDrawableCompat)
                                if(avd.isRunning){
                                    avd.stop()
                                }
                                avd.start()
                            }else if(imageViewHeart.drawable is AnimatedVectorDrawable){
                                val avd = (imageViewHeart.drawable as AnimatedVectorDrawable)
                                if(avd.isRunning){
                                    avd.stop()
                                }
                                avd.start()
                            }
                        }

                        override fun onClickedForTag() {
                            holder.onViewAppearForTag()
                        }
                    })
                    viewPagerImage.apply {
                        adapter = imageAdapter
                        imageAdapter.updateData(data.photos)
                    }
                    if(imageAdapter.itemCount == 1){
                        indicatorImage.visibility = View.GONE
                        numberIndicatorPager.visibility = View.GONE
                    }else{
                        indicatorImage.visibility = View.VISIBLE
                        numberIndicatorPager.visibility = View.VISIBLE
                    }
                    indicatorImage.setViewPager(viewPagerImage)
                    textTotal.text = imageAdapter.itemCount.toString()
                    viewPagerImage.registerOnPageChangeCallback(object  : ViewPager2.OnPageChangeCallback(){
                        override fun onPageSelected(position: Int) {
                            super.onPageSelected(position)
                            textCurrent.text = (position + 1).toString()
                            holder.onViewAppear()
                            holder.onViewAppearForTag()
                        }
                    })


                    textDateCreated.text = getBetween(data.createdAt!!.plusHours(9), ZonedDateTime.now(
                        ZoneId.of("Asia/Seoul")))
                    textLike.text = data.likeSum.toString()

                    buttonComment.setOnClickListener {
                        val intent = Intent(holder.itemView.context, CommentActivity::class.java)
                        intent.putExtra("id", data.id.toInt())
                        ContextCompat.startActivity(holder.itemView.context, intent, null)
                    }

                    layoutLike.setOnClickListener {
                        val intent = Intent(holder.itemView.context, LikeActivity::class.java)
                        intent.putExtra("id", data.id.toInt())
                        ContextCompat.startActivity(holder.itemView.context, intent, null)
                    }

                    buttonUserImage.setOnClickListener{
                        val intent = Intent(holder.itemView.context, DetailUserActivity::class.java)
                        intent.putExtra("id", data.author.id.toInt())
                        ContextCompat.startActivity(holder.itemView.context, intent, null)
                    }

                    buttonUsername.setOnClickListener {
                        val intent = Intent(holder.itemView.context, DetailUserActivity::class.java)
                        intent.putExtra("id", data.author.id.toInt())
                        ContextCompat.startActivity(holder.itemView.context, intent, null)
                    }

                    if(data.comments.isEmpty()){
                        layoutComment.visibility = View.GONE
                    }else{
                        layoutComment.visibility = View.VISIBLE
                    }

                    textCommentNumber.text = data.comments.size.toString()

                    layoutComment.setOnClickListener {
                        val intent = Intent(holder.itemView.context, CommentActivity::class.java)
                        intent.putExtra("id", data.id.toInt())
                        ContextCompat.startActivity(holder.itemView.context, intent, null)
                    }

                    buttonLike.isSelected = data.likes.contains(currUser)

                    buttonLike.setOnClickListener {
                        if(buttonLike.isSelected){
                            buttonLike.isSelected = false
                            feedInterface.unlike(data.id.toInt(), position)
                            textLike.text = (textLike.text.toString().toInt()-1).toString()
                        }else{
                            buttonLike.isSelected = true
                            feedInterface.like(data.id.toInt(), position)
                            textLike.text = (textLike.text.toString().toInt()+1).toString()
                        }
                        buttonLike.startAnimation(AnimationUtils.loadAnimation(holder.itemView.context, R.anim.heart))
                    }

                    Glide.with(holder.itemView.context).load(data.author.profilePhotoURL).centerCrop().into(holder.binding.buttonUserImage)

                    buttonItemMore.setOnClickListener {
                        val feedBottomSheetFragment = FeedBottomSheetFragment(holder.itemView.context)
                        val bundle = Bundle()
                        bundle.putInt("feedId", data.id.toInt())
                        bundle.putInt("userId", data.author.id.toInt())
                        bundle.putInt("currUserId", currUser.id.toInt())
                        bundle.putInt("position", position)
                        bundle.putInt("activity", 0)
                        feedBottomSheetFragment.arguments = bundle
                        feedBottomSheetFragment.setOnClickedListener(object : FeedBottomSheetFragment.ButtonClickListener{
                            override fun onClicked(id: Int, position: Int) {
                                feedInterface.deleteFeed(id, position)
                            }
                        })
                        feedBottomSheetFragment.show((FragmentComponentManager.findActivity(holder.itemView.context) as MainActivity).supportFragmentManager, FeedBottomSheetFragment.TAG)
                    }

                    if(data.userTags.isEmpty()){
                        buttonUserTag.visibility = View.GONE
                    }else{
                        buttonUserTag.visibility = View.VISIBLE
                    }

                    buttonUserTag.setOnClickListener {
                        if(buttonUserTag.alpha > 0){
                            val userTagBottomSheetFragment = UserTagBottomSheetFragment()
                            val bundle = Bundle()
                            bundle.putSerializable("userTags", data.userTags.toTypedArray())
                            userTagBottomSheetFragment.arguments = bundle
                            userTagBottomSheetFragment.show((FragmentComponentManager.findActivity(holder.itemView.context) as MainActivity).supportFragmentManager, UserTagBottomSheetFragment.TAG)
                        }
                    }
                }

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

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        when(holder){
            is FeedViewHolder ->{
                holder.onViewAppear()
            }
        }
        super.onViewAttachedToWindow(holder)
    }

    fun updateUser(user: User){
        this.currUser = user
        notifyDataSetChanged()
    }

    fun changeData(feed: Feed, position: Int){
        this.feeds[position] = feed
        notifyItemChanged(position)
    }

    fun updateData(feeds: MutableList<Feed>){
        this.feeds = feeds
    }

    fun deleteData(position: Int){
        this.feeds.removeAt(position)
        notifyItemRemoved(position)
    }

    fun addData(feeds: MutableList<Feed>){
        val size = this.feeds.size
        this.feeds.addAll(feeds)
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

    private fun getBetween(time: LocalDateTime, time2: ZonedDateTime) : String{
        for(timeFormat in listOf(ChronoUnit.SECONDS, ChronoUnit.MINUTES, ChronoUnit.HOURS, ChronoUnit.DAYS)){
            val between = timeFormat.between(time, time2)
            when(timeFormat){
                ChronoUnit.SECONDS -> if(between < 60) return between.toString() + "초 전"
                ChronoUnit.MINUTES -> if(between < 60) return between.toString() + "분 전"
                ChronoUnit.HOURS -> if(between < 24) return between.toString() + "시간 전"
                ChronoUnit.DAYS -> if(between < 7) return between.toString() + "일 전"
                else -> {}
            }
        }
        return time.format(DateTimeFormatter.ofPattern( "MM월 dd일"))
    }

    companion object{
        const val VIEW_TYPE_FEED = 0
        const val VIEW_TYPE_LOADING = 1
        const val NULL = -1
    }
}