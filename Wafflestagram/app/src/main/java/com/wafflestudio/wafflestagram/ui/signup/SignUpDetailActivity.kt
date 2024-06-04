package com.wafflestudio.wafflestagram.ui.signup

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.wafflestudio.wafflestagram.R
import com.wafflestudio.wafflestagram.databinding.ActivitySignUpDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpDetailActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignUpDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val phoneNumber = intent.getStringExtra(SIGNUP_ACTIVITY_EXTRA_PHONE_NUMBER)

        binding.editName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun afterTextChanged(p0: Editable?) {
                if(p0?.length!! > 0 && binding.editPassword.text.length >= 6){
                    binding.buttonContinue.isClickable = true
                    binding.buttonContinue.isEnabled = true
                    binding.buttonContinue.setTextColor(Color.parseColor("#FFFFFFFF"))
                }else{
                    binding.buttonContinue.isClickable = false
                    binding.buttonContinue.isEnabled = false
                    binding.buttonContinue.setTextColor(Color.parseColor("#EAEAEA"))
                }
            }

        })
        binding.editPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun afterTextChanged(p0: Editable?) {
                if(p0?.length!! >= 6 && binding.editName.text.isNotEmpty()){
                    binding.buttonContinue.isClickable = true
                    binding.buttonContinue.isEnabled = true
                    binding.buttonContinue.setTextColor(Color.parseColor("#FFFFFFFF"))
                }else{
                    binding.buttonContinue.isClickable = false
                    binding.buttonContinue.isEnabled = false
                    binding.buttonContinue.setTextColor(Color.parseColor("#EAEAEA"))
                }
                if(p0?.length!! < 6){
                    binding.textInputLayoutPassword.error = "비밀번호는 6자 이상이어야 합니다."
                }else{
                    binding.textInputLayoutPassword.error = null
                }
            }

        })

        binding.editPassword.setOnKeyListener { view, i, keyEvent ->
            if(i == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_DOWN){
                if(binding.buttonContinue.isEnabled){
                    binding.buttonContinue.performClick()
                    true
                }
            }
            false
        }

        binding.buttonContinue.setOnClickListener{
            val intent = Intent(this, AddBirthdayActivity::class.java)
            intent.putExtra(SIGNUP_ACTIVITY_EXTRA_PHONE_NUMBER, phoneNumber)
            intent.putExtra(SIGNUP_ACTIVITY_EXTRA_NAME, binding.editName.text.toString())
            intent.putExtra(SIGNUP_ACTIVITY_EXTRA_PASSWORD, binding.editPassword.text.toString())
            startActivity(intent)
            overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit)
        }
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
    }
}