package com.example.ridemate.Admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ridemate.User.SelectUserTypeActivity
import com.example.ridemate.databinding.ActivityAdminDashboardBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class AdminDashboard : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDashboardBinding
    private val db = FirebaseFirestore.getInstance()

    // Listeners ko active rakhne aur memory leak se bachane ke liye variables
    private var driversListener: ListenerRegistration? = null
    private var pendingListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- BACK BUTTON LOGIC ---
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@AdminDashboard, SelectUserTypeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
            }
        })

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // --- CARD CLICK LISTENERS ---
        binding.cardPending.setOnClickListener {
            startActivity(Intent(this, Admin_pending_Activity::class.java))
        }

        binding.cardVerified.setOnClickListener {
            startActivity(Intent(this, DriverApprovalActivity::class.java))
        }

        binding.cardReports.setOnClickListener {
            startActivity(Intent(this, Driver_Report_Activity::class.java))
        }

        binding.cardTracking.setOnClickListener {
            startActivity(Intent(this, Driver_LiveTracking_Activity::class.java))
        }

        // --- REAL-TIME STATS UPDATER ---
        setupRealtimeStats()
    }

    private fun setupRealtimeStats() {
        // 1. Live count for Total Drivers (isApproved == true)
        driversListener = db.collection("driver_registrations")
            .whereEqualTo("isApproved", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("AdminDashboard", "Error listening to drivers count", error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val count = snapshot.size()
                    binding.tvTotalDriversCount.text = String.format("%02d", count) // Jaise 05, 12, 142 etc. format me show hoga
                }
            }

        // 2. Live count for Pending Verification (isApproved == false)
        pendingListener = db.collection("driver_registrations")
            .whereEqualTo("isApproved", false)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("AdminDashboard", "Error listening to pending count", error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val count = snapshot.size()
                    binding.tvPendingCount.text = String.format("%02d", count)
                }
            }

        // 3. Live count for Safety Reports (Agar reports ki collection "safety_reports" hai to)
        db.collection("safety_reports")
            .addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null) {
                    binding.tvReportsCount.text = String.format("%02d", snapshot.size())
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        // App background me memory na khaye, isliye listeners ko close karna zaroori hai
        driversListener?.remove()
        pendingListener?.remove()
    }
}