package com.example.medilink_compose

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import java.util.Calendar

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import com.example.medilink_compose.BD_Files.ThemeModel
import com.example.medilink_compose.BD_Files.ThemeViewModel

@Composable
fun BarChartStyled(
    modifier: Modifier = Modifier,
    title: String = "Gráfico de Barras",
    data: List<Float>,
    labels: List<String>,
    barColor: List<Color> = listOf(Color(0xFFFFA726), Color(0xFFEF6C00)),
    textColor: Color = Color.Black,
    steps: Int = 5 // Cantidad de divisiones del eje Y
) {
    val maxValue = data.maxOrNull() ?: 0f

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(horizontal = 16.dp)
        ) {
            val barWidthPx = constraints.maxWidth / (data.size * 2)
            val spacePx = barWidthPx

            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasHeight = size.height
                val canvasWidth = size.width

                val bottomOffset = 30f
                val leftOffset = 60f

                // Ejes
                drawLine(
                    color = Color.Gray,
                    start = Offset(leftOffset, 0f),
                    end = Offset(leftOffset, canvasHeight - bottomOffset),
                    strokeWidth = 4f
                )
                drawLine(
                    color = Color.Gray,
                    start = Offset(leftOffset, canvasHeight - bottomOffset),
                    end = Offset(canvasWidth, canvasHeight - bottomOffset),
                    strokeWidth = 4f
                )

                // Líneas guía horizontales + valores Y
                val stepValue = maxValue / steps
                for (i in 0..steps) {
                    val y = (canvasHeight - bottomOffset) - (i * (canvasHeight - bottomOffset) / steps)
                    val label = (i * stepValue).toInt().toString()

                    // Línea guía
                    drawLine(
                        color = Color.LightGray,
                        start = Offset(leftOffset, y),
                        end = Offset(canvasWidth, y),
                        strokeWidth = 1f
                    )

                    // Texto del valor Y
                    drawContext.canvas.nativeCanvas.drawText(
                        label,
                        0f,
                        y + 10f,
                        android.graphics.Paint().apply {
                            color = textColor.toArgb()
                            textSize = 24f
                            textAlign = android.graphics.Paint.Align.LEFT
                        }
                    )
                }

                // Dibujar barras
                data.forEachIndexed { index, value ->
                    val barHeight = (value / maxValue) * (canvasHeight - bottomOffset - 30f)
                    val x = leftOffset + index * (barWidthPx + spacePx)
                    val y = (canvasHeight - bottomOffset) - barHeight

                    // Barra con gradiente
                    drawRect(
                        brush = Brush.verticalGradient(barColor),
                        topLeft = Offset(x, y),
                        size = Size(barWidthPx.toFloat(), barHeight)
                    )

                    // Valor encima
                    drawContext.canvas.nativeCanvas.drawText(
                        value.toInt().toString(),
                        x + barWidthPx / 2,
                        y - 10f,
                        android.graphics.Paint().apply {
                            color = textColor.toArgb()
                            textSize = 26f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                    )

                    // Etiqueta debajo
                    drawContext.canvas.nativeCanvas.drawText(
                        labels.getOrNull(index) ?: "",
                        x + barWidthPx / 2,
                        canvasHeight - 5f,
                        android.graphics.Paint().apply {
                            color = textColor.toArgb()
                            textSize = 26f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                    )
                }
            }
        }
    }
}



@Composable
fun outLinedText(
    variableMutable: MutableState<String>,
    etiqueta: String,
    modifier: Modifier,
    pass: Boolean,
    keyDown : Boolean,
    editable : Boolean = true,
    activo : Boolean = false) {

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
        ),
        readOnly = !editable,
        enabled = !activo

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
    label: String = "Hora"
){
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val horaActual = calendar.get(Calendar.HOUR_OF_DAY)
    val minutoActual = calendar.get(Calendar.MINUTE)

    val timePickerDialog = TimePickerDialog(
        context,
        { _, selectedHour, selectedMinute ->
            val horaSeleccionada = String.format("%02d:%02d", selectedHour, selectedMinute)

            hora.value = horaSeleccionada
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
        readOnly = true)

}




