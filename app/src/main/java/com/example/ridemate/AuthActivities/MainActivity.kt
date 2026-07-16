package com.example.ridemate.AuthActivities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ridemate.DriverData
import com.example.ridemate.Fragments.HomeActivity
import com.example.ridemate.User.SelectUserTypeActivity
import com.example.ridemate.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 3 second ka splash delay timer
        Handler(Looper.getMainLooper()).postDelayed({

            // ✅ LOGIN SESSION CHECK: Current user ko check karein
            val currentUser = FirebaseAuth.getInstance().currentUser

            if (currentUser != null) {
                // User logged in hai! Ab check karte hain ke kya isne driver data save kiya tha?
                val uid = currentUser.uid

                FirebaseFirestore.getInstance().collection("driver_registrations")
                    .document(uid)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            // Agar driver ka data Firestore me hai, direct Map screen par le jao
                            startActivity(Intent(this, HomeActivity::class.java))
                        } else {
                            // Agar driver ka data nahi mila, to check karne ki zaroorat nahi, default bhej dein
                            // Halanqy passenger/student ka alag check ho sakta hai, par safety ke liye hum map par bhej rahe hain
                            startActivity(Intent(this, HomeActivity::class.java))
                        }
                        finish()
                    }
                    .addOnFailureListener { e ->
                        // Network issue ki surat me direct map par bhej dein taake app stuck na ho
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    }
            } else {
                // ❌ User login NAHI hai, to use normal Select Mode screen par bhejo
                val intent = Intent(this, SelectUserTypeActivity::class.java)
                startActivity(intent)
                finish()
            }

        }, 3000) // 3000ms = 3 seconds
    }
}