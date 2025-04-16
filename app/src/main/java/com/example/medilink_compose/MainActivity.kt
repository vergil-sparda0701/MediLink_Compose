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
import com.example.medilink_compose.Notificacion.NotificacionWorker
import com.example.medilink_compose.Notificacion.mostrarNotificacion
import com.example.medilink_compose.Notificacion.pacienteCita
import com.example.medilink_compose.Notificacion.smsWorker
import com.example.medilink_compose.Pantallas.MyAppTheme
import com.example.medilink_compose.Pantallas.ThemeSwitcher


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

                }

            }
        }


        // PEDIR PERMISO PARA ENVIAR SMS
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("MainActivity", "Solicitando permiso SEND_SMS")
            requestPermissionLauncher.launch(android.Manifest.permission.SEND_SMS)
        } else {
            Log.d("MainActivity", "Permiso SEND_SMS ya concedido")
        }


        // PEDIR PERMISO PARA NOTIFICACIONES
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS

                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("MainActivity", "Solicitando permiso POST_NOTIFICATIONS")
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            } else {
                Log.d("MainActivity", "Permiso POST_NOTIFICATIONS ya concedido")
            }
        }

        // CREAR CANAL DE NOTIFICACIONES
        createNotificationChannel(this)
        programarNotificacionesPeriodicas(this)
        programarSMSPeriodicos(this)

    }

}

fun programarNotificacionesPeriodicas(context: Context) {
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
        .build()

    Log.d("WorkerTimer", "intervalo de tiempo en el worker")
    val request = PeriodicWorkRequestBuilder<NotificacionWorker>(5, TimeUnit.MINUTES)
        .setConstraints(constraints)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "notificacionesCitas",
        ExistingPeriodicWorkPolicy.KEEP,
        request
    )
}

fun programarSMSPeriodicos(context: Context) {
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
        .build()

    val request = PeriodicWorkRequestBuilder<smsWorker>(5, TimeUnit.MINUTES)
        .setConstraints(constraints)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "smsCitas",
        ExistingPeriodicWorkPolicy.KEEP,
        request
    )
}






