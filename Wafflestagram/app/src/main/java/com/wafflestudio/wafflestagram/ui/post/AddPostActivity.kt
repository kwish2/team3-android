package com.wafflestudio.wafflestagram.ui.post

import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.DocumentsContract
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.wafflestudio.wafflestagram.databinding.ActivityAddPostBinding
import dagger.hilt.android.AndroidEntryPoint
import ir.shahabazimi.instagrampicker.InstagramPicker
import ir.shahabazimi.instagrampicker.classes.MultiListener
import timber.log.Timber
import java.io.File
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.wafflestudio.wafflestagram.BuildConfig
import com.wafflestudio.wafflestagram.R
import com.wafflestudio.wafflestagram.model.User
import com.wafflestudio.wafflestagram.network.dto.AddPostRequest
import com.wafflestudio.wafflestagram.ui.main.MainActivity


@AndroidEntryPoint
class AddPostActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAddPostBinding
    private val viewModel: AddPostViewModel by viewModels()

    private lateinit var awsCredentials: BasicAWSCredentials
    private lateinit var s3Client: AmazonS3Client
    private lateinit var transferUtility : TransferUtility
    private lateinit var images : List<String>
    private var position = 0
    private var imageKeys : MutableList<String> = mutableListOf()
    private var isSelect = false
    private var users: List<User> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit)

        binding.imageSelected.isClickable = false
        binding.imageSelected.isEnabled = false

        if(!isSelect){
            showImagePicker()
        }

        awsCredentials = BasicAWSCredentials(BuildConfig.AWS_S3_ACCESS_KEY, BuildConfig.AWS_S3_SECRET_KEY)
        s3Client = AmazonS3Client(awsCredentials, Region.getRegion(Regions.AP_NORTHEAST_2))

        transferUtility = TransferUtility.builder().s3Client(s3Client).context(this).build()

        binding.buttonTag.setOnClickListener{
            openAddUserTagActivityForResult()
        }

        binding.imageSelected.setOnClickListener {
            val imageDialogFragment = ImageDialogFragment()
            val bundle = Bundle()
            bundle.putStringArray("images",images.toTypedArray())
            imageDialogFragment.arguments = bundle
            imageDialogFragment.show(
                supportFragmentManager, "ImageDialog"
            )
        }


        binding.buttonComplete.setOnClickListener {
            if(isSelect){
                startUpload()
                binding.buttonComplete.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
                binding.editContent.isEnabled = false
            }else{
                showImagePicker()
            }
        }

        binding.buttonBack.setOnClickListener {
            onBackPressed()
        }

        viewModel.feedResponse.observe(this, {response ->
            if(response.isSuccessful){

            }else{
                //에러메시지
            }
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        })
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
        if(result.resultCode == Activity.RESULT_OK){
            val data = result.data
            users = (data!!.getSerializableExtra("users")!! as ArrayList<User>).toList()
            if(users.isEmpty()){
                binding.textTagNumber.visibility = View.GONE
            }else{
                binding.textTagNumber.visibility = View.VISIBLE
            }
            binding.textTagNumber.text = users.size.toString() + "명"
        }
    }

    fun openAddUserTagActivityForResult(){
        val intent = Intent(this, AddUserTagActivity::class.java)
        intent.putExtra("images", images.toTypedArray())
        intent.putExtra("users", users.toTypedArray())
        resultLauncher.launch(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
    }

    private fun addPost(){
        val userTags = mutableListOf<String>()
        for(user in users){
            userTags.add(user.username!!)
        }
        val request = AddPostRequest(binding.editContent.text.toString(), imageKeys, emptyList(), userTags.toList())
        viewModel.addPost(request)
    }

    private fun startUpload(){
        upload(images[position])
    }

    private fun continueUpload(){
        position += 1
        if(position < images.size){
            upload(images[position])
        }else{
            addPost()
        }
    }

    private fun upload(image : String){
        val fileName = System.currentTimeMillis().toString()

        val uploadObserver = transferUtility.upload("waffle-team3-bucket", fileName, File(getRealPathFromURI(this, Uri.parse(image))))
        imageKeys.add(fileName)

        uploadObserver.setTransferListener(object :TransferListener{
            override fun onStateChanged(id: Int, state: TransferState?) {
                if(state == TransferState.COMPLETED){
                    //Timber.e(fileName)
                    Handler(Looper.getMainLooper()).postDelayed({
                        continueUpload()
                    }, 300)
                }else if(state == TransferState.FAILED || state == TransferState.WAITING_FOR_NETWORK){
                    Toast.makeText(this@AddPostActivity, "연결이 불안정합니다", Toast.LENGTH_LONG).show()
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

    private fun showImagePicker(){
        InstagramPicker(this).show(4,3,10, { addresses ->
            //저장
            images = addresses
            binding.imageSelected.setImageURI(Uri.parse(addresses[0]))
            binding.imageSelected.isClickable = true
            binding.imageSelected.isEnabled = true
            isSelect = true
            binding.buttonComplete.setImageResource(R.drawable.icon_check)
            binding.buttonTag.visibility = View.VISIBLE
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

    companion object{
        const val REQUEST_CODE_USER_TAG = 1000
    }
}