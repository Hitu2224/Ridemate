package com.example.ridemate.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ridemate.User.Chat_Activity
import com.example.ridemate.databinding.ActivityActiveRideBinding

class Active_Ride_Activity : AppCompatActivity() {
    private lateinit var binding: ActivityActiveRideBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActiveRideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Pichli screen se ID nikalen
        var rideId = intent.getStringExtra("RIDE_ID") ?: ""
        val driverName = intent.getStringExtra("DRIVER_NAME") ?: "Partner"

        // 2. DEBUGGING: Agar ID khali hai toh testing ke liye ek dummy ID de den
        if (rideId.isEmpty()) {
            Log.e("CHAT_DEBUG", "RIDE_ID nahi mili, dummy ID use kar rahe hain")
            rideId = "test_ride_123" // Ye sirf testing ke liye hai
        }

        // 3. Chat Button click logic
        binding.btnChat.setOnClickListener {
            // Check karein ke ID ab mojood hai
            if (rideId.isNotEmpty()) {
                val intent = Intent(this, Chat_Activity::class.java)
                intent.putExtra("RIDE_ID", rideId)
                intent.putExtra("PARTNER_NAME", driverName)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Ride ID still missing!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}