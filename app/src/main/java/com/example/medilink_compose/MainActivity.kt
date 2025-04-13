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
import com.example.medilink_compose.Notificacion.EmailWorker
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


        // ✅ PEDIR PERMISO PARA ENVIAR SMS (Necesario desde Android 6.0)
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


        // ✅ PEDIR PERMISO PARA NOTIFICACIONES EN ANDROID 13+
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

        // ✅ CREAR CANAL DE NOTIFICACIONES
        createNotificationChannel(applicationContext)

        // ✅ CONFIGURAR TRABAJOS CON WORKMANAGER (Ya lo tienes bien configurado)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(false)
            .setRequiresCharging(false)
            .build()

        val periodicWorkRequest = PeriodicWorkRequestBuilder<CitaWorker>(
            15, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setInitialDelay(5, TimeUnit.MINUTES)
            .addTag("cita_notification_work")
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "citaNotificationWork",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )

        val periodicWorkRequest2 = PeriodicWorkRequestBuilder<EmailWorker>(
            15, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setInitialDelay(5, TimeUnit.MINUTES)
            .addTag("email_notification_work")
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "emailNotificationWork",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest2
        )
    }

}





