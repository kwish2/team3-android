package com.wafflestudio.wafflestagram.ui.signup

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.wafflestudio.wafflestagram.databinding.ActivitySignUpCompleteBinding
import com.wafflestudio.wafflestagram.databinding.ActivitySocialLoginUsernameBinding
import com.wafflestudio.wafflestagram.network.dto.UpdateUserRequest
import com.wafflestudio.wafflestagram.ui.login.LoginActivity
import com.wafflestudio.wafflestagram.ui.main.MainActivity
import com.wafflestudio.wafflestagram.ui.profile.EditProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SocialLoginUsernameActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySocialLoginUsernameBinding
    private val viewModel: EditProfileViewModel by viewModels()

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySocialLoginUsernameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra(NAME)
        val website = intent.getStringExtra(WEBSITE)
        val bio = intent.getStringExtra(BIO)

        binding.editUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun afterTextChanged(p0: Editable?) {
                if(p0?.length!! > 0){
                    binding.buttonComplete.isClickable = true
                    binding.buttonComplete.isEnabled = true
                    binding.buttonComplete.setTextColor(Color.parseColor("#FFFFFFFF"))
                }else{
                    binding.buttonComplete.isClickable = false
                    binding.buttonComplete.isEnabled = false
                    binding.buttonComplete.setTextColor(Color.parseColor("#EAEAEA"))
                }
            }
        })

        binding.buttonComplete.setOnClickListener {
            val updateUserRequest = UpdateUserRequest(
                name, binding.editUsername.text.toString(),
                website, bio
            )
            viewModel.updateUser(updateUserRequest)
        }

        viewModel.fetchUserInfoResponse.observe(this, {response->
            if(response.isSuccessful){
                binding.textInputLayoutUsername.error = null
                sharedPreferences.edit{
                    putBoolean(LoginActivity.IS_LOGGED_IN, true)
                }
                Timber.d("isLoggedIn: ${sharedPreferences.getBoolean(MainActivity.IS_LOGGED_IN, false).toString()}")
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }else if(response.code() == 409){
                binding.textInputLayoutUsername.error = "이미 사용중인 사용자 이름입니다."
            }

        })
    }

    companion object {
        const val NAME = "name"
        const val WEBSITE = "website"
        const val BIO = "bio"
    }
}