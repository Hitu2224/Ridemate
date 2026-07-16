package com.example.ridemate.User

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.ridemate.databinding.ActivityReportBinding
import com.google.firebase.firestore.FirebaseFirestore

class Report_Activity : AppCompatActivity() {

    private lateinit var binding: ActivityReportBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Dropdown List Setup
        val reportReasons = arrayOf(
            "Overcharging",
            "Rash Driving",
            "Misbehavior",
            "Route Deviation",
            "Vehicle Condition",
            "Other"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, reportReasons)
        binding.autoCompleteReason.setAdapter(adapter)

        // 2. Submit Button Click
        binding.btnSubmitReport.setOnClickListener {
            sendReportToFirebase()
        }
    }

    // --- Function to Send Data ---
    private fun sendReportToFirebase() {
        val driverInfo = binding.etReportDriverInfo.text.toString().trim()
        val reason = binding.autoCompleteReason.text.toString().trim()
        val description = binding.etReportDesc.text.toString().trim()

        // Validation check
        if (driverInfo.isEmpty() || reason.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnSubmitReport.isEnabled = false
        binding.btnSubmitReport.text = "Submitting, please wait..."

        // ✅ Firestore se pehle unique document ID generate ki
        val reportRef = db.collection("safety_reports").document() // Collection name matching: safety_reports
        val generatedId = reportRef.id

        // ✅ Data Map with exact matching keys of ReportModel
        val reportData = hashMapOf(
            "id" to generatedId, // Pass generating document id here
            "driverInfo" to driverInfo,
            "reason" to reason,
            "description" to description,
            "timestamp" to System.currentTimeMillis(),
            "status" to "Pending"
        )

        // Firebase mein Save karein
        reportRef.set(reportData)
            .addOnSuccessListener {
                Toast.makeText(this, "Report Admin ko bhej di gayi hai! 🛑", Toast.LENGTH_SHORT).show()
                finish() // Activity band kar dein
            }
            .addOnFailureListener { e ->
                binding.btnSubmitReport.isEnabled = true
                binding.btnSubmitReport.text = "Submit Report to Admin"
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}