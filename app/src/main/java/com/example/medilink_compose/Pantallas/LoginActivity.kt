package com.example.medilink_compose.Pantallas

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.navigation.NavHostController
import com.example.medilink_compose.BD_Files.SQLiteHelper
import com.example.medilink_compose.BD_Files.UsuarioViewModel
import com.example.medilink_compose.R
import com.example.medilink_compose.databaseVersion


@Composable
fun LoginActivity(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    usuarioViewModel: UsuarioViewModel
){

    var usuario by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    val context = LocalContext.current
    var passwordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()


    Column (
        modifier = Modifier.fillMaxSize().padding(32.dp).verticalScroll(scrollState),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Text("MediLink", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Color(0xff00a9b0))

        Spacer(modifier = Modifier.height(8.dp))

        Image(
            painter = painterResource(id= R.drawable.icono),
            contentDescription= "Login Image",
            modifier = Modifier.size(150.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Accede a tu cuenta", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        //campos para ingresar datos
        OutlinedTextField(
            value = usuario,
            onValueChange = {usuario = it},
            label = {Text("Usuario")},
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier.fillMaxWidth()

        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = pass,
            onValueChange = {pass = it},
            label = {Text("Contraseña")},
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            ),
            trailingIcon = {
                val image = if (passwordVisible) R.drawable.visibility else R.drawable.visibility_off
                val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(painter = painterResource(image), contentDescription = description)
                }
            },
            modifier = Modifier.fillMaxWidth()

        )

        Spacer(modifier = Modifier.height(16.dp))

        Row{
            Button(onClick = {login(context, usuario, pass, navController, usuarioViewModel)},
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xff00a9b0) // Aquí el color hexadecimal
                )
            ) {
                Text("Iniciar sesion", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("¿Olvidaste tu contraseña?", modifier = Modifier.clickable {  })

        Spacer(modifier = Modifier.height(32.dp))

        Text("Registrate", modifier = Modifier.clickable {
            navController.navigate("usuario")
        })

        Spacer(modifier = Modifier.height(64.dp))

        Text("© 2025 MediLink", fontSize = 12.sp, color = Color.LightGray)
    }

}


@SuppressLint("Range")
fun login(
    context: Context,
    usuario: String,
    pass: String,
    navController: NavHostController,
    usuarioViewModel: UsuarioViewModel
) {
    val dbHelper = SQLiteHelper(context, "MediLink.db", null, databaseVersion)
    val baseDatos = dbHelper.writableDatabase

    val tabla = "usuarios"
    val columnas = arrayOf("usuario", "clave")
    val seleccion = "usuario = ? AND clave = ?"
    val seleccionArgs = arrayOf(usuario, pass)

    val cursor = baseDatos.query(
        tabla,
        columnas,
        seleccion,
        seleccionArgs,
        null,
        null,
        null
    )

    var valorEncontrado = false

    if (cursor.moveToFirst()) {
        val valorColumna = cursor.getString(cursor.getColumnIndex("usuario"))
        val valorColumna2 = cursor.getString(cursor.getColumnIndex("clave"))
        if (valorColumna == usuario && valorColumna2 == pass) {
            valorEncontrado = true
        }
    }

    cursor.close()

    if (valorEncontrado) {
        usuarioViewModel.setUsuario(usuario)
        navController.navigate("Menu")
    } else {
        Toast.makeText(context, "Usuario o contraseña incorrectos", Toast.LENGTH_LONG).show()
    }
}

