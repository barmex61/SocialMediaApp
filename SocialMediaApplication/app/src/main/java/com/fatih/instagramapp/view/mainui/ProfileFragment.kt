package com.fatih.instagramapp.view.mainui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fatih.instagramapp.R
import com.fatih.instagramapp.adapter.ViewPagerAdapter
import com.fatih.instagramapp.databinding.FragmentProfileBinding
import com.fatih.instagramapp.model.Users
import com.fatih.instagramapp.util.Status
import com.fatih.instagramapp.util.getNavOptions
import com.fatih.instagramapp.view.mainui.viewodel.ProfileFragmentViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment @Inject constructor(private val auth: FirebaseAuth,private val fireStore: FirebaseFirestore):Fragment(R.layout.fragment_profile) {

    private lateinit var binding:FragmentProfileBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var viewModel:ProfileFragmentViewModel
    private var selectedUser:Users?=null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding=DataBindingUtil.inflate(layoutInflater,R.layout.fragment_profile,container,false)
        mainActivity=requireActivity() as MainActivity
        doInit()
        return binding.root
    }

    private fun doInit(){
        viewModel=ViewModelProvider(requireActivity())[ProfileFragmentViewModel::class.java]
        viewModel.getUserDetails(auth.uid!!)
        viewModel.getFollowDetails(auth.uid!!)
        mainActivity.binding.bottomAppBar.performShow(true)
        getUserDetails()
        setupViewPager()
        setOnClickListeners()
    }

    private fun setupViewPager(){
        binding.profileFragmentViewPager.adapter=ViewPagerAdapter(requireActivity())
        TabLayoutMediator(binding.tabLayout, binding.profileFragmentViewPager) { tab, position ->
            when(position){
                0->{tab.setIcon(R.drawable.grid)}
                1->{tab.setIcon(R.drawable.profile_icon)}
            }
        }.attach()
    }

    private fun setOnClickListeners(){
        binding.editProfileText.setOnClickListener {
            mainActivity.binding.bottomAppBar.performHide(true)
         /*   val params=CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.WRAP_CONTENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT)
            params.setMargins(0,0,0,0)
            mainActivity.binding.fragmentContainer.layoutParams=params*/

            selectedUser?.let {
                val bundle=Bundle()
                bundle.putSerializable("user",binding.user)
                findNavController().navigate(R.id.editProfileFragment,bundle, getNavOptions())
            }
        }
    }

    private fun getUserDetails(){
        viewModel.user.observe(viewLifecycleOwner){
            when(it.status){
                Status.ERROR->{
                    Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                }
                Status.LOADING->{}
                Status.SUCCESS->{
                    it.data?.let { user->
                        selectedUser=user
                        binding.user=user
                        binding.statusText.visibility=user.status?.let {
                            View.VISIBLE
                        }?:View.GONE
                    }
                }
            }
        }
        viewModel.followDetail.observe(viewLifecycleOwner){
            when(it.status){
                Status.ERROR->{
                    Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                }
                Status.LOADING->{
                }
                Status.SUCCESS->{
                    it.data?.let { data->
                        binding.followers=data.first.size.toString()
                        binding.following=data.second.size.toString()
                    }
                }
            }
        }
    }
}