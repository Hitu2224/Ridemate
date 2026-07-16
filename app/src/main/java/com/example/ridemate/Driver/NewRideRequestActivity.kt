package com.example.ridemate.Driver

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.ridemate.R
import com.example.ridemate.databinding.ActivityNewRideRequestBinding

class NewRideRequestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewRideRequestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNewRideRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // FIXED: Yahan Driver ka Home Fragment load karein, User ka nahi
        if (savedInstanceState == null) {
            loadFragment(DriverHomeFragment())
            binding.bottomnav.selectedItemId = R.id.nav_driver_home
        }

        binding.bottomnav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_driver_home -> {
                    loadFragment(DriverHomeFragment())
                    true
                }
                R.id.nav_driver_history -> {
                    loadFragment(DriverHistoryFragment())
                    true
                }
                R.id.nav_driver_profile -> {
                    loadFragment(DriverProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.driver_container, fragment)
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .commit()
    }
}