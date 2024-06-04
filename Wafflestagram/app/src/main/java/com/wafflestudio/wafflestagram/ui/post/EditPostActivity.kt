package com.wafflestudio.wafflestagram.ui.post

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.aghajari.zoomhelper.ZoomHelper
import com.bumptech.glide.Glide
import com.wafflestudio.wafflestagram.R
import com.wafflestudio.wafflestagram.databinding.ActivityEditPostBinding
import com.wafflestudio.wafflestagram.model.Photo
import com.wafflestudio.wafflestagram.model.User
import com.wafflestudio.wafflestagram.model.UserTag
import com.wafflestudio.wafflestagram.network.UserService
import com.wafflestudio.wafflestagram.network.dto.EditPostRequest
import com.wafflestudio.wafflestagram.ui.detail.DetailFeedActivity
import com.wafflestudio.wafflestagram.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@AndroidEntryPoint
class EditPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditPostBinding
    private val viewModel: EditPostViewModel by viewModels()
    private lateinit var editPostAdapter: EditPostAdapter
    private var userTags: List<User> = listOf()
    private var photos: List<Photo> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit)

        ZoomHelper.getInstance().maxScale = 3f

        editPostAdapter = EditPostAdapter()

        binding.viewPagerImage.apply {
            adapter = editPostAdapter
        }

        binding.indicatorImage.setViewPager(binding.viewPagerImage)

        val feedId = intent.getIntExtra("feedId", 0)
        val userId = intent.getIntExtra("userId", 0)
        val position = intent.getIntExtra("position", 0)
        val activity = intent.getIntExtra("activity", ACTIVITY_DETAIL_FEED)

        viewModel.getFeedById(feedId)

        binding.buttonBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
        }

        binding.buttonComplete.setOnClickListener {
            val userTag = mutableListOf<String>()
            for(user in userTags){
                userTag.add(user.username!!)
            }
            val request = EditPostRequest(binding.editContent.text.toString(), emptyList(), userTag.toList())
            viewModel.editPost(feedId, request)
        }

        binding.buttonUserTag.setOnClickListener {
            openAddUserTagActivityForResult()
        }

        viewModel.feedResponse.observe(this, {response->
            if(response.isSuccessful){
                when(activity) {
                    ACTIVITY_DETAIL_FEED ->{
                        val intent = Intent(this, DetailFeedActivity::class.java)
                        intent.putExtra("userId", userId)
                        intent.putExtra("position", position)
                        startActivity(intent)
                        finish()
                    }
                    ACTIVITY_MAIN ->{
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("userId", userId)
                        intent.putExtra("position", position)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        })

        viewModel.fetchFeed.observe(this, {response->
            if(response.isSuccessful){
                val data = response.body()!!
                binding.buttonUsername.text = data.author!!.username
                Glide.with(this).load(data.author.profilePhotoURL).centerCrop().into(binding.buttonUserImage)
                binding.textCreated.text = getBetween(data.createdAt!!.plusHours(9), ZonedDateTime.now(
                    ZoneId.of("Asia/Seoul")))
                binding.editContent.setText(data.content)
                editPostAdapter.updateData(data.photos)
                userTags = data.userTags
                if(userTags.isEmpty()){
                    binding.textUserTag.text = "사람 태그하기"
                }else{
                    binding.textUserTag.text = userTags.size.toString() + "명"
                }
                photos = data.photos
                if(editPostAdapter.itemCount == 1){
                    binding.indicatorImage.visibility = View.GONE
                }else{
                    binding.indicatorImage.visibility = View.VISIBLE
                }
            }
        })
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == Activity.RESULT_OK){
            val data = result.data
            val users = (data!!.getSerializableExtra("users")!! as ArrayList<User>).toList()
            if(users.isEmpty()){
                binding.textUserTag.text = "사람 태그하기"
            }else{
                binding.textUserTag.text = users.size.toString() + "명"
            }
            val temp = mutableListOf<User>()
            for(user in users){
                temp.add(user)
            }
            userTags = temp
        }
    }

    fun openAddUserTagActivityForResult(){
        val intent = Intent(this, AddUserTagActivity::class.java)
        intent.putExtra("photos", photos.toTypedArray())
        val users = mutableListOf<User>()
        for(user in userTags){
            users.add(user)
        }
        intent.putExtra("users", users.toTypedArray())
        resultLauncher.launch(intent)
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
        const val ACTIVITY_MAIN = 0
        const val ACTIVITY_DETAIL_FEED = 1
    }
}