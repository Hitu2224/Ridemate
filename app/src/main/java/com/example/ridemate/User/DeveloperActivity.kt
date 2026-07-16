package com.example.ridemate.User

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ridemate.databinding.ActivityDeveloperBinding

class DeveloperActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeveloperBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        binding = ActivityDeveloperBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Supervisor Card par click listener
        binding.card.setOnClickListener {
            val websiteUrl = "https://sites.google.com/site/imtiazahalepoto" // Yahan Sir ki asli website ka link dalein
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(websiteUrl)

            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Browser nahi mil saka ya link galat hai", Toast.LENGTH_SHORT).show()
            }
        }



    }
}