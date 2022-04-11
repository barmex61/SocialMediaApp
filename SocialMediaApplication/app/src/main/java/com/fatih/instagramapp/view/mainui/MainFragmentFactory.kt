package com.fatih.instagramapp.view.mainui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.fatih.instagramapp.adapter.HomeFragmentAdapter
import com.fatih.instagramapp.adapter.SearchListAdapter
import com.fatih.instagramapp.view.mainui.profileui.EditProfileFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class MainFragmentFactory @Inject constructor(private val homeFragmentAdapter: HomeFragmentAdapter,private val auth: FirebaseAuth,private val fireStore:FirebaseFirestore,private val searchListAdapter: SearchListAdapter):FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when(className){
            SearchFragment::class.java.name->SearchFragment(searchListAdapter)
            HomeFragment::class.java.name->HomeFragment(homeFragmentAdapter)
            NotificationFragment::class.java.name->NotificationFragment()
            ProfileFragment::class.java.name->ProfileFragment(auth,fireStore)
            UserProfileFragment::class.java.name->UserProfileFragment()
            EditProfileFragment::class.java.name->EditProfileFragment(fireStore,auth)
            PostFragment::class.java.name->PostFragment(auth)
            else->{super.instantiate(classLoader, className)}
        }
    }
}