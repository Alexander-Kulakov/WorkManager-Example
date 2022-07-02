package com.example.workmanager_example.workers

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters

class SumWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        val a = inputData.getInt("a", 0)
        val b = inputData.getInt("b", 0)

        Thread.sleep(1000L)
        val c = a + b

        return Result.success(Data.Builder().putInt("c", c).build())
    }

}