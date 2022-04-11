package com.fatih.instagramapp.view.mainui.viewodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.instagramapp.model.Follow
import com.fatih.instagramapp.model.Users
import com.fatih.instagramapp.repositories.ModelRepositoriesInterface
import com.fatih.instagramapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileFragmentViewModel @Inject constructor(private val repositories:ModelRepositoriesInterface):ViewModel() {

    private val _user=MutableLiveData<Resource<Users>>()
    val user:LiveData<Resource<Users>>
        get() = _user

    private val _followDetail=MutableLiveData<Resource<Pair<MutableList<Follow>,MutableList<Follow>>>>()
    val followDetail:LiveData<Resource<Pair<MutableList<Follow>,MutableList<Follow>>>>
        get() = _followDetail


    fun getUserDetails(uid:String)=viewModelScope.launch{
        _user.value= Resource.loading(null)
        _user.value=repositories.getUserDetails(uid)
    }

    fun getFollowDetails(uid: String)=viewModelScope.launch{
        _followDetail.value= Resource.loading(null)
        _followDetail.value= repositories.getFollowDetails(uid)

    }
}