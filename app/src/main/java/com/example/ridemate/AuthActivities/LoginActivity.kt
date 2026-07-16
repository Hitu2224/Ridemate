package com.example.ridemate.AuthActivities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import android.view.KeyEvent
import android.widget.EditText
import com.example.ridemate.DriverData
import com.example.ridemate.R
import com.example.ridemate.User.StudentVerify
import com.example.ridemate.databinding.ActivityLoginBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var userType: String? = null

    // Firebase instances
    private lateinit var auth: FirebaseAuth
    private var verificationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()

        // SelectUserTypeActivity se jo string putExtra karke bheji thi, usko get kiya
        userType = intent.getStringExtra("user_type")

        // 6-digit auto advance aur backspace tracking setup
        setupOtpAutoAdvance()

        // MAIN ACTION BUTTON CLICK
        binding.btnMainAction.setOnClickListener {
            val buttonText = binding.btnMainAction.text.toString()

            if (buttonText == "Request code") {
                val inputNumber = binding.etPhoneNumber.text.toString().trim()

                if (inputNumber.isNotEmpty() && inputNumber.length >= 9) {
                    val fullPhoneNumber = "+92$inputNumber"

                    binding.btnMainAction.isEnabled = false
                    binding.btnMainAction.text = "Sending SMS..."

                    sendVerificationCode(fullPhoneNumber)
                } else {
                    Toast.makeText(this, "Meharbani karke sahi number likhein", Toast.LENGTH_SHORT).show()
                }

            } else if (buttonText == "Verify code") {
                // All 6 digits combination string build up
                val otpCode = binding.otpDigit1.text.toString().trim() +
                        binding.otpDigit2.text.toString().trim() +
                        binding.otpDigit3.text.toString().trim() +
                        binding.otpDigit4.text.toString().trim() +
                        binding.otpDigit5.text.toString().trim() +
                        binding.otpDigit6.text.toString().trim()

                if (otpCode.length == 6 && verificationId != null) {
                    binding.btnMainAction.isEnabled = false
                    binding.btnMainAction.text = "Verifying..."

                    val credential = PhoneAuthProvider.getCredential(verificationId!!, otpCode)
                    signInWithPhoneAuthCredential(credential)
                } else {
                    Toast.makeText(this, "Poora 6-digit verification code likhein", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // ---- A. Firebase SMS Requester ----
    private fun sendVerificationCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // ---- B. Firebase Callbacks ----
    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            binding.btnMainAction.isEnabled = true
            binding.btnMainAction.text = "Request code"
            Toast.makeText(this@LoginActivity, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
        }

        override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(id, token)
            verificationId = id

            binding.btnMainAction.isEnabled = true
            binding.llPhoneInput.visibility = View.GONE
            binding.llOtpInput.visibility = View.VISIBLE
            binding.btnMainAction.text = "Verify code"

            Toast.makeText(this@LoginActivity, "OTP sent successfully! 📩", Toast.LENGTH_SHORT).show()
        }
    }

    // ---- C. Final Sign-In ----
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Logged In Successfully! 🎉", Toast.LENGTH_SHORT).show()
                    openNextScreen()
                } else {
                    binding.btnMainAction.isEnabled = true
                    binding.btnMainAction.text = "Verify code"
                    Toast.makeText(this, "Invalid verification code!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // ---- 🗺️ D. DYNAMIC ROUTING RULES ----
    private fun openNextScreen() {
        if (userType == "driver") {
            // Driver ko details form screen par bhejein
            startActivity(Intent(this, DriverData::class.java))
        } else if (userType == "passenger") {
            // Student/Passenger ko Student Verification form par bhejein
            startActivity(Intent(this, StudentVerify::class.java))
        } else {
            // Agar piche se intent loose ho jaye, to default safety user profile standard route
            startActivity(Intent(this, StudentVerify::class.java))
        }
        finish()
    }

    // ---- ✅ FIX: Auto Next Box Focus AND Backspace Return Setup ----
    private fun setupOtpAutoAdvance() {
        // Forward movement tracking
        binding.otpDigit1.addTextChangedListener { if (it?.length == 1) binding.otpDigit2.requestFocus() }
        binding.otpDigit2.addTextChangedListener { if (it?.length == 1) binding.otpDigit3.requestFocus() }
        binding.otpDigit3.addTextChangedListener { if (it?.length == 1) binding.otpDigit4.requestFocus() }
        binding.otpDigit4.addTextChangedListener { if (it?.length == 1) binding.otpDigit5.requestFocus() }
        binding.otpDigit5.addTextChangedListener { if (it?.length == 1) binding.otpDigit6.requestFocus() }

        // Backward backspace movement handling
        setDeleteKeyListener(binding.otpDigit2, binding.otpDigit1)
        setDeleteKeyListener(binding.otpDigit3, binding.otpDigit2)
        setDeleteKeyListener(binding.otpDigit4, binding.otpDigit3)
        setDeleteKeyListener(binding.otpDigit5, binding.otpDigit4)
        setDeleteKeyListener(binding.otpDigit6, binding.otpDigit5)
    }

    private fun setDeleteKeyListener(currentBox: EditText, previousBox: EditText) {
        currentBox.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN && currentBox.text.isEmpty()) {
                previousBox.requestFocus()
                previousBox.setText("") // Purana character empty karein taake user naya likh sake
                true
            } else {
                false
            }
        }
    }
}