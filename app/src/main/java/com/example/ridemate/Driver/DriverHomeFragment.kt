package com.example.ridemate.Driver

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ridemate.DataClasses.Data
import com.example.ridemate.Driver.DriverItemAdapter
import com.example.ridemate.Driver.NotificationActivity
import com.example.ridemate.databinding.FragmentDriverHomeBinding
import com.example.ridemate.utils.LoadingDialog
import com.google.firebase.firestore.FirebaseFirestore

class DriverHomeFragment : Fragment() {

    private var _binding: FragmentDriverHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: FirebaseFirestore
    private lateinit var dialog: LoadingDialog
    private lateinit var rideList: ArrayList<Data>
    private lateinit var driveAdapter: DriverItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Binding initialize karein
        _binding = FragmentDriverHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Firebase aur Dialog initialize karein
        dialog = LoadingDialog(requireContext())
        db = FirebaseFirestore.getInstance()

        // RecyclerView Setup
        binding.recyclerMyRides.layoutManager = LinearLayoutManager(requireContext())
        rideList = ArrayList()
        driveAdapter = DriverItemAdapter(requireContext(), rideList)
        binding.recyclerMyRides.adapter = driveAdapter

        // FAB Click listener
        binding.btnPostNewRide.setOnClickListener {
            startActivity(Intent(requireContext(), DriverPostActivity::class.java))
        }

        binding.notificationIc.setOnClickListener {
            val intent=Intent(requireContext(), NotificationActivity::class.java)
            startActivity(intent)
        }

        // Data fetch karein
        fetchRides()
    }

    private fun fetchRides() {
        dialog.show()

        db.collection("drivers")
            .addSnapshotListener { snapshot, error ->
                // Fragment mein hamesha check karein ke binding null na ho (Safety)
                if (_binding == null) return@addSnapshotListener

                dialog.dismiss()

                if (error != null) {
                    Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    rideList.clear()
                    val currentTime = System.currentTimeMillis()
                    val oneDayMillis = 24 * 60 * 60 * 1000

                    for (document in snapshot.documents) {
                        val ride = document.toObject(Data::class.java)
                        ride?.let {
                            if (currentTime - it.timestamp <= oneDayMillis) {
                                rideList.add(it)
                            } else {
                                deleteOldRide(document.id)
                            }
                        }
                    }
                    driveAdapter.notifyDataSetChanged()

                    // Empty state handle karein
                    if (rideList.isEmpty()) {
                        binding.emptyState.visibility = View.VISIBLE
                        Toast.makeText(requireContext(), "No data found", Toast.LENGTH_SHORT).show()
                    } else {
                        binding.emptyState.visibility = View.GONE
                    }
                }
            }
    }

    private fun deleteOldRide(docId: String) {
        db.collection("drivers").document(docId).delete()
            .addOnFailureListener { e -> Log.e("Firestore", "Delete failed: ${e.message}") }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Memory leak se bachne ke liye binding ko null karein
        _binding = null
    }
}