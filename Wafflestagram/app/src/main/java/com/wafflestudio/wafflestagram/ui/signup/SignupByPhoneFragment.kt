package com.wafflestudio.wafflestagram.ui.signup

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wafflestudio.wafflestagram.R
import com.wafflestudio.wafflestagram.databinding.FragmentSignupByPhoneBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignupByPhoneFragment : Fragment() {
    private lateinit var binding: FragmentSignupByPhoneBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignupByPhoneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if(p0?.length!! > 0){
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

        binding.editPhone.setOnKeyListener { view, i, keyEvent ->
            if(i == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_DOWN){
                if(binding.buttonNext.isEnabled){
                    binding.buttonNext.performClick()
                    true
                }
            }
            false
        }

        binding.buttonNext.setOnClickListener{
            if(binding.editPhone.text.length == 11 && binding.editPhone.text[0] == '0'){
                val intent = Intent(context, AuthenticationByPhoneActivity::class.java)
                intent.putExtra(SIGNUP_ACTIVITY_EXTRA_PHONE_NUMBER, binding.editPhone.text.toString())
                startActivity(intent)
            }else{
                binding.textInputLayoutPhone.error = "유효하지 않은 형식입니다. 올바른 형식 예시:010XXXXXXXX"
            }
        }
    }

    companion object{
        const val SIGNUP_ACTIVITY_EXTRA_PHONE_NUMBER = "phoneNumber"
    }
}