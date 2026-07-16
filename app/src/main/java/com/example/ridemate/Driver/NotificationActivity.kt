package com.example.ridemate.Driver

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ridemate.User.Chat_Activity
import com.example.ridemate.R
import com.example.ridemate.Adapters.RequestAdapter
import com.example.ridemate.Adapters.RideRequest
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton

class NotificationActivity : AppCompatActivity(), RequestAdapter.OnRequestClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RequestAdapter
    private var requestList = mutableListOf<RideRequest>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_notification)

        val rootView = findViewById<View>(R.id.main)
        rootView?.let {
            ViewCompat.setOnApplyWindowInsetsListener(it) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        recyclerView = findViewById(R.id.notification_recycle)
        recyclerView.layoutManager = LinearLayoutManager(this)

        prepareDummyData()

        adapter = RequestAdapter(requestList, this)
        recyclerView.adapter = adapter
    }

    private fun prepareDummyData() {
        requestList.clear()
        // Numbers ko " " mein likhein taake wo String ban jayein
        requestList.add(
            RideRequest(
                "1",
                "Ali Ahmed",
                "QUEST",
                "Hyderabad",
                "450",
                "2",
                "Jaldi aana"
            )
        )
        requestList.add(
            RideRequest(
                "2",
                "Hitesh Kumar",
                "Nawabshah",
                "Khipro",
                "800",
                "1",
                "Gate pe hoon"
            )
        )
    }

    // --- BOTTOM SHEET LOGIC YAHAN HAI ---
    override fun onItemClick(request: RideRequest) {
        val dialog = BottomSheetDialog(this)
        // Tumne jo naya layout banaya hai uska naam yahan likho
        val sheetView = layoutInflater.inflate(R.layout.request_detail, null)

        // 1. Bottom Sheet ke Views link karein
        val tvName = sheetView.findViewById<TextView>(R.id.bsName)
        val tvRoute = sheetView.findViewById<TextView>(R.id.bsRoute)
        val tvFare = sheetView.findViewById<TextView>(R.id.bsFare)
        val tvSeats = sheetView.findViewById<TextView>(R.id.bsSeats)
        val tvMessage = sheetView.findViewById<TextView>(R.id.bsMessage)
        val btnAccept = sheetView.findViewById<MaterialButton>(R.id.btnAccept)
        val btnChat = sheetView.findViewById<MaterialButton>(R.id.btnChat)
        val btnDecline = sheetView.findViewById<MaterialButton>(R.id.btnDecline)

        // 2. Data set karein
        tvName.text = request.userName
        tvRoute.text = "${request.pickup} ➔ ${request.dropoff}"
        tvFare.text = "Rs. ${request.price}"
        tvSeats.text = "${request.seats} Seats"
        tvMessage.text = "Note: ${request.message}"

        // 3. CHAT BUTTON LOGIC
        btnChat.setOnClickListener {
            val intent = Intent(this, Chat_Activity::class.java)
            intent.putExtra("RIDE_ID", request.id)
            intent.putExtra("PARTNER_NAME", request.userName)
            startActivity(intent)
            dialog.dismiss()
        }

        // 4. ACCEPT BUTTON LOGIC
        btnAccept.setOnClickListener {
            // Yahan Driver ride accept karega (Firestore logic baad mein add kar lena)
            Toast.makeText(this, "Ride Accepted for ${request.userName}", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        // 5. REJECT BUTTON
        btnDecline.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(sheetView)
        dialog.show()
    }
}