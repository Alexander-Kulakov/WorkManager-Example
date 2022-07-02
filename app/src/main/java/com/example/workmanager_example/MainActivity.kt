package com.example.workmanager_example

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import com.example.workmanager_example.databinding.ActivityMainBinding
import com.example.workmanager_example.workers.MultiplyWorker
import com.example.workmanager_example.workers.ResultsWorker
import com.example.workmanager_example.workers.SumWorker
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.createChain.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    override fun onClick(v: View?) {
        binding.output.text = ""

        val data1 = Data.Builder()
            .putInt("a", binding.a1EditText.text?.toString()?.toInt() ?: 0)
            .putInt("b", binding.b1EditText.text?.toString()?.toInt() ?: 0)
            .build()

        val data2 = Data.Builder()
            .putInt("a", binding.a2EditText.text?.toString()?.toInt() ?: 0)
            .putInt("b", binding.b2EditText.text?.toString()?.toInt() ?: 0)
            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(
                if(binding.isNetworkRequired.isChecked) NetworkType.CONNECTED else NetworkType.NOT_REQUIRED
            )
            .setRequiresCharging(binding.isChargingRequired.isChecked)
            .build()

        val sumWorker = OneTimeWorkRequestBuilder<SumWorker>()
            .setConstraints(constraints)
            .setInputData(data1)
            .build()

        val multiplyWorker = OneTimeWorkRequestBuilder<MultiplyWorker>()
            .setInputData(data2)
            .build()

        val inputMerger = if(binding.overwriting.isChecked)
            OverwritingInputMerger::class.java
        else
            ArrayCreatingInputMerger::class.java

        val resultWorker = OneTimeWorkRequestBuilder<ResultsWorker>()
            .setInputMerger(inputMerger)
            .build()

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(sumWorker.id).observe(this) {
            binding.output.text = binding.output.text.toString() + "SumWorker - ${it.state}\n"
            if(it.state == WorkInfo.State.SUCCEEDED) {
                val c = it.outputData.getInt("c", 0)
                binding.output.text = binding.output.text.toString() + "a1 + b2 = $c\n"
            }
        }

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(multiplyWorker.id).observe(this) {
            binding.output.text = binding.output.text.toString() + "MultiplyWorker - ${it.state}\n"
            if(it.state == WorkInfo.State.SUCCEEDED) {
                val c = it.outputData.getInt("c", 0)
                binding.output.text = binding.output.text.toString() + "a1 * b2 = $c\n"
            }
        }

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(resultWorker.id).observe(this) {
            binding.output.text = binding.output.text.toString() + "ResultsWorker - ${it.state}\n"
            if(it.state == WorkInfo.State.SUCCEEDED) {
                val res = it.outputData.getString("res")
                binding.output.text = binding.output.text.toString() + "$res\n"
            }
        }

        WorkManager.getInstance(this)
            .beginWith(listOf(sumWorker, multiplyWorker))
            .then(resultWorker)
            .enqueue()
    }
}
