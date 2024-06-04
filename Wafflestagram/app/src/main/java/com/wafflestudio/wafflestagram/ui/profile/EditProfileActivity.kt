package com.wafflestudio.wafflestagram.ui.profile

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.bumptech.glide.Glide
import com.wafflestudio.wafflestagram.BuildConfig
import com.wafflestudio.wafflestagram.R
import com.wafflestudio.wafflestagram.databinding.ActivityEditProfileBinding
import com.wafflestudio.wafflestagram.network.dto.SetProfilePhotoRequest
import com.wafflestudio.wafflestagram.network.dto.UpdateUserRequest
import com.wafflestudio.wafflestagram.ui.login.LoginActivity
import com.wafflestudio.wafflestagram.ui.main.MainActivity
import com.wafflestudio.wafflestagram.ui.main.SocialLoginSignOutUtils
import com.wafflestudio.wafflestagram.ui.settings.SettingsActivity
import dagger.hilt.android.AndroidEntryPoint
import ir.shahabazimi.instagrampicker.InstagramPicker
import ir.shahabazimi.instagrampicker.classes.SingleListener
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileActivity: AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val viewModel: EditProfileViewModel by viewModels()

    private lateinit var awsCredentials: BasicAWSCredentials
    private lateinit var s3Client: AmazonS3Client
    private lateinit var transferUtility : TransferUtility

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit)
        awsCredentials = BasicAWSCredentials(
            BuildConfig.AWS_S3_ACCESS_KEY,
            BuildConfig.AWS_S3_SECRET_KEY
        )
        s3Client = AmazonS3Client(awsCredentials, Region.getRegion(Regions.AP_NORTHEAST_2))

        transferUtility = TransferUtility.builder().s3Client(s3Client).context(this).build()

        binding.buttonChangeProfile.setOnClickListener {
            InstagramPicker(this).show(1, 1, SingleListener { address ->
                upload(address)
            })
        }

        // UserFragment에서 진입할 때 Extra로 유저정보 받아오기
        binding.inputName.setText(intent.getStringExtra("name"))
        binding.inputUsername.setText(intent.getStringExtra("username"))
        binding.inputWebsite.setText(intent.getStringExtra("website"))
        binding.inputBio.setText(intent.getStringExtra("bio"))
        val id = intent.getIntExtra("id", 0)

        viewModel.getProfilePhoto(id)

        binding.buttonClose.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
        }

        binding.buttonCheck.setOnClickListener{
            if(binding.inputUsername.text.isNotBlank()){
                binding.textInputLayoutUsername.error = null
            }else{
                binding.textInputLayoutUsername.error = "사용자 이름을 입력해 주세요"
            }
            if(binding.inputWebsite.text.isBlank() || android.util.Patterns.WEB_URL.matcher(binding.inputWebsite.text).matches()){
                binding.textInputLayoutWebsite.error = null
            }else{
                binding.textInputLayoutWebsite.error = "올바른 웹사이트를 입력하세요"
            }
            if(binding.inputUsername.text.isNotBlank() && (binding.inputWebsite.text.isBlank() || android.util.Patterns.WEB_URL.matcher(binding.inputWebsite.text).matches())){
                val updateUserRequest = UpdateUserRequest(
                    binding.inputName.text.toString(), binding.inputUsername.text.toString(),
                    binding.inputWebsite.text.toString(), binding.inputBio.text.toString()
                )
                viewModel.updateUser(updateUserRequest)
            }
        }

        /*
        binding.buttonPersonalInfo.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("fragmentNum", SettingsActivity.SETTINGS_EDIT_PERSONAL_INFO_FRAGMENT)
            Timber.d(intent.getIntExtra("fragmentNum", 1).toString())
            startActivity(intent)
        }

         */

        viewModel.image.observe(this, {response->
            if(response.isSuccessful){
                Glide.with(this).load(response.body()!!.profilePhotoURL).centerCrop().into(binding.buttonUserImage)
            }else if(response.code() == 401){
                Toast.makeText(this, "다시 로그인해주세요", Toast.LENGTH_SHORT).show()
                // Remove Token(Sign Out)
                sharedPreferences.edit{
                    putString(MainActivity.TOKEN, "")
                    putInt(MainActivity.CURRENT_USER_ID, -1)
                    putBoolean(MainActivity.IS_LOGGED_IN, false)
                }
                SocialLoginSignOutUtils.signOut(this)
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
        })

        viewModel.response.observe(this, {
            viewModel.getProfilePhoto(id)
        })

        viewModel.fetchUserInfoResponse.observe(this, {response->
            if(response.isSuccessful){
                binding.textInputLayoutUsername.error = null
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("fragment", 2)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }else if(response.code() == 409){
                binding.textInputLayoutUsername.error = "이미 사용중인 사용자 이름입니다."
            }

        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(isFinishing){
            overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
        }
    }

    private fun upload(image : String){
        val fileName = System.currentTimeMillis().toString()

        val uploadObserver = transferUtility.upload("waffle-team3-bucket", fileName, File(getRealPathFromURI(this, Uri.parse(image))!!))


        uploadObserver.setTransferListener(object : TransferListener {
            override fun onStateChanged(id: Int, state: TransferState?) {
                if(state == TransferState.COMPLETED){
                    //서버에 사진 변경 리퀘스트 보내기, fileName
                    val request = SetProfilePhotoRequest(fileName)
                    viewModel.setProfilePhoto(request)
                }
            }
            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                if(bytesCurrent == bytesTotal){
                }
            }
            override fun onError(id: Int, ex: Exception?) {
                Timber.e(ex)
            }
        })
    }

    fun getRealPathFromURI(context: Context?, uri: Uri?): String? {

        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {

            // ExternalStorageProvider
            if (isExternalStorageDocument(uri!!)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split: Array<String?> = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                return if ("primary".equals(type, ignoreCase = true)) {
                    (Environment.getExternalStorageDirectory().toString() + "/"
                            + split[1])
                } else {
                    val SDcardpath = getRemovableSDCardPath(context)?.split("/Android".toRegex())!!.toTypedArray()[0]
                    SDcardpath + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(id))
                return getDataColumn(context!!, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split: Array<String?> = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(
                    context!!, contentUri, selection,
                    selectionArgs)
            }
        } else if (uri != null) {
            if ("content".equals(uri.getScheme(), ignoreCase = true)) {
                // Return the remote address
                return if (isGooglePhotosUri(uri)) uri.getLastPathSegment() else getDataColumn(
                    context!!, uri, null, null)
            } else if ("file".equals(uri.getScheme(), ignoreCase = true)) {
                return uri.getPath()
            }
        }
        return null
    }

    fun getDataColumn(
        context: Context, uri: Uri?,
        selection: String?, selectionArgs: Array<String?>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(
                uri!!, projection,
                selection, selectionArgs, null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri
            .authority
    }

    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri
            .authority
    }

    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri
            .authority
    }

    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri
            .authority
    }

    fun getRemovableSDCardPath(context: Context?): String? {
        val storages = ContextCompat.getExternalFilesDirs(context!!, null)
        return if (storages.size > 1 && storages[0] != null && storages[1] != null) storages[1].toString() else ""
    }
}