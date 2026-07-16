package com.example.ridemate.Admin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.ridemate.DataClasses.DriverModel
import com.example.ridemate.R
import com.example.ridemate.databinding.ActivityDriverVerifyDetailsBinding
import com.google.firebase.firestore.FirebaseFirestore

class Driver_verify_details : AppCompatActivity() {

    private lateinit var binding: ActivityDriverVerifyDetailsBinding
    private val db = FirebaseFirestore.getInstance()
    private var driverUid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverVerifyDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Intent se unique Driver UID lena jo list screen se pass hogi
        driverUid = intent.getStringExtra("DRIVER_UID")

        if (driverUid != null) {
            fetchDriverDetails(driverUid!!)
        } else {
            Toast.makeText(this, "Driver details not found!", Toast.LENGTH_SHORT).show()
            finish()
        }

        // 2. VERIFY (APPROVE) BUTTON CLICK LOGIC
        binding.btnVerify.setOnClickListener {
            updateDriverStatus(true) // Approved -> isApproved = true
        }

        // 3. REJECT BUTTON CLICK LOGIC
        binding.btnReject.setOnClickListener {
            updateDriverStatus(false) // Rejected -> isApproved = false (or handle rejection logic)
        }
    }

    // 🔍 Database se data fetch karke layout ke views me set karne ki logic
    private fun fetchDriverDetails(uid: String) {
        db.collection("driver_registrations").document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val driver = document.toObject(DriverModel::class.java)
                    if (driver != null) {
                        // ✅ XML IDs ke mutabiq text values set karna
                        binding.tvName.text = driver.name
                        binding.tvVehicle.text = driver.carModel
                        binding.tvPayment.text = driver.phone

                        // 🖼️ 1. Driver Profile Picture Load karna
                        if (!driver.profilePicUrl.isNullOrEmpty()) {
                            Glide.with(this)
                                .load(driver.profilePicUrl)
                                .placeholder(R.drawable.avatar) // Loading ke waqt default avatar dikhe
                                .into(binding.imgDriverProfile)
                        }

                        // 💳 2. Identity Document (CNIC) Image Load karna
                        if (!driver.cnicUrl.isNullOrEmpty()) {
                            Glide.with(this)
                                .load(driver.cnicUrl)
                                .into(binding.imgIdentityDoc)
                        }

                        // 🪪 3. Driving License Image Load karna
                        if (!driver.licenseUrl.isNullOrEmpty()) {
                            Glide.with(this)
                                .load(driver.licenseUrl)
                                .into(binding.imgLicense)
                        }
                    }
                } else {
                    Toast.makeText(this, "Document does not exist!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Data fetch fail: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // ⚡ Status Update logic
    private fun updateDriverStatus(approve: Boolean) {
        if (driverUid == null) return

        binding.btnVerify.isEnabled = false
        binding.btnReject.isEnabled = false

        db.collection("driver_registrations").document(driverUid!!)
            .update("isApproved", approve) // ✅ Field: isApproved ko true ya false update karega
            .addOnSuccessListener {
                val message = if (approve) "Driver Approved Successfully! 🎉" else "Driver Rejected/Pending!"
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                finish() // Activity close karke wapas list par bhej dega
            }
            .addOnFailureListener { e ->
                binding.btnVerify.isEnabled = true
                binding.btnReject.isEnabled = true
                Toast.makeText(this, "Failed to update: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}