package com.tmdbapi.cowlar.task.ui.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.navigation.NavigationBarView
import com.tmdbapi.cowlar.task.R
import com.tmdbapi.cowlar.task.databinding.ActivityHomeBinding
import com.tmdbapi.cowlar.task.adapter.ViewPagerAdapter
import com.tmdbapi.cowlar.task.utility.NetworkConnection
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private val mOnNavigationItemSelectedListener =
        NavigationBarView.OnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home_fragment -> {
                    binding.viewPager.currentItem = 0
                    return@OnItemSelectedListener true
                }

                R.id.search_fragment -> {
                    binding.viewPager.currentItem = 1
                    return@OnItemSelectedListener true
                }

                R.id.favourite_fragment -> {
                    binding.viewPager.currentItem = 2
                    return@OnItemSelectedListener true
                }

                R.id.profile_fragment -> {
                    binding.viewPager.currentItem = 3
                    return@OnItemSelectedListener true
                }
            }
            false
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
         initViewPagerWithBottomNavigation()
        initNetworkConnection()



    }

    private fun initNetworkConnection() {
        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this) { isConnected ->
            binding.noInternet.isVisible = !isConnected
        }
    }

    private fun initViewPagerWithBottomNavigation() {
        viewPagerAdapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = viewPagerAdapter
        binding.bottomNavigationView.setOnItemSelectedListener(mOnNavigationItemSelectedListener)
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> binding.bottomNavigationView.menu.findItem(R.id.home_fragment).isChecked =
                        true

                    1 -> binding.bottomNavigationView.menu.findItem(R.id.search_fragment).isChecked =
                        true

                    2 -> binding.bottomNavigationView.menu.findItem(R.id.favourite_fragment).isChecked =
                        true

                    3 -> binding.bottomNavigationView.menu.findItem(R.id.profile_fragment).isChecked =
                        true
                }
            }
        })    }
}
