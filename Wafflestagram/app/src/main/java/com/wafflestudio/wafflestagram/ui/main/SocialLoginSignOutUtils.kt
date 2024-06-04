package com.wafflestudio.wafflestagram.ui.main

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import androidx.core.content.edit
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.wafflestudio.wafflestagram.R
import com.wafflestudio.wafflestagram.ui.login.LoginActivity
import com.wafflestudio.wafflestagram.ui.settings.SettingsActivity
import com.wafflestudio.wafflestagram.ui.settings.SettingsMainFragment
import timber.log.Timber
import javax.inject.Inject

object SocialLoginSignOutUtils {
    fun signOut(context: Context){
        // Facebook Sign Out
        val accessToken = AccessToken.getCurrentAccessToken()
        if(accessToken != null && !accessToken.isExpired) {
            Timber.d("Facebook Sign Out")
            LoginManager.getInstance().logOut()
        }

        // Google Sign Out
        if(GoogleSignIn.getLastSignedInAccount(context) != null) {
            // Configure sign-in to request the user's ID, email address, and basic
            // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.google_web_client_id))
                .requestEmail()
                .build()

            // Build a GoogleSignInClient with the options specified by gso.
            val googleSignInClient = GoogleSignIn.getClient(context, gso)
            Timber.d("Google Sign Out")
            googleSignInClient.signOut()
        }
    }
}