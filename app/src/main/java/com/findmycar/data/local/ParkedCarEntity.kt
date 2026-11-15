package com.findmycar.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "parked_cars")
data class ParkedCarEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val carName: String,
    val latitude: Double,
    val longitude: Double,
    val photoUri: String?,
    val notes: String,
    val timestamp: Long
)
