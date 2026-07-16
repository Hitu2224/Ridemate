package com.example.ridemate.Admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ridemate.DataClasses.DriverModel
import com.example.ridemate.Adapters.DriverPendingAdpater
import com.example.ridemate.databinding.ActivityAdminPendingBinding
import com.google.firebase.firestore.FirebaseFirestore

class Admin_pending_Activity : AppCompatActivity(), DriverPendingAdpater.OnDriverClickListener {

    private lateinit var binding: ActivityAdminPendingBinding
    private lateinit var adapter: DriverPendingAdpater
    private lateinit var pendingList: ArrayList<DriverModel>
    private val db = FirebaseFirestore.getInstance() // 👈 Firestore DB Reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminPendingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup RecyclerView
        binding.rcPendingDrivers.layoutManager = LinearLayoutManager(this)
        pendingList = ArrayList()

        // Adapter initialization with Click Listener
        adapter = DriverPendingAdpater(pendingList, this)
        binding.rcPendingDrivers.adapter = adapter

        // ✅ Live pending drivers load karein
        loadPendingDrivers()
    }

    // 🔍 Real-time Database query to get pending drivers
    private fun loadPendingDrivers() {
        db.collection("driver_registrations")
            .whereEqualTo("isApproved", false) // 👈 Sirf wo drivers jo pending hain
            .get()
            .addOnSuccessListener { documents ->
                pendingList.clear()
                for (document in documents) {
                    val driver = document.toObject(DriverModel::class.java)
                    if (driver != null) {
                        pendingList.add(driver)
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load pending drivers: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Jab Admin kisi driver par click karega
    override fun onDriverClick(driver: DriverModel) {
        val intent = Intent(this, Driver_verify_details::class.java)
        // ✅ KEY MATCHING: "driver_id" ki jagah "DRIVER_UID" pass kiya taake detail screen extract kar sake
        intent.putExtra("DRIVER_UID", driver.id)
        startActivity(intent)
    }
}