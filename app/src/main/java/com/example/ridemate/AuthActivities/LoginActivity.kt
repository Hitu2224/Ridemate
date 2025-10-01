package com.example.ridemate.AuthActivities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.ridemate.Fragments.HomeActivity
import com.example.ridemate.NewRideRequestActivity
import com.example.ridemate.R
import com.example.ridemate.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {


    private lateinit var binding: ActivityLoginBinding
    private var userType:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.loginButton.setOnClickListener {
            // Normally you'd validate input and save data
            // For demo, we'll directly redirect based on userType

            if (userType == "driver") {
                startActivity(Intent(this, NewRideRequestActivity::class.java))
            } else if (userType == "passenger") {
                startActivity(Intent(this, HomeActivity::class.java))
            } else {
                Toast.makeText(this, "User type not found!", Toast.LENGTH_SHORT).show()
            }

            finish() // Optional: to remove SignUpActivity from back stack
        }


    }
}