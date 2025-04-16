package com.example.medilink_compose.Pantallas

import android.content.ContentValues
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberScrollableState
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
import androidx.compose.material3.MaterialTheme
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
import androidx.wear.compose.material3.ImageButton
import com.example.medilink_compose.BD_Files.SQLiteHelper
import com.example.medilink_compose.ComboBox
import com.example.medilink_compose.FechaConDatePicker
import com.example.medilink_compose.HoraConTimePicker
import com.example.medilink_compose.ImageButton
import com.example.medilink_compose.R
import com.example.medilink_compose.SeccionDesplegable
import com.example.medilink_compose.databaseVersion
import com.example.medilink_compose.outLinedText

@Composable
fun RegistrarDoctorActivity(modifier: Modifier, navController: NavHostController) {

    //region declaracion de variables

    val context = LocalContext.current
    val dbHelper = SQLiteHelper(context, "MediLink.db", null, databaseVersion)
    val baseDatos = dbHelper.writableDatabase

    val scrollState = rememberScrollState()
    val expandido = remember { mutableStateOf(false) }
    val expandido2 = remember { mutableStateOf(false) }
    val expandido3 = remember { mutableStateOf(false) }
    val expandido4 = remember { mutableStateOf(false) }


    //Datos personales
    val id = remember { mutableStateOf("") }
    val nombre = remember { mutableStateOf("") }
    val apellido = remember { mutableStateOf("") }
    val fecha = remember { mutableStateOf("") }
    val estadoCivil = listOf("Soltero/a", "Casado/a", "Divorciado/a", "Union libre")
    val estadoCivilSelec = remember { mutableStateOf("") }
    val cedula = remember { mutableStateOf("") }
    val sexo = listOf("Masculino", "Femenino")
    val sexoSelec = remember { mutableStateOf("") }
    val estado = listOf("Activo", "Inactivo")
    val estadoSelec = remember { mutableStateOf("") }

    //Datos de contacto
    val telefono = remember { mutableStateOf("") }
    val celular = remember { mutableStateOf("") }
    val correo = remember { mutableStateOf("") }
    val direccion = remember { mutableStateOf("") }

    //Datos profesionales
    val matricula = remember { mutableStateOf("") }
    val horaInicio = remember { mutableStateOf("") }
    val horaFin = remember { mutableStateOf("") }

    //Datos de contacto adicional
    val nombreContacto = remember { mutableStateOf("") }
    val cedulaContacto = remember { mutableStateOf("") }
    val calularContacto = remember { mutableStateOf("") }

    //metodos popup
    val showBuscarPopup = remember { mutableStateOf(false) }
    val textoBusqueda = remember { mutableStateOf("") }
    val resultadosBusqueda = remember { mutableStateOf(listOf<Map<String, String>>()) }

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

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ImageButton(imageResId = R.drawable.agregar, text = "Nuevo", onClick ={

                        id.value = ""
                        nombre.value = ""
                        apellido.value = ""
                        fecha.value =  ""
                        sexoSelec.value = ""
                        estadoCivilSelec.value = ""
                        cedula.value = ""

                        celular.value = ""
                        telefono.value = ""
                        correo.value = ""
                        direccion.value = ""

                        nombreContacto.value = ""
                        cedulaContacto.value = ""
                        calularContacto.value = ""

                        matricula.value = ""
                        horaInicio.value = ""
                        horaFin.value = ""

                    })
                    Spacer(modifier = Modifier.width(2.dp))
                    ImageButton(imageResId = R.drawable.modificar, text = "Modificar", onClick ={

                        val registro = ContentValues()

                        // Datos personales
                        registro.put("nombre_doc", nombre.value)
                        registro.put("apellido_doc", apellido.value)
                        registro.put("fechaNacimiento_doc", fecha.value)
                        registro.put("estadoCivil_doc", estadoCivilSelec.value)
                        registro.put("sexo_doc", sexoSelec.value)
                        registro.put("cedula_doc", cedula.value)
                        registro.put("estado_doc", estadoSelec.value)

                        // Datos de contacto
                        registro.put("celular_doc", celular.value)
                        registro.put("telefono_doc", telefono.value)
                        registro.put("correo_doc", correo.value)
                        registro.put("direccion_doc", direccion.value)

                        // Contacto adicional
                        registro.put("nombreContacto_doc", nombreContacto.value)
                        registro.put("cedulaContacto_doc", cedulaContacto.value)
                        registro.put("celularContacto_doc", calularContacto.value)

                        // Datos médicos
                        registro.put("matricula_doc", matricula.value)
                        registro.put("horaEntrada_doc", horaInicio.value)
                        registro.put("horaSalida_doc", horaInicio.value)

                        // Validar cédula antes de modificar
                        if (id.value.isNotEmpty()) {
                            val cantidad = baseDatos.update(
                                "doctores",
                                registro,
                                "id_doctor = ?",
                                arrayOf(id.value)
                            )

                            if (cantidad > 0) {
                                Toast.makeText(context, "Se modificó exitosamente", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context, "No se encontró un doctor con ese id", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(context, "el registro debe tener un id para modificar", Toast.LENGTH_LONG).show()
                        }

                    })
                    Spacer(modifier = Modifier.width(2.dp))
                    ImageButton(imageResId = R.drawable.buscar, text = "Buscar", onClick ={

                        showBuscarPopup.value = true

                    })
                    Spacer(modifier = Modifier.width(2.dp))
                    ImageButton(imageResId = R.drawable.guardar, text = "Guardar", onClick = {

                    try{
                        // Verificar que los campos obligatorios no estén vacíos
                        if (
                            nombre.value.isBlank() ||
                            apellido.value.isBlank() ||
                            fecha.value.isBlank() ||
                            sexoSelec.value.isBlank() ||
                            cedula.value.isBlank() ||
                            celular.value.isBlank() ||
                            correo.value.isBlank() ||
                            direccion.value.isBlank() ||
                            estadoSelec.value.isBlank()
                        ) {
                            Toast.makeText(
                                context,
                                "Por favor, complete todos los campos obligatorios",
                                Toast.LENGTH_LONG
                            ).show()
                            return@ImageButton
                        }

                        // Verificar si ya existe un paciente con esa cédula
                        val cursor = baseDatos.rawQuery(
                            "SELECT * FROM doctores WHERE cedula_doc = ? OR id_doctor = ?",
                            arrayOf(cedula.value, id.value)
                        )

                        if (cursor.moveToFirst()) {
                            // Ya existe un paciente con esa cédula
                            Toast.makeText(
                                context,
                                "Ya existe un doctor con esa cédula o matricula",
                                Toast.LENGTH_LONG
                            ).show()
                            cursor.close()
                            return@ImageButton
                        }

                        cursor.close()

                        // Si pasó ambas validaciones, insertamos
                        val registro = ContentValues()

                        // Datos personales
                        registro.put("nombre_doc", nombre.value)
                        registro.put("apellido_doc", apellido.value)
                        registro.put("fechaNacimiento_doc", fecha.value)
                        registro.put("estadoCivil_doc", estadoCivilSelec.value)
                        registro.put("sexo_doc", sexoSelec.value)
                        registro.put("cedula_doc", cedula.value)
                        registro.put("estado_doc", estadoSelec.value)

                        // Datos de contacto
                        registro.put("celular_doc", celular.value)
                        registro.put("telefono_doc", telefono.value)
                        registro.put("correo_doc", correo.value)
                        registro.put("direccion_doc", direccion.value)

                        // Contacto adicional
                        registro.put("nombreContacto_doc", nombreContacto.value)
                        registro.put("cedulaContacto_doc", cedulaContacto.value)
                        registro.put("celularContacto_doc", calularContacto.value)

                        // Datos médicos
                        registro.put("matricula_doc", matricula.value)
                        registro.put("horaEntrada_doc", horaInicio.value)
                        registro.put("horaSalida_doc", horaInicio.value)

                        baseDatos.insert("doctores", null, registro)
                        Toast.makeText(context, "Se insertó exitosamente", Toast.LENGTH_LONG).show()

                    }catch (e: Exception) {
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                        e.printStackTrace() // Útil si ves logs en Logcat
                    }

                    })
                    Spacer(modifier = Modifier.width(2.dp))
                    ImageButton(imageResId = R.drawable.eliminar, text = "Eliminar", onClick ={

                        if (id.value.isNotEmpty()) {
                            val filasAfectadas = baseDatos.delete(
                                "doctores",
                                "id_doctor = ?",
                                arrayOf(id.value)
                            )

                            if (filasAfectadas > 0) {
                                Toast.makeText(context, "Doctor eliminado exitosamente", Toast.LENGTH_LONG).show()

                                // Limpiar los campos después de eliminar
                                id.value = ""
                                nombre.value = ""
                                apellido.value = ""
                                fecha.value = ""
                                sexoSelec.value = ""
                                estadoCivilSelec.value = ""
                                cedula.value = ""

                                celular.value = ""
                                telefono.value = ""
                                correo.value = ""
                                direccion.value = ""

                                nombreContacto.value = ""
                                cedulaContacto.value = ""
                                calularContacto.value = ""

                                matricula.value = ""
                                horaInicio.value = ""
                                horaFin.value = ""

                            } else {
                                Toast.makeText(context, "No se encontró un doctor con ese id", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(context, "Debe tener un id registrado para eliminar", Toast.LENGTH_LONG).show()
                        }

                    })
                }
            }

        }
    ){innerPadding ->
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
                "Registrar doctor",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xff00a9b0)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Image(
                painter = painterResource(id = R.drawable.doctor),
                contentDescription = "Imagen doctor",
                modifier = Modifier.size(150.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            SeccionDesplegable("Datos Personales", expandido) {
                outLinedText(id, "Codigo", Modifier.fillMaxWidth(), false, keyDown = true, editable = false, activo = true)
                Spacer(modifier = Modifier.height(8.dp))
                outLinedText(nombre, "Nombre", Modifier.fillMaxWidth(), false, keyDown = true)
                Spacer(modifier = Modifier.height(8.dp))
                outLinedText(apellido, "Apellido", Modifier.fillMaxWidth(), false, keyDown = true)
                Spacer(modifier = Modifier.height(8.dp))
                FechaConDatePicker(fecha, label = "Fecha de nacimiento")
                Spacer(modifier = Modifier.height(8.dp))
                ComboBox(estadoCivil, estadoCivilSelec, "Estado civil", false)
                Spacer(modifier = Modifier.height(8.dp))
                outLinedText(cedula, "Cedula", Modifier.fillMaxWidth(), false, keyDown = true)
                Spacer(modifier = Modifier.height(8.dp))
                ComboBox(sexo, sexoSelec, "Sexo", false)
                Spacer(modifier = Modifier.height(8.dp))
                ComboBox(estado, estadoSelec, "Estado", false)
            }

            Spacer(modifier = Modifier.height(8.dp))

            SeccionDesplegable("Datos de contacto", expandido2) {
                outLinedText(telefono, "Telefono", Modifier.fillMaxWidth(), false, keyDown = true)
                Spacer(modifier = Modifier.height(8.dp))
                outLinedText(celular, "Celular", Modifier.fillMaxWidth(), false, keyDown = true)
                Spacer(modifier = Modifier.height(8.dp))
                outLinedText(correo, "E-mail", Modifier.fillMaxWidth(), false, keyDown = true)
                Spacer(modifier = Modifier.height(8.dp))
                outLinedText(direccion, "Direccion", Modifier.fillMaxWidth(), false, keyDown = true)

            }

            Spacer(modifier = Modifier.height(8.dp))

            SeccionDesplegable("Datos profesionales", expandido3) {
                outLinedText(matricula, "Matricula profesional", Modifier.fillMaxWidth(), false, keyDown = true)
                Spacer(modifier = Modifier.height(8.dp))
                HoraConTimePicker(horaInicio, "Hora de entrada")
                Spacer(modifier = Modifier.height(8.dp))
                HoraConTimePicker(horaFin,  "Hora de salida")
            }

            Spacer(modifier = Modifier.height(8.dp))

            SeccionDesplegable("Datos de contacto adicional", expandido4) {
                outLinedText(nombreContacto, "Nombre completo", Modifier.fillMaxWidth(), false, keyDown = true)
                Spacer(modifier = Modifier.height(8.dp))
                outLinedText(cedulaContacto, "Cédula", Modifier.fillMaxWidth(), false, true)
                Spacer(modifier = Modifier.height(8.dp))
                outLinedText(calularContacto, "Celular", Modifier.fillMaxWidth(), false, true)
            }

        }

    }

    //Dialog para buscar doctor
    if (showBuscarPopup.value) {
        Dialog(onDismissRequest = { showBuscarPopup.value = false }) {
            val colors = MaterialTheme.colorScheme

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .background(colors.background, shape = MaterialTheme.shapes.medium)
                    .padding(16.dp)
            ) {
                Text(
                    "Buscar doctor",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))

                outLinedText(
                    textoBusqueda,
                    "Buscar por cédula o nombre",
                    Modifier.fillMaxWidth(),
                    false,
                    true
                )

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = {
                        val db = baseDatos
                        val query = """
                        SELECT * FROM doctores
                        WHERE cedula_doc LIKE ? OR nombre_doc LIKE ?
                    """.trimIndent()

                        val cursor = db.rawQuery(
                            query,
                            arrayOf("%${textoBusqueda.value}%", "%${textoBusqueda.value}%")
                        )

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
                        resultadosBusqueda.value = resultados2
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary
                    )
                ) {
                    Text("Buscar", color = colors.onPrimary)
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (resultadosBusqueda.value.isNotEmpty()) {
                    Column(modifier = Modifier.heightIn(max = 300.dp)) {
                        resultadosBusqueda.value.forEach { doctor ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        // Rellenar campos
                                        id.value = doctor["id_doctor"] ?: ""
                                        nombre.value = doctor["nombre_doc"] ?: ""
                                        apellido.value = doctor["apellido_doc"] ?: ""
                                        fecha.value = doctor["fechaNacimiento_doc"] ?: ""
                                        sexoSelec.value = doctor["sexo_doc"] ?: ""
                                        estadoCivilSelec.value = doctor["estadoCivil_doc"] ?: ""
                                        estadoSelec.value = doctor["estado_doc"] ?: ""
                                        cedula.value = doctor["cedula_doc"] ?: ""

                                        celular.value = doctor["celular_doc"] ?: ""
                                        telefono.value = doctor["telefono_doc"] ?: ""
                                        correo.value = doctor["correo_doc"] ?: ""
                                        direccion.value = doctor["direccion_doc"] ?: ""

                                        nombreContacto.value = doctor["nombreContacto_doc"] ?: ""
                                        cedulaContacto.value = doctor["cedulaContacto_doc"] ?: ""
                                        calularContacto.value = doctor["celularContacto_doc"] ?: ""

                                        matricula.value = doctor["matricula_doc"] ?: ""
                                        horaInicio.value = doctor["horaEntrada_doc"] ?: ""
                                        horaFin.value = doctor["horaSalida_doc"] ?: ""

                                        showBuscarPopup.value = false
                                    }
                                    .background(colors.surfaceVariant)
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "${doctor["nombre_doc"]} ${doctor["apellido_doc"]} \n ${doctor["cedula_doc"]}",
                                    modifier = Modifier.weight(1f),
                                    color = colors.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    Text(
                        "Sin resultados",
                        color = colors.onBackground.copy(alpha = 0.5f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { showBuscarPopup.value = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.error
                    )
                ) {
                    Text("Cerrar", color = colors.onError)
                }
            }
        }
    }
}