package com.example.medilink_compose.Pantallas

import android.content.ContentValues
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.medilink_compose.BD_Files.SQLiteHelper
import com.example.medilink_compose.ImageButton
import com.example.medilink_compose.Notificacion.pacienteCita
import com.example.medilink_compose.R
import com.example.medilink_compose.databaseVersion
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotificacionActivity(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val context = LocalContext.current
    val dbHelper = remember { SQLiteHelper(context, "MediLink.db", null, databaseVersion) }
    val resultadosBusqueda = remember { mutableStateOf(listOf<Cita>()) }
    val ahora = remember { mutableStateOf(LocalDateTime.now()) }

    // Función para cargar citas pendientes desde la base de datos
    fun cargarCitasPendientes(): List<Cita> {
        val baseDatos = dbHelper.readableDatabase
        val query = """
            SELECT * FROM citas
            WHERE estado_cita = ?
            ORDER BY fecha_cita ASC, hora_cita ASC
        """.trimIndent()

        val cursor = baseDatos.rawQuery(query, arrayOf("Pendiente"))
        val resultados = mutableListOf<Cita>()

        if (cursor.moveToFirst()) {
            do {
                resultados.add(
                    Cita(
                        id = cursor.getString(cursor.getColumnIndexOrThrow("id_cita")) ?: "",
                        hora = cursor.getString(cursor.getColumnIndexOrThrow("hora_cita")) ?: "",
                        fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha_cita")) ?: "",
                        idPaciente = cursor.getString(cursor.getColumnIndexOrThrow("id_paciente")) ?: "",
                        nombrePaciente = cursor.getString(cursor.getColumnIndexOrThrow("nombre_paciente")) ?: "",
                        apellidoPaciente = cursor.getString(cursor.getColumnIndexOrThrow("apellido_paciente")) ?: "",
                        idDoctor = cursor.getString(cursor.getColumnIndexOrThrow("id_doctor")) ?: "",
                        nombreDoctor = cursor.getString(cursor.getColumnIndexOrThrow("nombre_doc")) ?: "",
                        apellidoDoctor = cursor.getString(cursor.getColumnIndexOrThrow("apellido_doc")) ?: "",
                        estado = cursor.getString(cursor.getColumnIndexOrThrow("estado_cita")) ?: ""
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        baseDatos.close()
        return resultados
    }

    // Actualizar la hora actual y recargar citas cada minuto
    LaunchedEffect(Unit) {
        while (true) {
            ahora.value = LocalDateTime.now()
            resultadosBusqueda.value = cargarCitasPendientes()
            kotlinx.coroutines.delay(60_000L) // 60 segundos
        }
    }

    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Row(
                Modifier.statusBarsPadding(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Navegar hacia atrás"
                    )
                }
            }
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color(0xFF00A9B0),
                contentColor = Color.White
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("© 2025 MediCita", fontSize = 15.sp, color = Color.White)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Citas pendientes",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xff00a9b0)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.drawable.icono),
                contentDescription = "Imagen citas",
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            val citasProximas = resultadosBusqueda.value.filter { cita ->
                try {
                    val fecha = LocalDate.parse(cita.fecha, dateFormatter)
                    val hora = LocalTime.parse(cita.hora, timeFormatter)
                    val citaDateTime = LocalDateTime.of(fecha, hora)
                    val diferencia = Duration.between(ahora.value, citaDateTime).toMinutes()
                    diferencia in 30..35
                } catch (e: Exception) {
                    false
                }
            }

            if (citasProximas.isEmpty()) {
                Text(
                    "No hay citas pendientes próximas.",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn {
                    items(citasProximas) { cita ->
                        Column(
                            modifier = Modifier
                                .padding(vertical = 6.dp, horizontal = 8.dp)
                                .border(1.dp, color = Color(0xff00a9b0), shape = RoundedCornerShape(5.dp))
                                .background(Color.Transparent, shape = RoundedCornerShape(5.dp))
                                .padding(12.dp)
                                .fillMaxWidth()
                        ) {

                            Text("Recordatorio de cita", fontWeight = FontWeight.Bold)
                            Text(
                                "Doctor ${cita.nombreDoctor} ${cita.apellidoDoctor} le recordamos que tiene una cita ${cita.estado} " +
                                        "\nhoy ${cita.fecha} con el Paciente: ${cita.nombrePaciente} ${cita.apellidoPaciente}." +
                                        "\na las ${cita.hora}"
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}
