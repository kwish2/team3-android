package com.wafflestudio.wafflestagram.ui.main

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.aghajari.zoomhelper.ZoomHelper
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.wafflestudio.wafflestagram.R
import com.wafflestudio.wafflestagram.databinding.ActivityMainBinding
import com.wafflestudio.wafflestagram.databinding.IconUserProfileBinding
import com.wafflestudio.wafflestagram.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject
import com.wafflestudio.wafflestagram.ui.settings.SettingsActivity





@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userProfileBinding: IconUserProfileBinding
    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val feedFragment = FeedFragment()
    private val searchFragment = SearchFragment()
    private val userFragment = UserFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        userProfileBinding = IconUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Logout 된 경우 LoginActivity로 이동
        if(!sharedPreferences.getBoolean(IS_LOGGED_IN, false) || sharedPreferences.getString(TOKEN, "") == ""){
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }

        Timber.d("TOKEN: ${sharedPreferences.getString(TOKEN, "")}")
        Timber.d("CURRENT_USER_ID: ${sharedPreferences.getInt(CURRENT_USER_ID, -1)}")
        Timber.d("isLoggedIn: ${sharedPreferences.getBoolean(IS_LOGGED_IN, false)}")

        // set max zoom scale
        ZoomHelper.getInstance().maxScale = 3f

        viewModel.getMyInfo()
        val fragment = intent.getIntExtra("fragment", FEED_FRAGMENT)

        binding.tabLayoutMain.getTabAt(2)!!.customView = userProfileBinding.root
        setFragment(fragment)
        binding.tabLayoutMain.getTabAt(fragment)?.select()
        if(fragment == 2){
            userProfileBinding.imageProfile.borderColor= ContextCompat.getColor(this, R.color.black)
            binding.tabLayoutMain.getTabAt(2)!!.customView = userProfileBinding.root
        }

        binding.tabLayoutMain.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab!!.position){
                    FEED_FRAGMENT -> setFragment(FEED_FRAGMENT)
                    // 1 -> search, 2 -> user Fragment
                    SEARCH_FRAGMENT -> setFragment(SEARCH_FRAGMENT)
                    USER_FRAGMENT -> {
                        setFragment(USER_FRAGMENT)
                        userProfileBinding.imageProfile.borderColor= ContextCompat.getColor(applicationContext, R.color.black)
                        binding.tabLayoutMain.getTabAt(2)!!.customView = userProfileBinding.root
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                if(tab!!.position == USER_FRAGMENT){
                    userProfileBinding.imageProfile.borderColor = ContextCompat.getColor(applicationContext, R.color.transparent)
                    binding.tabLayoutMain.getTabAt(2)!!.customView = userProfileBinding.root
                }

                if(tab.position == FEED_FRAGMENT){
                    feedFragment.clearRecyclerView()
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                when(tab!!.position){
                    FEED_FRAGMENT -> feedFragment.setRecyclerviewPosition(0)

                }
            }

        })

        viewModel.myInfo.observe(this, { response->
            if(response.isSuccessful){
                Glide.with(this).load(response.body()?.profilePhotoURL).centerCrop().into(userProfileBinding.imageProfile)
            }else if(response.code() == 401){
                SocialLoginSignOutUtils.signOut(this)
                // Remove Token(Sign Out)
                sharedPreferences.edit{
                    putString(TOKEN, "")
                    putInt(CURRENT_USER_ID, -1)
                    putBoolean(IS_LOGGED_IN, false)
                }
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
            }
        })
    }

    private fun setFragment(fragmentNum : Int){
        val fb = supportFragmentManager.beginTransaction()

        val currentUserId = sharedPreferences.getInt(CURRENT_USER_ID, -1)
        when(fragmentNum){
            FEED_FRAGMENT -> {
                fb.replace(binding.fragmentContainerViewMain.id, feedFragment)
                //fb.addToBackStack(null)
                fb.commit()
            }
            SEARCH_FRAGMENT -> {
                fb.replace(binding.fragmentContainerViewMain.id, searchFragment)
                //fb.addToBackStack(null)
                fb.commit()
            }
            USER_FRAGMENT -> {
                fb.replace(binding.fragmentContainerViewMain.id,
                    userFragment.apply {
                        arguments = Bundle().apply {
                            putInt(USER_ID, currentUserId)
                        }
                    })
                //fb.addToBackStack(null)
                fb.commit()
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return ZoomHelper.getInstance().dispatchTouchEvent(ev!!, this) || super.dispatchTouchEvent(ev)
    }

    companion object{
        const val FEED_FRAGMENT = 0
        const val SEARCH_FRAGMENT = 1
        const val USER_FRAGMENT = 2
        const val TOKEN = "token"
        const val CURRENT_USER_ID = "currentUserId"
        const val USER_ID = "userId"
        const val IS_LOGGED_IN = "isLoggedIn"
    }
}