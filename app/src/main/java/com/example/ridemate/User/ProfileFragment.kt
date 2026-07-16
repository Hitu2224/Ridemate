package com.example.ridemate.User

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
import com.example.ridemate.User.Report_Activity
import com.example.ridemate.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    // Expansion states
    private var isNameExpanded = false
    private var isRollExpanded = false
    private var isDeptExpanded = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }


    private fun setupClickListeners() {
        with(binding) {
            // 1. Name Expand
            cardName.setOnClickListener {
                isNameExpanded = !isNameExpanded
                toggleCard(tvNameExpanded, ivNameArrow, isNameExpanded)
            }

            // 2. Roll Expand
            cardRoll.setOnClickListener {
                isRollExpanded = !isRollExpanded
                toggleCard(tvRollExpanded, ivRollArrow, isRollExpanded)
            }

            // 3. Department Expand
            dpt.setOnClickListener {
                isDeptExpanded = !isDeptExpanded
                toggleCard(tvDept, ivDptArrow, isDeptExpanded)
            }

            // About Us
            cardAboutDevs.setOnClickListener {
                startActivity(Intent(requireContext(), DeveloperActivity::class.java))
            }

            // Logout
            btnLogout.setOnClickListener {
                val intent = Intent(requireActivity(), SelectUserTypeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
            binding.cardReportDriver.setOnClickListener {
                val intent= Intent(requireActivity(), Report_Activity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }

    private fun toggleCard(view: View, arrow: ImageView, expand: Boolean) {
        view.visibility = if (expand) View.VISIBLE else View.GONE

        // Arrow rotation animation
        val from = if (expand) 0f else 180f
        val to = if (expand) 180f else 0f

        RotateAnimation(
            from,
            to,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
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