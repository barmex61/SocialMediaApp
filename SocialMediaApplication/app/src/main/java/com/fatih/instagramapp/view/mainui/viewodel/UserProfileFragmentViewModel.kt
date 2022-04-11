package com.fatih.instagramapp.view.mainui.viewodel

import androidx.lifecycle.*
import com.fatih.instagramapp.model.Follow
import com.fatih.instagramapp.model.Users
import com.fatih.instagramapp.repositories.ModelRepositoriesInterface
import com.fatih.instagramapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import javax.inject.Inject

@HiltViewModel
class UserProfileFragmentViewModel @Inject constructor(private val repositories:ModelRepositoriesInterface):ViewModel() {

    private val _checkFollow=MutableLiveData<Resource<Boolean>>()
    val checkFollow:LiveData<Resource<Boolean>>
        get() = _checkFollow

    private val _followDetail=MutableLiveData<Resource<Pair<MutableList<Follow>,MutableList<Follow>>>>()
    val followDetail:LiveData<Resource<Pair<MutableList<Follow>,MutableList<Follow>>>>
        get() = _followDetail

    fun getFollowDetails(user: Users)=viewModelScope.launch{
        _followDetail.value= Resource.loading(null)
        user.uid?.let {
            _followDetail.value= repositories.getFollowDetails(it)
            return@launch
        }
        _followDetail.value= Resource.error(null,"Error")

    }

    fun checkFollow(searchUid:String)=viewModelScope.launch{

        _checkFollow.value=Resource.loading(null)
        _checkFollow.value=repositories.checkUser(searchUid)

    }

    fun addUserToFollow(searchUid: String,follow: Follow)=viewModelScope.launch {
        _checkFollow.value= Resource.loading(null)
        _checkFollow.value=repositories.addUserIntoFollow(searchUid, follow)

    }

    fun deleteUserFromFollow(searchUid: String,follow: Follow)=viewModelScope.launch {
        _checkFollow.value= Resource.loading(null)
        _checkFollow.value=repositories.deleteUserFromFollow(searchUid, follow)

    }
}