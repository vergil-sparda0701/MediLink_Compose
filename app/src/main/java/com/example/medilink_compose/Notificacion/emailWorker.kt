package com.example.medilink_compose.Notificacion

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class EmailWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        return try {
            Log.d("EmailWorker", "Ejecutando pacienteCita() en segundo plano")
            pacienteCitaEnviarSMS(applicationContext)

            Result.success()
        } catch (e: Exception) {
            Log.e("EmailWorker", "Error durante la ejecución en segundo plano: ${e.message}")
            Result.failure()
        }
    }
}