package com.fatih.instagramapp.repositories

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fatih.instagramapp.model.Follow
import com.fatih.instagramapp.model.Posts
import com.fatih.instagramapp.model.Users
import com.fatih.instagramapp.util.Resource
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow

interface ModelRepositoriesInterface {

    suspend fun logIn(email:String,password:String):Resource<String>
    suspend fun addGoogleUserIntoDatabase( account: GoogleSignInAccount, date:Long,currentUser:Users):Resource<String>
    suspend fun signUp(email:String, password:String, name:String, username:String, date:Long, photo: Uri?):Resource<String>
    suspend fun searchUser(searchText:String):Resource<List<Users>>
    suspend fun getUserDetails(uid:String):Resource<Users>
    suspend fun getFollowDetails(uid: String): Resource<Pair<MutableList<Follow>, MutableList<Follow>>>
    suspend fun checkUser(searchUid:String):Resource<Boolean>
    suspend fun addUserIntoFollow(searchUid: String,follow: Follow):Resource<Boolean>
    suspend fun deleteUserFromFollow(searchUid: String,follow: Follow):Resource<Boolean>
    suspend fun editUserInformation(uid: String,users: Users,uri: Uri?):Resource<Boolean>
    suspend fun postImage(post:Posts,uri: Uri):Resource<Boolean>
    suspend fun getPosts():Resource<List<Posts>>
}