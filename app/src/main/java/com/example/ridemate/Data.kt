package com.example.ridemate

class Data(
    var id: String = "",
    val from: String = "",
    val to: String = "",
    val date:String="",
    val timestamp: Long = System.currentTimeMillis(),
    val seats: Int = 1,
    val model: String = "",
    val price: Int= 0

)