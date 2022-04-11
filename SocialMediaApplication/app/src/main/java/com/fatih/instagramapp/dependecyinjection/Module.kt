package com.fatih.instagramapp.dependecyinjection

import com.fatih.instagramapp.repositories.ModelRepositories
import com.fatih.instagramapp.repositories.ModelRepositoriesInterface
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object Module {

    @Provides
    @Singleton
    fun injectFirestore()=FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun injectFirebaseAuth()=FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun injectFirebaseStorageReference()=FirebaseStorage.getInstance().reference

    @Provides
    @Singleton
    fun injectRepositories(auth: FirebaseAuth,fireStore:FirebaseFirestore,reference:StorageReference)=ModelRepositories(auth,fireStore,reference) as ModelRepositoriesInterface

}