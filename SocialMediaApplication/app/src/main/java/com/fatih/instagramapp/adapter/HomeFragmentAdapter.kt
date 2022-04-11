package com.fatih.instagramapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fatih.instagramapp.R
import com.fatih.instagramapp.databinding.MainRecyclerRowBinding
import com.fatih.instagramapp.model.Posts
import javax.inject.Inject

class HomeFragmentAdapter @Inject constructor(): RecyclerView.Adapter<HomeFragmentAdapter.MainViewHolder>() {

    private val diffUtil=object :DiffUtil.ItemCallback<Posts>(){
        override fun areContentsTheSame(oldItem: Posts, newItem: Posts): Boolean {
            return oldItem==newItem
        }

        override fun areItemsTheSame(oldItem: Posts, newItem: Posts): Boolean {
            return oldItem==newItem
        }
    }
    private val listDiffer=AsyncListDiffer(this,diffUtil)

    var postList:List<Posts>
        get() = listDiffer.currentList
        set(value) = listDiffer.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val binding:MainRecyclerRowBinding=DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.main_recycler_row,parent,false)
        return MainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.binding.post=postList[position]
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    class MainViewHolder(val binding:MainRecyclerRowBinding) :RecyclerView.ViewHolder(binding.root)

}