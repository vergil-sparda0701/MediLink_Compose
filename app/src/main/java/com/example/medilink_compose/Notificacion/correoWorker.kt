package com.example.medilink_compose.Notificacion

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters

@RequiresApi(Build.VERSION_CODES.O)
class correoWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    companion object {
        private const val TAG = "CorreoWorker"
    }

    override fun doWork(): Result {
        return try {
            Log.d(TAG, "Inicio de envío de recordatorios por correo")
            enviarRecordatoriosCorreo(applicationContext)
            Log.d(TAG, "Envío de recordatorios por correo completado con éxito")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error en CorreoWorker: ${e.message}", e)
            // Reintentar en caso de error transitorio
            Result.retry()
        }
    }
}
