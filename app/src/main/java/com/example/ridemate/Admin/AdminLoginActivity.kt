package com.example.ridemate.Admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ridemate.databinding.ActivityAdminLoginBinding

class AdminLoginActivity : AppCompatActivity() {


    private lateinit var binding: ActivityAdminLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivityAdminLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnAdminLogin.setOnClickListener {
            val email = binding.etAdminEmail.text.toString().trim()
            val pass = binding.etAdminPass.text.toString().trim()
            if (email == "admin@ridemate.com" && pass== "admin786") {
                Toast.makeText(this, "Welcome Admin", Toast.LENGTH_SHORT).show()

                val intent= Intent(this, AdminDashboard::class.java)
               startActivity(intent)
                finish()

            } else{
                Toast.makeText(this, "Only authorized admins can enter!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}