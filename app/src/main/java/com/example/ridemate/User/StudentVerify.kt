package com.example.ridemate.User

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.ridemate.Fragments.HomeActivity
import com.example.ridemate.databinding.ActivityStudentVerifyBinding
import com.example.ridemate.R // ✅ Aapka apna project R import kiya
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class StudentVerify : AppCompatActivity() {

    private lateinit var binding: ActivityStudentVerifyBinding
    private var selectedImageType = ""

    // Firebase Tools
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance().reference
    private val firestore = FirebaseFirestore.getInstance()

    private var profileUri: Uri? = null
    private var idCardUri: Uri? = null

    companion object {
        const val PROFILE = "profile"
        const val ID_CARD = "id_card"
    }

    private val getImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                when (selectedImageType) {
                    PROFILE -> {
                        profileUri = it
                        binding.imgStudentProfile.setImageURI(it)
                    }
                    ID_CARD -> {
                        idCardUri = it
                        binding.imgStudentID.visibility = View.VISIBLE
                        binding.layoutUploadUI.visibility = View.GONE
                        binding.imgStudentID.setImageURI(it)
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityStudentVerifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.root.alpha = 0f
        binding.root.animate().alpha(1f).setDuration(900).start()

        binding.imgStudentProfile.setOnClickListener {
            selectedImageType = PROFILE
            getImage.launch("image/*")
        }

        binding.btnUploadStudentID.setOnClickListener {
            selectedImageType = ID_CARD
            getImage.launch("image/*")
        }

        binding.btnSubmitStudent.setOnClickListener {
            validateAndUploadStudentData()
        }
    }

    private fun validateAndUploadStudentData() {
        val fullName = binding.etFullName.text.toString().trim()
        val rollNo = binding.etRollNo.text.toString().trim()
        val department = binding.etDepartment.text.toString().trim()
        val currentUserId = auth.currentUser?.uid

        if (currentUserId == null) {
            Toast.makeText(this, "Pehle account login karein!", Toast.LENGTH_SHORT).show()
            return
        }
        if (fullName.isEmpty() || rollNo.isEmpty() || department.isEmpty()) {
            Toast.makeText(this, "Tamam fields fill karein", Toast.LENGTH_SHORT).show()
            return
        }
        if (profileUri == null || idCardUri == null) {
            Toast.makeText(this, "Pics select karna lazmi hain", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnSubmitStudent.isEnabled = false
        binding.btnSubmitStudent.text = "Uploading..."

        lifecycleScope.launch {
            try {
                // 1. Storage Uploads
                val profileRef = storage.child("students/$currentUserId/profile.jpg")
                profileRef.putFile(profileUri!!).await()
                val profileUrl = profileRef.downloadUrl.await().toString()

                val idCardRef = storage.child("students/$currentUserId/id_card.jpg")
                idCardRef.putFile(idCardUri!!).await()
                val idCardUrl = idCardRef.downloadUrl.await().toString()

                // 2. Data Structure Map
                val studentData = hashMapOf(
                    "uid" to currentUserId,
                    "fullName" to fullName,
                    "rollNo" to rollNo,
                    "department" to department,
                    "profileImageUrl" to profileUrl,
                    "idCardImageUrl" to idCardUrl,
                    "isStudentVerified" to false
                )

                // 3. Firestore Collection Write
                firestore.collection("student_data")
                    .document(currentUserId)
                    .set(studentData)
                    .await()

                Toast.makeText(this@StudentVerify, "Data Saved Successfully! 🎉", Toast.LENGTH_SHORT).show()

                // 4. Safe Screen Transition
                val intent = Intent(this@StudentVerify, HomeActivity::class.java)
                startActivity(intent)
                // Default system anim use ki taake canvas crash na ho
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()

            } catch (e: Exception) {
                binding.btnSubmitStudent.isEnabled = true
                binding.btnSubmitStudent.text = "Complete Registration"
                Toast.makeText(this@StudentVerify, "Upload Failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}