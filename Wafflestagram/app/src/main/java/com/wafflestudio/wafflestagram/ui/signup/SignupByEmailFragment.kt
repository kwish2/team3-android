package com.wafflestudio.wafflestagram.ui.signup

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wafflestudio.wafflestagram.databinding.FragmentSignupByEmailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignupByEmailFragment : Fragment() {
    private lateinit var binding: FragmentSignupByEmailBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignupByEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editEmail.isEnabled = false
        binding.editEmail.addTextChangedListener(object :TextWatcher{
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
        binding.buttonNext.setOnClickListener{
            val intent = Intent(context, AuthenticationByPhoneActivity::class.java)
            startActivity(intent)
        }
    }
}