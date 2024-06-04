package com.wafflestudio.wafflestagram.ui.signup

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.wafflestudio.wafflestagram.R
import com.wafflestudio.wafflestagram.databinding.ActivitySignUpBinding
import com.wafflestudio.wafflestagram.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    private val signupByPhoneFragment = SignupByPhoneFragment()
    private val signupByEmailFragment = SignupByEmailFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFragment(0)
        binding.layoutTab.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab!!.position){
                    0 -> setFragment(0)
                    1 -> setFragment(1)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }


        })

        binding.buttonLogin.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(isFinishing){
            overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
        }
    }

    private fun setFragment(fragmentNum : Int){
        val fb = supportFragmentManager.beginTransaction()
        when(fragmentNum){
            0 -> {
                fb.replace(binding.fragmentContainerViewSignup.id, signupByPhoneFragment).commit()
            }
            1 ->{
                fb.replace(binding.fragmentContainerViewSignup.id, signupByEmailFragment).commit()
            }
        }
    }
}