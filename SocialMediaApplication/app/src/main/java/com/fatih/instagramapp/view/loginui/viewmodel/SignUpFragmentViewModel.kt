package com.fatih.instagramapp.view.loginui.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.instagramapp.repositories.ModelRepositoriesInterface
import com.fatih.instagramapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpFragmentViewModel @Inject constructor(private val repositories:ModelRepositoriesInterface):ViewModel() {

    private var _controlMessage=MutableLiveData<Resource<String>>()
    val controlMessage:LiveData<Resource<String>>
        get() = _controlMessage

    fun signUp(email:String,password:String,name:String,username:String,date:Long,photo:Uri?,auth: FirebaseAuth)=viewModelScope.launch{

        _controlMessage.value= Resource.loading(null)
        if(auth.currentUser!=null){
            _controlMessage.value= Resource.error(null,"There is an user already logged in")
            return@launch
        }
        if(email.isNotEmpty()&&password.isNotEmpty()&&name.isNotEmpty()&&username.isNotEmpty()&&auth.currentUser==null){
            _controlMessage.value=repositories.signUp(email, password, name, username, date, photo)
            return@launch
        }
        _controlMessage.value= Resource.error(null,"Fill the credentials")
    }
}