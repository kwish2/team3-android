package com.wafflestudio.wafflestagram.ui.settings.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.wafflestudio.wafflestagram.databinding.FragmentEditPersonalInfoBinding
import com.wafflestudio.wafflestagram.ui.main.UserViewModel
import com.wafflestudio.wafflestagram.ui.settings.SettingsActivity
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class EditPersonalInfoFragment: Fragment() {

    private lateinit var binding: FragmentEditPersonalInfoBinding
    private val viewModel: EditPersonalInfoViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditPersonalInfoBinding.inflate(inflater, container, false)

        (activity as SettingsActivity).replaceTitle(SettingsActivity.SETTINGS_EDIT_PERSONAL_INFO_FRAGMENT)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as SettingsActivity).replaceTitle(SettingsActivity.SETTINGS_EDIT_PERSONAL_INFO_FRAGMENT)

        binding.textBirthday.text = SimpleDateFormat("yyyy년MM월dd일", Locale.KOREAN).format(System.currentTimeMillis())
        binding.inputBirthday.updateDate(1, 1, 1)
        binding.inputBirthday.setOnDateChangedListener { _, year, month, day ->
            binding.textBirthday.text = "" + year + "년" + (month + 1) + "월" + day + "일"
        }
        binding.inputBirthday.maxDate = System.currentTimeMillis()

        /*
        binding.buttonConfirm.setOnClickListener {
            // TODO: 예외 처리
            val updateUserRequest = UpdateUserRequest(
                null, null, null, null
            )
            viewModel.updateUser(updateUserRequest)
        }
         */

        viewModel.fetchUserInfoResponse.observe(this, {response ->
            if(response.isSuccessful) {
                Toast.makeText(context, "개인정보가 변경되었습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object{
        const val SIGNUP_ACTIVITY_EXTRA_PHONE_NUMBER = "phoneNumber"
        const val SIGNUP_ACTIVITY_EXTRA_NAME = "name"
        const val SIGNUP_ACTIVITY_EXTRA_PASSWORD = "password"
        const val SIGNUP_ACTIVITY_EXTRA_BIRTHDAY = "birthday"
    }
}