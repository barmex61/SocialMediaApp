package com.fatih.instagramapp.view.loginui.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class MyFragmentFactory @Inject constructor(private val auth: FirebaseAuth,private val fireStore: FirebaseFirestore):FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {

        return when(className){
            SignInFragment::class.java.name-> SignInFragment(auth)
            SignUpFirstFragment::class.java.name-> SignUpFirstFragment(auth)
            SignUpSecondFragment::class.java.name-> SignUpSecondFragment(auth)
            else-> return super.instantiate(classLoader, className)
        }
    }
}