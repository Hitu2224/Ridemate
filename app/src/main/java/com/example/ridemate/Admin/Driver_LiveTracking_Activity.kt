package com.example.ridemate.Admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ridemate.databinding.ActivityDriverLiveTrackingBinding

class Driver_LiveTracking_Activity : AppCompatActivity() {


    private lateinit var binding: ActivityDriverLiveTrackingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDriverLiveTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)


        }
    }