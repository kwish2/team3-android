package com.wafflestudio.wafflestagram.ui.signup

import android.content.Intent
import android.os.Bundle
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import com.wafflestudio.wafflestagram.R
import com.wafflestudio.wafflestagram.databinding.ActivityAddBirthdayBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AddBirthdayActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddBirthdayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBirthdayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val phoneNumber = intent.getStringExtra(SIGNUP_ACTIVITY_EXTRA_PHONE_NUMBER)
        val name = intent.getStringExtra(SIGNUP_ACTIVITY_EXTRA_NAME)
        val password = intent.getStringExtra(SIGNUP_ACTIVITY_EXTRA_PASSWORD)

        binding.textBirthday.text = SimpleDateFormat("yyyy년MM월dd일", Locale.KOREAN).format(System.currentTimeMillis())
        binding.datePicker.setOnDateChangedListener(object : DatePicker.OnDateChangedListener{
            override fun onDateChanged(p0: DatePicker?, year: Int, month: Int, day: Int) {
                binding.textBirthday.text = ""+ year + "년" + (month+1) + "월" + day + "일"
            }

        })
        binding.datePicker.maxDate = System.currentTimeMillis()

        binding.buttonNext.setOnClickListener{
            val intent = Intent(this, SignUpCompleteActivity::class.java)
            intent.putExtra(SIGNUP_ACTIVITY_EXTRA_PHONE_NUMBER, phoneNumber)
            intent.putExtra(SIGNUP_ACTIVITY_EXTRA_NAME, name)
            intent.putExtra(SIGNUP_ACTIVITY_EXTRA_PASSWORD, password)
            intent.putExtra(SIGNUP_ACTIVITY_EXTRA_BIRTHDAY, binding.textBirthday.text.toString())
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
        const val SIGNUP_ACTIVITY_EXTRA_BIRTHDAY = "birthday"
    }
}