package com.example.medilink_compose.Notificacion

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.app.PendingIntent
import android.content.Intent
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import com.example.medilink_compose.BD_Files.SQLiteHelper
import com.example.medilink_compose.MainActivity
import com.example.medilink_compose.R
import com.example.medilink_compose.databaseVersion
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import android.util.Log

const val CHANNEL_ID = "mi_canal_notificaciones"
const val CHANNEL_NAME = "Notificaciones Generales"
const val CHANNEL_DESCRIPTION = "Este canal es para notificaciones generales de la aplicación."

// Crear canal de notificación
fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
            description = CHANNEL_DESCRIPTION
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

// Mostrar la notificación
@SuppressLint("MissingPermission")
fun mostrarNotificacion(context: Context, titulo: String, mensaje: String) {
    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.icono)
        .setContentTitle(titulo)
        .setContentText(mensaje)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    with(NotificationManagerCompat.from(context)) {
        notify(1, builder.build()) // ID único para cada notificación
    }
}



@RequiresApi(Build.VERSION_CODES.O)
fun pacienteCita2(context: Context) {
    val dbHelper = SQLiteHelper(context, "MediLink.db", null, databaseVersion)
    val db = dbHelper.readableDatabase

    val ahora = LocalDateTime.now()

    Log.d("PacienteCita", "Hora actual: $ahora")

    val cursor = db.rawQuery(
        "SELECT * FROM citas",
        null
    )

    while (cursor.moveToNext()) {
        val fechaStr = cursor.getString(cursor.getColumnIndexOrThrow("fecha_cita"))
        val horaStr = cursor.getString(cursor.getColumnIndexOrThrow("hora_cita"))

        Log.d("PacienteCita", "Cita encontrada - Fecha: $fechaStr, Hora: $horaStr")

        try {
            // **Reemplaza con el formato real de tu base de datos**
            val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy") // Ejemplo: "2025-04-12"
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")   // Ejemplo: "23:56"

            val fecha = LocalDate.parse(fechaStr, dateFormatter)
            val hora = LocalTime.parse(horaStr, timeFormatter)
            val citaDateTime = LocalDateTime.of(fecha, hora)

            val diferencia = Duration.between(ahora, citaDateTime).toMinutes()

            Log.d("PacienteCita", "Diferencia entre ahora y la cita: $diferencia minutos")

            if (diferencia in -5..5) {

                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre_paciente"))
                val apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido_paciente"))

                val mensaje = "Cita con el paciente: $nombre $apellido a las $horaStr"
                mostrarNotificacion(context, "Recordatorio de cita", mensaje)

                Log.d("PacienteCita", "Notificación enviada: $mensaje")

            }
        } catch (e: Exception) {
            Log.e("PacienteCita", "Error al analizar fecha u hora: ${e.message}")
            e.printStackTrace()
        }
    }

    cursor.close()
    db.close()
}

@RequiresApi(Build.VERSION_CODES.O)
fun pacienteCita(context: Context) {
    val dbHelper = SQLiteHelper(context, "MediLink.db", null, databaseVersion)
    val db = dbHelper.readableDatabase

    val ahora = LocalDateTime.now()

    Log.d("PacienteCita", "Hora actual: $ahora")

    val cursor = db.rawQuery(
        "SELECT * FROM citas",
        null
    )

    while (cursor.moveToNext()) {
        val fechaStr = cursor.getString(cursor.getColumnIndexOrThrow("fecha_cita"))
        val horaStr = cursor.getString(cursor.getColumnIndexOrThrow("hora_cita"))

        Log.d("PacienteCita", "Cita encontrada - Fecha: $fechaStr, Hora: $horaStr")

        try {
            // **Reemplaza con el formato real de tu base de datos**
            val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy") // Ejemplo: "2025-04-12"
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")   // Ejemplo: "23:56"

            val fecha = LocalDate.parse(fechaStr, dateFormatter)
            val hora = LocalTime.parse(horaStr, timeFormatter)
            val citaDateTime = LocalDateTime.of(fecha, hora)

            // Calcular la diferencia de tiempo en minutos entre la hora actual y la hora de la cita
            val diferencia = Duration.between(ahora, citaDateTime).toMinutes()

            Log.d("PacienteCita", "Diferencia entre ahora y la cita: $diferencia minutos")

            // Verifica si la cita es dentro de los 30 minutos previos a la hora de la cita
            if (diferencia in 25..35) { // 30 minutos antes (+/- 5 minutos)
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre_paciente"))
                val apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido_paciente"))

                val mensaje = "Cita con el paciente: $nombre $apellido a las $horaStr"
                mostrarNotificacion(context, "Recordatorio de cita", mensaje)

                Log.d("PacienteCita", "Notificación enviada: $mensaje")
            }
        } catch (e: Exception) {
            Log.e("PacienteCita", "Error al analizar fecha u hora: ${e.message}")
            e.printStackTrace()
        }
    }

    cursor.close()
    db.close()
}




