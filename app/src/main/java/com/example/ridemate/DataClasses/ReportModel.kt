package com.example.ridemate.DataClasses

data class ReportModel (
    var id: String = "",
    val driverInfo: String = "",
    val reason: String = "",
    val description: String = "",
    val timestamp: Long = 0,
    val status: String = ""
) : java.io.Serializable