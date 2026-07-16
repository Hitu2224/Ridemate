package com.example.ridemate.Adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ridemate.Admin.ReportDetailActivity
import com.example.ridemate.DataClasses.ReportModel
import com.example.ridemate.R

class AdminReportAdapter(private val reportList: ArrayList<ReportModel>) :
    RecyclerView.Adapter<AdminReportAdapter.ReportViewHolder>() {

    class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val reason: TextView = itemView.findViewById(R.id.tvReportReason)
        val driver: TextView = itemView.findViewById(R.id.tvDriverInfo)
        val desc: TextView = itemView.findViewById(R.id.tvDescription)
        val status: TextView = itemView.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.report_xml, parent, false)
        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = reportList[position]

        // ✅ Exact mapping with your data class variables:
        holder.reason.text = report.reason
        holder.driver.text = "Driver Info: ${report.driverInfo}"
        holder.desc.text = report.description
        holder.status.text = "Status: ${report.status}"

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ReportDetailActivity::class.java)

            // ✅ Serializable Object parsing (No crash!)
            intent.putExtra("REPORT_DATA", report)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = reportList.size
}