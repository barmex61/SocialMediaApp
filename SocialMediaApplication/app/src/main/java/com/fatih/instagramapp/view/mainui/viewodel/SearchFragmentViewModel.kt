package com.fatih.instagramapp.view.mainui.viewodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.instagramapp.model.Users
import com.fatih.instagramapp.repositories.ModelRepositoriesInterface
import com.fatih.instagramapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchFragmentViewModel @Inject constructor(private val repositories:ModelRepositoriesInterface):ViewModel() {

    private var _userList=MutableLiveData<Resource<List<Users>>>()
    val userList:LiveData<Resource<List<Users>>>
        get() = _userList

    fun searchUser(searchText:String)= viewModelScope.launch{
        _userList.value= Resource.loading(null)
        _userList.value=repositories.searchUser(searchText.lowercase())
    }
}