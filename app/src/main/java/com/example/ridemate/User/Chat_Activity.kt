package com.example.ridemate.User

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ridemate.Adapters.ChatAdapter
import com.example.ridemate.DataClasses.MessageModel
import com.example.ridemate.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class Chat_Activity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var adapter: ChatAdapter
    private val db = FirebaseFirestore.getInstance()

    // Lazy initialization taake Firebase properly load ho chuka ho
    private val currentUserId by lazy { FirebaseAuth.getInstance().currentUser?.uid ?: "" }
    private var rideId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Intent se data nikalen aur check karein
        rideId = intent.getStringExtra("RIDE_ID")
        val partnerName = intent.getStringExtra("PARTNER_NAME")

        // CRASH PREVENTER: Agar Ride ID nahi mili toh Activity band kar do
        if (rideId.isNullOrEmpty()) {
            Log.e("CHAT_DEBUG", "Invalid document reference: Ride ID is missing!")
            Toast.makeText(this, "Chat connection failed!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.tvChatPartnerName.text = partnerName ?: "Chat Partner"

        // 2. Adapter Setup
        adapter = ChatAdapter(currentUserId)
        binding.chatRecyclerView.adapter = adapter
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }

        // 3. Real-time Messages Listen karein
        listenForMessages(rideId!!)

        // 4. Send button logic
        binding.btnSend.setOnClickListener {
            val message = binding.etMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                sendMessage(rideId!!, message)
            }
        }

        binding.btnBack.setOnClickListener { finish() }
    }

    private fun sendMessage(rId: String, text: String) {
        val msg = MessageModel(currentUserId, text, System.currentTimeMillis())

        // "chats/rId/messages" -> Even segments (Correct Path)
        db.collection("chats").document(rId)
            .collection("messages")
            .add(msg)
            .addOnSuccessListener {
                binding.etMessage.text.clear()
                Log.d("CHAT_DEBUG", "Message sent!")
            }
            .addOnFailureListener { e ->
                Log.e("CHAT_DEBUG", "Send failed: ${e.message}")
            }
    }

    private fun listenForMessages(rId: String) {
        db.collection("chats").document(rId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("CHAT_DEBUG", "Listen failed: ${error.message}")
                    return@addSnapshotListener
                }

                val list = value?.toObjects(MessageModel::class.java) ?: listOf()
                adapter.setMessages(list)

                if (list.isNotEmpty()) {
                    binding.chatRecyclerView.scrollToPosition(list.size - 1)
                }
            }
    }
}