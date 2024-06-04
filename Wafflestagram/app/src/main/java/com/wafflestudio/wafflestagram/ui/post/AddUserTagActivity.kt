package com.wafflestudio.wafflestagram.ui.post

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wafflestudio.wafflestagram.databinding.ActivityAddUserTagBinding
import com.wafflestudio.wafflestagram.model.Photo
import com.wafflestudio.wafflestagram.model.User
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddUserTagActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAddUserTagBinding
    private val viewModel : AddUserTagViewModel by viewModels()
    private lateinit var addUserTagAdapter: AddUserTagAdapter
    private lateinit var addUSerTagLayoutManager: LinearLayoutManager
    private lateinit var viewPagerImageAdapter: ViewPagerImageAdapter

    private var users = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddUserTagBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.initializeUsers()

        addUserTagAdapter = AddUserTagAdapter()
        addUSerTagLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        binding.recyclerViewUserTag.apply {
            adapter = addUserTagAdapter
            layoutManager = addUSerTagLayoutManager
        }

        addUserTagAdapter.setOnClickedListener(object : AddUserTagAdapter.ButtonClickListener{
            override fun onClicked(position: Int) {
                viewModel.removeUser(position)
            }
        })

        viewPagerImageAdapter = ViewPagerImageAdapter()
        binding.viewPagerImage.adapter = viewPagerImageAdapter

        val images = intent.getStringArrayExtra("images")?.toList()
        if(!images.isNullOrEmpty()){
            viewPagerImageAdapter.updateImageUris(images)
        }
        val photos = (intent.getSerializableExtra("photos"))
        if(photos != null){
            viewPagerImageAdapter.updatePhotos((photos as Array<Photo>).toList())
        }
        val userList = (intent.getSerializableExtra("users") as Array<User>).toList()
        if(!userList.isNullOrEmpty()){
            viewModel.updateUsers(userList)
        }

        binding.buttonComplete.setOnClickListener {
            val resultIntent = Intent()
            val userTags = arrayListOf<User>()
            userTags.addAll(users)
            resultIntent.putExtra("users", userTags)
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        binding.buttonBack.setOnClickListener {
            finish()
        }

        binding.buttonAddTag.setOnClickListener {
            openSearchActivityForResult()
        }

        binding.layoutAddTag.setOnClickListener {
            openSearchActivityForResult()
        }

        viewModel.users.observe(this,{
            if(it.isEmpty()){
                binding.layoutUserTag.visibility = View.GONE
                binding.layoutAddTag.visibility = View.VISIBLE
            }else{
                binding.layoutUserTag.visibility = View.VISIBLE
                binding.layoutAddTag.visibility = View.GONE
            }
            users = it
            addUserTagAdapter.updateData(it)
        })
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == Activity.RESULT_OK){
            val data = result.data
            val user = data!!.getSerializableExtra("user")!! as User
            if(!users.contains(user)){
                viewModel.addUser(user)
            }
        }
    }

    fun openSearchActivityForResult(){
        val intent = Intent(this, SearchActivity::class.java)
        resultLauncher.launch(intent)
    }
}