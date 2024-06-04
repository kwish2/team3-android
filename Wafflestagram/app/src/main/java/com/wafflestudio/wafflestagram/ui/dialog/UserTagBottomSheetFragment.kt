package com.wafflestudio.wafflestagram.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.wafflestudio.wafflestagram.databinding.FragmentUserTagBottomSheetBinding
import com.wafflestudio.wafflestagram.model.User
import com.wafflestudio.wafflestagram.model.UserTag
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserTagBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentUserTagBottomSheetBinding
    private lateinit var userTagBottomSheetAdapter: UserTagBottomSheetAdapter
    private lateinit var userTagBottomSheetLayoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserTagBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userTagBottomSheetAdapter = UserTagBottomSheetAdapter()
        userTagBottomSheetLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        binding.recyclerViewUserTag.apply {
            adapter = userTagBottomSheetAdapter
            layoutManager = userTagBottomSheetLayoutManager
        }

        val userTags = (arguments?.getSerializable("userTags") as Array<User>).toList()
        if(!userTags.isNullOrEmpty()){
            userTagBottomSheetAdapter.updateData(userTags)
        }
    }

    companion object{
        const val TAG = "tag"
    }
}