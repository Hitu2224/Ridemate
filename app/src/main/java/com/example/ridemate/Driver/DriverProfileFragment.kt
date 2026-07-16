package com.example.ridemate.Driver

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.ridemate.User.DeveloperActivity
import com.example.ridemate.User.SelectUserTypeActivity
import com.example.ridemate.databinding.FragmentDriverProfileBinding

class DriverProfileFragment : Fragment() {

    private var _binding: FragmentDriverProfileBinding? = null
    private val binding get() = _binding!!

    // Expansion states
    private var isNameExpanded = false
    private var isPhoneExpanded = false
    private var isCarExpanded = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDriverProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        with(binding) {

            // 1. Full Name Card
            cardName.setOnClickListener {
                isNameExpanded = !isNameExpanded
                // XML ID: tv_name_expanded & iv_name_arrow
                toggleCard(tvNameExpanded, ivNameArrow, isNameExpanded)
            }

            // 2. Phone Number Card
            cardPhone.setOnClickListener {
                isPhoneExpanded = !isPhoneExpanded
                // XML ID: tv_roll_expanded & iv_phone_arrow
                toggleCard(tvRollExpanded, ivPhoneArrow, isPhoneExpanded)
            }

            // 3. Car Detail Card
            car.setOnClickListener {
                isCarExpanded = !isCarExpanded
                // XML ID: tv_dept & iv_car_arrow
                toggleCard(tvDept, ivCarArrow, isCarExpanded)
            }

            // About Project Developers
            cardAboutDevs.setOnClickListener {
                startActivity(Intent(requireContext(), DeveloperActivity::class.java))
            }

            // Logout Account
            btnLogout.setOnClickListener {
                val intent = Intent(requireActivity(), SelectUserTypeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }

    private fun toggleCard(view: View, arrow: ImageView, expand: Boolean) {
        // Expand/Collapse visibility
        view.visibility = if (expand) View.VISIBLE else View.GONE

        // Arrow rotation
        val toDegree = if (expand) 180f else 0f

        RotateAnimation(
            0f, toDegree,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 300
            fillAfter = true
            arrow.startAnimation(this)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}