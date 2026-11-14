package com.findmycar.data.local

data class ParkingEntity(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val availablePlaces: Int,
    val isFree: Boolean
)
