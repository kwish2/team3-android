package com.wafflestudio.wafflestagram.ui.detail

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aghajari.zoomhelper.ZoomHelper
import com.wafflestudio.wafflestagram.R
import com.wafflestudio.wafflestagram.databinding.ActivityDetailTaggedFeedBinding
import com.wafflestudio.wafflestagram.ui.login.LoginActivity
import com.wafflestudio.wafflestagram.ui.main.FeedFragment
import com.wafflestudio.wafflestagram.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import com.wafflestudio.wafflestagram.ui.main.SocialLoginSignOutUtils
import javax.inject.Inject

@AndroidEntryPoint
class DetailTaggedFeedActivity : AppCompatActivity(), DetailTaggedFeedInterface {

    private lateinit var binding: ActivityDetailTaggedFeedBinding
    private val viewModel: DetailTaggedFeedViewModel by viewModels()
    private lateinit var feedAdapter: DetailTaggedFeedAdapter
    private lateinit var feedLayoutManager: LinearLayoutManager
    private var isFirstCall: Boolean = true
    private var itemPosition : Int = -1

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailTaggedFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit)

        ZoomHelper.getInstance().maxScale = 3f

        feedAdapter = DetailTaggedFeedAdapter(this)
        feedLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        binding.recyclerViewFeed.apply {
            adapter = feedAdapter
            layoutManager = feedLayoutManager
        }

        val userId = intent.getIntExtra("userId", -1)
        val position = intent.getIntExtra("position" , 0)

        viewModel.getMe()
        viewModel.getTaggedFeedsByUserId(userId, 0, 100)


        binding.buttonBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
        }

        binding.refreshLayoutFeed.setColorSchemeColors(*intArrayOf(
            Color.parseColor("#F6F6F6"),
            Color.parseColor("#D5D5D5"),
            Color.parseColor("#A6A6A6"),
            Color.parseColor("#D5D5D5")))
        binding.refreshLayoutFeed.setOnRefreshListener {
            feedAdapter.clearData()
            viewModel.getTaggedFeedsByUserId(userId, 0, 100)
            isFirstCall = false
            Handler(Looper.getMainLooper()).postDelayed({
                binding.refreshLayoutFeed.setRefreshing(false)
            }, 1000)
        }

        binding.recyclerViewFeed.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val topRowVerticalPosition = if (recyclerView == null || recyclerView.childCount === 0) 0 else recyclerView.getChildAt(0).top
                binding.refreshLayoutFeed.isEnabled = topRowVerticalPosition >= 0
            }
        })

        viewModel.page.observe(this, {response->
            if(response.isSuccessful){
                feedAdapter.addData(response.body()!!.content.toMutableList())
                if(isFirstCall){
                    binding.recyclerViewFeed.scrollToPosition(position)
                }
            }else if(response.code() == 401){
                Toast.makeText(this, "다시 로그인해주세요", Toast.LENGTH_SHORT).show()
                sharedPreferences.edit{
                    putString(MainActivity.TOKEN, "")
                    putInt(MainActivity.CURRENT_USER_ID, -1)
                    putBoolean(MainActivity.IS_LOGGED_IN, false)
                }
                SocialLoginSignOutUtils.signOut(this)
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }else{

            }
        })

        viewModel.user.observe(this, {response->
            if(response.isSuccessful){
                feedAdapter.updateData(response.body()!!)
            }else if(response.code() == 401){

            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(isFinishing){
            overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
        }
    }

    override fun deleteFeed(id: Int, position: Int){
        viewModel.deleteFeed(id)
        feedAdapter.deleteData(position)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return ZoomHelper.getInstance().dispatchTouchEvent(ev!!, this) || super.dispatchTouchEvent(ev)
    }

    override fun like(id: Int, position: Int) {
        viewModel.like(id)
        itemPosition = position
    }

    override fun unlike(id: Int, position: Int) {
        viewModel.unlike(id)
        itemPosition = position
    }
}