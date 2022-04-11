package com.fatih.instagramapp.view.loginui.viewmodel

import androidx.lifecycle.*
import com.fatih.instagramapp.model.Users
import com.fatih.instagramapp.repositories.ModelRepositoriesInterface
import com.fatih.instagramapp.util.Resource
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(private val repositories: ModelRepositoriesInterface):ViewModel() {

    private var _controlMessage=MutableLiveData<Resource<String>>()
    val controlMessage:LiveData<Resource<String>>
        get() = _controlMessage

    fun logIn(email:String,password:String,auth: FirebaseAuth)=viewModelScope.launch{
        _controlMessage.value= Resource.loading("Loading")
        if(email.isNotEmpty()&&password.isNotEmpty()&&auth.currentUser==null){
        _controlMessage.value= repositories.logIn(email, password)
            return@launch
        }
        if(auth.currentUser!=null){
            _controlMessage.value= Resource.error(null,"There is an user already logged in")
            return@launch
        }
        _controlMessage.value= Resource.error(null,"Please fill to credentials")


    }

    fun addGoogleUserIntoDatabase(user:FirebaseUser?,account: GoogleSignInAccount,date:Long)=viewModelScope.launch{

        _controlMessage.value= Resource.loading("Loading")
        user?.let{
            val currentUser= Users(name = account.displayName, email = account.email, uid = user.uid, date =date, photo = account.photoUrl.toString())
            _controlMessage.value=repositories.addGoogleUserIntoDatabase(account, date, currentUser)
            return@launch
        }
        _controlMessage.value= Resource.error(null,"User not found")
    }

    fun resetControlMessage(){
        _controlMessage=MutableLiveData<Resource<String>>()
    }

}