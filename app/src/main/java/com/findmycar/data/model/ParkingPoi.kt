package com.findmycar.data.model

data class ParkingPoi(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val availablePlaces: Int = 0,
    val isFree: Boolean = true
)
