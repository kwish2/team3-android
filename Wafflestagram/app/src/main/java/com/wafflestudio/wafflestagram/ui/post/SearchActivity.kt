package com.wafflestudio.wafflestagram.ui.post

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wafflestudio.wafflestagram.databinding.ActivitySearchBinding
import com.wafflestudio.wafflestagram.model.User
import com.wafflestudio.wafflestagram.ui.login.LoginActivity
import com.wafflestudio.wafflestagram.ui.main.MainActivity
import com.wafflestudio.wafflestagram.ui.main.SocialLoginSignOutUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySearchBinding
    private val viewModel : SearchViewModel by viewModels()
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var searchLayoutManager: LinearLayoutManager

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        searchAdapter = SearchAdapter()
        searchLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        binding.recyclerViewSearch.apply {
            adapter = searchAdapter
            layoutManager = searchLayoutManager
        }

        searchAdapter.setOnClickedListener(object : SearchAdapter.ButtonClickListener{
            override fun onClicked(user: User) {
                val resultIntent = Intent()
                resultIntent.putExtra("user", user)
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        })

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


        viewModel.page.observe(this, {response->
            if(response.isSuccessful and binding.searchView.query.isNotBlank()){
                searchAdapter.updateData(response.body()!!.content)
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
            }
        })
    }

    companion object{
        const val TOKEN = "token"
    }
}