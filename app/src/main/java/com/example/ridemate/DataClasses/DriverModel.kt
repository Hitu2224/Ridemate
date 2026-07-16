package com.example.ridemate.DataClasses

class DriverModel (

    val id: String = "",
    val name: String = "",
    val phone: String = "",
    val rollNo: String = "",
    val department: String = "",
    val carModel: String = "",
    val carPlate: String = "",
    val cnicUrl: String = "",
    val profilePicUrl: String = "",
    val licenseUrl: String = "", // Admin ko dikhane ke liye image link
    val status: String = "Pending",
    val isApproved: Boolean = false
)