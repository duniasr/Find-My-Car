package com.findmycar.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

/**
 * Utilidades para la gestión de navegación e integración con aplicaciones de mapas externas.
 */
object NavigationUtils {

    /**
     * Inicia una aplicación de mapas o navegación hacia las coordenadas indicadas.
     * Intenta abrir primero la navegación en Google Maps en modo peatón (para volver al coche).
     * Si no está disponible, recurre al Intent estándar geo: y finalmente al navegador web.
     *
     * @param context Contexto de Android para lanzar el Intent.
     * @param latitude Latitud de destino.
     * @param longitude Longitud de destino.
     * @param label Nombre o etiqueta descriptiva del destino (por ejemplo, el nombre del coche).
     */
    fun navigateToCoordinates(
        context: Context,
        latitude: Double,
        longitude: Double,
        label: String = "Coche aparcado"
    ) {
        val encodedLabel = Uri.encode(label)

        // 1. Intent prioritario: Navegación peatonal directa en Google Maps
        val navigationUri = Uri.parse("google.navigation:q=$latitude,$longitude&mode=w")
        val navIntent = Intent(Intent.ACTION_VIEW, navigationUri).apply {
            setPackage("com.google.android.apps.maps")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        try {
            if (navIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(navIntent)
                return
            }
        } catch (_: Exception) {
            // Continuar con el fallback si falla la resolución de la app oficial
        }

        // 2. Fallback estándar: Intent geo: compatible con cualquier visor de mapas instalado
        val geoUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude($encodedLabel)")
        val geoIntent = Intent(Intent.ACTION_VIEW, geoUri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        try {
            if (geoIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(geoIntent)
                return
            }
        } catch (_: Exception) {
            // Continuar con el fallback web
        }

        // 3. Fallback final: Abrir Google Maps directamente en el navegador del dispositivo
        val webUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$latitude,$longitude")
        val webIntent = Intent(Intent.ACTION_VIEW, webUri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        try {
            context.startActivity(webIntent)
        } catch (_: Exception) {
            Toast.makeText(
                context,
                "No se encontró ninguna aplicación para iniciar la navegación",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
