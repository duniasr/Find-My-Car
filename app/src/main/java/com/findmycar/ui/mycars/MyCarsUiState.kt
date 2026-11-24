package com.findmycar.ui.mycars

import com.findmycar.data.local.ParkedCarEntity

/**
 * Representa el estado inmutable de la pantalla de Mis Coches (Funcionalidad 3).
 * Sigue el patrón Unidirectional Data Flow (UDF) dentro de la arquitectura MVVM.
 *
 * @param isLoading Indica si los datos iniciales se están cargando desde la base de datos.
 * @param parkedCars Lista de vehículos actualmente aparcados.
 * @param carToDelete Vehículo pendiente de confirmación en el diálogo de liberación de plaza.
 * @param userMessage Mensaje informativo temporal (e.g., feedback tras eliminar un registro).
 */
data class MyCarsUiState(
    val isLoading: Boolean = true,
    val parkedCars: List<ParkedCarEntity> = emptyList(),
    val carToDelete: ParkedCarEntity? = null,
    val userMessage: String? = null
)
