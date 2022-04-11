package com.fatih.instagramapp.view.mainui.profileui

import android.net.Uri
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
class EditProfileFragmentViewModel @Inject constructor(private val repositories: ModelRepositoriesInterface):ViewModel() {

    private val _controlMessage=MutableLiveData<Resource<Boolean>>()
    val controlMessage:LiveData<Resource<Boolean>>
        get() = _controlMessage


    fun editUserInformation(uid:String,user:Users,uri: Uri?)=viewModelScope.launch{
        _controlMessage.value= Resource.loading(null)
        _controlMessage.value=repositories.editUserInformation(uid,user,uri)
    }
}