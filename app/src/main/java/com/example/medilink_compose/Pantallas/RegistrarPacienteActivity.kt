package com.example.medilink_compose.Pantallas

import android.annotation.SuppressLint
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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.medilink_compose.BD_Files.SQLiteHelper
import com.example.medilink_compose.ComboBox
import com.example.medilink_compose.FechaConDatePicker
import com.example.medilink_compose.ImageButton
import com.example.medilink_compose.R
import com.example.medilink_compose.SeccionDesplegable
import com.example.medilink_compose.databaseVersion
import com.example.medilink_compose.outLinedText
import com.example.medilink_compose.databaseVersion

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RegistrarPacienteActivity(modifier: Modifier = Modifier, navController: NavHostController) {

    //region Declaracion de variables
    val context = LocalContext.current
    val dbHelper = SQLiteHelper(context, "MediLink.db", null, databaseVersion)
    val baseDatos = dbHelper.writableDatabase

    // Estado de secciones
    var expandido = remember { mutableStateOf(false) }
    val expandido2 = remember { mutableStateOf(false) }
    val expandido3 = remember { mutableStateOf(false) }
    val expandido4 = remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    // Datos personales
    val id = remember {mutableStateOf("")}
    val nombre = remember { mutableStateOf("") }
    val apellido = remember { mutableStateOf("") }
    val fecha = remember { mutableStateOf("") }
    val sexo = listOf("Masculino", "Femenino")
    val sexoSelec = remember { mutableStateOf("") }
    val cedula = remember { mutableStateOf("") }
    val estadoCivil = listOf("Soltero/a", "Casado/a", "Divorciado/a", "Union libre")
    val estadoCivilSelec = remember { mutableStateOf("") }
    val estado = listOf("Activo", "Inactivo")
    val estadoSelec = remember { mutableStateOf("") }

    // Datos de contacto
    val celular = remember { mutableStateOf("") }
    val telefono = remember { mutableStateOf("") }
    val correo = remember { mutableStateOf("") }
    val direccion = remember { mutableStateOf("") }

    // Contacto adicional
    val nombreContacto = remember { mutableStateOf("") }
    val celularContacto = remember { mutableStateOf("") }
    val cedulaContacto = remember { mutableStateOf("") }

    // Datos médicos
    val sangre = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-", "No sabe su tipo de sangre")
    val sangreSelec = remember { mutableStateOf("") }
    val condicion = remember { mutableStateOf("") }

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

                Row (horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier =  Modifier.fillMaxWidth()
                ) {

                    ImageButton(imageResId = R.drawable.agregar, text = "Nuevo", onClick = {
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
                        celularContacto.value = ""

                        condicion.value = ""
                        sangreSelec.value = ""

                    })
                    Spacer(modifier = Modifier.width(2.dp))
                    ImageButton(imageResId = R.drawable.modificar, text = "Modificar", onClick = {
                        val registro = ContentValues()

                        // Datos personales
                        registro.put("nombre", nombre.value)
                        registro.put("apellido", apellido.value)
                        registro.put("fechaNacimiento", fecha.value)
                        registro.put("sexo", sexoSelec.value)
                        registro.put("cedula", cedula.value)
                        registro.put("estado", estadoSelec.value)
                        registro.put("estadoCivil", estadoCivilSelec.value)

                        // Datos de contacto
                        registro.put("celular", celular.value)
                        registro.put("telefono", telefono.value)
                        registro.put("correo", correo.value)
                        registro.put("direccion", direccion.value)

                        // Contacto adicional
                        registro.put("nombreContacto", nombreContacto.value)
                        registro.put("cedulaContacto", cedulaContacto.value)
                        registro.put("celularContacto", celularContacto.value)

                        // Datos médicos
                        registro.put("condicionMedica", condicion.value)
                        registro.put("tipoSangre", sangreSelec.value)

                        // Validar cédula antes de modificar
                        if (id.value.isNotEmpty()) {
                            val cantidad = baseDatos.update(
                                "pacientes",
                                registro,
                                "id = ?",
                                arrayOf(id.value)
                            )

                            if (cantidad > 0) {
                                Toast.makeText(context, "Se modificó exitosamente", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context, "No se encontró un paciente con ese id", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(context, "Debe tener un id registrado para modificar", Toast.LENGTH_LONG).show()
                        }
                    })
                    Spacer(modifier = Modifier.width(2.dp))
                    ImageButton(imageResId = R.drawable.buscar, text = "Buscar", onClick = {

                        showBuscarPopup.value = true
                    })
                    Spacer(modifier = Modifier.width(2.dp))
                    ImageButton(imageResId = R.drawable.guardar, text = "Guardar", onClick = {

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
                            Toast.makeText(context, "Por favor, complete todos los campos obligatorios", Toast.LENGTH_LONG).show()
                            return@ImageButton
                        }

                        // Verificar si ya existe un paciente con esa cédula
                        val cursor = baseDatos.rawQuery(
                            "SELECT * FROM pacientes WHERE cedula = ? OR id = ?",
                            arrayOf(cedula.value, id.value)
                        )

                        if (cursor.moveToFirst()) {
                            // Ya existe un paciente con esa cédula
                            Toast.makeText(context, "Ya existe un paciente con esa cédula", Toast.LENGTH_LONG).show()
                            cursor.close()
                            return@ImageButton
                        }

                        cursor.close()

                        // Si pasó ambas validaciones, insertamos
                        val registro = ContentValues()

                        // Datos personales
                        registro.put("nombre", nombre.value)
                        registro.put("apellido", apellido.value)
                        registro.put("fechaNacimiento", fecha.value)
                        registro.put("estadoCivil", estadoCivilSelec.value)
                        registro.put("sexo", sexoSelec.value)
                        registro.put("cedula", cedula.value)
                        registro.put("estado", estadoSelec.value)

                        // Datos de contacto
                        registro.put("celular", celular.value)
                        registro.put("telefono", telefono.value)
                        registro.put("correo", correo.value)
                        registro.put("direccion", direccion.value)

                        // Contacto adicional
                        registro.put("nombreContacto", nombreContacto.value)
                        registro.put("cedulaContacto", cedulaContacto.value)
                        registro.put("celularContacto", celularContacto.value)

                        // Datos médicos
                        registro.put("condicionMedica", condicion.value)
                        registro.put("tipoSangre", sangreSelec.value)

                        baseDatos.insert("pacientes", null, registro)
                        Toast.makeText(context, "Se insertó exitosamente", Toast.LENGTH_LONG).show()


                    })
                    Spacer(modifier = Modifier.width(2.dp))
                    ImageButton(imageResId = R.drawable.eliminar, text = "Eliminar", onClick = {

                        if (id.value.isNotEmpty()) {
                            val filasAfectadas = baseDatos.delete(
                                "pacientes",
                                "id = ?",
                                arrayOf(id.value)
                            )

                            if (filasAfectadas > 0) {
                                Toast.makeText(context, "Paciente eliminado exitosamente", Toast.LENGTH_LONG).show()

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
                                celularContacto.value = ""

                                condicion.value = ""
                                sangreSelec.value = ""

                            } else {
                                Toast.makeText(context, "No se encontró un paciente con ese id", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(context, "Debe tener un id registrado para eliminar", Toast.LENGTH_LONG).show()
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
        )
        {
            Text(
                "Registrar paciente",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xff00a9b0)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Image(
                painter = painterResource(id = R.drawable.paciente),
                contentDescription = "Imagen paciente",
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            SeccionDesplegable("Datos personales", expandido) {
                outLinedText(id, "Codigo", Modifier.fillMaxWidth(), false, keyDown = true, editable = false, activo = true)
                Spacer(modifier = Modifier.height(8.dp))
                outLinedText(nombre, "Nombre", Modifier.fillMaxWidth(), false, keyDown = true)
                Spacer(modifier = Modifier.height(8.dp))
                outLinedText(apellido, "Apellido", Modifier.fillMaxWidth(), false, true)
                Spacer(modifier = Modifier.height(8.dp))
                FechaConDatePicker(fecha, "Fecha de nacimiento")
                Spacer(modifier = Modifier.height(8.dp))
                ComboBox(opciones = sexo, seleccion = sexoSelec, "Sexo", true)
                Spacer(modifier = Modifier.height(8.dp))
                outLinedText(cedula, "Cédula", Modifier.fillMaxWidth(), false, true)
                Spacer(modifier = Modifier.height(8.dp))
                ComboBox(opciones = estadoCivil, seleccion = estadoCivilSelec, "Estado civil", false)
                Spacer(modifier = Modifier.height(8.dp))
                ComboBox(opciones = estado, seleccion = estadoSelec, "Estado", false)
            }

            Spacer(modifier = Modifier.height(16.dp))

            SeccionDesplegable("Datos de contacto", expandido2) {
                outLinedText(celular, "Celular", Modifier.fillMaxWidth(), false, keyDown = true)
                Spacer(modifier = Modifier.height(8.dp))
                outLinedText(telefono, "Teléfono", Modifier.fillMaxWidth(), false, true)
                Spacer(modifier = Modifier.height(8.dp))
                outLinedText(correo, "Correo", Modifier.fillMaxWidth(), false, true)
                Spacer(modifier = Modifier.height(8.dp))
                outLinedText(direccion, "Dirección", Modifier.fillMaxWidth(), false, false)
            }

            Spacer(modifier = Modifier.height(16.dp))

            SeccionDesplegable("Datos de contacto adicional", expandido3) {
                outLinedText(nombreContacto, "Nombre completo", Modifier.fillMaxWidth(), false, keyDown = true)
                Spacer(modifier = Modifier.height(8.dp))
                outLinedText(cedulaContacto, "Cédula", Modifier.fillMaxWidth(), false, true)
                Spacer(modifier = Modifier.height(8.dp))
                outLinedText(celularContacto, "Celular", Modifier.fillMaxWidth(), false, true)
            }

            Spacer(modifier = Modifier.height(16.dp))

            SeccionDesplegable("Datos médicos", expandido4) {
                outLinedText(condicion, "Condición médica", Modifier.fillMaxWidth(), false, keyDown = true)
                Spacer(modifier = Modifier.height(8.dp))
                ComboBox(sangre, sangreSelec, "Tipo de sangre", false)
            }
        }
    }// fin scaffold

    //Dialog para buscar paciente
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
                    "Buscar paciente",
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
                        containerColor = colors.primary
                    )
                ) {
                    Text("Buscar", color = colors.onPrimary)
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
                                        id.value = paciente["id"] ?: ""
                                        nombre.value = paciente["nombre"] ?: ""
                                        apellido.value = paciente["apellido"] ?: ""
                                        fecha.value = paciente["fechaNacimiento"] ?: ""
                                        sexoSelec.value = paciente["sexo"] ?: ""
                                        estadoCivilSelec.value = paciente["estadoCivil"] ?: ""
                                        cedula.value = paciente["cedula"] ?: ""
                                        estadoSelec.value = paciente["estado"] ?:""

                                        celular.value = paciente["celular"] ?: ""
                                        telefono.value = paciente["telefono"] ?: ""
                                        correo.value = paciente["correo"] ?: ""
                                        direccion.value = paciente["direccion"] ?: ""

                                        nombreContacto.value = paciente["nombreContacto"] ?: ""
                                        cedulaContacto.value = paciente["cedulaContacto"] ?: ""
                                        celularContacto.value = paciente["celularContacto"] ?: ""

                                        condicion.value = paciente["condicionMedica"] ?: ""
                                        sangreSelec.value = paciente["tipoSangre"] ?: ""

                                        showBuscarPopup.value = false

                                    }
                                    .background(colors.surfaceVariant)
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "${paciente["nombre"]} ${paciente["apellido"]} \n ${paciente["cedula"]}",
                                    modifier = Modifier.weight(1f),
                                    color = colors.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    Text("Sin resultados", color = colors.onBackground.copy(alpha = 0.5f))
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