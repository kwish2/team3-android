package com.wafflestudio.wafflestagram.ui.settings

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.wafflestudio.wafflestagram.R
import com.wafflestudio.wafflestagram.databinding.ActivitySettingsBinding
import com.wafflestudio.wafflestagram.ui.settings.account.EditPersonalInfoFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity: AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingsViewModel by viewModels()

    private var settingsMainFragment = SettingsMainFragment()
    private var settingsPersonalInfoFragment = SettingsPersonalInfoFragment()
    private var settingsSecurityFragment = SettingsSecurityFragment()
    private var settingsAccountFragment = SettingsAccountFragment()
    private var settingsEditPersonalInfoFragment = EditPersonalInfoFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_part_exit)

        replaceFragment(SETTINGS_MAIN_FRAGMENT, 1)

        // back
        binding.buttonBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_left_part_enter, R.anim.slide_left_exit)
    }

    // (mode) 0: Exit, 1: Enter
    fun replaceFragment(fragmentNum: Int, mode: Int){
        val fb = supportFragmentManager.beginTransaction()
        when(mode){
            EXIT -> {
                fb.setCustomAnimations(R.anim.slide_left_part_enter, R.anim.slide_left_exit)
            }
            ENTER -> {
                fb.setCustomAnimations(R.anim.slide_right_enter, R.anim.slide_right_part_exit)
            }
        }

        when(fragmentNum){
            SETTINGS_MAIN_FRAGMENT -> {
                fb.replace(R.id.settings_pref, settingsMainFragment)
                fb.commit()
            }
            SETTINGS_PERSONAL_INFO_FRAGMENT -> {
                fb.replace(R.id.settings_pref, settingsPersonalInfoFragment)
                fb.addToBackStack(null)
                fb.commit()
            }
            SETTINGS_SECURITY_FRAGMENT -> {
                fb.replace(R.id.settings_pref, settingsSecurityFragment)
                fb.addToBackStack(null)
                fb.commit()
            }
            SETTINGS_ACCOUNT_FRAGMENT -> {
                fb.replace(R.id.settings_pref, settingsAccountFragment)
                fb.addToBackStack(null)
                fb.commit()
            }
            SETTINGS_EDIT_PERSONAL_INFO_FRAGMENT -> {
                fb.replace(R.id.settings_pref, settingsEditPersonalInfoFragment)
                fb.addToBackStack(null)
                fb.commit()
            }
            else -> {
                Toast.makeText(this, "존재하지 않는 fragment입니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun replaceTitle(fragmentNum: Int) {
        when(fragmentNum){
            SETTINGS_MAIN_FRAGMENT -> {
                binding.buttonSettingTitle.text = "설정"
            }
            SETTINGS_PERSONAL_INFO_FRAGMENT -> {
                binding.buttonSettingTitle.text = "개인정보 보호"
            }
            SETTINGS_SECURITY_FRAGMENT -> {
                binding.buttonSettingTitle.text = "보안"
            }
            SETTINGS_ACCOUNT_FRAGMENT -> {
                binding.buttonSettingTitle.text = "계정"
            }
            SETTINGS_EDIT_PERSONAL_INFO_FRAGMENT -> {
                binding.buttonSettingTitle.text = "개인정보"
            }
        }
    }

    companion object {
        const val SETTINGS_MAIN_FRAGMENT = 0
        const val SETTINGS_PERSONAL_INFO_FRAGMENT = 1
        const val SETTINGS_SECURITY_FRAGMENT = 2
        const val SETTINGS_ACCOUNT_FRAGMENT = 3
        const val SETTINGS_EDIT_PERSONAL_INFO_FRAGMENT = 31

        const val EXIT = 0
        const val ENTER = 1
    }
}