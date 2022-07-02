package com.example.workmanager_example.workers

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters

class ResultsWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {

        val cArray = inputData.getIntArray("c")

        val summ = cArray?.sum() ?: inputData.getInt("c", 0)

        val content = if(cArray == null) summ.toString() else "${cArray.joinToString(" + ")} = $summ"

        val data = Data.Builder().putString("res", content).build()

        return Result.success(data)
    }

}