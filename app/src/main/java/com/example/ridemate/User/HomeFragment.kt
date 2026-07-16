package com.example.ridemate.User

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ridemate.DataClasses.Data
import com.example.ridemate.Adapters.RideAdapter
import com.example.ridemate.User.bookingSytem
import com.example.ridemate.databinding.FragmentHomeBinding
import com.example.ridemate.utils.LoadingDialog
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

    private lateinit var dialog: LoadingDialog
    private lateinit var mapView: MapView
    private lateinit var mapController: IMapController
    private lateinit var myLocationOverlay: MyLocationNewOverlay
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Configuration setup
        Configuration.getInstance().load(requireContext(), requireContext().getSharedPreferences("osmdroid", 0))
        Configuration.getInstance().userAgentValue = requireContext().packageName

        // 1. Initializations
        db = FirebaseFirestore.getInstance()
        dialog = LoadingDialog(requireContext()) // ✅ Correct Initialization
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        setupRecyclerView()
        setupMap()
        fetchDataFromFirestore()

        binding.btnMyLocation.setOnClickListener { goToMyLocation() }

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        rideList = ArrayList()
        rideAdapter = RideAdapter(requireContext(), rideList) { selectedRide ->
            val intent = Intent(requireContext(), bookingSytem::class.java).apply {
                putExtra("from", selectedRide.from)
                putExtra("to", selectedRide.to)
                putExtra("price", selectedRide.price.toString())
                putExtra("date", selectedRide.date)
                putExtra("car_name", selectedRide.model)
            }
            startActivity(intent)
        }
        binding.recyclerView.adapter = rideAdapter
    }

    private fun setupMap() {
        mapView = binding.mapView
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapController = mapView.controller
        mapController.setZoom(15.0)

        myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), mapView)
        mapView.overlays.add(myLocationOverlay)
        mapController.setCenter(GeoPoint(30.3753, 69.3451))

        if (checkLocationPermission()) {
            myLocationOverlay.enableMyLocation()
            mapView.post { goToMyLocation() }
        } else {
            requestLocationPermission()
        }
    }

    private fun fetchDataFromFirestore() {
        // 2. Dialog ko listener ke bahar show karein
        dialog.show()

        db.collection("drivers")
            .addSnapshotListener { snapshot, error ->
                // 3. Fragment safety check aur dismiss
                if (_binding == null) return@addSnapshotListener
                dialog.dismiss()

                if (error != null) {
                    Log.e("FirestoreError", error.message.toString())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val filteredList = mutableListOf<Data>()
                    val currentTime = System.currentTimeMillis()
                    val oneDay = 24 * 60 * 60 * 1000

                    snapshot.documents.forEach { document ->
                        val ride = document.toObject(Data::class.java)
                        ride?.let {
                            it.id = document.id
                            if (currentTime - it.timestamp <= oneDay) {
                                filteredList.add(it)
                            } else {
                                // Background delete
                                db.collection("drivers").document(document.id).delete()
                            }
                        }
                    }

                    rideList.clear()
                    rideList.addAll(filteredList)
                    rideAdapter.notifyDataSetChanged()
                }
            }
    }

    // --- Location Helpers ---
    private fun goToMyLocation() {
        if (!checkLocationPermission()) {
            requestLocationPermission()
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val point = GeoPoint(it.latitude, it.longitude)
                mapController.animateTo(point)
                mapController.setZoom(17.0)
            }
        }
    }

    private fun checkLocationPermission() = ContextCompat.checkSelfPermission(
        requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private fun requestLocationPermission() {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
    }

    override fun onResume() { super.onResume(); mapView.onResume() }
    override fun onPause() { super.onPause(); mapView.onPause() }
    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDetach()
        _binding = null
    }
}