package com.wafflestudio.wafflestagram.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.wafflestudio.wafflestagram.databinding.FragmentSettingsAccountBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsAccountFragment: Fragment() {

    private lateinit var binding: FragmentSettingsAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsAccountBinding.inflate(inflater, container, false)

        (activity as SettingsActivity).replaceTitle(SettingsActivity.SETTINGS_ACCOUNT_FRAGMENT)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*
        binding.buttonAccountPersonalInfo.setOnClickListener {
            (activity as SettingsActivity).replaceFragment(SettingsActivity.SETTINGS_EDIT_PERSONAL_INFO_FRAGMENT, 1)
        }

         */
    }
}