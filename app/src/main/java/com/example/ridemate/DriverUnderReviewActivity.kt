package com.example.ridemate.Admin // Apne sahi package (Driver ya Admin jahan aapne activity rakhi hai) ke mutabiq check karlein

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ridemate.AuthActivities.LoginActivity
import com.example.ridemate.databinding.ActivityDriverUnderReviewBinding
import com.google.firebase.auth.FirebaseAuth

class DriverUnderReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDriverUnderReviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverUnderReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 🚪 LOGOUT BUTTON LOGIC
        binding.btnLogout.setOnClickListener {
            // FirebaseAuth se session khatam karna
            FirebaseAuth.getInstance().signOut()


          val intent = Intent(this, LoginActivity::class.java) // Apni login activity ka naam yahan likhein
             startActivity(intent)
            finish()
        }
    }
}