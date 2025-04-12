package com.example.medilink_compose.Pantallas

import android.content.ContentValues
import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.medilink_compose.ImageButton
import com.example.medilink_compose.OutlinedPasswordField
import com.example.medilink_compose.R
import com.example.medilink_compose.databaseVersion
import com.example.medilink_compose.outLinedText

@Composable
fun UsuarioActivity(modifier: Modifier = Modifier, navController: NavHostController) {

    val scrollState = rememberScrollState()

    val usuario = remember { mutableStateOf("") }
    val clave = remember { mutableStateOf("") }
    val rol = listOf("Doctor", "Administrador", "Paciente")
    val rolSelec = remember { mutableStateOf("") }

    val context = LocalContext.current
    val dbHelper = SQLiteHelper(context, "MediLink.db", null, databaseVersion)
    val baseDatos = dbHelper.writableDatabase

    //metodos popup
    val showBuscarPopup = remember { mutableStateOf(false) }
    val textoBusqueda = remember { mutableStateOf("") }
    val resultadosBusqueda = remember { mutableStateOf(listOf<Map<String, String>>()) }


    Scaffold(
        topBar = {

            Row (Modifier.statusBarsPadding(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 20.dp)){
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
                        usuario.value = ""
                        clave.value = ""
                        rolSelec.value = ""
                    })
                    Spacer(modifier = Modifier.width(2.dp))
                    ImageButton(imageResId = R.drawable.modificar, text = "Modificar", onClick = {

                        val registro = ContentValues()

                        // Datos personales
                        registro.put("usuario", usuario.value)
                        registro.put("clave", clave.value)
                        registro.put("rol", rolSelec.value)

                        // Validar cédula antes de modificar
                        if (usuario.value.isNotEmpty()) {
                            val cantidad = baseDatos.update(
                                "usuarios",
                                registro,
                                "usuario = ?",
                                arrayOf(usuario.value)
                            )

                            if (cantidad > 0) {
                                Toast.makeText(context, "Se modificó exitosamente", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context, "No se encontró un paciente con ese usuario", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(context, "Debe ingresar un usuario para modificar", Toast.LENGTH_LONG).show()
                        }

                        })
                    Spacer(modifier = Modifier.width(2.dp))
                    ImageButton(imageResId = R.drawable.buscar, text = "Buscar", onClick = {

                        showBuscarPopup.value = true

                    })
                    Spacer(modifier = Modifier.width(2.dp))
                    ImageButton(imageResId = R.drawable.guardar, text = "Guardar", onClick = {
                        try {
                            if (
                                usuario.value.isBlank() ||
                                clave.value.isBlank() ||
                                rolSelec.value.isBlank()
                            ) {
                                Toast.makeText(context, "Por favor, complete todos los campos obligatorios", Toast.LENGTH_LONG).show()
                                return@ImageButton
                            } else {
                                val cursor = baseDatos.rawQuery(
                                    "SELECT * FROM usuarios WHERE usuario = ?",
                                    arrayOf(usuario.value)
                                )

                                if (cursor.moveToFirst()) {
                                    Toast.makeText(context, "Ya existe un registro con ese usuario", Toast.LENGTH_LONG).show()
                                    cursor.close()
                                    return@ImageButton
                                }

                                cursor.close()

                                val registro = ContentValues()
                                registro.put("usuario", usuario.value)
                                registro.put("clave", clave.value)
                                registro.put("rol", rolSelec.value)

                                baseDatos.insert("usuarios", null, registro)
                                Toast.makeText(context, "Se insertó exitosamente", Toast.LENGTH_LONG).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                            e.printStackTrace()
                        }
                    })
                    Spacer(modifier = Modifier.width(2.dp))
                    ImageButton(imageResId = R.drawable.eliminar, text = "Eliminar", onClick = {

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
        )
        {

            Text(
                "Registrar usuario",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xff00a9b0)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.drawable.usuario),
                contentDescription = "Imagen usuario",
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Card estilo GroupBox
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Row (horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically){
                    Text(
                        text = "Entrada de datos",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                }
                Column(
                    modifier = Modifier
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    outLinedText(usuario, "Nombre de usuario", Modifier.fillMaxWidth(), false, true)
                    OutlinedPasswordField(clave, "Contraseña", Modifier.fillMaxWidth(), true)
                    ComboBox(rol, rolSelec, "Rol", false)
                }
            }// fin card

        }

    }// fin main content

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
                Text("Buscar usuario", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                outLinedText(textoBusqueda, "Buscar por usuario o rol", Modifier.fillMaxWidth(), false, true)

                Spacer(modifier = Modifier.height(4.dp))

                Button(onClick = {
                    val db = baseDatos
                    val query = """
                    SELECT * FROM usuarios
                    WHERE usuario LIKE ? OR rol LIKE ?
                """.trimIndent()

                    val cursor = db.rawQuery(query, arrayOf("%${textoBusqueda.value}%", "%${textoBusqueda.value}%"))

                    val resultados = mutableListOf<Map<String, String>>()

                    if (cursor.moveToFirst()) {
                        do {
                            val usuario = mutableMapOf<String, String>()
                            for (i in 0 until cursor.columnCount) {
                                usuario[cursor.getColumnName(i)] = cursor.getString(i) ?: ""
                            }
                            resultados.add(usuario)
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
                        resultadosBusqueda.value.forEach { usuarios ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        // Rellenar campos
                                        usuario.value = usuarios["usuario"] ?: ""
                                        clave.value = usuarios["clave"] ?: ""
                                        rolSelec.value = usuarios["rol"] ?: ""

                                        showBuscarPopup.value = false

                                    }
                                    .background(Color(0xFFE0F7FA))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "${usuarios["usuario"]} \n ${usuarios["rol"]}",
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

}