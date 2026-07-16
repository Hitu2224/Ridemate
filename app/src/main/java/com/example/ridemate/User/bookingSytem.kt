package com.example.ridemate.User

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.ridemate.User.BookingRequestActivity
import com.example.ridemate.User.PaymentActivity
import com.example.ridemate.databinding.ActivityBookingSytemBinding

class bookingSytem : AppCompatActivity() {

    private lateinit var binding: ActivityBookingSytemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingSytemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()

        // Key names check karein (HomeFragment se match honi chahiye)
        val rideId = intent.getStringExtra("RIDE_ID") ?: ""
        val from = intent.getStringExtra("from")
        val to = intent.getStringExtra("to")
        val price = intent.getStringExtra("price") ?: "0" // ✅ String pakrein
        val carName = intent.getStringExtra("car_name") // Ab ye "car_name" match karega

        binding.tvDetailPrice.text = "$price"
        binding.tvDetailCar.text = carName
        binding.tvDetailFrom.text = from
        binding.tvDetailTo.text = to

        binding.btnBookRide.setOnClickListener {
            val intent = Intent(this, BookingRequestActivity::class.java)
            intent.putExtra("RIDE_ID", rideId)
            intent.putExtra("from", from)
            intent.putExtra("to", to)
            intent.putExtra("price", price) // Price yahan se agli screen par chali jayegi
            startActivity(intent)
        }
    }
}