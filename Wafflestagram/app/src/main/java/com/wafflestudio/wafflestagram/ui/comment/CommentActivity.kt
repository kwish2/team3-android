package com.wafflestudio.wafflestagram.ui.comment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.TypedValue
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.wafflestudio.wafflestagram.R
import com.wafflestudio.wafflestagram.databinding.ActivityCommentBinding
import com.wafflestudio.wafflestagram.databinding.DialogReconfirmBinding
import com.wafflestudio.wafflestagram.model.Comment
import com.wafflestudio.wafflestagram.network.dto.AddCommentRequest
import com.wafflestudio.wafflestagram.network.dto.AddReplyRequest
import com.wafflestudio.wafflestagram.ui.login.LoginActivity
import com.wafflestudio.wafflestagram.ui.main.FeedFragment
import com.wafflestudio.wafflestagram.ui.main.MainActivity
import com.wafflestudio.wafflestagram.ui.main.SocialLoginSignOutUtils
import com.wafflestudio.wafflestagram.ui.settings.SettingsMainFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CommentActivity : AppCompatActivity(), CommentInterface{

    private lateinit var binding: ActivityCommentBinding
    private val viewModel: CommentViewModel by viewModels()
    private lateinit var commentAdapter : CommentAdapter
    private lateinit var commentLayoutManager: LinearLayoutManager
    private var isReply : Boolean = false
    private var commentId : Int = 0
    private var position: Int = 0

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit)

        commentAdapter = CommentAdapter(this)
        commentLayoutManager = LinearLayoutManager(this)

        binding.recyclerViewComment.apply {
            adapter = commentAdapter
            layoutManager = commentLayoutManager
        }

        val id = intent.getIntExtra("id", 0)

        viewModel.getMyInfo()
        viewModel.getFeedById(id)

        binding.editComment.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if(p0?.length!! > 0){
                    binding.buttonComment.isClickable = true
                    binding.buttonComment.isEnabled = true
                    binding.buttonComment.setTextColor(Color.parseColor("#368AFF"))
                }else{
                    binding.buttonComment.isClickable = false
                    binding.buttonComment.isEnabled = false
                    binding.buttonComment.setTextColor(Color.parseColor("#9DCFFF"))
                }
            }
        })

        binding.editComment.imeOptions = EditorInfo.IME_ACTION_SEND
        binding.editComment.setRawInputType(InputType.TYPE_CLASS_TEXT)

        binding.editComment.setOnKeyListener { view, i, keyEvent ->
            if(i == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_DOWN){
                if(binding.buttonComment.isEnabled){
                    binding.buttonComment.performClick()
                    true
                }
            }
            false
        }

        binding.buttonComment.setOnClickListener {
            if(!isReply){
                val request = AddCommentRequest(binding.editComment.text.toString())
                viewModel.addComment(id, request)
            }else{
                val request = AddReplyRequest(binding.editComment.text.toString())
                viewModel.addReply(commentId, request)
                isReply = false
                binding.layoutReply.visibility = View.GONE
            }
            binding.editComment.text = null
        }

        setEmojiButton(binding.buttonEmoji1)
        setEmojiButton(binding.buttonEmoji2)
        setEmojiButton(binding.buttonEmoji3)
        setEmojiButton(binding.buttonEmoji4)
        setEmojiButton(binding.buttonEmoji5)
        setEmojiButton(binding.buttonEmoji6)
        setEmojiButton(binding.buttonEmoji7)
        setEmojiButton(binding.buttonEmoji8)

        binding.buttonReplyClose.setOnClickListener {
            isReply = false
            binding.layoutReply.visibility = View.GONE
        }

        viewModel.feed.observe(this, {response ->
            if(response.isSuccessful){
                commentAdapter.updateData(response.body()!!)
            }else if(response.code() == 401){
                Toast.makeText(this, "다시 로그인해주세요", Toast.LENGTH_SHORT).show()
                // Remove Token(Sign Out)
                sharedPreferences.edit{
                    putString(MainActivity.TOKEN, "")
                    putInt(MainActivity.CURRENT_USER_ID, -1)
                    putBoolean(MainActivity.IS_LOGGED_IN, false)
                }
                SocialLoginSignOutUtils.signOut(this)
            }else{
                Toast.makeText(this, response.errorBody()?.string()!!, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.comment.observe(this , {response ->
            if(response.isSuccessful){
                viewModel.getFeedById(id)
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
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }else{
                Toast.makeText(this, response.errorBody()?.string()!!, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.reply.observe(this, {response->
            if(response.isSuccessful){
                viewModel.getFeedById(id)
                commentAdapter.expandGroup(position, false)
            }
        })

        viewModel.myInfo.observe(this, { response->
            if(response.isSuccessful){
                Glide.with(this).load(response.body()?.profilePhotoURL).centerCrop().into(binding.imageUserProfile)
                commentAdapter.updateUser(response.body()!!)
            }
        })

        binding.buttonBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
        }
        viewModel.deleteCommentResponse.observe(this, {response->
            if(response.isSuccessful){
                viewModel.getFeedById(id)
                //commentAdapter.notifyGroupRemove(position)
            }
        })

    }

    override fun deleteComment(id: Int, position: Int) {
        showDialog(DELETE_TYPE_COMMENT, id, position)
    }

    override fun addReply(comment: Comment, position: Int) {
        isReply = true
        binding.layoutReply.visibility = View.VISIBLE
        commentId = comment.id.toInt()
        binding.textReplyUsername.text = comment.writer.username
        this.position = position
    }

    override fun deleteReply(id: Int, position: Int) {
        showDialog(DELETE_TYPE_REPLY, id, position)
    }

    private fun showDialog(type: Int, id: Int, position: Int){
        val dialogBinding = DialogReconfirmBinding.inflate(layoutInflater)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setCancelable(false)

        val dialog = dialogBuilder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        resizeDialog(this, dialog, 0.65F, 0.35F)
        when(type){
            DELETE_TYPE_COMMENT -> dialogBinding.textDialogContents.text = "이 댓글을\n삭제하시겠어요?"
            DELETE_TYPE_REPLY -> dialogBinding.textDialogContents.text = "이 답글을\n삭제하시겠어요?"
            else -> {}
        }
        dialogBinding.buttonConfirm.setOnClickListener {
            //delete Comment
            //(FragmentComponentManager.findActivity(context) as? DetailFeedActivity)?.deleteFeed(feedId)
            if(type == DELETE_TYPE_COMMENT){
                viewModel.deleteComment(id)
            }else{
                viewModel.deleteReply(id)
            }
            dialog.dismiss()
        }
        dialogBinding.buttonCancel.setOnClickListener {
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

    override fun onBackPressed() {
        super.onBackPressed()
        if(isFinishing){
            overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setEmojiButton(button: TextView){
        button.setOnTouchListener { view, motionEvent ->
            when(motionEvent.action){
                MotionEvent.ACTION_DOWN ->{
                    button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21F)
                }
                MotionEvent.ACTION_UP ->{
                    button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23F)
                }
            }
            false
        }
        button.setOnClickListener {
            var str = binding.editComment.text.toString()
            str += button.text.toString()
            binding.editComment.setText(str)
            binding.editComment.setSelection(str.length)
        }
    }

    companion object{
        const val DELETE_TYPE_COMMENT = 0
        const val DELETE_TYPE_REPLY = 1
    }
}