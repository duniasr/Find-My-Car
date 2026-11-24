package com.findmycar.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Utilidades para el formateo y cálculo de fechas y tiempos relativos.
 */
object DateTimeUtils {

    /**
     * Formatea una marca de tiempo en una cadena legible con fecha y hora (ej: "24 nov 2026, 14:30").
     */
    fun formatDateTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    /**
     * Calcula una representación amigable del tiempo transcurrido desde que se aparcó el coche.
     * Ejemplo: "Hace un momento", "Hace 20 min", "Hace 2 horas", "Ayer", etc.
     */
    fun getRelativeTimeSpan(timestamp: Long, currentTimeMillis: Long = System.currentTimeMillis()): String {
        val diffMillis = (currentTimeMillis - timestamp).coerceAtLeast(0)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diffMillis)
        val hours = TimeUnit.MILLISECONDS.toHours(diffMillis)
        val days = TimeUnit.MILLISECONDS.toDays(diffMillis)

        return when {
            minutes < 1 -> "Hace un momento"
            minutes < 60 -> "Hace $minutes min"
            hours == 1L -> "Hace 1 hora"
            hours < 24 -> "Hace $hours horas"
            days == 1L -> "Ayer"
            days < 7 -> "Hace $days días"
            else -> formatDateTime(timestamp)
        }
    }
}
