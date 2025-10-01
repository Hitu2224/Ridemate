package com.example.ridemate

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.ridemate.AuthActivities.LoginActivity
import com.example.ridemate.AuthActivities.SignUpActivity
import com.example.ridemate.databinding.ActivitySelectUserTypeBinding

class SelectUserTypeActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivitySelectUserTypeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySelectUserTypeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.driverButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            intent.putExtra("user_type", "driver")
            startActivity(intent)
        }

        binding.passengerButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            intent.putExtra("user_type", "passenger")
            startActivity(intent)


        }
    }
}