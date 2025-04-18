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

data class Cita(
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

@Composable
fun CitasPendientesActivity(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val context = LocalContext.current
    val dbHelper = remember { SQLiteHelper(context, "MediLink.db", null, databaseVersion) }
    val resultadosBusqueda = remember { mutableStateOf(listOf<Cita>()) }

    // Cargar las citas una sola vez
    LaunchedEffect(Unit) {
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

                Row (horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier =  Modifier.fillMaxWidth()
                ) {
                    Text("© 2025 MediLink", fontSize = 15.sp, color = Color.White)
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
            Text("Citas pendientes", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xff00a9b0))

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
                        Text("Doctor: ${cita.nombreDoctor} ${cita.apellidoDoctor}")
                        Text("Estado: ${cita.estado}")

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()){

                            Button(
                                onClick = {

                                    //Marcar como Cancelada
                                    val db = dbHelper.writableDatabase
                                    val updateQuery = """
                                    UPDATE citas
                                    SET estado_cita = 'Atendida'
                                    WHERE id_cita = ?
                                     """.trimIndent()
                                    db.execSQL(updateQuery, arrayOf(cita.id))

                                    //Insertar en tabla historial
                                    val insertHistorialQuery = """
                                    INSERT INTO historial (id_cita, id_paciente, nombre_paciente, apellido_paciente, id_doctor, nombre_doc, apellido_doc, fecha_cita, hora_cita, estado_cita)
                                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                                    """.trimIndent()
                                    db.execSQL(
                                        insertHistorialQuery, arrayOf(
                                            cita.id,
                                            cita.idPaciente,
                                            cita.nombrePaciente,
                                            cita.apellidoPaciente,
                                            cita.idDoctor,
                                            cita.nombreDoctor,
                                            cita.apellidoDoctor,
                                            cita.fecha,
                                            cita.hora,
                                            "Atendida"
                                        )
                                    )

                                    db.close()

                                    // Recargar citas después de actualizar
                                    val updatedDb = dbHelper.readableDatabase
                                    val refreshQuery = """
                            SELECT * FROM citas
                            WHERE estado_cita = ?
                            ORDER BY fecha_cita ASC, hora_cita ASC
                        """.trimIndent()
                                    val newCursor = updatedDb.rawQuery(refreshQuery, arrayOf("Pendiente"))
                                    val nuevosResultados = mutableListOf<Cita>()
                                    if (newCursor.moveToFirst()) {
                                        do {
                                            nuevosResultados.add(
                                                Cita(
                                                    id = newCursor.getString(
                                                        newCursor.getColumnIndexOrThrow(
                                                            "id_cita"
                                                        )
                                                    ) ?: "",
                                                    hora = newCursor.getString(
                                                        newCursor.getColumnIndexOrThrow(
                                                            "hora_cita"
                                                        )
                                                    ) ?: "",
                                                    fecha = newCursor.getString(
                                                        newCursor.getColumnIndexOrThrow(
                                                            "fecha_cita"
                                                        )
                                                    ) ?: "",
                                                    idPaciente = newCursor.getString(
                                                        newCursor.getColumnIndexOrThrow(
                                                            "id_paciente"
                                                        )
                                                    ) ?: "",
                                                    nombrePaciente = newCursor.getString(
                                                        newCursor.getColumnIndexOrThrow(
                                                            "nombre_paciente"
                                                        )
                                                    ) ?: "",
                                                    apellidoPaciente = newCursor.getString(
                                                        newCursor.getColumnIndexOrThrow(
                                                            "apellido_paciente"
                                                        )
                                                    ) ?: "",
                                                    idDoctor = newCursor.getString(
                                                        newCursor.getColumnIndexOrThrow(
                                                            "id_doctor"
                                                        )
                                                    ) ?: "",
                                                    nombreDoctor = newCursor.getString(
                                                        newCursor.getColumnIndexOrThrow(
                                                            "nombre_doc"
                                                        )
                                                    ) ?: "",
                                                    apellidoDoctor = newCursor.getString(
                                                        newCursor.getColumnIndexOrThrow(
                                                            "apellido_doc"
                                                        )
                                                    ) ?: "",
                                                    estado = newCursor.getString(
                                                        newCursor.getColumnIndexOrThrow(
                                                            "estado_cita"
                                                        )
                                                    ) ?: ""
                                                )
                                            )
                                        } while (newCursor.moveToNext())
                                    }
                                    newCursor.close()
                                    updatedDb.close()
                                    resultadosBusqueda.value = nuevosResultados

                                    Toast.makeText(context, "Cita atendida", Toast.LENGTH_LONG).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xff0d730d))) // Verde
                             {
                                Text("Atendida?")
                            }


                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {

                                    //Marcar como Cancelada
                                    val db = dbHelper.writableDatabase
                                    val updateQuery = """
                                    UPDATE citas
                                    SET estado_cita = 'Cancelada'
                                    WHERE id_cita = ?
                                     """.trimIndent()
                                    db.execSQL(updateQuery, arrayOf(cita.id))

                                    //Insertar en tabla historial
                                    val insertHistorialQuery = """
                                    INSERT INTO historial (id_cita, id_paciente, nombre_paciente, apellido_paciente, id_doctor, nombre_doc, apellido_doc, fecha_cita, hora_cita, estado_cita)
                                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                                    """.trimIndent()
                                    db.execSQL(
                                        insertHistorialQuery, arrayOf(
                                            cita.id,
                                            cita.idPaciente,
                                            cita.nombrePaciente,
                                            cita.apellidoPaciente,
                                            cita.idDoctor,
                                            cita.nombreDoctor,
                                            cita.apellidoDoctor,
                                            cita.fecha,
                                            cita.hora,
                                            "Cancelada"
                                        )
                                    )

                                    db.close()

                                    // Recargar citas después de actualizar
                                    val updatedDb = dbHelper.readableDatabase
                                    val refreshQuery = """
                            SELECT * FROM citas
                            WHERE estado_cita = ?
                            ORDER BY fecha_cita ASC, hora_cita ASC
                        """.trimIndent()
                                    val newCursor = updatedDb.rawQuery(refreshQuery, arrayOf("Pendiente"))
                                    val nuevosResultados = mutableListOf<Cita>()
                                    if (newCursor.moveToFirst()) {
                                        do {
                                            nuevosResultados.add(
                                                Cita(
                                                    id = newCursor.getString(
                                                        newCursor.getColumnIndexOrThrow(
                                                            "id_cita"
                                                        )
                                                    ) ?: "",
                                                    hora = newCursor.getString(
                                                        newCursor.getColumnIndexOrThrow(
                                                            "hora_cita"
                                                        )
                                                    ) ?: "",
                                                    fecha = newCursor.getString(
                                                        newCursor.getColumnIndexOrThrow(
                                                            "fecha_cita"
                                                        )
                                                    ) ?: "",
                                                    idPaciente = newCursor.getString(
                                                        newCursor.getColumnIndexOrThrow(
                                                            "id_paciente"
                                                        )
                                                    ) ?: "",
                                                    nombrePaciente = newCursor.getString(
                                                        newCursor.getColumnIndexOrThrow(
                                                            "nombre_paciente"
                                                        )
                                                    ) ?: "",
                                                    apellidoPaciente = newCursor.getString(
                                                        newCursor.getColumnIndexOrThrow(
                                                            "apellido_paciente"
                                                        )
                                                    ) ?: "",
                                                    idDoctor = newCursor.getString(
                                                        newCursor.getColumnIndexOrThrow(
                                                            "id_doctor"
                                                        )
                                                    ) ?: "",
                                                    nombreDoctor = newCursor.getString(
                                                        newCursor.getColumnIndexOrThrow(
                                                            "nombre_doc"
                                                        )
                                                    ) ?: "",
                                                    apellidoDoctor = newCursor.getString(
                                                        newCursor.getColumnIndexOrThrow(
                                                            "apellido_doc"
                                                        )
                                                    ) ?: "",
                                                    estado = newCursor.getString(
                                                        newCursor.getColumnIndexOrThrow(
                                                            "estado_cita"
                                                        )
                                                    ) ?: ""
                                                )
                                            )
                                        } while (newCursor.moveToNext())
                                    }
                                    newCursor.close()
                                    updatedDb.close()
                                    resultadosBusqueda.value = nuevosResultados

                                    Toast.makeText(context, "Cita cancelada", Toast.LENGTH_LONG).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red) // Verde
                            ) {
                                Text("Cancelada?")
                            }


                        }

                    }
                }
            }
        }

    }
}
