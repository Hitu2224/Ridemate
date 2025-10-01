package com.example.ridemate

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ridemate.databinding.ActivityNewRideRequestBinding
import com.google.firebase.firestore.FirebaseFirestore

class NewRideRequestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewRideRequestBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var rideList: ArrayList<Data>
    private lateinit var driveAdapter: DriverItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewRideRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRequestRide.setOnClickListener {
            startActivity(Intent(this, DriverPostActivity::class.java))
        }

        db = FirebaseFirestore.getInstance()
        binding.recyclerRides.layoutManager = LinearLayoutManager(this)

        rideList = ArrayList()
        driveAdapter = DriverItemAdapter(this, rideList)
        binding.recyclerRides.adapter = driveAdapter

        db.collection("drivers")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG)
                        .show()
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    rideList.clear()
                    for (document in snapshot.documents) {
                        val ride = document.toObject(Data::class.java)
                        val docId = document.id
                        ride?.let {
                            val currentTime = System.currentTimeMillis()
                            val postTime = it.timestamp
                            val timeDiff = currentTime - postTime

                            if (timeDiff <= 24 * 60 * 60 * 1000) {
                                // Post is within 24 hours
                                rideList.add(it)
                            } else {
                                // Delete post if older than 24 hours
                                db.collection("drivers").document(docId).delete()
                            }
                        }
                    }
                    driveAdapter.notifyDataSetChanged()
                } else {
                    rideList.clear()
                    driveAdapter.notifyDataSetChanged()
                    Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show()
                }
            }
    }
    }