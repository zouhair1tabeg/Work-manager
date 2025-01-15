package com.example.workmanager

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.work.Worker
import androidx.work.WorkerParameters

class NetworkWorker(private val context: Context,params: WorkerParameters): Worker(context,params) {
    override fun doWork(): Result {

        Handler(Looper.getMainLooper()).post{
            Toast.makeText(context, "Connexion Intenet disponible", Toast.LENGTH_SHORT).show()
        }

        return Result.success()
    }
}