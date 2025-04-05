package com.example.medilink_compose

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RegistrarPacienteActivity (modifier: Modifier = Modifier, navController: NavHostController) {

    val expandido = remember { mutableStateOf(false) }
    val expandido2 = remember { mutableStateOf(false) }
    val expandido3 = remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    //region declaracion de variables datos personales
    val nombre = remember { mutableStateOf("") }
    val apellido = remember { mutableStateOf("") }
    val fecha = remember { mutableStateOf("") }
    val sexo = listOf("Masculino", "Femenino")
    val sexoSelec = remember { mutableStateOf(sexo[0]) }
    val cedula = remember { mutableStateOf("") }
    val estadoCivil = listOf("Soltero/a", "Casado/a", "Divorciado/a", "Union libre")
    val estadoCivilSelec = remember { mutableStateOf(estadoCivil[0]) }
    //endregion

    //region declaracion de variables datos de contacto
    val celular = remember { mutableStateOf("") }
    val telefono = remember {mutableStateOf("")}
    val correo = remember {mutableStateOf("")}
    val direccion = remember {mutableStateOf("")}
    //endregion

    //region declaracion de variables datos de contacto adicional
    val nombreContacto = remember { mutableStateOf("") }
    val celularContacto = remember {mutableStateOf("")}
    val cedulaContacto = remember {mutableStateOf("")}
    //endregion


    Column (
        modifier = Modifier
            .padding(horizontal = 32.dp, vertical = 32.dp)
            .padding(WindowInsets.navigationBars.asPaddingValues())
            .verticalScroll(scrollState)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Text("Registrar paciente",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xff00a9b0)) //label registrar

        Spacer(modifier = Modifier.height(8.dp))

        Image(
            painter = painterResource(id= R.drawable.paciente),
            contentDescription= "reigstrar paciente Image",
            modifier = Modifier.size(100.dp)
        ) //imagen

        Spacer(modifier = Modifier.height(16.dp))

        SeccionDesplegable(
            titulo = "Datos personales",
            expandido = expandido
        ) {
            outLinedText(nombre, "Nombre", Modifier.fillMaxWidth(), false, keyDown = true)
            Spacer(modifier = Modifier.height(8.dp))
            outLinedText(apellido, "Apellido", Modifier.fillMaxWidth(), false, true)
            Spacer(modifier = Modifier.height(8.dp))
            outLinedText(fecha, "Fecha", Modifier.fillMaxWidth(), false, true)
            Spacer(modifier = Modifier.height(8.dp))
            ComboBox(opciones = sexo, seleccion = sexoSelec,"Sexo", true)
            Spacer(modifier = Modifier.height(8.dp))
            outLinedText(cedula, "Cedula", Modifier.fillMaxWidth(), false, true)
            Spacer(modifier = Modifier.height(8.dp))
            ComboBox(opciones = estadoCivil, seleccion = estadoCivilSelec,"Estado civil",false)

        }

        Spacer(modifier = Modifier.height(16.dp))

        SeccionDesplegable(
            titulo = "Datos de contacto",
            expandido = expandido2
        ) {
            outLinedText(celular, "Celular", Modifier.fillMaxWidth(), false, keyDown = true)
            Spacer(modifier = Modifier.height(8.dp))
            outLinedText(telefono, "Telefono", Modifier.fillMaxWidth(), false, true)
            Spacer(modifier = Modifier.height(8.dp))
            outLinedText(correo, "Correo", Modifier.fillMaxWidth(), false, true)
            Spacer(modifier = Modifier.height(8.dp))
            outLinedText(direccion, "Direccion", Modifier.fillMaxWidth(), false, false)
        }

        Spacer(modifier = Modifier.height(16.dp))

        //Desplegable contacto adicional
        SeccionDesplegable(
            titulo = "Datos de contacto adicional",
            expandido = expandido3
        ){
            outLinedText(nombreContacto, "Nombre completo", Modifier.fillMaxWidth(), false, keyDown = true)
            Spacer(modifier = Modifier.height(8.dp))
            outLinedText(cedulaContacto, "Cedula", Modifier.fillMaxWidth(), false, true)
            Spacer(modifier = Modifier.height(8.dp))
            outLinedText(celularContacto, "Celular", Modifier.fillMaxWidth(), false, true)

        }

    }

}


@Composable
fun outLinedText(
    variableMutable: MutableState<String>,
    etiqueta: String,
    modifier: Modifier,
    pass: Boolean,
    keyDown : Boolean) {

    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = variableMutable.value,
        onValueChange = { variableMutable.value = it },
        label = {Text(etiqueta)},
        modifier = modifier,
        visualTransformation = if (pass == true) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(imeAction = if (keyDown==true) ImeAction.Next else ImeAction.Done),
        keyboardActions = KeyboardActions(
            onNext = {
                if (keyDown){
                    focusManager.moveFocus(FocusDirection.Down)
                }else
                {
                    focusManager.clearFocus()
                }
            }
        )//fin del evento keyDown
    )// fin del outfieldtext

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComboBox(
    opciones: List<String>,
    seleccion: MutableState<String>,
    label: String,
    keyDown : Boolean
) {
    var expanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val modifier: Modifier = Modifier

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier.fillMaxWidth() // Asegurar que el ComboBox ocupe el ancho completo
    ) {
        OutlinedTextField(
            value = seleccion.value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            keyboardOptions = KeyboardOptions(imeAction = if (keyDown==true) ImeAction.Next else ImeAction.Done),
            keyboardActions = KeyboardActions(
                onNext = {
                    if (keyDown){
                        focusManager.moveFocus(FocusDirection.Down)
                    }else
                    {
                        focusManager.clearFocus()
                    }
                }
            ),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )// fin del primer outfieldtext

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        seleccion.value = opcion
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun SeccionDesplegable(
    titulo: String,
    expandido: MutableState<Boolean>,
    contenido: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expandido.value = !expandido.value }
    ) {
        // Cabecera
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = titulo,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (expandido.value) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null
            )
        }
        // Contenido
        AnimatedVisibility(visible = expandido.value) {
            Column(modifier = Modifier.padding(top = 5.dp)) {
                contenido()
            }
        }
    }
}


