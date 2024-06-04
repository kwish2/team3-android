package com.wafflestudio.wafflestagram.ui.signup

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.wafflestudio.wafflestagram.databinding.ActivityAuthenticationByEmailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthenticationByEmailActivity : AppCompatActivity() {
    lateinit var binding: ActivityAuthenticationByEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationByEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.editCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if(p0?.length!! == 6){
                    binding.buttonNext.isClickable = true
                    binding.buttonNext.isEnabled = true
                    binding.buttonNext.setTextColor(Color.parseColor("#FFFFFFFF"))
                }else{
                    binding.buttonNext.isClickable = false
                    binding.buttonNext.isEnabled = false
                    binding.buttonNext.setTextColor(Color.parseColor("#EAEAEA"))
                }
            }

        })
        binding.buttonNext.setOnClickListener{
            val intent = Intent(this, SignUpDetailActivity::class.java)
            startActivity(intent)
        }
    }
}