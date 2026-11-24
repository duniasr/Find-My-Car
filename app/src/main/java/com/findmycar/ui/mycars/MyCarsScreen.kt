package com.findmycar.ui.mycars

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.findmycar.data.local.ParkedCarEntity
import com.findmycar.ui.components.StatusCardComponent
import com.findmycar.ui.theme.FindMyCarTheme
import com.findmycar.utils.DateTimeUtils
import com.findmycar.utils.NavigationUtils

/**
 * Pantalla principal de la Funcionalidad 3: Estado de vehículos aparcados y regreso.
 * Sigue el patrón MVVM + Clean Architecture y Jetpack Compose Material 3.
 */
@Composable
fun MyCarsScreen(
    viewModel: MyCarsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Feedback al usuario (mensajes informativos como plaza liberada)
    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.onUserMessageShown()
        }
    }

    MyCarsContent(
        uiState = uiState,
        onNavigateToCar = { car ->
            NavigationUtils.navigateToCoordinates(
                context = context,
                latitude = car.latitude,
                longitude = car.longitude,
                label = car.carName
            )
        },
        onDeleteCarClick = { car ->
            viewModel.onDeleteCarRequested(car)
        },
        modifier = modifier
    )

    // Diálogo de confirmación para liberar la plaza
    uiState.carToDelete?.let { car ->
        DeleteCarConfirmationDialog(
            carName = car.carName,
            onConfirm = { viewModel.onConfirmDeleteCar() },
            onDismiss = { viewModel.onDismissDeleteDialog() }
        )
    }
}

/**
 * Contenido desacoplado del estado para facilitar tests y Compose Previews.
 */
@Composable
fun MyCarsContent(
    uiState: MyCarsUiState,
    onNavigateToCar: (ParkedCarEntity) -> Unit,
    onDeleteCarClick: (ParkedCarEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Encabezado de la pantalla
        Text(
            text = "Mis coches aparcados",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Componente de estado informativo
        val statusMessage = when {
            uiState.isLoading -> "Actualizando el estado de tus vehículos..."
            uiState.parkedCars.isEmpty() -> "No tienes ningún vehículo aparcado actualmente."
            uiState.parkedCars.size == 1 -> "Tienes 1 vehículo aparcado. Puedes consultar su ubicación y navegar hacia él."
            else -> "Tienes ${uiState.parkedCars.size} vehículos aparcados actualmente."
        }
        StatusCardComponent(message = statusMessage)

        Spacer(modifier = Modifier.height(16.dp))

        // Cuerpo condicional según el estado
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.parkedCars.isEmpty() -> {
                EmptyParkedCarsView(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        items = uiState.parkedCars,
                        key = { car -> car.id }
                    ) { car ->
                        ParkedCarCard(
                            car = car,
                            onNavigateClick = { onNavigateToCar(car) },
                            onDeleteClick = { onDeleteCarClick(car) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

/**
 * Tarjeta individual que muestra el estado y detalles de un coche aparcado.
 */
@Composable
fun ParkedCarCard(
    car: ParkedCarEntity,
    onNavigateClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Fila superior: Nombre del coche y chip de estado relativo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = car.carName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = DateTimeUtils.getRelativeTimeSpan(car.timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Información de fecha y hora exacta
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = DateTimeUtils.formatDateTime(car.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Información de coordenadas GPS
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Lat: ${"%.5f".format(car.latitude)}, Lng: ${"%.5f".format(car.longitude)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Imagen de la plaza de aparcamiento si está disponible
            if (!car.photoUri.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                AsyncImage(
                    model = car.photoUri,
                    contentDescription = "Foto de la plaza de aparcamiento para ${car.carName}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            // Notas o referencias adicionales si existen
            if (car.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Notes,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = car.notes,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(12.dp))

            // Botones de acción: Navegar de regreso y Liberar plaza
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onNavigateClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Directions,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Cómo llegar")
                }

                OutlinedButton(
                    onClick = onDeleteClick,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Liberar")
                }
            }
        }
    }
}

/**
 * Vista mostrada cuando no hay coches registrados actualmente en la base de datos.
 */
@Composable
fun EmptyParkedCarsView(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.DirectionsCar,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Sin coches aparcados",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Cuando registres la ubicación de tu vehículo desde la pestaña de aparcamiento, aparecerá aquí junto con la ruta de regreso.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Diálogo de confirmación para liberar la plaza del vehículo.
 */
@Composable
fun DeleteCarConfirmationDialog(
    carName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Liberar plaza")
        },
        text = {
            Text(text = "¿Estás seguro de que deseas marcar \"$carName\" como retirado y liberar la plaza?")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "Liberar", color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancelar")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun MyCarsScreenEmptyPreview() {
    FindMyCarTheme {
        MyCarsContent(
            uiState = MyCarsUiState(isLoading = false, parkedCars = emptyList()),
            onNavigateToCar = {},
            onDeleteCarClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MyCarsScreenWithDataPreview() {
    FindMyCarTheme {
        val sampleCars = listOf(
            ParkedCarEntity(
                id = 1,
                carName = "Mi Seat Ibiza",
                latitude = 40.416775,
                longitude = -3.703790,
                photoUri = null,
                notes = "Plaza 34B cerca del ascensor azul en la planta -2",
                timestamp = System.currentTimeMillis() - 45 * 60 * 1000
            ),
            ParkedCarEntity(
                id = 2,
                carName = "Coche de empresa",
                latitude = 40.418000,
                longitude = -3.705000,
                photoUri = null,
                notes = "Aparcado en zona verde",
                timestamp = System.currentTimeMillis() - 180 * 60 * 1000
            )
        )
        MyCarsContent(
            uiState = MyCarsUiState(isLoading = false, parkedCars = sampleCars),
            onNavigateToCar = {},
            onDeleteCarClick = {}
        )
    }
}
