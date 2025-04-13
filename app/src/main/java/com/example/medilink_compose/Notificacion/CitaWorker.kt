package com.example.medilink_compose.Notificacion

import android.os.Build
import androidx.annotation.RequiresApi
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class CitaWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        return try {
            Log.d("CitaWorker", "Ejecutando pacienteCita() en segundo plano")
            pacienteCita(applicationContext)
            Result.success()
        } catch (e: Exception) {
            Log.e("CitaWorker", "Error durante la ejecución en segundo plano: ${e.message}")
            Result.failure()
        }
    }
}
