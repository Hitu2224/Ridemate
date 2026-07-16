package com.example.ridemate.User

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.ridemate.databinding.ActivityPaymentBinding

class PaymentActivity : AppCompatActivity() {

    private var selectMethod: String = ""
    private var currentPackage: String = ""
    private var imageUri: Uri? = null
    private lateinit var binding: ActivityPaymentBinding

    private val getImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it
            binding.ivReceiptPreview.visibility = View.VISIBLE
            binding.ivReceiptPreview.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)



        // JazzCash Logic
        binding.cardJazzCash.setOnClickListener {
            updateUI("JazzCash", "Send to 0300-1111111 (Admin)", "com.techlogix.mobilinkcustomer")
        }

        // EasyPaisa Logic
        binding.cardEasyPaisa.setOnClickListener {
            updateUI("EasyPaisa", "Send to 0300-2222222 (Admin)", "pk.com.telenor.phoenix")
        }

        // Bank Logic
        binding.cardBank.setOnClickListener {
            updateUI("Bank Transfer", "HBL: 1234-5678-9012 (Title: RideMate)", null)
        }

        // Cash Logic
        binding.cardCash.setOnClickListener {
            selectMethod = "Cash"
            binding.detailInputLayout.visibility = View.GONE
            Toast.makeText(this, "Cash on Ride Selected", Toast.LENGTH_SHORT).show()
        }

        binding.btnOpenApp.setOnClickListener {
            val intent = packageManager.getLaunchIntentForPackage(currentPackage)
            if (intent != null) {
                startActivity(intent)
            } else {
                // Agar app nahi hai to direct playstore par bhej do
                val playStoreIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$currentPackage"))
                startActivity(playStoreIntent)
            }
        }

        binding.btnUploadReceipt.setOnClickListener {
            getImage.launch("image/*")
        }

        binding.btnFinalConfirm.setOnClickListener {
            val tid = binding.etTID.text.toString()

            if (selectMethod.isEmpty()) {
                Toast.makeText(this, "Please select a method", Toast.LENGTH_SHORT).show()
            } else if (selectMethod == "Cash") {
                Toast.makeText(this, "Booking Successful (Cash)", Toast.LENGTH_LONG).show()
                finish()
            } else if (tid.isEmpty() || imageUri == null) {
                Toast.makeText(this, "Please provide TID and Screenshot", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Payment Submitted for Verification!", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun updateUI(method: String, instruction: String, packageName: String?) {
        selectMethod = method
        binding.detailInputLayout.visibility = View.VISIBLE
        binding.tvInstruction.text = instruction

        if (packageName != null) {
            currentPackage = packageName
            binding.btnOpenApp.visibility = View.VISIBLE
            binding.btnOpenApp.text = "Open $method App"
        } else {
            binding.btnOpenApp.visibility = View.GONE
        }

        // Cards highlight reset (Elevation trick)
        Toast.makeText(this, "$method Selected", Toast.LENGTH_SHORT).show()
    }
}