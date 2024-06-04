package com.wafflestudio.wafflestagram.ui.detail

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.wafflestudio.wafflestagram.databinding.FragmentDetailUserTaggedFeedBinding
import com.wafflestudio.wafflestagram.ui.main.UserFeedAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.lang.IllegalStateException
import javax.inject.Inject

@AndroidEntryPoint
class DetailUserTaggedFeedFragment: Fragment() {

    private lateinit var binding: FragmentDetailUserTaggedFeedBinding
    private val viewModel: DetailFeedViewModel by activityViewModels()

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var userTaggedFeedAdapter: DetailUserFeedAdapter
    private lateinit var userLayoutManager: GridLayoutManager
    private var pageNumber: Int = 0
    private var totalPage: Int = 1
    private var isLast: Boolean = false
    private var isFirst: Boolean = true
    private var isLoading: Boolean = false
    private var userId : Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailUserTaggedFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // val id = intent.getIntExtra("id", -1)
        userId = arguments!!.getInt("id", -1)

        // Setting Adapter and LayoutManager
        userTaggedFeedAdapter = DetailUserFeedAdapter{startActivity(
            Intent(activity, DetailTaggedFeedActivity::class.java)
                .putExtra("position", it)
                .putExtra("userId", userId)
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

        viewModel.getTaggedFeedsByUserId(userId, 0, 12)

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
                    userTaggedFeedAdapter.updateData(response.body()!!.content.toMutableList())
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
                viewModel.getTaggedFeedsByUserId(userId, pageNumber, 12)
            }
        }
    }
}