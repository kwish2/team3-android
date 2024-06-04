package com.wafflestudio.wafflestagram.ui.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.wafflestagram.model.User
import kotlinx.coroutines.launch


class AddUserTagViewModel : ViewModel(){
    private val _users = MutableLiveData<MutableList<User>>()
    var users : LiveData<MutableList<User>> = _users

    fun initializeUsers(){
        viewModelScope.launch {
            _users.value = mutableListOf()
        }
    }
    fun addUser(user: User){
        viewModelScope.launch {
            _users.value?.add(user)
            _users.value = _users.value
        }
    }

    fun removeUser(position: Int){
        viewModelScope.launch {
            _users.value?.removeAt(position)
            _users.value = _users.value
        }
    }

    fun updateUsers(users : List<User>){
        viewModelScope.launch {
            _users.value = users.toMutableList()
        }
    }
}