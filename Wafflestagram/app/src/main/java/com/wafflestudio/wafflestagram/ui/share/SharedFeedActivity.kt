package com.wafflestudio.wafflestagram.ui.share

import android.animation.ObjectAnimator
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.graphics.drawable.AnimatedVectorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import androidx.viewpager2.widget.ViewPager2
import com.aghajari.zoomhelper.ZoomHelper
import com.bumptech.glide.Glide
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.wafflestudio.wafflestagram.R
import com.wafflestudio.wafflestagram.databinding.ActivitySharedFeedBinding
import com.wafflestudio.wafflestagram.model.User
import com.wafflestudio.wafflestagram.ui.comment.CommentActivity
import com.wafflestudio.wafflestagram.ui.detail.DetailUserActivity
import com.wafflestudio.wafflestagram.ui.dialog.FeedBottomSheetFragment
import com.wafflestudio.wafflestagram.ui.dialog.UserTagBottomSheetFragment
import com.wafflestudio.wafflestagram.ui.like.LikeActivity
import com.wafflestudio.wafflestagram.ui.login.LoginActivity
import com.wafflestudio.wafflestagram.ui.main.MainActivity
import com.wafflestudio.wafflestagram.ui.main.SocialLoginSignOutUtils
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@AndroidEntryPoint
class SharedFeedActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySharedFeedBinding
    private val viewModel : SharedFeedViewModel by viewModels()
    private lateinit var sharedFeedAdapter: SharedFeedAdapter
    private lateinit var currUser : User
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    lateinit var animator: ObjectAnimator
    lateinit var animator2: ObjectAnimator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySharedFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        animator2 = ObjectAnimator.ofFloat(binding.numberIndicatorPager, "alpha", 1f, 0f).apply {
            duration = 2000
            startDelay = 2000
        }

        animator = ObjectAnimator.ofFloat(binding.buttonUserTag, "alpha", 1f, 0f).apply {
            duration = 2000
            startDelay = 2000
        }

        sharedFeedAdapter = SharedFeedAdapter()
        ZoomHelper.getInstance().maxScale = 3f

        viewModel.getMe()
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this){pendingDynamicLinkData ->
                var deepLink: Uri? = null
                if(pendingDynamicLinkData != null){
                    deepLink = pendingDynamicLinkData.link

                    val feedId = deepLink?.getQueryParameter("content")
                    if(feedId == null){
                        binding.layoutError.visibility = View.VISIBLE
                        binding.layoutFeed.visibility = View.GONE
                    }else{
                        viewModel.getFeedById(feedId!!.toInt())
                    }

                }

            }
            .addOnFailureListener(this){e ->
                Timber.e(e)
                Toast.makeText(this, "잘못된 링크입니다.", Toast.LENGTH_LONG).show()
                finish()
            }

        binding.buttonBack.setOnClickListener{
            finish()
            overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
        }

        viewModel.user.observe(this, {response->
            if(response.isSuccessful){
                currUser = response.body()!!
            }else if(response.code() == 401){
                Toast.makeText(this, "해당 게시물을 보려면 로그인을 먼저 해주세요.", Toast.LENGTH_LONG).show()
                // Remove Token(Sign Out)
                sharedPreferences.edit{
                    putString(MainActivity.TOKEN, "")
                    putInt(MainActivity.CURRENT_USER_ID, -1)
                    putBoolean(MainActivity.IS_LOGGED_IN, false)
                }
                SocialLoginSignOutUtils.signOut(this)
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        })

        viewModel.feed.observe(this, {response ->
            if(response.isSuccessful){
                val data = response.body()!!
                binding.apply {
                    buttonUsername.text = data.author!!.username
                    val spannable = SpannableStringBuilder(data.author.username)
                    spannable.setSpan(StyleSpan(Typeface.BOLD), 0, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannable.setSpan(object : ClickableSpan(){
                        override fun updateDrawState(ds: TextPaint) {
                            ds.isUnderlineText = false
                        }
                        override fun onClick(p0: View) {
                            // user page
                            val intent = Intent(this@SharedFeedActivity, DetailUserActivity::class.java)
                            intent.putExtra("id", data.author.id.toInt())
                            ContextCompat.startActivity(this@SharedFeedActivity,intent, null)
                        }
                    }, 0, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannable.append(" " + data.content)

                    textContent.linksClickable = true
                    textContent.isClickable = true
                    textContent.movementMethod = LinkMovementMethod.getInstance()
                    textContent.text = spannable

                    sharedFeedAdapter.setOnClickedListener(object : SharedFeedAdapter.ButtonClickListener{
                        override fun onClicked(id: Int, position: Int) {
                            if(buttonLike.isSelected){
                            }else{
                                buttonLike.isSelected = true
                                viewModel.like(data.id.toInt())
                                textLike.text = (textLike.text.toString().toInt()+1).toString()
                            }
                            buttonLike.startAnimation(AnimationUtils.loadAnimation(this@SharedFeedActivity, R.anim.heart))
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
                            onViewAppearForTag()
                        }
                    })

                    viewPagerImage.apply {
                        adapter = sharedFeedAdapter
                        sharedFeedAdapter.updateData(data.photos)
                    }
                    if(sharedFeedAdapter.itemCount == 1){
                        indicatorImage.visibility = View.GONE
                        numberIndicatorPager.visibility = View.GONE
                    }else{
                        indicatorImage.visibility = View.VISIBLE
                        numberIndicatorPager.visibility = View.VISIBLE
                    }
                    indicatorImage.setViewPager(viewPagerImage)
                    textTotal.text = sharedFeedAdapter.itemCount.toString()
                    viewPagerImage.registerOnPageChangeCallback(object  : ViewPager2.OnPageChangeCallback(){
                        override fun onPageSelected(position: Int) {
                            super.onPageSelected(position)
                            textCurrent.text = (position + 1).toString()
                            onViewAppear()
                            onViewAppearForTag()
                        }
                    })

                    textDateCreated.text = getBetween(data.createdAt!!.plusHours(9), ZonedDateTime.now(
                        ZoneId.of("Asia/Seoul")))
                    textLike.text = data.likeSum.toString()

                    buttonComment.setOnClickListener {
                        val intent = Intent(this@SharedFeedActivity, CommentActivity::class.java)
                        intent.putExtra("id", data.id.toInt())
                        ContextCompat.startActivity(this@SharedFeedActivity, intent, null)
                    }

                    layoutLike.setOnClickListener {
                        val intent = Intent(this@SharedFeedActivity, LikeActivity::class.java)
                        intent.putExtra("id", data.id.toInt())
                        ContextCompat.startActivity(this@SharedFeedActivity, intent, null)
                    }

                    buttonUserImage.setOnClickListener{
                        val intent = Intent(this@SharedFeedActivity, DetailUserActivity::class.java)
                        intent.putExtra("id", data.author.id.toInt())
                        ContextCompat.startActivity(this@SharedFeedActivity, intent, null)
                    }

                    buttonUsername.setOnClickListener {
                        val intent = Intent(this@SharedFeedActivity, DetailUserActivity::class.java)
                        intent.putExtra("id", data.author.id.toInt())
                        ContextCompat.startActivity(this@SharedFeedActivity, intent, null)
                    }

                    if(data.comments.isEmpty()){
                        layoutComment.visibility = View.GONE
                    }else{
                        layoutComment.visibility = View.VISIBLE
                    }

                    textCommentNumber.text = data.comments.size.toString()

                    layoutComment.setOnClickListener {
                        val intent = Intent(this@SharedFeedActivity, CommentActivity::class.java)
                        intent.putExtra("id", data.id.toInt())
                        ContextCompat.startActivity(this@SharedFeedActivity, intent, null)
                    }

                    buttonLike.isSelected = data.likes.contains(currUser)

                    buttonLike.setOnClickListener {
                        if(buttonLike.isSelected){
                            buttonLike.isSelected = false
                            viewModel.unlike(data.id.toInt())
                            textLike.text = (textLike.text.toString().toInt()-1).toString()
                        }else{
                            buttonLike.isSelected = true
                            viewModel.like(data.id.toInt())
                            textLike.text = (textLike.text.toString().toInt()+1).toString()
                        }
                        buttonLike.startAnimation(AnimationUtils.loadAnimation(this@SharedFeedActivity, R.anim.heart))
                    }

                    Glide.with(this@SharedFeedActivity).load(data.author.profilePhotoURL).centerCrop().into(buttonUserImage)

                    buttonItemMore.setOnClickListener {
                        val feedBottomSheetFragment = FeedBottomSheetFragment(this@SharedFeedActivity)
                        val bundle = Bundle()
                        bundle.putInt("feedId", data.id.toInt())
                        bundle.putInt("userId", data.author.id.toInt())
                        bundle.putInt("currUserId", currUser.id.toInt())
                        bundle.putInt("activity", 0)
                        feedBottomSheetFragment.arguments = bundle
                        feedBottomSheetFragment.setOnClickedListener(object : FeedBottomSheetFragment.ButtonClickListener{
                            override fun onClicked(id: Int, position: Int) {
                                viewModel.deleteFeed(id)
                            }
                        })
                        feedBottomSheetFragment.show(supportFragmentManager, FeedBottomSheetFragment.TAG)
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
                            userTagBottomSheetFragment.show(supportFragmentManager, UserTagBottomSheetFragment.TAG)
                        }
                    }
                }


            }else if(response.code() == 401){
                Toast.makeText(this, "해당 게시물을 보려면 로그인을 먼저 해주세요.", Toast.LENGTH_LONG).show()
                // Remove Token(Sign Out)
                sharedPreferences.edit{
                    putString(MainActivity.TOKEN, "")
                    putInt(MainActivity.CURRENT_USER_ID, -1)
                    putBoolean(MainActivity.IS_LOGGED_IN, false)
                }
                SocialLoginSignOutUtils.signOut(this)
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }else if(response.code() == 404){
                // Feed Not Found
                binding.layoutError.visibility = View.VISIBLE
                binding.layoutFeed.visibility = View.GONE
            }
        })
        viewModel.deleteResponse.observe(this, {response->
            if(response.isSuccessful){
                finish()
                overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(isFinishing){
            overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
        }
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

    fun onViewAppear(){
        binding.numberIndicatorPager.alpha = 1.0f
        fadeAwayIndicatorWithDelay()
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

    private fun fadeAwayIndicatorWithDelayForTag(){

        if(animator.isStarted){
            animator.cancel()
            animator.start()
        }else{
            animator.start()
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return ZoomHelper.getInstance().dispatchTouchEvent(ev!!, this) || super.dispatchTouchEvent(ev)
    }

    companion object{
        const val TOKEN = "token"
    }
}