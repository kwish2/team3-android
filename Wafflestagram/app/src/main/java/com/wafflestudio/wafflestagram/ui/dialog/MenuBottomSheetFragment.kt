package com.wafflestudio.wafflestagram.ui.dialog

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.wafflestudio.wafflestagram.databinding.DialogReconfirmBinding
import com.wafflestudio.wafflestagram.databinding.FragmentMenuBottomSheetBinding
import com.wafflestudio.wafflestagram.ui.settings.SettingsActivity
import com.wafflestudio.wafflestagram.ui.settings.SettingsMainFragment

class MenuBottomSheetFragment(context: Context) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentMenuBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMenuBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSetting.setOnClickListener {
            val intent = Intent(context, SettingsActivity::class.java)
            startActivity(intent)
            dismiss()
        }

        binding.buttonCollection.setOnClickListener {
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
            //(FragmentComponentManager.findActivity(context) as? DetailFeedActivity)?.deleteFeed(feedId)
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

    companion object {
        const val TAG = "MenuBottomSheet"
    }
}