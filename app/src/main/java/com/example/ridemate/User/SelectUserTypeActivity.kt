package com.example.ridemate.User

import android.R
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ridemate.Admin.AdminLoginActivity
import com.example.ridemate.AuthActivities.LoginActivity
import com.example.ridemate.databinding.ActivitySelectUserTypeBinding
import com.example.ridemate.utils.LoadingDialog

class SelectUserTypeActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectUserTypeBinding
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        binding = ActivitySelectUserTypeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.root.alpha = 0f
        binding.root.animate().alpha(1f).setDuration(900).start()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)


        loadingDialog = LoadingDialog(this)

        // Driver Button
        binding.driverMode.setOnClickListener {
            showLoadingAndNavigate("driver")
        }

        // Passenger Button
        binding.passengerButton.setOnClickListener {
            showLoadingAndNavigate("passenger")
        }

        // Admin Button
        binding.adminMode.setOnClickListener {
          val intent = Intent(this, AdminLoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun showLoadingAndNavigate(userType: String) {
        loadingDialog.show()

        // Buttons disable taake double click na ho
        binding.driverMode.isEnabled = false
        binding.passengerButton.isEnabled = false


        Handler(Looper.getMainLooper()).postDelayed({
            loadingDialog.dismiss()

            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("user_type", userType)
            startActivity(intent)

            // Buttons wapas enable
            binding.driverMode.isEnabled = true
            binding.passengerButton.isEnabled = true

        }, 1500)
    }
}