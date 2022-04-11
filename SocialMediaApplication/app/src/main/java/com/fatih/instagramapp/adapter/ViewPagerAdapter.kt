package com.fatih.instagramapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.fatih.instagramapp.view.mainui.profileui.ViewPagerFirstFragment
import com.fatih.instagramapp.view.mainui.profileui.ViewPagerSecondFragment

class ViewPagerAdapter(fm:FragmentActivity): FragmentStateAdapter(fm) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0->{ ViewPagerFirstFragment()
            }
            1->{ ViewPagerSecondFragment()
            }
            else->{
                ViewPagerFirstFragment()
            }
        }
    }

}