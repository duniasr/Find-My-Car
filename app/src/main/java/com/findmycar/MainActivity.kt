package com.findmycar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.lifecycle.viewmodel.compose.viewModel
import com.findmycar.ui.map.MapScreen
import com.findmycar.ui.mycars.MyCarsScreen
import com.findmycar.ui.mycars.MyCarsViewModel
import com.findmycar.ui.park.ParkCarScreen
import com.findmycar.ui.park.ParkCarViewModel
import com.findmycar.ui.theme.FindMyCarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FindMyCarTheme {
                FindMyCarApp()
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun FindMyCarApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.MAP) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                AppDestinations.entries.forEach { destination ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painterResource(destination.icon),
                                contentDescription = destination.label
                            )
                        },
                        label = { Text(destination.label) },
                        selected = destination == currentDestination,
                        onClick = { currentDestination = destination }
                    )
                }
            }
        }
    ) { innerPadding ->
        when (currentDestination) {
            AppDestinations.MAP -> MapScreen(modifier = Modifier.padding(innerPadding))
            AppDestinations.PARK -> {
                if (LocalInspectionMode.current) {
                    PlaceholderScreen("Registrar aparcamiento (Vista previa)", modifier = Modifier.padding(innerPadding))
                } else {
                    val context = LocalContext.current
                    val parkCarViewModel: ParkCarViewModel = viewModel(
                        factory = ParkCarViewModel.provideFactory(context)
                    )
                    ParkCarScreen(
                        viewModel = parkCarViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
            AppDestinations.MY_CARS -> {
                if (LocalInspectionMode.current) {
                    PlaceholderScreen("Mis coches aparcados (Vista previa)", modifier = Modifier.padding(innerPadding))
                } else {
                    val context = LocalContext.current
                    val myCarsViewModel: MyCarsViewModel = viewModel(
                        factory = MyCarsViewModel.provideFactory(context)
                    )
                    MyCarsScreen(
                        viewModel = myCarsViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: Int,
) {
    MAP("Mapa", R.drawable.ic_home),
    PARK("Aparcar", R.drawable.ic_favorite),
    MY_CARS("Mis coches", R.drawable.ic_account_box),
}

@Composable
fun PlaceholderScreen(title: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Pantalla: $title")
    }
}

@Preview(showBackground = true)
@Composable
fun FindMyCarAppPreview() {
    FindMyCarTheme {
        FindMyCarApp()
    }
}
