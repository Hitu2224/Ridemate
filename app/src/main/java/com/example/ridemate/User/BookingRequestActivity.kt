package com.example.ridemate.User

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.ridemate.Fragments.Active_Ride_Activity
import com.example.ridemate.databinding.ActivityBookingRequestBinding

class BookingRequestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookingRequestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityBookingRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- 1. DATA RECEIVE (Pichli Activity se Data Pakarna) ---
        val rideId = intent.getStringExtra("RIDE_ID") ?: ""
        val from = intent.getStringExtra("from") ?: "N/A"
        val to = intent.getStringExtra("to") ?: "N/A"

        // Agar aapne HomeFragment ya bookingSytem mein .toString() use kiya hai,
        // to yahan getStringExtra use karke toInt() karna sabse best hai.
        val priceString = intent.getStringExtra("price") ?: "0"
        val basePrice = priceString.filter { it.isDigit() }.toIntOrNull() ?: 0

        // --- 2. INITIAL UI SETUP (Activity Khulne Par Kya Dikhega) ---
        binding.tvFrom.text = from
        binding.tvTo.text = to
        // Shuru mein Driver wali price dikhayein
        binding.tvTotalFare.text = "$basePrice PKR"

        // --- 3. LIVE CALCULATION (Seats ke hisab se Fare change karna) ---
        binding.etSeatInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s.toString().trim()

                if (input.isNotEmpty()) {
                    val numSeats = input.toIntOrNull() ?: 1

                    // Valid Range: 1 to 4 Seats
                    if (numSeats in 1..4) {
                        // Formula: Seats × Driver Price
                        val total = numSeats * basePrice
                        binding.tvTotalFare.text = "$total PKR"
                    } else {
                        // Agar user 4 se zyada likhe
                        binding.etSeatInput.error = "Max 4 seats allowed"
                        binding.tvTotalFare.text = "$basePrice PKR"
                    }
                } else {
                    // Agar input khali ho, to base price wapas dikha do
                    binding.tvTotalFare.text = "$basePrice PKR"
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // --- 4. SEND REQUEST (Data agli screen par bhejna) ---
        binding.btnSendRequest.setOnClickListener {
            val seatsInput = binding.etSeatInput.text.toString().trim()

            if (seatsInput.isNotEmpty() && seatsInput.toInt() in 1..4) {
                val intent = Intent(this, Active_Ride_Activity::class.java)

                // Agli screen (Active Ride) ko poora data pass karein
                intent.putExtra("RIDE_ID", rideId)
                intent.putExtra("TOTAL_PRICE", binding.tvTotalFare.text.toString())
                intent.putExtra("SEATS_BOOKED", seatsInput)

                startActivity(intent)
                finish() // User wapas is form par na aa sake
            } else {
                binding.etSeatInput.error = "Please enter valid seats (1-4)"
            }
        }
    }
}