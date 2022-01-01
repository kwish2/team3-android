package com.wafflestudio.wafflestagram.ui.signup

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.wafflestudio.wafflestagram.databinding.ActivityAuthenticationByPhoneBinding
import com.wafflestudio.wafflestagram.databinding.DialogBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class AuthenticationByPhoneActivity : AppCompatActivity() {
    lateinit var binding: ActivityAuthenticationByPhoneBinding
    private lateinit var auth: FirebaseAuth
    private var storedVerificationId = ""
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var phoneNumber : String = ""

    private val callbacks by lazy {
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                binding.editCode.setText(credential.smsCode.toString())
                binding.editCode.isEnabled = false
                Handler(Looper.getMainLooper()).postDelayed({
                    verifyPhoneWithCode(credential)
                }, 1000)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                if(e is FirebaseAuthInvalidCredentialsException){
                    showDialog("  유효하지 않은 전화번호입니다.")
                }else if(e is FirebaseTooManyRequestsException){
                    showDialog("  이용할 수 있는 인증 요청 횟수를 초과하였습니다.")
                }
            }

            override fun onCodeSent(verificationId: String, forceResendingToken : PhoneAuthProvider.ForceResendingToken) {
                storedVerificationId = verificationId
                resendToken = forceResendingToken
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationByPhoneBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        auth.useAppLanguage()
        phoneNumber = intent.getStringExtra("phoneNumber")!!
        binding.textInput.text = intent.getStringExtra("phoneNumber")

        startPhoneVerification("+82"+phoneNumber!!.substring(1))

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
            val phoneAuthCredential = PhoneAuthProvider.getCredential(storedVerificationId, binding.editCode.text.toString())
            verifyPhoneWithCode(phoneAuthCredential)
        }
        binding.buttonResend.setOnClickListener{
            resendVerificationCode("+82"+phoneNumber!!.substring(1), resendToken)
        }
    }

    private fun startPhoneVerification(phoneNumber: String){
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(120L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun resendVerificationCode(phoneNumber: String, token: PhoneAuthProvider.ForceResendingToken?){
        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(120L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
        if(token != null){
            optionsBuilder.setForceResendingToken(token)
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }

    private fun verifyPhoneWithCode(phoneAuthCredential: PhoneAuthCredential){
        auth.signInWithCredential(phoneAuthCredential)
            .addOnCompleteListener(this){task ->
                if(task.isSuccessful){
                    auth.signOut()
                    val intent = Intent(this, SignUpDetailActivity::class.java)
                    intent.putExtra("phoneNumber", phoneNumber)
                    startActivity(intent)
                }else{
                    showDialog("  코드가 유효하지 않습니다. 새 코드를 요청하세요.")
                }
            }
    }

    private fun showDialog(contents: String){
        val dialogBinding = DialogBinding.inflate(layoutInflater)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setCancelable(false)

        val dialog = dialogBuilder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        resizeDialog(this, dialog, 0.65F, 0.35F)
        dialogBinding.textDialogContents.text = contents
        dialogBinding.buttonDialog.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun resizeDialog(context: Context, dialog: AlertDialog, width: Float, height: Float){
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.R){
            @Suppress("DEPRECATION")
            val display = windowManager.defaultDisplay
            val size = Point()
            @Suppress("DEPRECATION")
            display.getSize(size)
            val window = dialog.window
            val x = (size.x * width).toInt()
            val y = (size.y * height).toInt()

            window?.setLayout(x, y)
        }else{
            val bound = windowManager.currentWindowMetrics.bounds

            val window = dialog.window
            val x = (bound.width() * width).toInt()
            val y = (bound.height() * height).toInt()

            window?.setLayout(x, y)
        }
    }
}