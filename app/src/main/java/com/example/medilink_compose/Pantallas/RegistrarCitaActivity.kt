package com.example.medilink_compose.Pantallas

import android.content.ContentValues
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.example.medilink_compose.BD_Files.SQLiteHelper
import com.example.medilink_compose.ComboBox
import com.example.medilink_compose.FechaConDatePicker
import com.example.medilink_compose.HoraConTimePicker
import com.example.medilink_compose.ImageButton
import com.example.medilink_compose.Notificacion.pacienteCita
import com.example.medilink_compose.R
import com.example.medilink_compose.SeccionDesplegable
import com.example.medilink_compose.databaseVersion
import com.example.medilink_compose.outLinedText
import com.google.accompanist.permissions.ExperimentalPermissionsApi


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RegistrarCitaActivity(modifier: Modifier = Modifier, navController: NavHostController){

    //region declaracion de variables

    val scrollState = rememberScrollState()
    val scrollState2 = rememberScrollState()

    val expandido = remember {mutableStateOf(false)}
    val expandido2 = remember {mutableStateOf(false)}
    val expandido3 = remember {mutableStateOf(false)}

    val context = LocalContext.current
    val dbHelper = SQLiteHelper(context, "MediLink.db", null, databaseVersion)
    val baseDatos = dbHelper.writableDatabase

    //datos de la cita
    val id = remember {mutableStateOf("")}
    val fecha = remember {mutableStateOf("")}
    val hora = remember {mutableStateOf("")}
    val estado = listOf("Pendiente", "Atendida")
    val estadoSelec = remember {mutableStateOf("")}

    //datos del paciente
    val idPaciente = remember {mutableStateOf("")}
    val nombrePaciente = remember {mutableStateOf("")}
    val apellidoPaciente = remember {mutableStateOf("")}

    //datos del paciente
    val idDoctor = remember {mutableStateOf("")}
    val nombreDoctor = remember {mutableStateOf("")}
    val apellidoDoctor = remember {mutableStateOf("")}

    //metodos popup 1
    val showBuscarPopup = remember { mutableStateOf(false) }
    val textoBusqueda = remember { mutableStateOf("") }
    val resultadosBusqueda = remember { mutableStateOf(listOf<Map<String, String>>()) }

    //metodos popup 2
    val showBuscarPopup2 = remember { mutableStateOf(false) }
    val textoBusqueda2 = remember { mutableStateOf("") }
    val resultadosBusqueda2 = remember { mutableStateOf(listOf<Map<String, String>>()) }

    //metodos popup 3
    val showBuscarPopup3 = remember { mutableStateOf(false) }
    val textoBusqueda3 = remember { mutableStateOf("") }
    val resultadosBusqueda3 = remember { mutableStateOf(listOf<Map<String, String>>()) }

    //endregion

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

                    ImageButton(imageResId = R.drawable.agregar, text = "Nuevo", onClick = {

                        id.value = ""
                        hora.value = ""
                        fecha.value = ""
                        estadoSelec.value = ""
                        idDoctor.value = ""
                        nombreDoctor.value = ""
                        apellidoDoctor.value = ""
                        idPaciente.value = ""
                        nombrePaciente.value = ""
                        apellidoPaciente.value = ""

                    })
                    Spacer(modifier = Modifier.width(2.dp))
                    ImageButton(imageResId = R.drawable.modificar, text = "Modificar", onClick = {

                        try {

                            val registro = ContentValues()

                            // Datos personales
                            registro.put("fecha_cita", fecha.value)
                            registro.put("hora_cita", hora.value)
                            registro.put("estado_cita", estadoSelec.value)
                            registro.put("id_doctor", idDoctor.value)
                            registro.put("nombre_doc", nombreDoctor.value)
                            registro.put("apellido_doc", apellidoDoctor.value)
                            registro.put("id_paciente", idPaciente.value)
                            registro.put("nombre_paciente", nombrePaciente.value)
                            registro.put("apellido_paciente", apellidoPaciente.value)

                            if (id.value.isNotEmpty()) {
                                val cantidad = baseDatos.update(
                                    "citas",
                                    registro,
                                    "id_cita = ?",
                                    arrayOf(id.value)
                                )

                                if (cantidad > 0) {
                                    // Comprobación y acción sobre historial
                                    val historialCursor = baseDatos.rawQuery(
                                        "SELECT COUNT(*) FROM historial WHERE id_cita = ?",
                                        arrayOf(id.value)
                                    )
                                    var existeEnHistorial = false
                                    if (historialCursor.moveToFirst()) {
                                        existeEnHistorial = historialCursor.getInt(0) > 0
                                    }
                                    historialCursor.close()

                                    if (estadoSelec.value == "Atendida" && !existeEnHistorial) {
                                        // Insertar en historial
                                        val insertQuery = """
                                            INSERT INTO historial (id_cita, nombre_paciente, apellido_paciente, nombre_doc, apellido_doc, fecha_cita, hora_cita, estado_cita)
                                            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                                        """.trimIndent()
                                        baseDatos.execSQL(
                                            insertQuery, arrayOf(
                                                id.value,
                                                nombrePaciente.value,
                                                apellidoPaciente.value,
                                                nombreDoctor.value,
                                                apellidoDoctor.value,
                                                fecha.value,
                                                hora.value,
                                                "Atendida"
                                            )
                                        )
                                    } else if (estadoSelec.value == "Pendiente" && existeEnHistorial) {
                                        // Eliminar del historial si vuelve a estado "Pendiente"
                                        baseDatos.delete("historial", "id_cita = ?", arrayOf(id.value))
                                    }

                                    Toast.makeText(context, "Se modificó exitosamente", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, "No se encontró una cita con ese id", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                Toast.makeText(context, "Debe tener un id registrado para modificar", Toast.LENGTH_LONG).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                            e.printStackTrace()
                        }
                    })

                    Spacer(modifier = Modifier.width(2.dp))
                    ImageButton(imageResId = R.drawable.buscar, text = "Buscar", onClick = {showBuscarPopup3.value = true})
                    Spacer(modifier = Modifier.width(2.dp))
                    ImageButton(imageResId = R.drawable.guardar, text = "Guardar", onClick = {
                        try{

                            // Verificar que los campos obligatorios no estén vacíos
                            if (
                                hora.value.isBlank() ||
                                fecha.value.isBlank() ||
                                estadoSelec.value.isBlank() ||
                                idPaciente.value.isBlank() ||
                                nombrePaciente.value.isBlank() ||
                                apellidoPaciente.value.isBlank() ||
                                idDoctor.value.isBlank() ||
                                nombreDoctor.value.isBlank() ||
                                apellidoDoctor.value.isBlank()
                            ) {
                                Toast.makeText(context, "Por favor, complete todos los campos obligatorios", Toast.LENGTH_LONG).show()
                                return@ImageButton
                            }


                            // Si pasó ambas validaciones, insertamos
                            val registro = ContentValues()

                            // Datos personales
                            registro.put("fecha_cita", fecha.value)
                            registro.put("hora_cita", hora.value)
                            registro.put("estado_cita", estadoSelec.value)
                            registro.put("id_doctor", idDoctor.value)
                            registro.put("nombre_doc", nombreDoctor.value)
                            registro.put("apellido_doc", apellidoDoctor.value)
                            registro.put("id_paciente", idPaciente.value)
                            registro.put("nombre_paciente", nombrePaciente.value)
                            registro.put("apellido_paciente", apellidoPaciente.value)

                            val queryConflicto = """
                                SELECT * FROM citas 
                                WHERE fecha_cita = ? AND hora_cita = ?
                            """.trimIndent()
                            val cursorConflicto = baseDatos.rawQuery(queryConflicto, arrayOf(fecha.value, hora.value))

                            if (cursorConflicto.moveToFirst()) {
                                Toast.makeText(context, "Ya existe una cita registrada en esa fecha y hora.", Toast.LENGTH_LONG).show()
                                cursorConflicto.close()
                                return@ImageButton
                            }
                            cursorConflicto.close()


                            baseDatos.insert("citas", null, registro)
                            Toast.makeText(context, "Se insertó exitosamente", Toast.LENGTH_LONG).show()
                        }catch (e: Exception) {
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                            e.printStackTrace() // Útil si ves logs en Logcat
                        }

                    })
                    Spacer(modifier = Modifier.width(2.dp))
                    ImageButton(imageResId = R.drawable.eliminar, text = "Eliminar", onClick = {

                        try{

                            if (id.value.isNotEmpty()) {
                                val filasAfectadas = baseDatos.delete(
                                    "citas",
                                    "id_cita = ?",
                                    arrayOf(id.value)
                                )

                                if (filasAfectadas > 0) {
                                    Toast.makeText(context, "cita eliminada exitosamente", Toast.LENGTH_LONG).show()

                                    id.value = ""
                                    hora.value = ""
                                    fecha.value = ""
                                    estadoSelec.value = ""
                                    idDoctor.value = ""
                                    nombreDoctor.value = ""
                                    apellidoDoctor.value = ""
                                    idPaciente.value = ""
                                    nombrePaciente.value = ""
                                    apellidoPaciente.value = ""

                                } else {
                                    Toast.makeText(context, "No se encontró una cita con ese id", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                Toast.makeText(context, "Debe tener un id registrado para eliminar", Toast.LENGTH_LONG).show()
                            }

                        }catch (e: Exception) {
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                            e.printStackTrace() // Útil si ves logs en Logcat
                        }

                    })
                }
            }
        }

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 10.dp, vertical = 10.dp)
                .verticalScroll(scrollState)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){

            Text(
                "Registrar cita",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xff00a9b0)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Image(
                painter = painterResource(id = R.drawable.icono),
                contentDescription = "Imagen paciente",
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))


            SeccionDesplegable("Datos de la cita", expandido) {
                outLinedText(id, "Codigo", Modifier.fillMaxWidth(), false, keyDown = true, editable = false, activo = true)
                Spacer(modifier = Modifier.height(8.dp))
                HoraConTimePicker(hora, "Hora de la cita")
                Spacer(modifier = Modifier.height(8.dp))
                FechaConDatePicker(fecha, "Fecha de la cita")
                ComboBox(opciones = estado, seleccion = estadoSelec, "Estado", false)
            }

            Spacer(modifier = Modifier.height(16.dp))

            SeccionDesplegable("Datos del paciente", expandido2) {
                Button(onClick = {showBuscarPopup.value = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xff00a9b0) // Aquí el color hexadecimal
                    )
                ) {
                    Text("Agregar paciente", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                outLinedText(idPaciente, "Codigo paciente", Modifier.fillMaxWidth(), false, keyDown = true, editable = false, activo = true)
                Spacer(modifier = Modifier.height(8.dp))
                outLinedText(nombrePaciente, "Nombre paciente", Modifier.fillMaxWidth(), false, keyDown = true, editable = false, activo = true)
                Spacer(modifier = Modifier.height(8.dp))
                outLinedText(apellidoPaciente, "Apellido paciente", Modifier.fillMaxWidth(), false, keyDown = true, editable = false, activo = true)
            }

            Spacer(modifier = Modifier.height(16.dp))

            SeccionDesplegable("Datos del doctor", expandido3) {
                Button(onClick = {showBuscarPopup2.value = true},
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xff00a9b0) // Aquí el color hexadecimal
                    )
                ) {
                    Text("Agregar doctor", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                outLinedText(idDoctor, "Codigo doctor", Modifier.fillMaxWidth(), false, keyDown = true, editable = false, activo = true)
                Spacer(modifier = Modifier.height(8.dp))
                outLinedText(nombreDoctor, "Nombre doctor", Modifier.fillMaxWidth(), false, keyDown = true, editable = false, activo = true)
                Spacer(modifier = Modifier.height(8.dp))
                outLinedText(apellidoDoctor, "Apellido doctor", Modifier.fillMaxWidth(), false, keyDown = true, editable = false, activo = true)
            }


        }
    }


    //Dialog para buscar paciente
    if (showBuscarPopup.value) {
        Dialog(onDismissRequest = { showBuscarPopup.value = false }) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Text("Buscar paciente", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                outLinedText(textoBusqueda, "Buscar por cédula o nombre", Modifier.fillMaxWidth(), false, true)

                Spacer(modifier = Modifier.height(4.dp))

                Button(onClick = {
                    val db = baseDatos
                    val query = """
                    SELECT * FROM pacientes
                    WHERE cedula LIKE ? OR nombre LIKE ?
                """.trimIndent()

                    val cursor = db.rawQuery(query, arrayOf("%${textoBusqueda.value}%", "%${textoBusqueda.value}%"))

                    val resultados = mutableListOf<Map<String, String>>()

                    if (cursor.moveToFirst()) {
                        do {
                            val paciente = mutableMapOf<String, String>()
                            for (i in 0 until cursor.columnCount) {
                                paciente[cursor.getColumnName(i)] = cursor.getString(i) ?: ""
                            }
                            resultados.add(paciente)
                        } while (cursor.moveToNext())
                    }
                    cursor.close()
                    resultadosBusqueda.value = resultados

                },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xff00a9b0) // Aquí el color hexadecimal
                    )) {
                    Text("Buscar")
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (resultadosBusqueda.value.isNotEmpty()) {
                    Column(modifier = Modifier.heightIn(max = 300.dp)) {
                        resultadosBusqueda.value.forEach { paciente ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        // Rellenar campos
                                        idPaciente.value = paciente["id"] ?: ""
                                        nombrePaciente.value = paciente["nombre"] ?: ""
                                        apellidoPaciente.value = paciente["apellido"] ?: ""

                                        showBuscarPopup.value = false

                                    }
                                    .background(Color(0xFFE0F7FA))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "${paciente["nombre"]} ${paciente["apellido"]} \n ${paciente["cedula"]}",
                                    modifier = Modifier.weight(1f)

                                )
                            }
                        }
                    }
                } else {
                    Text("Sin resultados", color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(onClick = { showBuscarPopup.value = false }, colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red // Aquí el color hexadecimal
                )) {
                    Text("Cerrar")
                }
            }
        }
    }

    //Dialog para buscar doctor
    if (showBuscarPopup2.value) {
        Dialog(onDismissRequest = { showBuscarPopup2.value = false }) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)


            ) {
                Text("Buscar doctor", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                outLinedText(textoBusqueda2, "Buscar por cédula o nombre", Modifier.fillMaxWidth(), false, true)

                Spacer(modifier = Modifier.height(4.dp))

                Button(onClick = {
                    val db = baseDatos
                    val query = """
                    SELECT * FROM doctores
                    WHERE cedula_doc LIKE ? OR nombre_doc LIKE ?
                """.trimIndent()

                    val cursor = db.rawQuery(query, arrayOf("%${textoBusqueda2.value}%", "%${textoBusqueda2.value}%"))

                    val resultados2 = mutableListOf<Map<String, String>>()

                    if (cursor.moveToFirst()) {
                        do {
                            val doctor = mutableMapOf<String, String>()
                            for (i in 0 until cursor.columnCount) {
                                doctor[cursor.getColumnName(i)] = cursor.getString(i) ?: ""
                            }
                            resultados2.add(doctor)
                        } while (cursor.moveToNext())
                    }
                    cursor.close()
                    resultadosBusqueda2.value = resultados2

                },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xff00a9b0) // Aquí el color hexadecimal
                    )) {
                    Text("Buscar")
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (resultadosBusqueda2.value.isNotEmpty()) {
                    Column(modifier = Modifier.heightIn(max = 300.dp)) {
                        resultadosBusqueda2.value.forEach { doctor ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        // Rellenar campos
                                        idDoctor.value = doctor["id_doctor"] ?: ""
                                        nombreDoctor.value = doctor["nombre_doc"] ?: ""
                                        apellidoDoctor.value = doctor["apellido_doc"] ?: ""

                                        showBuscarPopup2.value = false

                                    }
                                    .background(Color(0xFFE0F7FA))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "${doctor["nombre_doc"]} ${doctor["apellido_doc"]} \n ${doctor["cedula_doc"]}",
                                    modifier = Modifier.weight(1f)

                                )
                            }
                        }
                    }
                } else {
                    Text("Sin resultados", color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(onClick = { showBuscarPopup2.value = false }, colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red // Aquí el color hexadecimal
                )) {
                    Text("Cerrar")
                }
            }
        }
    }

    //Dialog para buscar doctor
    if (showBuscarPopup3.value) {
        Dialog(onDismissRequest = { showBuscarPopup2.value = false }) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()

                    .background(Color.White)
                    .padding(16.dp)


            ) {
                Text("Buscar cita", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                outLinedText(textoBusqueda3, "Buscar por hora o nombre del doctor", Modifier.fillMaxWidth(), false, true)

                Spacer(modifier = Modifier.height(4.dp))

                Button(onClick = {
                    val db = baseDatos
                    val query = """
                    SELECT * FROM citas
                    WHERE hora_cita LIKE ? OR nombre_doc LIKE ?
                """.trimIndent()

                    val cursor = db.rawQuery(query, arrayOf("%${textoBusqueda3.value}%", "%${textoBusqueda3.value}%"))

                    val resultados2 = mutableListOf<Map<String, String>>()

                    if (cursor.moveToFirst()) {
                        do {
                            val cita = mutableMapOf<String, String>()
                            for (i in 0 until cursor.columnCount) {
                                cita[cursor.getColumnName(i)] = cursor.getString(i) ?: ""
                            }
                            resultados2.add(cita)
                        } while (cursor.moveToNext())
                    }
                    cursor.close()
                    resultadosBusqueda3.value = resultados2

                },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xff00a9b0) // Aquí el color hexadecimal
                    )) {
                    Text("Buscar")
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (resultadosBusqueda3.value.isNotEmpty()) {
                    Column(modifier = Modifier.heightIn(max = 300.dp).verticalScroll(scrollState2)) {
                        resultadosBusqueda3.value.forEach { cita ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        // Rellenar campos
                                        id.value = cita["id_cita"] ?:""
                                        hora.value = cita["hora_cita"] ?:""
                                        fecha.value = cita["fecha_cita"] ?:""
                                        estadoSelec.value = cita["estado_cita"] ?:""

                                        //doctor
                                        idDoctor.value = cita["id_doctor"] ?: ""
                                        nombreDoctor.value = cita["nombre_doc"] ?: ""
                                        apellidoDoctor.value = cita["apellido_doc"] ?: ""

                                        //paciente
                                        idPaciente.value = cita["id_paciente"] ?: ""
                                        nombrePaciente.value = cita["nombre_paciente"] ?: ""
                                        apellidoPaciente.value = cita["apellido_paciente"] ?: ""

                                        showBuscarPopup3.value = false

                                    }
                                    .background(Color(0xFFE0F7FA))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = """ 
                                        Id: ${cita["id_cita"]}
                                        Hora: ${cita["hora_cita"]}
                                        Paciente ${cita["nombre_paciente"]} ${cita["apellido_paciente"]} 
                                        Doctor: ${cita["nombre_doc"]}
                                    """.trimIndent(),
                                    modifier = Modifier.weight(1f)

                                )
                            }
                        }
                    }
                } else {
                    Text("Sin resultados", color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(onClick = { showBuscarPopup3.value = false }, colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red // Aquí el color hexadecimal
                )) {
                    Text("Cerrar")
                }
            }
        }
    }


}