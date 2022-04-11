package com.fatih.instagramapp.view.mainui.viewodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.instagramapp.model.Posts
import com.fatih.instagramapp.repositories.ModelRepositoriesInterface
import com.fatih.instagramapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostFragmentViewModel @Inject constructor(private val repositories:ModelRepositoriesInterface):ViewModel() {

    private var _controlMessage=MutableLiveData<Resource<Boolean>>()
    val controlMessage: LiveData<Resource<Boolean>>
        get() = _controlMessage

    fun postImage(post: Posts,uri:Uri)=viewModelScope.launch {
        _controlMessage.value= Resource.loading(false)
       _controlMessage.value=repositories.postImage(post,uri) }

    fun resetControlMessage(){
        _controlMessage=MutableLiveData<Resource<Boolean>>()
    }
}