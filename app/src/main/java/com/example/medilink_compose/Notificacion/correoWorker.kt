package com.example.medilink_compose.Notificacion

import android.content.Context
import android.os.Build
import androidx.work.Worker
import androidx.work.WorkerParameters

class correoWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                pacienteCorreo(applicationContext)
            }
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}