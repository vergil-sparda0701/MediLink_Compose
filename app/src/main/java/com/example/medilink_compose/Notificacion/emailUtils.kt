package com.example.medilink_compose.Notificacion

import android.content.Context
import android.os.Build
import android.telephony.SmsManager
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.medilink_compose.BD_Files.SQLiteHelper
import com.example.medilink_compose.databaseVersion
import java.time.*
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
fun pacienteCitaEnviarSMS(context: Context) {
    val dbHelper = SQLiteHelper(context, "MediLink.db", null, databaseVersion)
    val db = dbHelper.readableDatabase
    val ahora = LocalDateTime.now()

    val cursor = db.rawQuery(
        """
        SELECT
            citas.fecha_cita,
            citas.hora_cita,
            pacientes.celular,
            citas.nombre_paciente,
            citas.apellido_paciente
        FROM citas
        INNER JOIN pacientes ON citas.id_paciente = pacientes.id
        """, null
    )

    while (cursor.moveToNext()) {
        try {
            val fechaStr = cursor.getString(cursor.getColumnIndexOrThrow("fecha_cita"))
            val horaStr = cursor.getString(cursor.getColumnIndexOrThrow("hora_cita"))
            val telefonoPaciente = cursor.getString(cursor.getColumnIndexOrThrow("celular"))
            val nombrePaciente = cursor.getString(cursor.getColumnIndexOrThrow("nombre_paciente"))
            val apellidoPaciente = cursor.getString(cursor.getColumnIndexOrThrow("apellido_paciente"))

            val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
            val fecha = LocalDate.parse(fechaStr, dateFormatter)
            val hora = LocalTime.parse(horaStr, timeFormatter)
            val citaDateTime = LocalDateTime.of(fecha, hora)

            val diferencia = Duration.between(ahora, citaDateTime).toMinutes()

            if (diferencia in 25..35) {
                val mensaje = "Hola $nombrePaciente $apellidoPaciente, este es un recordatorio de tu cita médica hoy a las $horaStr. ¡No faltes!"

                Log.d("SMSCita", "Enviando SMS a: $telefonoPaciente")
                enviarSMS(telefonoPaciente, mensaje, context)
            }
        } catch (e: Exception) {
            Log.e("SMSCita", "Error al procesar cita: ${e.message}")
        }
    }

    cursor.close()
    db.close()
}

fun enviarSMS(numero: String, mensaje: String, context: Context) {
    try {
        val smsManager: SmsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(numero, null, mensaje, null, null)
        Log.d("SMSCita", "SMS enviado a $numero")
    } catch (e: Exception) {
        Log.e("SMSCita", "Error al enviar SMS: ${e.message}")
    }
}

