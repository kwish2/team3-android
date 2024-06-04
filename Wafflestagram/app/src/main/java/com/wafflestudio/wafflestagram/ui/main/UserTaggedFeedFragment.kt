package com.wafflestudio.wafflestagram.ui.main

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.wafflestudio.wafflestagram.databinding.FragmentUserTaggedFeedBinding
import com.wafflestudio.wafflestagram.ui.detail.DetailFeedActivity
import com.wafflestudio.wafflestagram.ui.detail.DetailTaggedFeedActivity
import com.wafflestudio.wafflestagram.ui.detail.DetailUserFeedAdapter
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.lang.IllegalStateException
import javax.inject.Inject

@AndroidEntryPoint
class UserTaggedFeedFragment: Fragment() {

    private lateinit var binding: FragmentUserTaggedFeedBinding
    private val viewModel: UserViewModel by activityViewModels()

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private var currentUserId: Int = -1 // -1: default value

    private lateinit var userTaggedFeedAdapter: UserFeedAdapter
    private lateinit var userLayoutManager: GridLayoutManager
    private var pageNumber: Int = 0
    private var totalPage: Int = 1
    private var isLast: Boolean = false
    private var isFirst: Boolean = true
    private var isLoading: Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserTaggedFeedBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUserId = sharedPreferences.getInt(UserFragment.CURRENT_USER_ID, -1)
        Timber.d(currentUserId.toString())

        // Setting Adapter and LayoutManager
        userTaggedFeedAdapter = UserFeedAdapter{startActivity(
            Intent(activity, DetailTaggedFeedActivity::class.java)
                .putExtra("position", it)
                .putExtra("userId", currentUserId)
        )}
        userLayoutManager = GridLayoutManager(context, 3).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup(){
                override fun getSpanSize(position: Int): Int {
                    return when(userTaggedFeedAdapter.getItemViewType(position)){
                        UserFeedAdapter.VIEW_TYPE_LOADING -> 3
                        UserFeedAdapter.VIEW_TYPE_FEED -> 1
                        else -> throw IllegalStateException("viewType must be 0 or 1")
                    }
                }
            }
        }
        binding.recyclerViewTaggedPhotos.apply {
            adapter = userTaggedFeedAdapter
            layoutManager = userLayoutManager
        }

        viewModel.getTaggedFeedsByUserId(currentUserId, 0, 12)

        viewModel.taggedPage.observe(viewLifecycleOwner, { response ->
            if(response.isSuccessful){
                isLoading = false
                totalPage = response.body()!!.totalPages
                pageNumber = response.body()!!.pageNumber + 1
                if(pageNumber > 0 && userTaggedFeedAdapter.itemCount > 0){
                    userTaggedFeedAdapter.deleteLoading()
                    userTaggedFeedAdapter.notifyItemRemoved(userTaggedFeedAdapter.itemCount)
                }
                isLast = response.body()!!.last
                isFirst = response.body()!!.fisrt
                if(isFirst){
                    userTaggedFeedAdapter.updateData(response.body()!!.content.toMutableList(), context!!)
                    userTaggedFeedAdapter.notifyDataSetChanged()
                }else{
                    userTaggedFeedAdapter.addData(response.body()!!.content.toMutableList())
                }
                if(!response.body()!!.last){
                    userTaggedFeedAdapter.addLoading()
                    userTaggedFeedAdapter.notifyItemInserted(userTaggedFeedAdapter.itemCount)
                }
            }else{

            }
        })
    }

    fun getFeeds(){
        if(!isLoading){
            if(!isLast){
                isLoading = true
                viewModel.getTaggedFeedsByUserId(currentUserId, pageNumber, 12)
            }
        }
    }
}