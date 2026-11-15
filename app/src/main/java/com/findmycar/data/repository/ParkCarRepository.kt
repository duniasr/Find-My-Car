package com.findmycar.data.repository

import com.findmycar.data.local.ParkedCarEntity
import com.findmycar.data.local.dao.ParkedCarDao
import kotlinx.coroutines.flow.Flow

class ParkCarRepository(private val parkedCarDao: ParkedCarDao) {

    suspend fun saveParkedCar(carName: String, latitude: Double, longitude: Double, photoUri: String?, notes: String) {
        val parkedCar = ParkedCarEntity(
            carName = carName,
            latitude = latitude,
            longitude = longitude,
            photoUri = photoUri,
            notes = notes,
            timestamp = System.currentTimeMillis()
        )
        parkedCarDao.insertParkedCar(parkedCar)
    }

    fun getAllParkedCars(): Flow<List<ParkedCarEntity>> {
        return parkedCarDao.getAllParkedCars()
    }

    suspend fun deleteParkedCar(id: Int) {
        parkedCarDao.deleteParkedCar(id)
    }
}
