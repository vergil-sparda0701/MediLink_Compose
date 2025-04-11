package com.example.medilink_compose

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lint.kotlin.metadata.Visibility
import java.util.Calendar

@Composable
public fun outLinedText(
    variableMutable: MutableState<String>,
    etiqueta: String,
    modifier: Modifier,
    pass: Boolean,
    keyDown : Boolean) {

    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = variableMutable.value,
        onValueChange = { variableMutable.value = it },
        label = { Text(etiqueta) },
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

@Composable
fun OutlinedPasswordField(
    variableMutable: MutableState<String>,
    etiqueta: String,
    modifier: Modifier,
    keyDown: Boolean
) {
    val focusManager = LocalFocusManager.current
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = variableMutable.value,
        onValueChange = { variableMutable.value = it },
        label = { Text(etiqueta) },
        modifier = modifier,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = if (keyDown) ImeAction.Next else ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onNext = {
                if (keyDown) {
                    focusManager.moveFocus(FocusDirection.Down)
                } else {
                    focusManager.clearFocus()
                }
            }
        ),
        trailingIcon = {
            val image = if (passwordVisible) R.drawable.visibility else R.drawable.visibility_off
            val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"

            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(painter = painterResource(image), contentDescription = description)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComboBox(
    opciones: List<String>,
    seleccion: MutableState<String>,
    label: String,
    keyDown : Boolean,
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
            Column {
                contenido()
            }
        }
    }
}

@Composable
fun ImageButton(
    modifier: Modifier = Modifier,
    imageResId: Int,
    text: String,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = text,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = text,
            fontSize = 14.sp
        )
    }
}

@Composable
fun FechaConDatePicker(
    fecha: MutableState<String>,
    label: String = "Fecha"
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            val fechaSeleccionada = "${selectedDay.toString().padStart(2, '0')}/" +
                    "${(selectedMonth + 1).toString().padStart(2, '0')}/$selectedYear"
            fecha.value = fechaSeleccionada
        },
        year, month, day
    )

    OutlinedTextField(
        value = fecha.value,
        onValueChange = {},
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                datePickerDialog.show()
            },
        label = { Text(label) },
        enabled = false, // No editable directamente
        readOnly = true
    )
}

@Composable
fun HoraConTimePicker(
    hora: MutableState<String>,
    horasOcupadas: List<String>,
    label: String = "Hora"
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val horaActual = calendar.get(Calendar.HOUR_OF_DAY)
    val minutoActual = calendar.get(Calendar.MINUTE)

    val timePickerDialog = TimePickerDialog(
        context,
        { _, selectedHour, selectedMinute ->
            val horaSeleccionada = String.format("%02d:%02d", selectedHour, selectedMinute)

            if (horaSeleccionada in horasOcupadas) {
                Toast.makeText(context, "Esa hora ya está ocupada", Toast.LENGTH_LONG).show()
            } else {
                hora.value = horaSeleccionada
            }
        },
        horaActual,
        minutoActual,
        false
    )

    OutlinedTextField(
        value = hora.value,
        onValueChange = {},
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                timePickerDialog.show()
            },
        label = { Text(label) },
        enabled = false, // Para que no se edite a mano
        readOnly = true
    )
}



