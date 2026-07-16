package com.example.ridemate.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ridemate.DataClasses.DriverModel
import com.example.ridemate.databinding.VerifiedDriverItemBinding

class DriverVerifiedAdapter(
    private val driverList: ArrayList<DriverModel>,
    private val onRejectListener: (DriverModel, Int) -> Unit
) : RecyclerView.Adapter<DriverVerifiedAdapter.VerifiedViewHolder>() {

    inner class VerifiedViewHolder(val binding: VerifiedDriverItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerifiedViewHolder {
        val binding = VerifiedDriverItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VerifiedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VerifiedViewHolder, position: Int) {
        val driver = driverList[position]

        // ✅ Updated Texts (Model ke exact keys ke mutabiq)
        holder.binding.tvVerifiedName.text = driver.name
        holder.binding.tvVerifiedPhone.text = driver.phone // Model: paymentWalletNumber
        holder.binding.tvVerifiedVehicle.text = driver.carModel// Model: carDetails

        // ✅ Updated Driver ki real profile pic loading logic (Model: profilePic)
        if (!driver.profilePicUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(driver.profilePicUrl)
                .into(holder.binding.imgVerifiedProfile)
        } else {
            holder.binding.imgVerifiedProfile.setImageResource(com.example.ridemate.R.drawable.images)
        }

        // 🛑 REJECT BUTTON CLICK LOGIC
        holder.binding.btnItemReject.setOnClickListener {
            onRejectListener(driver, position)
        }
    }

    override fun getItemCount(): Int = driverList.size

    fun removeItem(position: Int) {
        driverList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, driverList.size)
    }
}