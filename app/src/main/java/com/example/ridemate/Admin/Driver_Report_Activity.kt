package com.example.ridemate.Admin

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ridemate.Adapters.AdminReportAdapter
import com.example.ridemate.DataClasses.ReportModel

import com.example.ridemate.databinding.ActivityDriverReportBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class Driver_Report_Activity : AppCompatActivity() {

    private lateinit var binding: ActivityDriverReportBinding
    private lateinit var reportsList: ArrayList<ReportModel>
    private lateinit var adapter: AdminReportAdapter // 👈 Ye adapter aapko create karna hoga
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. RecyclerView Setup (Layout Manager aur List set karein)
        setupRecyclerView()

        // 2. Data mangwaein (Fetch Reports)
        fetchReports()
    }

    private fun setupRecyclerView() {
        reportsList = ArrayList()
        adapter = AdminReportAdapter(reportsList)

        binding.rvReports.apply { // XML mein id 'rvAdminReports' honi chahiye
            layoutManager = LinearLayoutManager(this@Driver_Report_Activity)
            this.adapter = this@Driver_Report_Activity.adapter
        }
    }

    private fun fetchReports() {
        // Firebase se 'reports' collection fetch karein (Latest pehle)
        db.collection("reports")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FirebaseError", "Error fetching data: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    reportsList.clear() // Purana data saaf karein taake duplicate na ho
                    for (doc in snapshot.documents) {
                        val report = doc.toObject(ReportModel::class.java)
                        if (report != null) {
                            report.id = doc.id // Document ID save karein
                            reportsList.add(report)
                        }
                    }
                    // Adapter ko batayein ke data badal gaya hai
                    adapter.notifyDataSetChanged()
                }
            }
    }
}