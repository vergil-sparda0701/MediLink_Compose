package com.example.medilink_compose

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.work.WorkManager
import com.example.medilink_compose.Notificacion.createNotificationChannel
import com.example.medilink_compose.ui.theme.MediLink_ComposeTheme
import java.util.concurrent.TimeUnit
import androidx.work.*
import com.example.medilink_compose.Notificacion.CitaWorker
import com.example.medilink_compose.Notificacion.mostrarNotificacion
import com.example.medilink_compose.Notificacion.pacienteCita


val databaseVersion : Int = 4


class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permiso concedido, puedes mostrar notificaciones
                Log.d("MainActivity", "Permiso de notificación concedido")
                // Aquí podrías llamar a tu lógica para programar o mostrar notificaciones iniciales
            } else {
                // Permiso denegado, informa al usuario que las notificaciones no funcionarán
                Log.w("MainActivity", "Permiso de notificación denegado")
                // Puedes mostrar un mensaje al usuario explicando por qué necesita el permiso
            }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MediLink_ComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(Modifier.padding(innerPadding))

                    //createNotificationChannel(this)
                }

            }
        }

        createNotificationChannel(applicationContext) // Asegúrate de crear el canal aquí o en tu MainActivity

        // Programar la ejecución periódica de CitaWorker
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED) // No requiere red
            .setRequiresBatteryNotLow(false) // No requiere batería baja
            .setRequiresCharging(false) // No requiere carga
            .build()

        val periodicWorkRequest = PeriodicWorkRequestBuilder<CitaWorker>(
            15, // Intervalo de repetición
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setInitialDelay(5, TimeUnit.MINUTES) // Opcional: Retraso inicial
            .addTag("cita_notification_work") // Opcional: Etiqueta para identificar el Worker
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "citaNotificationWork", // Nombre único para el trabajo periódico
            ExistingPeriodicWorkPolicy.KEEP, // Política si ya existe un trabajo con este nombre
            periodicWorkRequest
        )
    }

}





