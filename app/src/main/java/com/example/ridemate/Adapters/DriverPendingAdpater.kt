package com.example.ridemate.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ridemate.DataClasses.DriverModel
import com.example.ridemate.R
import com.example.ridemate.databinding.ItemPendingDriverBinding

class DriverPendingAdpater(
    private val driverList: List<DriverModel>,
    private val listener: OnDriverClickListener
) : RecyclerView.Adapter<DriverPendingAdpater.DriverViewHolder>() {

    interface OnDriverClickListener {
        fun onDriverClick(driver: DriverModel)
    }

    inner class DriverViewHolder(val binding: ItemPendingDriverBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DriverViewHolder {
        val binding = ItemPendingDriverBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return DriverViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DriverViewHolder, position: Int) {
        val driver = driverList[position]
        holder.binding.apply {
            tvDriverName.text = driver.name
            tvDriverPhone.text = driver.phone
            tvStatus.text = driver.status

            // 🖼️ Real-time Profile Image Loading (With Fallback/Placeholder)
            if (!driver.profilePicUrl.isNullOrEmpty()) {
                Glide.with(holder.itemView.context)
                    .load(driver.profilePicUrl)
                    .placeholder(R.drawable.avatar) // Loading ke waqt default image
                    .error(R.drawable.avatar)       // Error aane par default image
                    .into(imgDriver)
            } else {
                imgDriver.setImageResource(R.drawable.avatar)
            }

            // ⚡ Card Click Listener
            root.setOnClickListener { listener.onDriverClick(driver) }
        }
    }

    override fun getItemCount(): Int = driverList.size
}