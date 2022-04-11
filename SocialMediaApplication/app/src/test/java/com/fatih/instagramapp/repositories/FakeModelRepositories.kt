package com.fatih.instagramapp.repositories

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fatih.instagramapp.model.Users
import com.fatih.instagramapp.util.Resource
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class FakeModelRepositories:ModelRepositoriesInterface {

    private val mutableList= mutableListOf<String>()
    private val _mutableList=MutableLiveData<List<String>>(mutableList)

    override suspend fun logIn(email: String, password: String, auth: FirebaseAuth): Resource<String> {
        return Resource.success("Success")
    }

    override suspend fun addGoogleUserIntoDatabase(user: FirebaseUser, account: GoogleSignInAccount, date: Long, currentUser: Users): Resource<String> {
        return Resource.success("Success")
    }

    override suspend fun signUp(email: String, password: String, name: String, username: String, date: Long, photo: Uri?, auth: FirebaseAuth): Resource<String> {
        return Resource.success("Success")
    }

    override suspend fun searchUser(
        searchText: String,
        fireStore: FirebaseFirestore
    ): LiveData<List<Resource<Users>>> {
        TODO("Not yet implemented")
    }

    private fun refreshLiveData(){
        _mutableList.postValue(mutableList)
    }
}