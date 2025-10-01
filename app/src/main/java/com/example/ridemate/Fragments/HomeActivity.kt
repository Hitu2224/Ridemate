package com.example.ridemate.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.ridemate.R
import com.example.ridemate.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadFragment(HomeFragment())
        binding.bottomNavigationView.selectedItemId = R.id.nav_home

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            val handled = when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_history -> {
                    loadFragment(HistoryFragment())
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }

            if (handled) {
                val menuView = binding.bottomNavigationView.getChildAt(0) as com.google.android.material.bottomnavigation.BottomNavigationMenuView

                val index = (0 until menuView.childCount).firstOrNull { i ->
                    val menuItem = binding.bottomNavigationView.menu.getItem(i)
                    menuItem.itemId == item.itemId
                } ?: -1

                if (index != -1) {
                    val itemView = menuView.getChildAt(index)
                    itemView.setBackgroundColor(android.graphics.Color.parseColor("#CFFFCF"))

                    itemView.postDelayed({
                        itemView.setBackgroundColor(android.graphics.Color.TRANSPARENT)
                    }, 1000)
                }
            }

            handled
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainerView.id, fragment)
            .commit()
    }
}
