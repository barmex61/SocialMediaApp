package com.fatih.instagramapp.view.mainui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fatih.instagramapp.R
import com.fatih.instagramapp.adapter.HomeFragmentAdapter
import com.fatih.instagramapp.databinding.FragmentHomeBinding
import com.fatih.instagramapp.util.Status
import com.fatih.instagramapp.view.mainui.viewodel.HomeFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment @Inject constructor(private val homeFragmentAdapter:HomeFragmentAdapter):Fragment(R.layout.fragment_home) {

    private lateinit var binding:FragmentHomeBinding
    private lateinit var viewmodel: HomeFragmentViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding=DataBindingUtil.inflate(layoutInflater,R.layout.fragment_home,container,false)
        doInit()
        return binding.root
    }

    private fun doInit() {
        viewmodel=ViewModelProvider(requireActivity())[HomeFragmentViewModel::class.java]
        binding.recyclerviewHome.layoutManager=LinearLayoutManager(requireContext())
        binding.recyclerviewHome.adapter=homeFragmentAdapter
        viewmodel.getPosts()
        observeLiveData()
    }
    private fun observeLiveData(){
        viewmodel.postList.observe(viewLifecycleOwner){
            when(it.status){
                Status.SUCCESS->{
                    println(it.data?.size)
                    Toast.makeText(requireContext(),"Success",Toast.LENGTH_SHORT).show()
                    it.data?.let {  data->
                        homeFragmentAdapter.postList=data
                    }}
                Status.LOADING->{
                    println("loading")}
                Status.ERROR->{Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                println(it.message)}
            }
        }
    }

}