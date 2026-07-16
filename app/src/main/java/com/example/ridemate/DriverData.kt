package com.example.ridemate

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.ridemate.Admin.DriverUnderReviewActivity // Apne package ke mutabiq import check karlein
import com.example.ridemate.databinding.ActivityDriverDataBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DriverData : AppCompatActivity() {

    private lateinit var binding: ActivityDriverDataBinding
    private var currentDocumentSelection = "CNIC"

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storageRef = FirebaseStorage.getInstance().reference

    private var profileUri: Uri? = null
    private var cnicUri: Uri? = null
    private var licenseUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDriverDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.imgDriverProfile.setOnClickListener {
            pickProfileLauncher.launch("image/*")
        }

        binding.cardUploadDocument.setOnClickListener {
            pickDocumentLauncher.launch("image/*")
        }

        binding.btnSelectCNIC.setOnClickListener {
            currentDocumentSelection = "CNIC"
            binding.btnSelectCNIC.setBackgroundColor(resources.getColor(R.color.primary))
            binding.btnSelectCNIC.setTextColor(resources.getColor(R.color.white))
            binding.btnSelectLicense.setBackgroundColor(resources.getColor(android.R.color.transparent))
            binding.btnSelectLicense.setTextColor(resources.getColor(android.R.color.darker_gray))

            if (cnicUri != null) {
                binding.tvUploadLabel.text = "CNIC Attached! ✅"
                binding.imgDocPreview.setImageURI(cnicUri)
            } else {
                binding.tvUploadLabel.text = "Tap to upload CNIC Image"
                binding.imgDocPreview.setImageResource(R.drawable.avatar)
            }
        }

        binding.btnSelectLicense.setOnClickListener {
            currentDocumentSelection = "License"
            binding.btnSelectLicense.setBackgroundColor(resources.getColor(R.color.primary))
            binding.btnSelectLicense.setTextColor(resources.getColor(R.color.white))
            binding.btnSelectCNIC.setBackgroundColor(resources.getColor(android.R.color.transparent))
            binding.btnSelectCNIC.setTextColor(resources.getColor(android.R.color.darker_gray))

            if (licenseUri != null) {
                binding.tvUploadLabel.text = "License Attached! ✅"
                binding.imgDocPreview.setImageURI(licenseUri)
            } else {
                binding.tvUploadLabel.text = "Tap to upload Driving License Image"
                binding.imgDocPreview.setImageResource(R.drawable.avatar)
            }
        }

        binding.btnSubmitVerification.setOnClickListener {
            processAndUploadDriverData()
        }
    }

    private val pickProfileLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            val fileType = contentResolver.getType(uri)
            if (fileType != null && (fileType.contains("jpeg") || fileType.contains("png") || fileType.contains("jpg"))) {
                profileUri = uri
                binding.imgDriverProfile.setImageURI(uri)
                Toast.makeText(this, "Profile Pic Selected! ✅", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Sirf tasveer (JPG/PNG) select karein!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val pickDocumentLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            val fileType = contentResolver.getType(uri)
            if (fileType != null && (fileType.contains("jpeg") || fileType.contains("png") || fileType.contains("jpg"))) {
                if (currentDocumentSelection == "CNIC") {
                    cnicUri = uri
                    binding.tvUploadLabel.text = "CNIC Attached Successfully! ✅"
                    binding.imgDocPreview.setImageURI(uri)
                    Toast.makeText(this, "CNIC Document Saved! ✅", Toast.LENGTH_SHORT).show()
                } else {
                    licenseUri = uri
                    binding.tvUploadLabel.text = "License Attached Successfully! ✅"
                    binding.imgDocPreview.setImageURI(uri)
                    Toast.makeText(this, "License Document Saved! ✅", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Ghalat format! Meharbani karke sirf tasveer (JPG/PNG) upload karein.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun processAndUploadDriverData() {
        val name = binding.etDriverName.text.toString().trim()
        val walletNumber = binding.etDriverPaymentPhone.text.toString().trim()
        val carInfo = binding.etCarDetails.text.toString().trim()
        val driverUid = auth.currentUser?.uid

        if (driverUid == null) {
            Toast.makeText(this, "Error: User Session null!", Toast.LENGTH_SHORT).show()
            return
        }
        if (name.isEmpty() || walletNumber.isEmpty() || carInfo.isEmpty()) {
            Toast.makeText(this, "Saari fields fill karna lazmi hain!", Toast.LENGTH_SHORT).show()
            return
        }
        if (profileUri == null || cnicUri == null || licenseUri == null) {
            Toast.makeText(this, "Profile, CNIC aur License teeno upload karna lazmi hain!", Toast.LENGTH_LONG).show()
            return
        }

        binding.btnSubmitVerification.isEnabled = false
        binding.btnSubmitVerification.text = "Uploading, please wait..."

        lifecycleScope.launch {
            try {
                // 🔥 SPEED BOOST LOGIC: Teeno files parallel upload hongi ek sath!
                val profileUploadDeferred = async {
                    val ref = storageRef.child("drivers/$driverUid/profile.jpg")
                    ref.putFile(profileUri!!).await()
                    ref.downloadUrl.await().toString()
                }

                val cnicUploadDeferred = async {
                    val ref = storageRef.child("drivers/$driverUid/cnic.jpg")
                    ref.putFile(cnicUri!!).await()
                    ref.downloadUrl.await().toString()
                }

                val licenseUploadDeferred = async {
                    val ref = storageRef.child("drivers/$driverUid/license.jpg")
                    ref.putFile(licenseUri!!).await()
                    ref.downloadUrl.await().toString()
                }

                // Wait for all uploads to complete simultaneously
                val profileUrl = profileUploadDeferred.await()
                val cnicUrl = cnicUploadDeferred.await()
                val licenseUrl = licenseUploadDeferred.await()

                val driverPayload = hashMapOf(
                    "uid" to driverUid,
                    "name" to name,
                    "paymentWalletNumber" to walletNumber,
                    "carDetails" to carInfo,
                    "profilePic" to profileUrl,
                    "cnicPic" to cnicUrl,
                    "licensePic" to licenseUrl,
                    "isApproved" to false // Submit ke waqt isApproved = false set ho raha hai
                )

                firestore.collection("driver_registrations")
                    .document(driverUid)
                    .set(driverPayload)
                    .await()

                Toast.makeText(this@DriverData, "Details submitted for approval! 🎉", Toast.LENGTH_SHORT).show()

                // ✅ REDIRECT: Ab driver seedha Under Review screen par block ho jayega!
                val intent = Intent(this@DriverData, DriverUnderReviewActivity::class.java)
                startActivity(intent)
                finish()

            } catch (e: Exception) {
                binding.btnSubmitVerification.isEnabled = true
                binding.btnSubmitVerification.text = "Submit for Approval"
                Toast.makeText(this@DriverData, "Upload Failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}