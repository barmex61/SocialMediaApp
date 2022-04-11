package com.fatih.instagramapp.repositories


import android.net.Uri
import com.fatih.instagramapp.model.Follow
import com.fatih.instagramapp.model.Posts
import com.fatih.instagramapp.model.Users
import com.fatih.instagramapp.util.Resource
import com.fatih.instagramapp.util.Status
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class ModelRepositories @Inject constructor(private val auth: FirebaseAuth,private val fireStore:FirebaseFirestore,private val reference:StorageReference) :ModelRepositoriesInterface{

    override suspend fun logIn(email: String, password: String): Resource<String> = coroutineScope{
        var resource=Resource.loading("Loading")
        try {

            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task->
                resource = if(task.isSuccessful){
                    Resource.success("Success")
                }else{
                    Resource.error("Error","Task failed")
                }
            }.await()
            return@coroutineScope resource
        }catch (e:Exception){
            resource= Resource.error("Error",e.message)
            return@coroutineScope resource
        }
    }


    override suspend fun addGoogleUserIntoDatabase(account: GoogleSignInAccount, date:Long,currentUser:Users):Resource<String> =
        coroutineScope{
        var resource=Resource.loading("Loading")
       try {
           fireStore.collection("Users").document(auth.uid!!).set(currentUser).await()
           resource= Resource.success("Success")
           return@coroutineScope resource
       }catch (e:Exception){
           resource= Resource.error("Error",e.message)
           return@coroutineScope resource
       }

    }

    override suspend fun signUp(email:String, password:String, name:String, username:String, date:Long, photo: Uri?): Resource<String> =
        coroutineScope{
            var resource=Resource.loading("Loading")
            try {
           auth.createUserWithEmailAndPassword(email, password).await()
           photo?.let {
               val uuid=UUID.randomUUID()
               reference.child("Images").child(auth.uid!!).child("$uuid").putFile(it).await()
                   val downloadUrl= reference.child("Images").child(auth.uid!!).child("$uuid").downloadUrl.await()
                       val currentUser=Users(userName = username, password = password,date=date, email = email, name = name, uid = auth.uid!!, photo =downloadUrl.toString() )
                       fireStore.collection("Users").document(auth.uid!!).set(currentUser).await()
               resource= Resource.success("Success")
           }?:addUserIntoDatabase(email,password, name,  username, date)
                return@coroutineScope resource
        }catch (e:Exception){
                resource= Resource.error("Error",e.message)
                return@coroutineScope resource
        }
    }

    private suspend fun addUserIntoDatabase(email: String, password: String, name: String, username: String, date: Long)=
        coroutineScope{
            try {
                val currentUser=Users(userName = username, password = password,date=date, email = email, name = name, uid = auth.uid!!)
                fireStore.collection("Users").document(auth.uid!!).set(currentUser).await()
            }catch (e:Exception){
                println(e.message)
            }
    }


    override suspend fun searchUser(searchText: String):Resource<List<Users>> = coroutineScope{
       try {
           val snapshot=fireStore.collection("Users").orderBy("searchName").startAt(searchText).endAt(searchText+"\uf8ff").get().await()
           val snapshot2=fireStore.collection("Users").orderBy("searchUserName").startAt(searchText).endAt(searchText+"\uf8ff").get().await()

            if(snapshot.isEmpty&&snapshot2.isEmpty){
               return@coroutineScope Resource.error(null,"Search result is empty")
            }else{
                val list1=snapshot.toObjects(Users::class.java)
                val list2=snapshot2.toObjects(Users::class.java)
                val myList=ArrayList<Users>(list1).apply {
                    addAll(list2)
                }
                for(user in list1){
                    if(user!=null&&list2.contains(user)) {
                        myList.remove(user)
                    }
                }
                myList.removeIf {
                    it.uid==auth.uid
                }
               return@coroutineScope Resource.success(myList)
            }
       }
        catch (e:Exception){
            return@coroutineScope Resource.error(null,e.message)
        }
    }

    override suspend fun getUserDetails(uid: String): Resource<Users> {
        return try {
            val user=fireStore.collection("Users").document(uid).get().await().toObject(Users::class.java)
            user?.let {
                Resource.success(user)
            }?: Resource.error(null,"User not found")
        }catch (e:Exception){
            Resource.error(null,e.message)
        }

    }

    override suspend fun getFollowDetails(uid: String): Resource<Pair<MutableList<Follow>, MutableList<Follow>>> = coroutineScope{

        return@coroutineScope try {
            val followers=fireStore.collection("Users").document(uid).collection("Followers").get().await().toObjects(Follow::class.java)
            val following=fireStore.collection("Users").document(uid).collection("Following").get().await().toObjects(Follow::class.java)
            Resource.success(Pair(followers,following))

        }catch (e:Exception){
            Resource.error(null,e.message)
        }
    }


    override suspend fun checkUser(searchUid:String): Resource<Boolean> = coroutineScope {
        return@coroutineScope try {
            val data=fireStore.collection("Users").document(auth.uid!!).collection("Following").document(searchUid).get().await()
            data.toObject(Follow::class.java)?.let {
                Resource.success(true)
            }?: Resource.error(false,"Data null")
        }catch (e:Exception){
            Resource.error(false,e.message)
        }
    }

    override suspend fun addUserIntoFollow(searchUid: String,follow: Follow): Resource<Boolean> = coroutineScope {
        var status=Resource.loading(false)
        try {
           fireStore.collection("Users").document(auth.uid!!).collection("Following").document(searchUid).set(follow).addOnCompleteListener { task->
                if(task.isSuccessful){
                    status=Resource.success(true)
                    return@addOnCompleteListener
                }
                status=Resource.error(false,task.exception?.message)
            }.await()
            if (status.status==Status.SUCCESS){
            fireStore.collection("Users").document(searchUid).collection("Followers").document(auth.uid!!).set(follow).addOnCompleteListener { task->
                if(task.isSuccessful){
                    status= Resource.success(true)
                    return@addOnCompleteListener
                }
                status= Resource.error(false,task.exception?.message)
            }
            }
        }catch (e:Exception){
           status= Resource.error(false,e.message)
        }
        return@coroutineScope status
    }

    override suspend fun deleteUserFromFollow(searchUid: String, follow: Follow): Resource<Boolean> = coroutineScope{

        var status=Resource.loading(false)
        try {
            fireStore.collection("Users").document(auth.uid!!).collection("Following").document(searchUid).delete().addOnCompleteListener { task->
                if(task.isSuccessful){
                    status=Resource.success(true)
                    return@addOnCompleteListener
                }
                status=Resource.error(false,task.exception?.message)
            }.await()
            if (status.status==Status.SUCCESS){
                fireStore.collection("Users").document(searchUid).collection("Followers").document(auth.uid!!).delete().addOnCompleteListener { task->
                    if(task.isSuccessful){
                        status= Resource.success(true)
                        return@addOnCompleteListener
                    }
                    status= Resource.error(false,task.exception?.message)
                }
            }
        }catch (e:Exception){
            status= Resource.error(false,e.message)
        }
        return@coroutineScope status
    }

    override suspend fun editUserInformation(uid: String,users:Users,uri: Uri?):Resource<Boolean> =
        coroutineScope{
        try {
          fireStore.collection("Users").document(uid).update("bio",users.bio,
              "name", users.name,
              "searchName",users.searchName,
              "searchUserName",users.searchUserName,
              "date", users.date,
              "website",users.website,
              "userName",users.userName ).await()
              val uuid=UUID.randomUUID()
              uri?.let {it->
                  reference.child("Images").child(uid).child("$uuid").putFile(it).await()
                  val downloadUrl=reference.child("Images").child(uid).child("$uuid").downloadUrl.await()
                  fireStore.collection("Users").document(uid).update("photo",downloadUrl.toString()).await()
          }
            return@coroutineScope Resource.success(true)
        }
        catch (e:Exception){
          return@coroutineScope Resource.error(false,e.message)
        }
    }

    override suspend fun postImage(post: Posts,uri: Uri): Resource<Boolean> = coroutineScope{

        try {
            val uuid=UUID.randomUUID()
            reference.child("Images").child("Posts").child(auth.uid!!).child("$uuid").putFile(uri).await()
            val downloadUrl=reference.child("Images").child("Posts").child(auth.uid!!).child("$uuid").downloadUrl.await()
            post.photo=downloadUrl.toString()
            post.postId=uuid.toString()
            fireStore.collection("Users").document(auth.uid!!).collection("Posts").document(uuid.toString()).set(post).await()
            return@coroutineScope Resource.success(true)

        }catch (e:Exception){
            return@coroutineScope Resource.error(false,e.message)
        }
    }

    override suspend fun getPosts(): Resource<List<Posts>> = coroutineScope {
        return@coroutineScope try {

            val followingList=fireStore.collection("Users").document(auth.uid!!).collection("Following").get().await().toObjects(Follow::class.java)
            val list= followingList.map {
                println(it.userUid)
                it.userUid
            }.toMutableList()
            list.add(auth.uid!!)


           val snapshot= fireStore.collectionGroup("Posts").whereIn("publisher",list).orderBy("date",Query.Direction.DESCENDING).get().await()
           val data= snapshot.toObjects(Posts::class.java)
            Resource.success(data)
        }catch (e:Exception){
            Resource.error(null,e.message)
        }
    }

}