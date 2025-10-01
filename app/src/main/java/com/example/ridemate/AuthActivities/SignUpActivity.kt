package com.example.ridemate.AuthActivities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.example.ridemate.DriverPostActivity
import com.example.ridemate.Fragments.HomeActivity
import com.example.ridemate.NewRideRequestActivity
import com.example.ridemate.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private var userType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get user_type from intent
        userType = intent.getStringExtra("user_type")

        binding.signUpButton.setOnClickListener {
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