package com.wafflestudio.wafflestagram.ui.main

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wafflestudio.wafflestagram.databinding.FragmentSearchBinding
import com.wafflestudio.wafflestagram.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment(), SearchInterface {

    private lateinit var binding: FragmentSearchBinding
    private val viewModel: SearchViewModel by activityViewModels()
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var searchLayoutManager: LinearLayoutManager

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchAdapter = SearchAdapter(this)
        searchLayoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        binding.recyclerViewSearch.apply {
            adapter = searchAdapter
            layoutManager = searchLayoutManager
        }


        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(!newText.isNullOrBlank()){
                    viewModel.search(newText.toString())
                }else{
                    searchAdapter.deleteData()
                }
                return false
            }

        })

        viewModel.getMe()

        viewModel.page.observe(this, {response ->
            if(response.isSuccessful and binding.searchView.query.isNotBlank()){
                searchAdapter.updateData(response.body()!!.content)
            }else if(response.code() == 401){
                Toast.makeText(context, "다시 로그인해주세요", Toast.LENGTH_SHORT).show()
                sharedPreferences.edit {
                    putString(FeedFragment.TOKEN, "")
                }
                val intent = Intent(context, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
        })

        viewModel.followPage.observe(this,{response->
            if(response.isSuccessful){
                searchAdapter.updateFollowing(response.body()!!.content)
            }
        })

        viewModel.user.observe(this, {reponse ->
            if(reponse.isSuccessful){
                searchAdapter.updateUser(reponse.body()!!)
                viewModel.getMyFollowing(0, 500)
            }
        })
    }

    /*override fun checkFollowing(id: Int) : Response<Boolean> {
        return viewModel.checkFollowing(id)
    }*/

    override fun follow(id: Int){
        viewModel.follow(id)
    }

    override fun unfollow(id: Int){
        viewModel.unfollow(id)
    }
}