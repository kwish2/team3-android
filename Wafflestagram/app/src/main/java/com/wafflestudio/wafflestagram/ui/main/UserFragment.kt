package com.wafflestudio.wafflestagram.ui.main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.wafflestudio.wafflestagram.R
import com.wafflestudio.wafflestagram.databinding.FragmentUserBinding
import com.wafflestudio.wafflestagram.ui.detail.DetailFeedActivity
import com.wafflestudio.wafflestagram.ui.dialog.MenuBottomSheetFragment
import com.wafflestudio.wafflestagram.ui.follow.FollowActivity
import com.wafflestudio.wafflestagram.ui.login.LoginActivity
import com.wafflestudio.wafflestagram.ui.profile.EditProfileActivity
import com.wafflestudio.wafflestagram.ui.post.AddPostActivity
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.internal.managers.FragmentComponentManager
import javax.inject.Inject

@AndroidEntryPoint
class UserFragment: Fragment() {
    // Assume that "username" is passed using putExtra() method
    // which contains username of user whose profile is showed
    // and we have "currentUsername" as sharedPreference

    private lateinit var binding: FragmentUserBinding
    private val viewModel: UserViewModel by activityViewModels()
    private var isLoading: Boolean = false

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private var currentUserId: Int = -1 // -1: default value

    private lateinit var _context: Context
    private val userMyFeedFragment = UserMyFeedFragment()
    private val userTaggedFeedFragment = UserTaggedFeedFragment()

    private var tabPosition: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _context = container!!.context
        binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // change UI and get info according to user ID
        currentUserId = sharedPreferences.getInt(CURRENT_USER_ID, -1)
        val userId = arguments!!.getInt(USER_ID, -1)
        if(userId == currentUserId){
            // the user is me
            viewModel.getMyInfo()
            viewModel.getMyFollowerCount()
            viewModel.getMyFollowingCount()
            viewModel.getMyFeedCount()

            binding.buttonDM.visibility = View.GONE

            // post 작성 버튼
            binding.buttonAdd.setOnClickListener{
                val intent = Intent(context, AddPostActivity::class.java)
                startActivity(intent)
            }

            binding.refreshLayoutUser.setColorSchemeColors(*intArrayOf(
                Color.parseColor("#F6F6F6"),
                Color.parseColor("#D5D5D5"),
                Color.parseColor("#A6A6A6"),
                Color.parseColor("#D5D5D5")))
            binding.refreshLayoutUser.setOnRefreshListener {

                Handler(Looper.getMainLooper()).postDelayed({
                    binding.refreshLayoutUser.setRefreshing(false)
                }, 1000)
                if(!isLoading){
                    viewModel.getMyInfo()
                    viewModel.getMyFollowerCount()
                    viewModel.getMyFollowingCount()
                    viewModel.getMyFeedCount()
                    viewModel.getMyFeeds(0, 12)
                    isLoading = true
                }
            }

            binding.nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                if(v!!.getChildAt(v.childCount - 1) != null) {
                    if ((scrollY >= (v.getChildAt(v.childCount - 1).measuredHeight - v.measuredHeight)) &&
                        scrollY > oldScrollY) {
                        when(tabPosition) {
                            MY_FEED ->
                                userMyFeedFragment.getFeeds()
                            TAGGED_FEED ->
                                userTaggedFeedFragment.getFeeds()
                        }
                    }
                }
            })

            binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    tabPosition = tab.position
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }
            })

            // profile 편집 버튼
            binding.buttonEditProfile.setOnClickListener {
                val intent = Intent(context, EditProfileActivity::class.java)
                val userInfo = viewModel.fetchUserInfo.value!!.body()
                intent.putExtra("name", userInfo!!.name)
                    .putExtra("username", userInfo.username)
                    .putExtra("website", userInfo.website)
                    .putExtra("bio", userInfo.bio)
                    .putExtra("id", userInfo.id.toInt())
                startActivity(intent)
            }

            binding.buttonMenu.setOnClickListener {
                val menuBottomSheetFragment = MenuBottomSheetFragment(_context)
                menuBottomSheetFragment.show((FragmentComponentManager.findActivity(_context) as MainActivity).supportFragmentManager, MenuBottomSheetFragment.TAG)
            }
        } else {
            // the user is other one
            viewModel.getInfoByUserId(userId!!)
            viewModel.getFollowerCountByUserId(userId)
            viewModel.getFollowingCountByUserId(userId)
            viewModel.getFeedCountByUserId(userId)

            binding.buttonAdd.visibility = View.GONE
            binding.buttonMenu.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.icon_more))
            checkFollowingStatus()
        }

        // Set ViewPager
        val viewpagerUserFragmentAdapter = ViewPagerUserFragmentAdapter(activity!!)
        binding.viewPager.adapter = viewpagerUserFragmentAdapter

        viewpagerUserFragmentAdapter.assignFragment(listOf(userMyFeedFragment, userTaggedFeedFragment))

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> Unit }.attach()

        binding.tabLayout.getTabAt(0)!!.setIcon(R.drawable.icon_grid)
        binding.tabLayout.getTabAt(1)!!.setIcon(R.drawable.icon_tagged)

        // 데이터 저장

        viewModel.fetchFollowingCount.observe(viewLifecycleOwner, { response ->
                val data = response.body()!!
                if(response.isSuccessful){
                    binding.followings.text = data.string()
                } else {
                    Toast.makeText(context, "팔로잉 정보를 불러 올 수 없습니다", Toast.LENGTH_SHORT).show()
                }
        })

        viewModel.fetchFollowerCount.observe(viewLifecycleOwner, { response ->
            val data = response.body()!!
            if(response.isSuccessful){
                binding.followers.text = data.string()
            } else {
                Toast.makeText(context, "팔로워 정보를 불러 올 수 없습니다", Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.fetchFeedCount.observe(viewLifecycleOwner, { response ->
            val data = response.body()!!
            if(response.isSuccessful){
                binding.feeds.text = data.string()
            } else {
                Toast.makeText(context, "피드 정보를 불러 올 수 없습니다", Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.fetchUserInfo.observe(viewLifecycleOwner, { response ->
            if(response.isSuccessful){
                val data = response.body()!!
                Glide.with(_context)
                    .load(data.profilePhotoURL)
                    .centerCrop()
                    .into(binding.userImage)
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
                isLoading = false
            } else if(response.code() == 401){
                Toast.makeText(context, "다시 로그인해주세요", Toast.LENGTH_SHORT).show()
                // Remove Token(Sign Out)
                sharedPreferences.edit{
                    putString(MainActivity.TOKEN, "")
                    putInt(MainActivity.CURRENT_USER_ID, -1)
                    putBoolean(MainActivity.IS_LOGGED_IN, false)
                }
                SocialLoginSignOutUtils.signOut(context!!)
                val intent = Intent(context, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            } else{
                Toast.makeText(context, "유저 정보를 불러올 수 없습니다", Toast.LENGTH_SHORT).show()
            }
        })

        binding.areaFeeds.setOnClickListener {
            val intent = Intent(context, DetailFeedActivity::class.java)
            intent.putExtra("userId", currentUserId)
            startActivity(intent)
        }

        binding.areaFollowers.setOnClickListener {
            val intent = Intent(context, FollowActivity::class.java)
            intent.putExtra("id", currentUserId)
            intent.putExtra("activity", 0)
            startActivity(intent)
        }

        binding.areaFollowings.setOnClickListener {
            val intent = Intent(context, FollowActivity::class.java)
            intent.putExtra("id", currentUserId)
            intent.putExtra("activity", 1)
            startActivity(intent)
        }
    }

    private fun checkFollowingStatus() {
        // check whether this user is followed by me
        // and set text on buttonEditProfile according to result
        binding.buttonEditProfile.text = "팔로우"
    }

    companion object {
        const val CURRENT_USER_ID = "currentUserId"
        const val USER_ID = "userId"
        const val MY_FEED = 0
        const val TAGGED_FEED = 1
    }
}