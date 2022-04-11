package com.fatih.instagramapp.view.mainui

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fatih.instagramapp.R
import com.fatih.instagramapp.adapter.ViewPagerAdapter
import com.fatih.instagramapp.databinding.FragmentUserProfileBinding
import com.fatih.instagramapp.model.Follow
import com.fatih.instagramapp.model.Users
import com.fatih.instagramapp.util.Status
import com.fatih.instagramapp.view.mainui.viewodel.UserProfileFragmentViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserProfileFragment @Inject constructor(): Fragment() {

    private lateinit var binding:FragmentUserProfileBinding
    private lateinit var viewModel:UserProfileFragmentViewModel
    private var selectedUser:Users?=null
    private var isFollowed:Boolean=false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_user_profile,container,false)
        doInit()
        return binding.root
    }

    private fun doInit(){
        viewModel=ViewModelProvider(this)[UserProfileFragmentViewModel::class.java]
        getUserFromBundle()
        checkFollow()
        observeFollowDetails()
        setupViewPager()
        setOnClickListeners()
    }

    private fun setupViewPager(){
        binding.userProfileFragmentViewPager.adapter= ViewPagerAdapter(requireActivity())
        TabLayoutMediator(binding.tabLayout, binding.userProfileFragmentViewPager) { tab, position ->
            when(position){
                0->{tab.setIcon(R.drawable.grid)}
                1->{tab.setIcon(R.drawable.profile_icon)}
            }
        }.attach()
    }

    private fun setOnClickListeners(){
        binding.backButtonUserProfile.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.followingText.setOnClickListener {
            selectedUser?.uid?.let { uid->
            val follow=Follow(true,uid)
            if(isFollowed){
                viewModel.deleteUserFromFollow(uid,follow).invokeOnCompletion {
                    viewModel.getFollowDetails(selectedUser!!).invokeOnCompletion {
                        viewModel.checkFollow(uid)
                    }
                }
                return@setOnClickListener
            }
                viewModel.addUserToFollow(uid, follow).invokeOnCompletion {
                    viewModel.getFollowDetails(selectedUser!!).invokeOnCompletion {
                        viewModel.checkFollow(uid)
                    }
                }
            }
        }
    }

    private fun getUserFromBundle(){
        arguments?.let {
            selectedUser=UserProfileFragmentArgs.fromBundle(it).user
            binding.user=selectedUser
            selectedUser?.let { user->
                viewModel.getFollowDetails(user)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observeFollowDetails(){
        viewModel.followDetail.observe(viewLifecycleOwner){
            when(it.status){

                Status.ERROR->{
                    Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()}
                Status.LOADING->{
                }
                Status.SUCCESS->{
                    it.data?.let { data->
                        println("hello")
                        binding.followers=data.first.size.toString()
                        binding.following=data.second.size.toString()
                    }
                }
            }
        }
        viewModel.checkFollow.observe(viewLifecycleOwner){
            when(it.status){
                Status.LOADING->{}
                Status.ERROR->{
                    isFollowed=false
                    binding.followingText.text="Follow"
                    binding.followingText.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
                    binding.followingText.setBackgroundResource(R.drawable.sign_in_facebook_bg)}
                Status.SUCCESS->{
                    it.data?.let {

                        isFollowed=true
                        binding.followingText.text = "Following"
                        binding.followingText.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
                        binding.followingText.setBackgroundResource(R.drawable.edit_profile_bg)

                    }
                }
            }
        }
    }
    private fun checkFollow(){
        selectedUser?.uid?.let {
            viewModel.checkFollow(it)
        }
    }

}