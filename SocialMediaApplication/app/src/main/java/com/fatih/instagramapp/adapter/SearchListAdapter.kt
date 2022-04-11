package com.fatih.instagramapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fatih.instagramapp.databinding.SearchLayoutItemBinding
import com.fatih.instagramapp.model.Users
import javax.inject.Inject

class SearchListAdapter @Inject constructor():RecyclerView.Adapter<SearchListAdapter.SearchListViewHolder>(){

    private var listener:((Users)->Unit)?=null
    fun setOnItemClickListener(listener:(Users)->Unit){
        this.listener=listener
    }

    private val diffUtil=object :DiffUtil.ItemCallback<Users>(){
        override fun areContentsTheSame(oldItem: Users, newItem: Users): Boolean {
            return oldItem==newItem
        }
        override fun areItemsTheSame(oldItem: Users, newItem: Users): Boolean {
            return oldItem==newItem
        }
    }
    private val asyncListDiffer=AsyncListDiffer(this,diffUtil)
    var userList:List<Users>
        get() = asyncListDiffer.currentList
        set(value) = asyncListDiffer.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchListViewHolder {
        val binding=SearchLayoutItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SearchListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchListViewHolder, position: Int) {
        holder.binding.user=userList[position]
        holder.itemView.setOnClickListener {
            listener?.let {
                it(userList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class SearchListViewHolder(val binding:SearchLayoutItemBinding) :RecyclerView.ViewHolder(binding.root)

}