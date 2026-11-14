package com.findmycar.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmycar.data.model.ParkingPoi
import com.findmycar.data.repository.ParkingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MapUiState(
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val userLatitude: Double = 40.416775, // Madrid (Coordenada por defecto)
    val userLongitude: Double = -3.703790,
    val parkingPois: List<ParkingPoi> = emptyList()
)

class MapViewModel(
    private val repository: ParkingRepository = ParkingRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        loadParkingPois()
    }

    private fun loadParkingPois() {
        viewModelScope.launch {
            repository.getParkingPois().collect { pois ->
                _uiState.update { it.copy(parkingPois = pois) }
            }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _uiState.update { it.copy(searchQuery = newQuery) }
    }
}
