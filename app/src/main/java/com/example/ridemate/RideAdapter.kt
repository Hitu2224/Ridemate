package com.example.ridemate

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RideAdapter(
    private val context: Context,
    private val rideList: List<Data>,
) : RecyclerView.Adapter<RideAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fromText: TextView = itemView.findViewById(R.id.tvFromLocation)
        val toText: TextView = itemView.findViewById(R.id.tvToLocation)
        val dateText: TextView = itemView.findViewById(R.id.tvDate)
        val seatText: TextView = itemView.findViewById(R.id.tvSeats)
        val modelText: TextView = itemView.findViewById(R.id.tvModel)
        val priceText: TextView = itemView.findViewById(R.id.tvPrice)
        val timeset:TextView=itemView.findViewById(R.id.time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_trip, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = rideList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ride = rideList[position]
        holder.fromText.text = ride.from
        holder.toText.text = ride.to
        holder.dateText.text = ride.date
        holder.seatText.text = ride.seats.toString()
        holder.modelText.text = ride.model
        holder.priceText.text = "Rs. ${ride.price}"

        val now = System.currentTimeMillis()
        val diff = now - ride.timestamp

        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60

        holder.timeset.text = when {
            minutes < 1 -> "Just now"
            minutes < 60 -> "$minutes minutes ago"
            hours < 24 -> "$hours hours ago"
            else -> "Yesterday"
        }

    }
}
