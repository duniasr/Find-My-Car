package com.findmycar.data.repository

import com.findmycar.data.model.ParkingPoi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class ParkingRepository {

    /**
     * Retorna el flujo de puntos de interés de parkings.
     * (Actualmente retorna una lista vacía según la especificación inicial).
     */
    fun getParkingPois(): Flow<List<ParkingPoi>> {
        return flowOf(emptyList())
    }
}
