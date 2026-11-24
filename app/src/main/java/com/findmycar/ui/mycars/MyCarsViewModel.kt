package com.findmycar.ui.mycars

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.findmycar.data.local.AppDatabase
import com.findmycar.data.local.ParkedCarEntity
import com.findmycar.data.repository.ParkCarRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel para la gestión de la pantalla de estado de coches aparcados y regreso (Funcionalidad 3).
 * Sigue la arquitectura MVVM, exponiendo un StateFlow reactivo hacia la UI y coordinando las operaciones
 * con la capa de datos (Repository).
 */
class MyCarsViewModel(
    private val repository: ParkCarRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyCarsUiState())
    val uiState: StateFlow<MyCarsUiState> = _uiState.asStateFlow()

    init {
        observeParkedCars()
    }

    /**
     * Se suscribe reactivamente a la lista de coches aparcados desde la base de datos Room.
     * Cualquier inserción o borrado emitirá una nueva lista y actualizará el estado de la UI.
     */
    private fun observeParkedCars() {
        viewModelScope.launch {
            repository.getAllParkedCars()
                .catch {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            userMessage = "Error al consultar los vehículos aparcados"
                        )
                    }
                }
                .collect { cars ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            parkedCars = cars
                        )
                    }
                }
        }
    }

    /**
     * Inicia el diálogo de confirmación para liberar la plaza del vehículo.
     */
    fun onDeleteCarRequested(car: ParkedCarEntity) {
        _uiState.update { it.copy(carToDelete = car) }
    }

    /**
     * Cierra el diálogo de confirmación sin realizar ninguna acción.
     */
    fun onDismissDeleteDialog() {
        _uiState.update { it.copy(carToDelete = null) }
    }

    /**
     * Confirma la liberación de la plaza y elimina el vehículo de la base de datos persistente.
     */
    fun onConfirmDeleteCar() {
        val car = _uiState.value.carToDelete ?: return
        viewModelScope.launch {
            try {
                repository.deleteParkedCar(car.id)
                _uiState.update {
                    it.copy(
                        carToDelete = null,
                        userMessage = "Plaza de \"${car.carName}\" liberada correctamente"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        carToDelete = null,
                        userMessage = "No se pudo liberar la plaza. Inténtalo de nuevo."
                    )
                }
            }
        }
    }

    /**
     * Limpia el mensaje informativo tras haber sido procesado o mostrado al usuario.
     */
    fun onUserMessageShown() {
        _uiState.update { it.copy(userMessage = null) }
    }

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val db = AppDatabase.getInstance(context)
                val repository = ParkCarRepository(db.parkedCarDao())
                return MyCarsViewModel(repository) as T
            }
        }
    }
}
