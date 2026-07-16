package com.example.ridemate.Adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ridemate.R
import com.example.ridemate.Adapters.RideRequest
import com.example.ridemate.User.BookingRequestActivity
import com.google.android.material.card.MaterialCardView

class RequestAdapter(
    private val requestList: List<RideRequest>,
    private val listener: OnRequestClickListener
) : RecyclerView.Adapter<RequestAdapter.RequestViewHolder>() {

    // 1. Click Listener Interface
    interface OnRequestClickListener {
        fun onItemClick(request: RideRequest)
    }

    // 2. ViewHolder Class: Jo XML views ko hold karta hai
    class RequestViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rootCard: MaterialCardView = view as MaterialCardView
        val ivUserThumb: ImageView = view.findViewById(R.id.ivUserThumb)
        val tvName: TextView = view.findViewById(R.id.tvRequesterName)
        val tvRoute: TextView = view.findViewById(R.id.tvRouteSummary)
        val tvSeats: TextView = view.findViewById(R.id.tvSeats)
        val tvTime: TextView = view.findViewById(R.id.tvTimestamp)
        val tvPrice: TextView = view.findViewById(R.id.tvPrice)
    }

    // 3. Layout Inflate karna
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.notification_card, parent, false) // Notification card layout file
        return RequestViewHolder(view)
    }

    // 4. Data ko Views ke saath bind karna
    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val request = requestList[position]

        holder.tvName.text = request.userName
        holder.tvRoute.text = "${request.pickup} ➔ ${request.dropoff}"
        holder.tvPrice.text = "Rs. ${request.price}"
        holder.tvSeats.text = "${request.seats} Seats"
        holder.tvTime.text = "Just now" // Agar model mein time hai toh wo use karein

        // Poore card par click listener
        holder.rootCard.setOnClickListener {
             listener.onItemClick(request)
        }

        // Agar profile pic set karni ho (Example with static drawable)
        // holder.ivUserThumb.setImageResource(R.drawable.developer)
    }

    override fun getItemCount() = requestList.size
}