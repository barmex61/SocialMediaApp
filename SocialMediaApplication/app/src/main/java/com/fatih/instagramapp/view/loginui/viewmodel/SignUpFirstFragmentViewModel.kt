package com.fatih.instagramapp.view.loginui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fatih.instagramapp.model.Users
import com.fatih.instagramapp.repositories.ModelRepositoriesInterface
import com.fatih.instagramapp.util.Resource
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpFirstFragmentViewModel @Inject constructor(private val repositories:ModelRepositoriesInterface):ViewModel() {

    private var _controlMessage= MutableLiveData<Resource<String>>()
    val controlMessage: LiveData<Resource<String>>
        get() = _controlMessage

    fun addGoogleUserIntoDatabase(user: FirebaseUser?, account: GoogleSignInAccount, date:Long)=viewModelScope.launch{
        _controlMessage.value= Resource.loading("Loading")
        user?.let{
            val currentUser= Users(name = account.displayName, email = account.email, uid = user.uid, date =date, photo = account.photoUrl.toString())
            _controlMessage.value=repositories.addGoogleUserIntoDatabase(account,date,currentUser)
            return@launch
        }
        _controlMessage.value= Resource.error(null,"User not found")

    }

    fun resetControlMessage(){
        _controlMessage= MutableLiveData<Resource<String>>()
    }
}