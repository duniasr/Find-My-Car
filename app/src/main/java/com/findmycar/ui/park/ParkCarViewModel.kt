package com.findmycar.ui.park

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.findmycar.data.local.AppDatabase
import com.findmycar.data.repository.ParkCarRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ParkCarUiState(
    val carName: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val photoUri: String? = null,
    val notes: String = "",
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)

class ParkCarViewModel(private val repository: ParkCarRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ParkCarUiState())
    val uiState: StateFlow<ParkCarUiState> = _uiState.asStateFlow()

    fun updateCarName(name: String) {
        _uiState.update { it.copy(carName = name) }
    }

    fun updateLocation(lat: Double, lng: Double) {
        _uiState.update { it.copy(latitude = lat, longitude = lng) }
    }

    fun updatePhotoUri(uri: String?) {
        _uiState.update { it.copy(photoUri = uri) }
    }

    fun updateNotes(notes: String) {
        _uiState.update { it.copy(notes = notes) }
    }

    fun saveParkedCar() {
        val currentState = _uiState.value

        if (currentState.carName.isBlank() || currentState.latitude == null || currentState.longitude == null) {
            return
        }

        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            repository.saveParkedCar(
                carName = currentState.carName,
                latitude = currentState.latitude,
                longitude = currentState.longitude,
                photoUri = currentState.photoUri,
                notes = currentState.notes
            )

            _uiState.update {
                it.copy(
                    isSaving = false,
                    saveSuccess = true,
                    carName = "",
                    photoUri = null,
                    notes = ""
                )
            }
        }
    }

    fun resetSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val db = AppDatabase.getInstance(context)
                val repository = ParkCarRepository(db.parkedCarDao())
                return ParkCarViewModel(repository) as T
            }
        }
    }
}
