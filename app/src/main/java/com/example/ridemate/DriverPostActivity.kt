package com.example.ridemate

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ridemate.databinding.ActivityDriverPostBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class DriverPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDriverPostBinding
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()

        binding.textDateTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                binding.textDateTime.text = formattedDate
            }, year, month, day)

            datePickerDialog.show()
        }

        binding.btnPostRide.setOnClickListener {
            val from = binding.etSource.text.toString().trim()
            val to = binding.etDestination.text.toString().trim()
            val date = binding.textDateTime.text.toString().trim()
            val model = binding.etCarModel.text.toString().trim()
            val priceInput = binding.etPrice.text.toString().trim()
            val price = priceInput.toIntOrNull() ?: 0

            val selectedChipId = binding.seatChipGroup.checkedChipId
            val seats = if (selectedChipId != -1) {
                val chip = binding.seatChipGroup.findViewById<com.google.android.material.chip.Chip>(selectedChipId)
                chip.text.toString().toIntOrNull() ?: 1
            } else {
                1
            }

            val ride = Data(
                from = from,
                to = to,
                date = date,
                seats = seats,
                model = model,
                price = price,
                timestamp = System.currentTimeMillis() // Auto set current time
            )

            db.collection("drivers")
                .add(ride)
                .addOnSuccessListener {
                    Toast.makeText(this, "Ride posted successfully!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        // Optionally clean old posts when screen opens
        deleteOldPosts()
    }

    private fun deleteOldPosts() {
        val twentyFourHoursAgo = System.currentTimeMillis() - (24 * 60 * 60 * 1000)
        db.collection("drivers")
            .whereLessThan("timestamp", twentyFourHoursAgo)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    db.collection("drivers").document(document.id).delete()
                }
            }
    }
}
