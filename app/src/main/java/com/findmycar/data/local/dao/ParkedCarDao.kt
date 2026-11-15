package com.findmycar.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.findmycar.data.local.ParkedCarEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ParkedCarDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParkedCar(parkedCar: ParkedCarEntity)

    @Query("SELECT * FROM parked_cars ORDER BY timestamp DESC")
    fun getAllParkedCars(): Flow<List<ParkedCarEntity>>

    @Query("DELETE FROM parked_cars WHERE id = :id")
    suspend fun deleteParkedCar(id: Int)
}
