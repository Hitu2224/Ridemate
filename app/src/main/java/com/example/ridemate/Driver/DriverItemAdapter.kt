package com.example.ridemate.Driver

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ridemate.DataClasses.Data
import com.example.ridemate.R
import com.google.android.material.bottomsheet.BottomSheetDialog

class DriverItemAdapter(private val context: Context, private val rideList: List<Data>) :
    RecyclerView.Adapter<DriverItemAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fromText: TextView = itemView.findViewById(R.id.tvFromLocation)
        val toText: TextView = itemView.findViewById(R.id.tvToLocation)
        val dateText: TextView = itemView.findViewById(R.id.tvDateTime) // XML se match karein
        val settime: TextView = itemView.findViewById(R.id.tvDateTime) // ya alag field
//        val seatText: TextView = itemView.findViewById(R.id.tvSeatsStatus) // Updated ID
        val priceText: TextView = itemView.findViewById(R.id.tvPrice)
        val btndelt: ImageView = itemView.findViewById(R.id.btnDelete)

        // Naya Button Jo Humne XML mein add kiya tha
        val btnViewPayments: Button = itemView.findViewById(R.id.btnViewPayments)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.driver_ride_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = rideList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ride = rideList[position]
        holder.fromText.text = ride.from
        holder.toText.text = ride.to

        // Seats Status (Professional touch)
//        holder.seatText.text = "💺 Seats: 1/${ride.seats} Confirmed"
        holder.priceText.text = "${ride.price} PKR"

        // Timestamp Logic (Jo aapne likha tha)
        val now = System.currentTimeMillis()
        val diff = now - ride.timestamp
        val minutes = (diff / 1000) / 60
        val hours = minutes / 60

        holder.dateText.text = when {
            minutes < 1 -> "Just now"
            minutes < 60 -> "$minutes mins ago"
            hours < 24 -> "$hours hours ago"
            else -> ride.date
        }

        // --- BOTTOM SHEET LOGIC ---
        holder.btnViewPayments.setOnClickListener {
            showPaymentBottomSheet(ride)
        }

        // Delete button logic (Optional)
        holder.btndelt.setOnClickListener {
            // Delete function call karein
        }
    }

    private fun showPaymentBottomSheet(ride: Data) {
        val bottomSheetDialog = BottomSheetDialog(context)
        val view = LayoutInflater.from(context).inflate(R.layout.payment_request, null)

        val rvRequests = view.findViewById<RecyclerView>(R.id.rvPaymentRequests)
        rvRequests.layoutManager = LinearLayoutManager(context)

        // TODO: Yahan aap bacho ki payment list ka adapter set karenge
        // val adapter = PaymentRequestAdapter(ride.id)
        // rvRequests.adapter = adapter

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }
}