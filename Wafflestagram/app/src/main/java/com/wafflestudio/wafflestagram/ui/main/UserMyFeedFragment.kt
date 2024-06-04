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
import com.wafflestudio.wafflestagram.databinding.FragmentUserMyFeedBinding
import com.wafflestudio.wafflestagram.ui.detail.DetailFeedActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.lang.IllegalStateException
import javax.inject.Inject

@AndroidEntryPoint
class UserMyFeedFragment: Fragment() {

    private lateinit var binding: FragmentUserMyFeedBinding
    private val viewModel: UserViewModel by activityViewModels()

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private var currentUserId: Int = -1 // -1: default value

    private lateinit var userFeedAdapter: UserFeedAdapter
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
        binding = FragmentUserMyFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUserId = sharedPreferences.getInt(UserFragment.CURRENT_USER_ID, -1)
        Timber.d(currentUserId.toString())

        // Setting Adapter and LayoutManager
        userFeedAdapter = UserFeedAdapter {
            startActivity(
                Intent(context, DetailFeedActivity::class.java)
                    .putExtra("position", it)
                    .putExtra("userId", currentUserId)
            )
        }
        // recyclerView -> 3행 Grid 형식으로 지정
        userLayoutManager = GridLayoutManager(context, 3).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup(){
                override fun getSpanSize(position: Int): Int {
                    return when(userFeedAdapter.getItemViewType(position)){
                        UserFeedAdapter.VIEW_TYPE_LOADING -> 3
                        UserFeedAdapter.VIEW_TYPE_FEED -> 1
                        else -> throw IllegalStateException("viewType must be 0 or 1")
                    }
                }
            }
        }
        binding.recyclerViewPhotos.apply {
            adapter = userFeedAdapter
            layoutManager = userLayoutManager
        }

        /*
        binding.recyclerViewPhotos.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastVisibleItemPosition = (userLayoutManager as GridLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                val itemTotalCount = userPhotoAdapter.itemCount-1

                if(!isLoading){
                    if(!binding.recyclerViewPhotos.canScrollVertically(1) && lastVisibleItemPosition == itemTotalCount && !isLast){
                        isLoading = true
                        viewModel.getMyFeeds(pageNumber, 12)
                    }
                }
            }
        })
         */
        viewModel.getMyFeeds(0, 12)

        viewModel.myPage.observe(viewLifecycleOwner, { response ->
            if(response.isSuccessful){
                isLoading = false
                totalPage = response.body()!!.totalPages
                pageNumber = response.body()!!.pageNumber + 1
                if(pageNumber > 0 && userFeedAdapter.itemCount > 0){
                    userFeedAdapter.deleteLoading()
                    userFeedAdapter.notifyItemRemoved(userFeedAdapter.itemCount)
                }
                isLast = response.body()!!.last
                isFirst = response.body()!!.fisrt
                if(isFirst){
                    userFeedAdapter.updateData(response.body()!!.content.toMutableList(), context!!)
                    userFeedAdapter.notifyDataSetChanged()
                }else{
                    userFeedAdapter.addData(response.body()!!.content.toMutableList())
                    userFeedAdapter.notifyItemRangeInserted((response.body()!!.pageNumber) * 10, response.body()!!.numberOfElements)
                }
                if(!response.body()!!.last){
                    userFeedAdapter.addLoading()
                    userFeedAdapter.notifyItemInserted(userFeedAdapter.itemCount)
                }
            }else{

            }
        })
    }

    fun getFeeds(){
        if(!isLoading){
            if(!isLast){
                isLoading = true
                viewModel.getMyFeeds(pageNumber, 12)
            }
        }
    }
}