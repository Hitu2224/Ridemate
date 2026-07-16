
package com.example.ridemate.utils // Apne package ka naam check karlein

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.model.KeyPath
import com.example.ridemate.R

class LoadingDialog(context: Context) {
    private val dialog: Dialog = Dialog(context)

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_loading_dialog, null)
        dialog.setContentView(view)
        dialog.setCancelable(false) // User bahar click karke band na kar sake
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val lottieView = view.findViewById<LottieAnimationView>(R.id.Loader)

        // Green Color Set Karein
        lottieView.addValueCallback(
            KeyPath("**"),
            LottieProperty.COLOR_FILTER
        ) { PorterDuffColorFilter(Color.parseColor("#9ACD32"), PorterDuff.Mode.SRC_ATOP) }
    }

    fun show() {
        if (!dialog.isShowing) dialog.show()
    }

    fun dismiss() {
        if (dialog.isShowing) dialog.dismiss()
    }
}