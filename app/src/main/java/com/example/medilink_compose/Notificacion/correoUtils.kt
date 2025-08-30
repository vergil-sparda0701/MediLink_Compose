package com.example.medilink_compose.Notificacion

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.medilink_compose.BD_Files.SQLiteHelper
import com.example.medilink_compose.databaseVersion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

private const val TAG = "CorreoUtils"
private const val SMTP_HOST = "smtp-mail.outlook.com"
private const val SMTP_PORT = "587"
private const val SMTP_USERNAME = "mediLinkApp2025@outlook.com"
private const val SMTP_PASSWORD = "bsqbcylsbbsfnikb"

@RequiresApi(Build.VERSION_CODES.O)
fun enviarRecordatoriosCorreo(context: Context) {
    val dbHelper = SQLiteHelper(context, "MediLink.db", null, databaseVersion)
    val ahora = LocalDateTime.now()

    dbHelper.readableDatabase.use { db ->
        val query = """
            SELECT
                citas.fecha_cita,
                citas.hora_cita,
                pacientes.correo,
                citas.nombre_paciente,
                citas.apellido_paciente
            FROM citas
            INNER JOIN pacientes ON citas.id_paciente = pacientes.id
            WHERE estado_cita = 'Pendiente'
        """.trimIndent()

        db.rawQuery(query, null).use { cursor ->
            val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

            while (cursor.moveToNext()) {
                try {
                    val fechaStr = cursor.getString(cursor.getColumnIndexOrThrow("fecha_cita"))
                    val horaStr = cursor.getString(cursor.getColumnIndexOrThrow("hora_cita"))
                    val correoPaciente = cursor.getString(cursor.getColumnIndexOrThrow("correo"))
                    val nombrePaciente = cursor.getString(cursor.getColumnIndexOrThrow("nombre_paciente"))
                    val apellidoPaciente = cursor.getString(cursor.getColumnIndexOrThrow("apellido_paciente"))

                    val fecha = LocalDate.parse(fechaStr, dateFormatter)
                    val hora = LocalTime.parse(horaStr, timeFormatter)
                    val citaDateTime = LocalDateTime.of(fecha, hora)

                    val diferenciaMinutos = Duration.between(ahora, citaDateTime).toMinutes()

                    if (diferenciaMinutos in 30..35) {
                        val asunto = "Recordatorio de cita"
                        val cuerpo = """
                            Hola $nombrePaciente $apellidoPaciente,
                            
                            Este es un recordatorio de tu cita médica hoy a las $horaStr.
                            ¡No faltes!
                            
                            MediLink
                        """.trimIndent()

                        Log.d(TAG, "Enviando correo a: $correoPaciente")
                        enviarCorreo(asunto, cuerpo, correoPaciente)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error al procesar cita: ${e.message}", e)
                }
            }
        }
    }
}

fun enviarCorreo(asunto: String, cuerpo: String, destinatario: String) {
    val props = Properties().apply {
        put("mail.smtp.auth", "true")
        put("mail.smtp.starttls.enable", "true")
        put("mail.smtp.host", SMTP_HOST)
        put("mail.smtp.port", SMTP_PORT)
    }

    val session = Session.getInstance(props, object : javax.mail.Authenticator() {
        override fun getPasswordAuthentication() =
            PasswordAuthentication(SMTP_USERNAME, SMTP_PASSWORD)
    })

    // Usamos corutinas para no bloquear el hilo principal
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(SMTP_USERNAME))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario))
                subject = asunto
                setText(cuerpo)
            }
            Transport.send(message)
            Log.d(TAG, "Correo enviado a $destinatario")
        } catch (e: Exception) {
            Log.e(TAG, "Error enviando correo: ${e.message}", e)
        }
    }
}
