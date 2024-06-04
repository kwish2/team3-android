package com.wafflestudio.wafflestagram.ui.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wafflestudio.wafflestagram.databinding.FragmentImageDialogBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImageDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentImageDialogBinding
    private lateinit var imageDialogAdapter: ImageDialogAdapter
    private lateinit var imageDialogLayoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentImageDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val array = arguments?.getStringArray("images")

        imageDialogAdapter = ImageDialogAdapter()
        imageDialogLayoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)

        binding.viewPagerImage.apply {
            adapter = imageDialogAdapter
        }

        imageDialogAdapter.updateData(array?.toList()!!)
    }


}