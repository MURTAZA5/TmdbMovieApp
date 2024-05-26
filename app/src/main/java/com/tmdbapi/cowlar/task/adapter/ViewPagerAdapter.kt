package com.tmdbapi.cowlar.task.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tmdbapi.cowlar.task.ui.fragments.FavouriteFragment
import com.tmdbapi.cowlar.task.ui.fragments.HomeFragment
import com.tmdbapi.cowlar.task.ui.fragments.ProfileFragment
import com.tmdbapi.cowlar.task.ui.fragments.SearchFragment
import com.tmdbapi.cowlar.task.ui.fragments.SearchMultipleFragment

class ViewPagerAdapter (appCompatActivity : AppCompatActivity) : FragmentStateAdapter(appCompatActivity) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> SearchFragment()
//            2 -> FavouriteFragment()
//            3 -> ProfileFragment()
            else -> HomeFragment()
        }
    }
}