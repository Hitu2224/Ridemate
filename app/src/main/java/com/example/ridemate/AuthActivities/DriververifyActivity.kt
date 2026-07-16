package com.example.ridemate.AuthActivities

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.ridemate.Driver.NewRideRequestActivity
import com.example.ridemate.databinding.ActivityDriverVerifyDetailsBinding
import com.example.ridemate.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DriververifyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDriverVerifyDetailsBinding
    private val db= FirebaseFirestore.getInstance()

    private var targetDriverUid="PASTE_ANY_DRIVER_UID_HERE_FOR_TESTING"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDriverVerifyDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fetchDriverDetails(targetDriverUid)


    }

    private fun fetchDriverDetails(uid: String) {
        db.collection("driver")
            .document(uid).get()
            .addOnSuccessListener { document ->
                if (document!=null && document.exists()){
                    val name= document.getString("name")?: "No name"
                    val carDetail=document.getString("carDetails")?:"No detail"
                    val paymentPhone=document.getString("Walletnumber")?:"no number"

                    binding.tvName.text=name
                    binding.tvVehicle.text=carDetail
                    binding.tvPayment.text=paymentPhone

                    val profilePicUri=document.getString("ProfilePic")?:""
                    val NicImage=document.getString("Nic")?:""
                    val lincesImage=document.getString("linces")?:""

                    if (profilePicUri.isEmpty()){
                        Glide.with(this).load(profilePicUri).into(binding.imgDriverProfile)
                    }
                    if (NicImage.isEmpty()){
                        Glide.with(this).load((NicImage)).into(binding.imgIdentityDoc)
                    }
                    if (lincesImage.isEmpty()){
                        Glide.with(this).load(lincesImage).into(binding.imgLicense)
                    }
                    else{
                        Toast.makeText(this,"Data nahi hai", Toast.LENGTH_SHORT).show()

                    }

                }


            }
            .addOnFailureListener {  exception ->
                Toast.makeText(this,"Error: ${exception.message}", Toast.LENGTH_SHORT).show()

            }


        binding.btnVerify.setOnClickListener{
            binding.btnVerify.isEnabled=false
            binding.btnVerify.text="Approved"

            approvedDriverStatus(targetDriverUid)

        }

        binding.btnReject.setOnClickListener{
            binding.btnVerify.isEnabled=false
            binding.btnVerify.text="Reject"

            rejectDriverStatus(targetDriverUid)

        }

    }


private fun DriververifyActivity.approvedDriverStatus(uid: String) {
    db.collection("driver_registrations").document(uid)
        .update("isApproved", true)
        .addOnSuccessListener {
            Toast.makeText(this, "Driver Approved Successfully! 🎉", Toast.LENGTH_LONG).show()

            binding.tvStatusLabel.text = "Approved"
            binding.tvStatusLabel.setTextColor(resources.getColor(android.R.color.primary_text_dark))

            binding.btnVerify.text = "Verified"
        }
        .addOnFailureListener { exception ->
            binding.btnVerify.isEnabled = true
            binding.btnVerify.text = "Verify Driver"
            Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
}

    private fun rejectDriverStatus(uid: String) {
        // Hum security ke liye document delete karne ke bajaye usme "isApproved" ko false hi rakhte hain
        // Aur ek nayi field "isRejected" to true daal dete hain taake baad me track ho sake
        db.collection("driver_registrations").document(uid)
            .update("isRejected", true)
            .addOnSuccessListener {
                Toast.makeText(this, "Driver request rejected!", Toast.LENGTH_SHORT).show()

                binding.tvStatusLabel.text = "REJECTED"
                binding.btnReject.text = "Rejected"
            }
            .addOnFailureListener { exception ->
                binding.btnReject.isEnabled = true
                binding.btnReject.text = "Reject"
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

}

