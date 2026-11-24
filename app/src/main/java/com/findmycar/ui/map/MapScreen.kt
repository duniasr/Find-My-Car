package com.findmycar.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.findmycar.ui.components.SearchBarComponent
import com.findmycar.utils.LocationUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var hasLocationPermission by remember {
        mutableStateOf(
            (ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
        )
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(uiState.userLatitude ?: 40.416775, uiState.userLongitude ?: -3.703790),
            16f
        )
    }

    fun fetchRealLocationAndCenter() {
        if (hasLocationPermission) {
            LocationUtils.getCurrentLocation(
                context = context,
                onLocationReceived = { location ->
                    viewModel.updateUserLocation(location.latitude, location.longitude)
                    val latLng = LatLng(location.latitude, location.longitude)
                    coroutineScope.launch {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(latLng, 17f)
                        )
                    }
                },
                onError = {
                    Toast.makeText(context, "Buscando señal GPS...", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineGranted || coarseGranted) {
            hasLocationPermission = true
            fetchRealLocationAndCenter()
        } else {
            Toast.makeText(context, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        if (hasLocationPermission) {
            fetchRealLocationAndCenter()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // --- 1. MAPA OFICIAL DE GOOGLE MAPS ---
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = hasLocationPermission
            ),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = false,
                zoomControlsEnabled = false
            )
        )

        // --- 2. BARRA DE BÚSQUEDA SUPERIOR ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter)
        ) {
            SearchBarComponent(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.onSearchQueryChange(it) }
            )
        }

        // --- 3. BOTÓN FLOTANTE PARA RECENTRAR UBICACIÓN GPS REAL ---
        FloatingActionButton(
            onClick = {
                if (hasLocationPermission) {
                    fetchRealLocationAndCenter()
                } else {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp, end = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "Ir a mi ubicación GPS"
            )
        }
    }
}
