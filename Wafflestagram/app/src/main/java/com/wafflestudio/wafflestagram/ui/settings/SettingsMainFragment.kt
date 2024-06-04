package com.wafflestudio.wafflestagram.ui.settings

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.wafflestudio.wafflestagram.R
import com.wafflestudio.wafflestagram.databinding.FragmentSettingsMainBinding
import com.wafflestudio.wafflestagram.ui.login.LoginActivity.Companion.IS_LOGGED_IN
import com.wafflestudio.wafflestagram.ui.main.MainActivity
import com.wafflestudio.wafflestagram.ui.main.SocialLoginSignOutUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsMainFragment: Fragment() {

    private lateinit var binding: FragmentSettingsMainBinding
    private lateinit var callback: OnBackPressedCallback

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var _context: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _context = container!!.context
        binding = FragmentSettingsMainBinding.inflate(inflater, container, false)

        (activity as SettingsActivity).replaceTitle(SettingsActivity.SETTINGS_MAIN_FRAGMENT)

        /** fragments in setting (implemented) **/
        binding.buttonPersonalInfo.setOnClickListener {
            (activity as SettingsActivity).replaceFragment(SettingsActivity.SETTINGS_PERSONAL_INFO_FRAGMENT, 1)
        }

        binding.buttonSecurity.setOnClickListener {
            (activity as SettingsActivity).replaceFragment(SettingsActivity.SETTINGS_SECURITY_FRAGMENT, 1)
        }

        binding.buttonAccount.setOnClickListener {
            (activity as SettingsActivity).replaceFragment(SettingsActivity.SETTINGS_ACCOUNT_FRAGMENT, 1)
        }

        /** other menu in setting (not yet implemented) **/

        // sign out button (include social login sign out)
        binding.buttonSignout.setOnClickListener {
            // Remove Token(Sign Out)
            sharedPreferences.edit{
                putString(MainActivity.TOKEN, "")
                putInt(MainActivity.CURRENT_USER_ID, -1)
                putBoolean(MainActivity.IS_LOGGED_IN, false)
            }
            SocialLoginSignOutUtils.signOut(context!!)
            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

            }
        }
    }

    override fun onDetach() {
        super.onDetach()
    }

    companion object {
        const val TOKEN = "token"
        const val CURRENT_USER_ID = "currentUserId"
    }
}