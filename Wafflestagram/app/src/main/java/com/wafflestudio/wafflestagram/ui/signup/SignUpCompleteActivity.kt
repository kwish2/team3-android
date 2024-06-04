package com.wafflestudio.wafflestagram.ui.signup

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import androidx.activity.viewModels
import androidx.core.content.edit
import com.wafflestudio.wafflestagram.R
import com.wafflestudio.wafflestagram.databinding.ActivitySignUpCompleteBinding
import com.wafflestudio.wafflestagram.network.dto.SignUpRequest
import com.wafflestudio.wafflestagram.ui.login.LoginActivity
import com.wafflestudio.wafflestagram.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern
import javax.inject.Inject

@AndroidEntryPoint
class SignUpCompleteActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignUpCompleteBinding
    private val viewModel: SignUpViewModel by viewModels()

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpCompleteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val phoneNumber = intent.getStringExtra(SIGNUP_ACTIVITY_EXTRA_PHONE_NUMBER)!!
        val name = intent.getStringExtra(SIGNUP_ACTIVITY_EXTRA_NAME)!!
        val password = intent.getStringExtra(SIGNUP_ACTIVITY_EXTRA_PASSWORD)!!
        val birthday = intent.getStringExtra(SIGNUP_ACTIVITY_EXTRA_BIRTHDAY)!!

        binding.editEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun afterTextChanged(p0: Editable?) {
                if(p0?.length!! > 0 && binding.editUsername.text.isNotEmpty()){
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
        binding.editUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun afterTextChanged(p0: Editable?) {
                if(p0?.length!! > 0 && binding.editEmail.text.isNotEmpty()){
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

        binding.editUsername.setOnKeyListener { view, i, keyEvent ->
            if(i == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_DOWN){
                if(binding.buttonComplete.isEnabled){
                    binding.buttonComplete.performClick()
                    true
                }
            }
            false
        }

        binding.buttonComplete.setOnClickListener{
            if(android.util.Patterns.EMAIL_ADDRESS.matcher(binding.editEmail.text).matches()){
                binding.textInputLayoutEmail.error = null
                val request = SignUpRequest(
                    binding.editEmail.text.toString(),
                    password,
                    true,
                    name,
                    binding.editUsername.text.toString(),
                    birthday,
                    phoneNumber
                )
                viewModel.signUp(request)
            }else{
                binding.textInputLayoutEmail.error = "올바른 이메일 주소를 입력하세요."
            }
        }

        viewModel.idResponse.observe(this, {response ->
            if(response.isSuccessful){
                binding.textInputLayoutUsername.error = null
                //token 저장
                //response.headers().get("Authentication") 를 이용
                //메인으로 이동
                sharedPreferences.edit {
                    putString(LoginActivity.TOKEN, response.headers()["Authentication"])
                    putInt(LoginActivity.CURRENT_USER_ID, response.body()!!.id.toInt())
                }
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit)
            }else if(response.code() == 400){
                //에러 메시지
                binding.textInputLayoutUsername.error = "이미 사용 중인 이메일입니다. 다시 입력해주세요."
            }else if(response.code() == 409){
                binding.textInputLayoutUsername.error = "이미 사용 중인 사용자 이름입니다. 다시 입력해주세요."
            } else{
                binding.textInputLayoutUsername.error = "잘못된 접근입니다."
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(isFinishing){
            overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
        }
    }

    companion object{
        const val SIGNUP_ACTIVITY_EXTRA_PHONE_NUMBER = "phoneNumber"
        const val SIGNUP_ACTIVITY_EXTRA_NAME = "name"
        const val SIGNUP_ACTIVITY_EXTRA_PASSWORD = "password"
        const val SIGNUP_ACTIVITY_EXTRA_BIRTHDAY = "birthday"
        const val TOKEN = "token"
        const val CURRENT_USER_ID = "currentUserId"
    }
}