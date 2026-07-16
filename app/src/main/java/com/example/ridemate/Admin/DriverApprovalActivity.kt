package com.example.ridemate.Admin

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ridemate.Adapters.DriverVerifiedAdapter
import com.example.ridemate.DataClasses.DriverModel
import com.example.ridemate.databinding.ActivityDriverApprovalBinding
import com.google.firebase.firestore.FirebaseFirestore

class DriverApprovalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDriverApprovalBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var verifiedAdapter: DriverVerifiedAdapter
    private val driverlist = ArrayList<DriverModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        binding = ActivityDriverApprovalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. RecyclerView setup
        binding.rvReports.layoutManager = LinearLayoutManager(this)

        // 2. Adapter initialize kiya (Model ki unique "id" pass kar rahe hain reject function me)
        verifiedAdapter = DriverVerifiedAdapter(driverlist) { clickedDriver, position ->
            rejectVerifiedDriver(clickedDriver.id, position) // 👈 Model's "id" used
        }

        // 3. RecyclerView ko adapter assign kiya
        binding.rvReports.adapter = verifiedAdapter

        // 4. Live verified data load karne ka call
        loadVerifiedDrivers()
    }

    // 🔍 Database se sirf VERIFIED drivers fetch karna (isApproved == true)
    private fun loadVerifiedDrivers() {
        db.collection("driver_registrations")
            .whereEqualTo("isApproved", true) // 👈 Status query updated to Boolean check
            .get()
            .addOnSuccessListener { documents ->
                driverlist.clear()

                for (document in documents) {
                    val driver = document.toObject(DriverModel::class.java)
                    if (driver != null) {
                        driverlist.add(driver)
                    }
                }
                verifiedAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // 🛑 Driver ko status "Pending" ya "Rejected" par wapas bhejna
    private fun rejectVerifiedDriver(driverId: String, position: Int) {
        // Validation check
        if (driverId.isEmpty()) return

        db.collection("driver_registrations").document(driverId)
            .update(
                "isApproved", false,
                "status", "Rejected" // 👈 isApproved ko false kiya aur status Rejected kiya
            )
            .addOnSuccessListener {
                Toast.makeText(this, "Driver status reverted to Rejected!", Toast.LENGTH_SHORT).show()

                // Real-time animation ke sath list se remove karna
                verifiedAdapter.removeItem(position)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Action failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}