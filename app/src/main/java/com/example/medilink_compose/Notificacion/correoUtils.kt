package com.example.medilink_compose.Notificacion

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.medilink_compose.BD_Files.SQLiteHelper
import com.example.medilink_compose.R
import com.example.medilink_compose.databaseVersion
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Properties
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


@RequiresApi(Build.VERSION_CODES.O)
fun pacienteCorreo(context: Context) {
    val dbHelper = SQLiteHelper(context, "MediLink.db", null, databaseVersion)
    val db = dbHelper.readableDatabase
    val ahora = LocalDateTime.now()

    val cursor = db.rawQuery(
        """
    SELECT
        citas.fecha_cita,
        citas.hora_cita,
        pacientes.correo,
        citas.nombre_paciente,
        citas.apellido_paciente
    FROM citas
    INNER JOIN pacientes ON citas.id_paciente = pacientes.id
    WHERE estado_cita = 'Pendiente'
    """, null
    )


    while (cursor.moveToNext()) {
        try {
            val fechaStr = cursor.getString(cursor.getColumnIndexOrThrow("fecha_cita"))
            val horaStr = cursor.getString(cursor.getColumnIndexOrThrow("hora_cita"))
            val correoPaciente = cursor.getString(cursor.getColumnIndexOrThrow("correo"))
            val nombrePaciente = cursor.getString(cursor.getColumnIndexOrThrow("nombre_paciente"))
            val apellidoPaciente = cursor.getString(cursor.getColumnIndexOrThrow("apellido_paciente"))

            val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
            val fecha = LocalDate.parse(fechaStr, dateFormatter)
            val hora = LocalTime.parse(horaStr, timeFormatter)
            val citaDateTime = LocalDateTime.of(fecha, hora)

            val diferencia = Duration.between(ahora, citaDateTime).toMinutes()

            if (diferencia in 25..35) {

                val icon = R.drawable.icono

                val mensaje = "$icon \n MediLink \nHola $nombrePaciente $apellidoPaciente, este es un recordatorio de tu cita médica hoy a las $horaStr. ¡No faltes!"

                Log.d("SMSCita", "Enviando SMS a: $correoPaciente")

                sendEmail("Recordatorio de cita", mensaje, correoPaciente)
            }
        } catch (e: Exception) {
            Log.e("SMSCita", "Error al procesar cita: ${e.message}")
        }
    }

    cursor.close()
    db.close()
}

fun sendEmail(asunto : String, cuerpo : String, destinatario : String) {
    val username = "mediLinkApp2025@outlook.com"
    val password = "bsqbcylsbbsfnikb" // ojo: Gmail ya no permite contraseña normal, debes generar un "App Password"

    val props = Properties().apply {
        put("mail.smtp.auth", "true")
        put("mail.smtp.starttls.enable", "true")
        put("mail.smtp.host", "smtp-mail.outlook.com")
        put("mail.smtp.port", "587")
    }

    val session = Session.getInstance(props,
        object : javax.mail.Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(username, password)
            }
        })

    try {
        val message = MimeMessage(session).apply {
            setFrom(InternetAddress(username))
            setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario))
            subject = asunto
            setText(cuerpo)
        }

        // Importante: usar un hilo, no el main thread
        Transport.send(message)

    } catch (e: Exception) {
        e.printStackTrace()
    }
}
