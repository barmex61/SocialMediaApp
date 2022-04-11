package com.fatih.instagramapp.view.mainui.viewodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.instagramapp.model.Follow
import com.fatih.instagramapp.model.Posts
import com.fatih.instagramapp.repositories.ModelRepositoriesInterface
import com.fatih.instagramapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeFragmentViewModel @Inject constructor(private val repositories:ModelRepositoriesInterface):ViewModel() {


    private val _postList=MutableLiveData<Resource<List<Posts>>>()
    val postList:LiveData<Resource<List<Posts>>>
        get() = _postList


    fun getPosts()=viewModelScope.launch{
        _postList.value= Resource.loading(null)
        _postList.value=repositories.getPosts()

    }
}