package com.example.medilink_compose.Pantallas

import android.content.ContentValues
import android.widget.Toast
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
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

data class historial(
    val id: String,
    val hora: String,
    val fecha: String,
    val idPaciente: String,
    val nombrePaciente: String,
    val apellidoPaciente: String,
    val idDoctor: String,
    val nombreDoctor: String,
    val apellidoDoctor: String,
    val estado: String
)

val navItems = listOf(
    NavItem("Inicio", Icons.Default.Home, "Menu"),
    NavItem("Historial", Icons.Default.Refresh, "historiales"),
    NavItem("Stats", Icons.Filled.BarChart, "stats"),
    NavItem("Config", Icons.Default.Settings, "config")
)


@Composable
fun HistorialActivitys(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val context = LocalContext.current
    val dbHelper = remember { SQLiteHelper(context, "MediLink.db", null, databaseVersion) }
    val resultadosBusqueda = remember { mutableStateOf(listOf<historial>()) }

    // Cargar las citas una sola vez
    LaunchedEffect(Unit) {
        val baseDatos = dbHelper.readableDatabase
        val query = """
        SELECT * FROM historial
        WHERE estado_cita = ? OR estado_cita = ?
        ORDER BY fecha_cita ASC, hora_cita ASC
         """.trimIndent()

        val cursor = baseDatos.rawQuery(query, arrayOf("Atendida", "Cancelada"))
        val resultados = mutableListOf<historial>()

        if (cursor.moveToFirst()) {
            do {
                resultados.add(
                    historial(
                        id = cursor.getString(cursor.getColumnIndexOrThrow("id_cita")) ?: "",
                        hora = cursor.getString(cursor.getColumnIndexOrThrow("hora_cita")) ?: "",
                        fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha_cita")) ?: "",
                        idPaciente = cursor.getString(cursor.getColumnIndexOrThrow("id_paciente")) ?: "",
                        nombrePaciente = cursor.getString(cursor.getColumnIndexOrThrow("nombre_paciente"))
                            ?: "",
                        apellidoPaciente = cursor.getString(cursor.getColumnIndexOrThrow("apellido_paciente"))
                            ?: "",
                        idDoctor = cursor.getString(cursor.getColumnIndexOrThrow("id_doctor")) ?: "",
                        nombreDoctor = cursor.getString(cursor.getColumnIndexOrThrow("nombre_doc"))
                            ?: "",
                        apellidoDoctor = cursor.getString(cursor.getColumnIndexOrThrow("apellido_doc"))
                            ?: "",
                        estado = cursor.getString(cursor.getColumnIndexOrThrow("estado_cita")) ?: ""
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        baseDatos.close()

        resultadosBusqueda.value = resultados
    }

    // SCaffold con BottomAppBar
    Scaffold(

        modifier = Modifier.fillMaxSize(),
        topBar = {

            Row (Modifier.statusBarsPadding(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically){
                IconButton(onClick = {navController.popBackStack()}) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Navegar hacia atras"
                    )
                }
            }
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color(0xFF00A9B0),
                contentColor = Color.White
            ) {
                NavigationBar(containerColor = Color(0xFF00A9B0)) {
                    navItems.forEach { item ->
                        NavigationBarItem(
                            selected = false,
                            onClick = { navController.navigate(item.route) },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label
                                )
                            },
                            label = {
                                Text(item.label)
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                unselectedIconColor = Color.White.copy(alpha = 0.7f),
                                indicatorColor = Color(0xFF00777A)
                            )
                        )
                    }
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
            Text("Historial de citas", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xff00a9b0))

            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.drawable.icono),
                contentDescription = "Imagen citas",
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn() {
                items(resultadosBusqueda.value) { cita ->
                    Column(
                        modifier = Modifier
                            .padding(vertical = 6.dp, horizontal = 8.dp)
                            .border(1.dp, color = Color(0xff00a9b0), shape = RoundedCornerShape(5.dp))
                            .background(Color.Transparent, shape = RoundedCornerShape(5.dp))
                            .padding(12.dp)
                            .fillMaxWidth()
                    ) {
                        Text("ID: ${cita.id}", fontWeight = FontWeight.Bold)
                        Text("Hora: ${cita.hora}")
                        Text("Fecha: ${cita.fecha}")
                        Text("Paciente: ${cita.nombrePaciente} ${cita.apellidoPaciente}")
                        Text("Doctor: ${cita.nombreDoctor}")
                        Text("Estado: ${cita.estado}")


                    }
                }
            }
        }

    }
}

