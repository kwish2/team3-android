package com.wafflestudio.wafflestagram.ui.detail

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.widget.NestedScrollView
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import com.wafflestudio.wafflestagram.R
import com.wafflestudio.wafflestagram.databinding.ActivityDetailUserBinding
import com.wafflestudio.wafflestagram.ui.follow.FollowActivity
import com.wafflestudio.wafflestagram.ui.login.LoginActivity
import com.wafflestudio.wafflestagram.ui.main.MainActivity
import com.wafflestudio.wafflestagram.ui.main.SocialLoginSignOutUtils
import com.wafflestudio.wafflestagram.ui.main.UserFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DetailUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailUserBinding
    private val viewModel: DetailUserViewModel by viewModels()
    private var isLoading: Boolean = false
    private val detailUserMyFeedFragment = DetailUserMyFeedFragment()
    private val detailUserTaggedFeedFragment = DetailUserTaggedFeedFragment()

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private var tabPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit)

        val id = intent.getIntExtra("id", -1)

        val viewpagerUserFragmentAdapter = ViewPagerDetailUserAdapter(this)
        viewpagerUserFragmentAdapter.setId(id)
        binding.viewPager.adapter = viewpagerUserFragmentAdapter

        viewpagerUserFragmentAdapter.assignFragment(listOf(detailUserMyFeedFragment, detailUserTaggedFeedFragment))

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> Unit }.attach()

        binding.tabLayout.getTabAt(UserFragment.MY_FEED)!!.setIcon(R.drawable.icon_grid)
        binding.tabLayout.getTabAt(UserFragment.TAGGED_FEED)!!.setIcon(R.drawable.icon_tagged)

        viewModel.getMyInfo()
        viewModel.getInfoById(id)
        viewModel.getFeedCount(id)
        viewModel.getFollowerCount(id)
        viewModel.getFollowingCount(id)
        viewModel.checkFollowing(id)

        binding.refreshLayoutUser.setColorSchemeColors(*intArrayOf(
                Color.parseColor("#F6F6F6"),
                Color.parseColor("#D5D5D5"),
                Color.parseColor("#A6A6A6"),
                Color.parseColor("#D5D5D5")))
        binding.refreshLayoutUser.setOnRefreshListener {

            Handler(Looper.getMainLooper()).postDelayed({
                binding.refreshLayoutUser.setRefreshing(false) }, 1000)
            if(!isLoading){
                viewModel.getMyInfo()
                viewModel.getInfoById(id)
                viewModel.getFeedCount(id)
                viewModel.getFollowerCount(id)
                viewModel.getFollowingCount(id)
                viewModel.getFeedsById(id, 0, 12)
                viewModel.checkFollowing(id)
                isLoading = true
            }
        }

        binding.nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                if(v!!.getChildAt(v.childCount - 1) != null) {
                    if ((scrollY >= (v.getChildAt(v.childCount - 1).measuredHeight - v.measuredHeight)) &&
                        scrollY > oldScrollY) {
                        when(tabPosition) {
                            UserFragment.MY_FEED ->
                                detailUserMyFeedFragment.getFeeds()

                            UserFragment.TAGGED_FEED ->
                                detailUserTaggedFeedFragment.getFeeds()
                        }
                    }
                }
            })

        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                tabPosition = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        viewModel.fetchUserInfo.observe(this, {response->
            if(response.isSuccessful){
                val data = response.body()!!
                binding.buttonUsername.text = data.username
                if(data.bio.isNullOrBlank()){
                    binding.textBio.visibility = View.GONE
                }else{
                    binding.textBio.text = data.bio
                    binding.textBio.visibility = View.VISIBLE
                }
                if(data.website.isNullOrBlank()){
                    binding.textWebsite.visibility = View.GONE
                }else{
                    binding.textWebsite.visibility = View.VISIBLE
                    binding.textWebsite.text = data.website
                }
                if(data.name.isNullOrBlank()){
                    binding.textName.visibility = View.GONE
                }else{
                    binding.textName.visibility = View.VISIBLE
                    binding.textName.text = data.name
                }
                Glide.with(this).load(response.body()!!.profilePhotoURL).centerCrop().into(binding.userImage)
                isLoading = false
            }else if(response.code() == 401){
                Toast.makeText(this, "다시 로그인해주세요", Toast.LENGTH_SHORT).show()
                // Remove Token(Sign Out)
                sharedPreferences.edit{
                    putString(MainActivity.TOKEN, "")
                    putInt(MainActivity.CURRENT_USER_ID, -1)
                    putBoolean(MainActivity.IS_LOGGED_IN, false)
                }
                SocialLoginSignOutUtils.signOut(this)

                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
        })

        viewModel.fetchFeedCount.observe(this,{response ->
            if(response.isSuccessful){
                binding.posts.text = response.body()!!.string()
            }
        })

        viewModel.fetchFollowerCount.observe(this,{response ->
            if(response.isSuccessful){
                binding.followers.text = response.body()!!.string()
            }
        })

        viewModel.fetchFollowingCount.observe(this,{response ->
            if(response.isSuccessful){
                binding.followings.text = response.body()!!.string()
            }
        })

        viewModel.checkFollowing.observe(this, {response->
            if(response.isSuccessful){
                if(response.body()!!.string() == "true"){
                    binding.buttonFollow.isSelected = true
                    binding.buttonFollow.setTextColor(ContextCompat.getColor(this, R.color.black))
                    binding.buttonFollow.text = "팔로잉"
                }else{
                    binding.buttonFollow.isSelected = false
                    binding.buttonFollow.setTextColor(Color.parseColor("#FFFFFFFF"))
                    binding.buttonFollow.text = "팔로우"
                }
            }
        })

        viewModel.fetchMyInfo.observe(this, {response ->
            if(response.isSuccessful){
                if(response.body()!!.id.toInt() == id){
                    binding.layoutFollowMessage.visibility = View.GONE
                }
            }
        })

        binding.buttonFollow.setOnClickListener {
            if(binding.buttonFollow.isSelected){
                binding.buttonFollow.isSelected = false
                binding.buttonFollow.setTextColor(Color.parseColor("#FFFFFFFF"))
                binding.buttonFollow.text = "팔로우"
                viewModel.unfollow(id)
            }else{
                binding.buttonFollow.isSelected = true
                binding.buttonFollow.setTextColor(ContextCompat.getColor(this, R.color.black))
                binding.buttonFollow.text = "팔로잉"
                viewModel.follow(id)
            }
        }

        binding.buttonBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
        }

        binding.areaFollowers.setOnClickListener {
            val intent = Intent(this, FollowActivity::class.java)
            intent.putExtra("id", id)
            intent.putExtra("activity", 0)
            startActivity(intent)
        }

        binding.areaFollowings.setOnClickListener {
            val intent = Intent(this, FollowActivity::class.java)
            intent.putExtra("id", id)
            intent.putExtra("activity", 1)
            startActivity(intent)
        }

        binding.areaPosts.setOnClickListener {
            val intent = Intent(this, DetailFeedActivity::class.java)
            intent.putExtra("userId", id)
            startActivity(intent)
        }


    }
    override fun onBackPressed() {
        super.onBackPressed()
        if(isFinishing){
            overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
        }
    }
}