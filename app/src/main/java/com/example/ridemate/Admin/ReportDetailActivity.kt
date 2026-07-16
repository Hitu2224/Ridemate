package com.example.ridemate.Admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ridemate.DataClasses.ReportModel
import com.example.ridemate.databinding.ActivityReportDetailBinding // Apne layout binding ka naam likhein

class ReportDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 📥 Intent se report model ka custom data extract karna
        val report = intent.getSerializableExtra("REPORT_DATA") as? ReportModel

        if (report != null) {
            // UI views par set karna (Apni exact layout IDs check karlein)
            binding.tvDetailReason.text = report.reason
            binding.tvDetailDriver.text = report.driverInfo
            binding.tvDetailDesc.text = report.description
//            binding.tvDetailStatus.text = report.status
        }
    }
}