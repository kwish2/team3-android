package com.wafflestudio.wafflestagram.ui.dialog

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.dynamiclinks.ktx.*
import com.google.firebase.ktx.Firebase
import com.wafflestudio.wafflestagram.databinding.DialogReconfirmBinding
import com.wafflestudio.wafflestagram.databinding.FragmentFeedBottomSheetBinding
import com.wafflestudio.wafflestagram.ui.detail.DetailFeedActivity
import com.wafflestudio.wafflestagram.ui.post.EditPostActivity
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.internal.managers.FragmentComponentManager
import timber.log.Timber

@AndroidEntryPoint
class FeedBottomSheetFragment(context: Context) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentFeedBottomSheetBinding
    private var feedId: Int = 0
    private var position: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        feedId = arguments?.getInt("feedId")!!
        val userId = arguments?.getInt("userId")
        val currUserId = arguments?.getInt("currUserId")
        position = arguments?.getInt("position")!!
        val activityNum = arguments?.getInt("activity")


        if(userId == currUserId){
            binding.buttonDelete.visibility = View.VISIBLE
            binding.buttonEdit.visibility = View.VISIBLE
        }else{
            binding.buttonDelete.visibility = View.GONE
            binding.buttonEdit.visibility = View.GONE
        }

        binding.buttonShare.setOnClickListener {
            createDynamicLink()
        }

        binding.buttonDelete.setOnClickListener {
            showDialog()
            dismiss()
        }

        binding.buttonEdit.setOnClickListener {
            val intent = Intent(activity, EditPostActivity::class.java)
            intent.putExtra("feedId", feedId)
            intent.putExtra("userId", userId)
            intent.putExtra("position", position)
            intent.putExtra("activity", activityNum)
            startActivity(intent)
            dismiss()
        }
    }

    private fun showDialog(){
        val dialogBinding = DialogReconfirmBinding.inflate(layoutInflater)
        val dialogBuilder = AlertDialog.Builder(context!!)
            .setView(dialogBinding.root)
            .setCancelable(false)

        val dialog = dialogBuilder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        resizeDialog(context!!, dialog, 0.65F, 0.35F)
        dialogBinding.buttonConfirm.setOnClickListener {
            //delete feed
            onClickedListener.onClicked(feedId, position)
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

    fun createDynamicLink(){
        val dynamicLink = Uri.parse("https://wafflestagram.page.link/?link=https://wafflestudio.com/?content=$feedId&apn=com.wafflestudio.wafflestagram&amv=2&afl=https://github.com/wafflestudio19-5/team3-android/issues/46&st=Wafflestagram&sd=%EC%97%AC%EA%B8%B0%EB%A5%BC+%EB%88%8C%EB%9F%AC+%EB%A7%81%ED%81%AC%EB%A5%BC+%ED%99%95%EC%9D%B8%ED%95%98%EC%84%B8%EC%9A%94.&si=https://raw.githubusercontent.com/wafflestudio/19.5-rookies/master/wafflestudio_logo.png"
            +"&ofl=https://github.com/wafflestudio19-5/team3-android/issues/46"
        )
        shortenLongLink(dynamicLink)
    }

    fun shortenLongLink(dynamicLink: Uri) {
        val shortLinkTask = Firebase.dynamicLinks.shortLinkAsync {
            longLink = dynamicLink
        }.addOnSuccessListener { (shortLink, flowChartLink) ->
            shareLink(shortLink!!)
        }.addOnFailureListener {
            e -> Timber.e(e)
        }
    }

    fun shareLink(dynamicLink: Uri){
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, dynamicLink.toString())
            type = "text/plain"
        }
        val intent = Intent.createChooser(sendIntent, "share")
        context?.startActivity(intent)
        dismiss()
    }

    interface ButtonClickListener{
        fun onClicked(id: Int, position: Int)
    }

    private lateinit var onClickedListener: ButtonClickListener

    fun setOnClickedListener(listener: ButtonClickListener){
        onClickedListener = listener
    }

    companion object {
        const val TAG = "FeedBottomSheet"
    }
}