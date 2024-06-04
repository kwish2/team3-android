package com.wafflestudio.wafflestagram.ui.like

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import com.wafflestudio.wafflestagram.R
import com.wafflestudio.wafflestagram.databinding.ActivityLikeBinding
import com.wafflestudio.wafflestagram.ui.login.LoginActivity
import com.wafflestudio.wafflestagram.ui.main.MainActivity
import com.wafflestudio.wafflestagram.ui.main.SocialLoginSignOutUtils
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Response
import javax.inject.Inject

@AndroidEntryPoint
class LikeActivity : AppCompatActivity() , LikeInterface{

    private lateinit var binding: ActivityLikeBinding
    private val viewModel: LikeViewModel by viewModels()
    private lateinit var likeAdapter: LikeAdapter
    private lateinit var likeLayoutManager: LinearLayoutManager

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLikeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit)

        likeAdapter = LikeAdapter(this)
        likeLayoutManager = LinearLayoutManager(this)

        binding.recyclerViewLike.apply {
            adapter = likeAdapter
            layoutManager = likeLayoutManager
        }

        binding.buttonBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
        }

        val id = intent.getIntExtra("id", 0)

        viewModel.getMe()

        //viewModel.getFeedById(id)

        viewModel.feed.observe(this, {response ->
            if(response.isSuccessful){
                likeAdapter.updateData(response.body()!!.likes)
            }else if(response.code() == 401){
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
            }else{

            }
        })

        viewModel.page.observe(this, {response->
            if(response.isSuccessful){
                likeAdapter.updateFollowing(response.body()!!.content)
                viewModel.getFeedById(id)
            }
        })

        viewModel.user.observe(this, {reponse ->
            if(reponse.isSuccessful){
                likeAdapter.updateUser(reponse.body()!!)
                viewModel.getMyFollowing(0, 500)
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(isFinishing){
            overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
        }
    }

    fun checkFollowing(id: Int) : Response<Boolean> {
        return viewModel.checkFollowing(id)
    }

    override fun follow(id: Int){
        viewModel.follow(id)
    }

    override fun unfollow(id: Int){
        viewModel.unfollow(id)
    }
}