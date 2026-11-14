package com.findmycar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.findmycar.ui.map.MapScreen
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

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            painterResource(it.icon),
                            contentDescription = it.label
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            when (currentDestination) {
                AppDestinations.MAP -> MapScreen(modifier = Modifier.padding(innerPadding))
                AppDestinations.PARK -> PlaceholderScreen("Formulario de Aparcar", modifier = Modifier.padding(innerPadding))
                AppDestinations.MY_CARS -> PlaceholderScreen("Mis Coches Aparcados", modifier = Modifier.padding(innerPadding))
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
    MY_CARS("Mis Coches", R.drawable.ic_account_box),
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
