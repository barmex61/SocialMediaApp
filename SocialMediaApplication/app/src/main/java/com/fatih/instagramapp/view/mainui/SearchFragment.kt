package com.fatih.instagramapp.view.mainui

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fatih.instagramapp.R
import com.fatih.instagramapp.adapter.SearchListAdapter
import com.fatih.instagramapp.databinding.FragmentSearchBinding
import com.fatih.instagramapp.util.Status
import com.fatih.instagramapp.util.getNavOptions
import com.fatih.instagramapp.view.mainui.viewodel.SearchFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SearchFragment @Inject constructor(private val searchListAdapter: SearchListAdapter): Fragment(R.layout.fragment_search) {

    private lateinit var binding:FragmentSearchBinding
    private lateinit var viewModel:SearchFragmentViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_search,container,false)
        doInit()
        return binding.root
    }

    private fun doInit() {
        binding.recyclerviewSearch.layoutManager=LinearLayoutManager(requireContext())
        binding.recyclerviewSearch.adapter=searchListAdapter
        viewModel=ViewModelProvider(requireActivity())[SearchFragmentViewModel::class.java]
        viewModel.searchUser("")
        setOnClickListeners()
        observeLiveData()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnClickListeners(){
        binding.searchText.setOnFocusChangeListener { _, _->
            binding.backButton.visibility=View.VISIBLE
        }
        binding.searchText.setOnClickListener {
            binding.backButton.visibility=View.VISIBLE
        }
        binding.backButton.setOnClickListener {
            binding.searchText.setText("")
            val inputMethodManager: InputMethodManager = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            if (inputMethodManager.isAcceptingText) { inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus!!.windowToken, 0) }
            binding.searchText.clearFocus()
            binding.backButton.visibility=View.GONE
        }
        binding.searchText.addTextChangedListener {
            viewModel.searchUser(it.toString())
        }
        searchListAdapter.setOnItemClickListener {
            val bundle=Bundle()
            bundle.putSerializable("user",it)
            findNavController().navigate(R.id.userProfileFragment,bundle, getNavOptions())

        }

    }

    private fun observeLiveData(){
        viewModel.userList.observe(viewLifecycleOwner){ resource->

            when(resource.status){
                Status.LOADING->{}
                Status.SUCCESS->{
                    resource.data?.let { list->
                      searchListAdapter.userList=list
                    }}
                Status.ERROR->{
                    searchListAdapter.userList= listOf()
                    }
            }
        }
    }
}