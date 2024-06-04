package com.wafflestudio.wafflestagram.ui.main

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wafflestudio.wafflestagram.databinding.FragmentFeedBinding
import com.wafflestudio.wafflestagram.network.dto.FeedPageRequest
import com.wafflestudio.wafflestagram.ui.login.LoginActivity
import com.wafflestudio.wafflestagram.ui.post.AddPostActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FeedFragment : Fragment() ,FeedInterface {

    private lateinit var binding: FragmentFeedBinding
    private val viewModel: FeedViewModel by activityViewModels()
    private lateinit var feedAdapter: FeedAdapter
    private lateinit var feedLayoutManager: LinearLayoutManager
    private var itemPosition : Int = 0
    private var pageNumber: Int = 0
    private var totalPage: Int = 1
    private var isLast: Boolean = false
    private var isFirst: Boolean = true
    private var isLoading: Boolean = false


    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        feedAdapter = FeedAdapter(this)
        feedLayoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        binding.recyclerViewFeed.apply {
            adapter = feedAdapter
            layoutManager = feedLayoutManager
        }

        binding.buttonLogo.setOnClickListener{
            binding.recyclerViewFeed.smoothScrollToPosition(0)
        }

        binding.buttonAdd.setOnClickListener{
            val intent = Intent(context, AddPostActivity::class.java)
            startActivity(intent)
        }
        itemPosition = 0
        pageNumber = 0
        isLast = false
        viewModel.getMe()
        feedAdapter.clearData()
        val request = FeedPageRequest(pageNumber, 10)
        viewModel.getFeeds(request)

        binding.refreshLayoutFeed.setColorSchemeColors(*intArrayOf(Color.parseColor("#F6F6F6"),Color.parseColor("#D5D5D5"),Color.parseColor("#A6A6A6"),Color.parseColor("#D5D5D5")))
        binding.refreshLayoutFeed.setOnRefreshListener {

            Handler(Looper.getMainLooper()).postDelayed({
                binding.refreshLayoutFeed.setRefreshing(false)
            }, 1000)
            feedAdapter.clearData()
            feedAdapter.notifyItemRangeRemoved(0, feedAdapter.itemCount)
            if(!isLoading){
                pageNumber = 0
                isLast = false
                val request = FeedPageRequest(pageNumber, 10)
                viewModel.getFeeds(request)
                isLoading = true
            }
        }

        binding.recyclerViewFeed.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastVisibleItemPosition = (feedLayoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                val itemTotalCount = feedAdapter.itemCount-1

                if(!isLoading){
                    if(!binding.recyclerViewFeed.canScrollVertically(1) && lastVisibleItemPosition == itemTotalCount && !isLast){
                        isLoading = true
                        val request = FeedPageRequest(pageNumber, 10)
                        viewModel.getFeeds(request)
                    }else if(isLast){
                        //feedAdapter.deleteLoading()
                    }
                }
                val topRowVerticalPosition = if (recyclerView == null || recyclerView.childCount === 0) 0 else recyclerView.getChildAt(0).top
                binding.refreshLayoutFeed.isEnabled = topRowVerticalPosition >= 0
            }
        })

        viewModel.page.observe(viewLifecycleOwner, {response ->
            if(response.isSuccessful){
                if(response.body()!!.totalElements== 0){
                    binding.layoutWelcome.visibility = View.VISIBLE
                    binding.recyclerViewFeed.visibility = View.GONE
                }else{
                    binding.layoutWelcome.visibility = View.GONE
                    binding.recyclerViewFeed.visibility = View.VISIBLE
                }
                isLoading = false
                totalPage = response.body()!!.totalPages
                pageNumber = response.body()!!.pageNumber + 1
                if(pageNumber > 0 && feedAdapter.itemCount > 0){
                    feedAdapter.deleteLoading()
                    feedAdapter.notifyItemRemoved(feedAdapter.itemCount)
                }
                isLast = response.body()!!.last
                isFirst = response.body()!!.fisrt
                if(isFirst){
                    feedAdapter.updateData(response.body()!!.content.toMutableList())
                    feedAdapter.notifyDataSetChanged()
                }else{
                    feedAdapter.addData(response.body()!!.content.toMutableList())
                    feedAdapter.notifyItemRangeInserted((response.body()!!.pageNumber) * 10, response.body()!!.numberOfElements)
                }

                if(!response.body()!!.last){
                    feedAdapter.addLoading()
                    feedAdapter.notifyItemInserted(feedAdapter.itemCount)
                }
            }else if(response.code() == 401){
                // 토큰 만료시 토큰 삭제
                Toast.makeText(context, "다시 로그인해주세요", Toast.LENGTH_SHORT).show()
                sharedPreferences.edit {
                    putString(TOKEN, "")
                }
                val intent = Intent(context, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
        })

        viewModel.user.observe(viewLifecycleOwner, {response ->
            if(response.isSuccessful){
                feedAdapter.updateUser(response.body()!!)
            }else if(response.code() == 401){

            }
        })

    }

    fun setRecyclerviewPosition(position: Int){
        binding.recyclerViewFeed.smoothScrollToPosition(position)
    }

    fun clearRecyclerView(){
        //feedAdapter.clearData()
        //feedAdapter.notifyDataSetChanged()
    }

    override fun deleteFeed(id: Int, position: Int){
        viewModel.deleteFeed(id)
        feedAdapter.deleteData(position)
    }

    override fun like(id:Int ,position: Int){
        viewModel.like(id)
        itemPosition = position
    }

    override fun unlike(id: Int, position: Int){
        viewModel.unlike(id)
        itemPosition = position
    }

    companion object{
        const val TOKEN = "token"
    }

}