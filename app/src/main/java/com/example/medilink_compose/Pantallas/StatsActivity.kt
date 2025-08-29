package com.example.medilink_compose.Pantallas

import android.annotation.SuppressLint
import android.database.sqlite.SQLiteDatabase
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.medilink_compose.BarChartStyled
import com.example.medilink_compose.FechaConDatePicker
import com.example.medilink_compose.R
import com.example.medilink_compose.databaseVersion
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("SimpleDateFormat")
@Composable
fun StatsActivity(modifier: Modifier, navController: NavHostController) {
    val context = LocalContext.current
    val db = remember { SQLiteHelper(context, "MediLink.db", null, databaseVersion) }
    val database = db.readableDatabase

    val fechaInicio = remember { mutableStateOf("") }
    val fechaFin = remember { mutableStateOf("") }
    val showGraph = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Row(
                Modifier.statusBarsPadding(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.navigate("Menu") }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Navegar hacia atrás"
                    )
                }
            }
        },
        bottomBar = {
            BottomAppBar(containerColor = Color(0xFF00A9B0), contentColor = Color.White) {
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
                .padding(15.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Gráfico de citas",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xff00a9b0)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.drawable.chart),
                contentDescription = "Imagen gráfico",
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            FechaConDatePicker(fechaInicio, "Fecha inicial")
            FechaConDatePicker(fechaFin, "Fecha final")

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                errorMessage.value = ""

                if (fechaInicio.value.isEmpty() || fechaFin.value.isEmpty()) {
                    errorMessage.value = "Debe seleccionar ambas fechas"
                    showGraph.value = false
                    return@Button
                }

                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                try {
                    val startDate = sdf.parse(fechaInicio.value)
                    val endDate = sdf.parse(fechaFin.value)

                    if (startDate?.after(endDate) == true) {
                        errorMessage.value = "La fecha inicial no puede ser posterior a la fecha final"
                        showGraph.value = false
                        return@Button
                    }

                    showGraph.value = true
                } catch (e: Exception) {
                    errorMessage.value = "Formato de fecha inválido"
                    showGraph.value = false
                }
            }) {
                Text("Mostrar Citas")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (errorMessage.value.isNotEmpty()) {
                Text(errorMessage.value, color = Color.Red)
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (showGraph.value) {
                // Aquí se obtienen los datos una sola vez
                val (data, labels) = getCitasByEstado(database, fechaInicio.value, fechaFin.value)

                if (data.isNotEmpty()) {
                    // Y aquí se usa la función `BarChartStyled` directamente con esos datos
                    BarChartStyled(
                        title = "Citas (${fechaInicio.value} a ${fechaFin.value})",
                        data = data,
                        labels = labels,
                        barColor = listOf(Color.Green, Color(0xff006400)),
                        textColor = Color.LightGray
                    )
                } else {
                    Text("No hay citas en el rango seleccionado", color = Color.Red)
                }
            }
        }
    }
}

@SuppressLint("Range")
fun getCitasByEstado(db: SQLiteDatabase, startDate: String, endDate: String): Pair<List<Float>, List<String>> {
    val query = """
        SELECT estado_cita, COUNT(*) AS cantidad
        FROM historial
        WHERE substr(fecha_cita, 7, 4) || '-' || substr(fecha_cita, 4, 2) || '-' || substr(fecha_cita, 1, 2)
        BETWEEN substr(?, 7, 4) || '-' || substr(?, 4, 2) || '-' || substr(?, 1, 2)
        AND substr(?, 7, 4) || '-' || substr(?, 4, 2) || '-' || substr(?, 1, 2)
        GROUP BY estado_cita
    """

    val cursor = db.rawQuery(query, arrayOf(startDate, startDate, startDate, endDate, endDate, endDate))

    val estados = mutableListOf<Float>()
    val labels = mutableListOf<String>()

    while (cursor.moveToNext()) {
        val estado = cursor.getString(cursor.getColumnIndex("estado_cita"))
        val cantidad = cursor.getInt(cursor.getColumnIndex("cantidad")).toFloat()

        estados.add(cantidad)
        labels.add(estado)
    }

    cursor.close()
    return Pair(estados, labels)
}



//codigo viejo
/*
@Composable
fun StatsActivity(modifier: Modifier, navController: NavHostController) {

    val context = LocalContext.current
    val db = remember { SQLiteHelper(context, "MediLink.db", null, databaseVersion) }
    val database = db.readableDatabase

    // Estados para almacenar las fechas seleccionadas
    val fechaInicio = remember { mutableStateOf("") }
    val fechaFin = remember { mutableStateOf("") }

    // SCaffold con BottomAppBar
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Row(Modifier.statusBarsPadding(), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Navegar hacia atras"
                    )
                }
            }
        },
        bottomBar = {
            BottomAppBar(containerColor = Color(0xFF00A9B0), contentColor = Color.White) {
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
                .padding(15.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("Grafico de citas", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xff00a9b0))

            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.drawable.chart),
                contentDescription = "Imagen grafico",
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Utilizando FechaConDatePicker para seleccionar las fechas
            FechaConDatePicker(fechaInicio, "Fecha inicial")
            FechaConDatePicker(fechaFin, "Fecha final")

            Spacer(modifier = Modifier.height(16.dp))

            // Solo mostrar el gráfico si ambas fechas están seleccionadas
            if (fechaInicio.value.isNotEmpty() && fechaFin.value.isNotEmpty()) {
                val (data, labels) = getCitasByEstado(database, fechaInicio.value, fechaFin.value)
                if (data.isNotEmpty()) {
                    CitasGrafico(db = database, startDate = fechaInicio.value, endDate = fechaFin.value)
                } else {
                    Text("No hay datos para el rango de fechas seleccionado", color = Color.Red)
                }
            } else {
                Text("Seleccione un rango de fechas", color = Color.Red)
            }
        }
    }
}

@SuppressLint("Range")
fun getCitasByEstado(db: SQLiteDatabase, startDate: String, endDate: String): Pair<List<Float>, List<String>> {
    // Asegúrate de que las fechas estén en el formato correcto
    val query = """
        SELECT estado_cita, COUNT(*) AS cantidad
        FROM historial
        WHERE fecha_cita >= ? AND fecha_cita <= ?
        GROUP BY estado_cita
    """
    val cursor = db.rawQuery(query, arrayOf(startDate, endDate))

    val estados = mutableListOf<Float>()
    val labels = mutableListOf<String>()

    while (cursor.moveToNext()) {
        val estado = cursor.getString(cursor.getColumnIndex("estado_cita"))
        val cantidad = cursor.getInt(cursor.getColumnIndex("cantidad")).toFloat()

        estados.add(cantidad)
        labels.add(estado)
    }

    cursor.close()

    return Pair(estados, labels)
}


@Composable
fun CitasGrafico(db: SQLiteDatabase, startDate: String, endDate: String) {
    val (data, labels) = getCitasByEstado(db, startDate, endDate)

    BarChartStyled(
        title = "Citas Atendidas y Canceladas",
        data = data,
        labels = labels,
        barColor = listOf(Color.Green, Color(0xff006400)), // Colores para atendidas y canceladas
        textColor = Color.Black
    )
}
*/
