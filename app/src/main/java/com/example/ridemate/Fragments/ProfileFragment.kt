package com.example.ridemate.Fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.ridemate.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    // Card expansion states
    private var isNameExpanded = false
    private var isEmailExpanded = false
    private var isPasswordExpanded = false
    private var isPaymentExpanded = false

    // SharedPreferences
    private val sharedPreferences: SharedPreferences by lazy {
        requireActivity().getSharedPreferences(PREF_NAME, AppCompatActivity.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        loadUserData()
        animateCardsEntrance()
    }

    private fun setupClickListeners() {
        with(binding) {
            // Card click listeners with lambda expressions
            cardName.setOnClickListener {
                toggleCard(
                    expandedLayout = layoutNameExpanded,
                    expandIcon = ivNameExpand,
                    isExpanded = isNameExpanded
                ) { isNameExpanded = !isNameExpanded }
            }

            cardEmail.setOnClickListener {
                toggleCard(
                    expandedLayout = layoutEmailExpanded,
                    expandIcon = ivEmailExpand,
                    isExpanded = isEmailExpanded
                ) { isEmailExpanded = !isEmailExpanded }
            }

            cardPassword.setOnClickListener {
                toggleCard(
                    expandedLayout = layoutPasswordExpanded,
                    expandIcon = ivPasswordExpand,
                    isExpanded= isPasswordExpanded
                ) { isPasswordExpanded = !isPasswordExpanded }
            }

            cardPayment.setOnClickListener {
                toggleCard(
                    expandedLayout = layoutPaymentExpanded,
                    expandIcon = ivPaymentExpand,
                    isExpanded = isPaymentExpanded
                ) { isPaymentExpanded = !isPaymentExpanded }
            }

            // Button click listeners
            btnSignout.setOnClickListener { showSignOutDialog() }
            btnChangePassword.setOnClickListener { handlePasswordChange() }


            // Profile avatar click for image change
            ivProfileAvatar.setOnClickListener { handleAvatarChange() }
        }
    }

    private fun loadUserData() {
        with(binding) {
            // Load user data from SharedPreferences or API
            tvUserName.text = sharedPreferences.getString(KEY_USER_NAME, "Hitesh Kumar")
            tvUniversity.text = sharedPreferences.getString(KEY_UNIVERSITY, "QUEST University Nawabshah")
            tvNameValue.text = sharedPreferences.getString(KEY_USER_NAME, "Hitesh Kumar")
            tvEmailValue.text = sharedPreferences.getString(KEY_EMAIL, "hitesh.kumar@quest.edu.pk")
            tvPaymentValue.text = sharedPreferences.getString(KEY_PAYMENT_METHOD, "JazzCash - 03XX XXXX XXX")
        }
    }

    private fun toggleCard(
        expandedLayout: LinearLayout,
        expandIcon: ImageView,
        isExpanded: Boolean,
        onToggle: () -> Unit
    ) {
        if (isExpanded) {
            collapseView(expandedLayout)
            rotateIcon(expandIcon, FROM_EXPANDED, TO_COLLAPSED)
        } else {
            expandView(expandedLayout)
            rotateIcon(expandIcon, FROM_COLLAPSED, TO_EXPANDED)
        }
        onToggle()
    }

    private fun expandView(view: LinearLayout) {
        with(view) {
            visibility = View.VISIBLE

            // Measure the view to get its target height
            measure(
                View.MeasureSpec.makeMeasureSpec((parent as View).width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )

            val targetHeight = measuredHeight
            layoutParams.height = 0

            // Create smooth expansion animation
            ValueAnimator.ofInt(0, targetHeight).apply {
                addUpdateListener { animator ->
                    layoutParams.height = animator.animatedValue as Int
                    requestLayout()
                }
                duration = ANIMATION_DURATION
                interpolator = DecelerateInterpolator()
                start()
            }
        }
    }

    private fun collapseView(view: LinearLayout) {
        val initialHeight = view.measuredHeight

        ValueAnimator.ofInt(initialHeight, 0).apply {
            addUpdateListener { animator ->
                view.layoutParams.height = animator.animatedValue as Int
                view.requestLayout()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.apply {
                        visibility = View.GONE
                        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    }
                }
            })
            duration = ANIMATION_DURATION
            interpolator = AccelerateInterpolator()
            start()
        }
    }

    private fun rotateIcon(icon: ImageView, fromDegree: Float, toDegree: Float) {
        RotateAnimation(
            fromDegree, toDegree,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = ANIMATION_DURATION
            fillAfter = true
            icon.startAnimation(this)
        }
    }

    private fun showSignOutDialog() {
        context?.let { ctx ->
            AlertDialog.Builder(ctx)
                .setTitle("Sign Out")
                .setMessage("Are you sure you want to sign out from your account?")
                .setPositiveButton("Sign Out") { _, _ -> performSignOut() }
                .setNegativeButton("Cancel", null)
                .setCancelable(true)
                .show()
        }
    }

    private fun performSignOut() {
        try {
            // Clear all user data
            with(sharedPreferences.edit()) {
                clear()
                apply()
            }

            // Show success message
            showToast("Signed out successfully")

            // Navigate to login (implement based on your navigation structure)
            navigateToLogin()

        } catch (e: Exception) {
            showToast("Sign out failed. Please try again.")
        }
    }

    private fun handlePasswordChange() {
        showToast("Redirecting to change password...")
        // TODO: Navigate to password change fragment/activity
        // findNavController().navigate(R.id.action_profile_to_change_password)
    }

    private fun handlePaymentUpdate() {
        showToast("Opening payment methods...")
        // TODO: Navigate to payment methods fragment/activity
        // findNavController().navigate(R.id.action_profile_to_payment_methods)
    }

    private fun handleAvatarChange() {
        showToast("Change profile picture feature coming soon!")
        // TODO: Implement image picker and upload functionality
    }

    private fun navigateToLogin() {
        // TODO: Implement navigation to login screen
        // Example for Navigation Component:
        // findNavController().navigate(R.id.action_profile_to_login)

        // Example for Activity navigation:
        // val intent = Intent(requireActivity(), LoginActivity::class.java)
        // intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        // startActivity(intent)
        // requireActivity().finish()
    }

    private fun animateCardsEntrance() {
        val animatableViews = with(binding) {
            listOf(
                cardProfileHeader,
                cardName,
                cardEmail,
                cardPassword,
                cardPayment,
                btnSignout
            )
        }

        animatableViews.forEachIndexed { index, view ->
            view.apply {
                alpha = 0f
                translationY = INITIAL_TRANSLATION_Y

                animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(ENTRANCE_ANIMATION_DURATION)
                    .setStartDelay(index * STAGGER_DELAY)
                    .setInterpolator(DecelerateInterpolator())
                    .start()
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        // Animation constants
        private const val ANIMATION_DURATION = 300L
        private const val ENTRANCE_ANIMATION_DURATION = 400L
        private const val STAGGER_DELAY = 100L
        private const val INITIAL_TRANSLATION_Y = 50f
        private const val FROM_COLLAPSED = 0f
        private const val TO_EXPANDED = 180f
        private const val FROM_EXPANDED = 180f
        private const val TO_COLLAPSED = 0f

        // SharedPreferences constants
        private const val PREF_NAME = "user_preferences"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_EMAIL = "user_email"
        private const val KEY_UNIVERSITY = "user_university"
        private const val KEY_PAYMENT_METHOD = "payment_method"

        @JvmStatic
        fun newInstance(): ProfileFragment = ProfileFragment()
    }
}