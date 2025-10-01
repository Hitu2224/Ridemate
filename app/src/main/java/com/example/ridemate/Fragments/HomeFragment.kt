package com.example.ridemate.Fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ridemate.Data
import com.example.ridemate.RideAdapter
import com.example.ridemate.databinding.FragmentHomeBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: FirebaseFirestore
    private lateinit var rideList: ArrayList<Data>
    private lateinit var rideAdapter: RideAdapter

    // Map variables
    private lateinit var mapView: MapView
    private lateinit var mapController: IMapController
    private lateinit var myLocationOverlay: MyLocationNewOverlay
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // OSM Configuration
        Configuration.getInstance().load(requireContext(),
            requireContext().getSharedPreferences("osmdroid", 0))
        Configuration.getInstance().userAgentValue = requireContext().packageName

        // Initialize Firebase
        db = FirebaseFirestore.getInstance()

        // Setup Location Services FIRST
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Setup My Location Button
        binding.btnMyLocation.setOnClickListener {
            goToMyLocation()
        }

        // Setup RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        rideList = ArrayList()
        rideAdapter = RideAdapter(requireContext(), rideList)
        binding.recyclerView.adapter = rideAdapter

        // Setup Map AFTER location client is initialized
        setupMap()

        // Fetch Firebase data
        fetchDataFromFirestore()

        return binding.root
    }

    private fun setupMap() {
        mapView = binding.mapView  // This should match your layout ID: map_view
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)  // Finger gestures enabled
        mapView.setBuiltInZoomControls(false)  // No +/- buttons

        mapController = mapView.controller
        mapController.setZoom(15.0)

        // Location overlay
        myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), mapView)
        mapView.overlays.add(myLocationOverlay)

        // Default center - Pakistan
        mapController.setCenter(GeoPoint(30.3753, 69.3451))

        // Check and request location permission - but don't call goToMyLocation immediately
        if (checkLocationPermission()) {
            myLocationOverlay.enableMyLocation()
            // Call goToMyLocation with a slight delay to ensure everything is ready
            mapView.post {
                goToMyLocation()
            }
        } else {
            requestLocationPermission()
        }
    }

    private fun goToMyLocation() {
        if (!checkLocationPermission()) {
            requestLocationPermission()
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val currentPoint = GeoPoint(it.latitude, it.longitude)
                mapController.animateTo(currentPoint)
                mapController.setZoom(17.0)
                Toast.makeText(requireContext(), "Your location", Toast.LENGTH_SHORT).show()
            } ?: run {
                Toast.makeText(requireContext(), "Getting location...", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Location error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                myLocationOverlay.enableMyLocation()
                // Use post to ensure everything is ready
                mapView.post {
                    goToMyLocation()
                }
                Toast.makeText(requireContext(), "Location enabled", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Location permission needed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchDataFromFirestore() {
        db.collection("drivers")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    rideList.clear()
                    for (document in snapshot.documents) {
                        val ride = document.toObject(Data::class.java)
                        val docId = document.id

                        ride?.let {
                            val currentTime = System.currentTimeMillis()
                            val twentyFourHoursInMillis = 24 * 60 * 60 * 1000
                            val timeDiff = currentTime - it.timestamp

                            if (timeDiff <= twentyFourHoursInMillis) {
                                rideList.add(it)
                            } else {
                                // Delete expired post
                                db.collection("drivers").document(docId).delete()
                            }
                        }
                    }
                    rideAdapter.notifyDataSetChanged()
                } else {
                    rideList.clear()
                    rideAdapter.notifyDataSetChanged()
                    Toast.makeText(requireContext(), "No data found", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDetach()
        _binding = null
    }
}