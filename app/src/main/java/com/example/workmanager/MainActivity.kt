package com.example.workmanager

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val smartList = findViewById<ListView>(R.id.lv)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<NetworkWorker>()
            .setInitialDelay(5, TimeUnit.SECONDS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueue(workRequest)

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(workRequest.id)
            .observe(this) { workInfo ->
                if (workInfo != null && workInfo.state == WorkInfo.State.SUCCEEDED) {
                    fetchAndDisplayData(smartList)
                } else if (workInfo.state == WorkInfo.State.FAILED) {
                    Toast.makeText(this, "Pas de connexion Internet", Toast.LENGTH_LONG).show()
                }
            }
    }


    private fun fetchAndDisplayData(smartList: ListView) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://apiyes.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        apiService.getWatches().enqueue(object : Callback<List<SmartWatch>> {
            override fun onResponse(call: Call<List<SmartWatch>>, response: Response<List<SmartWatch>>) {
                if (response.isSuccessful) {
                    val watches = response.body() ?: emptyList()
                    val watch_names = ArrayList<String>()

                    for (smrt in watches){
                        watch_names.add("${smrt.name} - ${smrt.price} MAD")
                    }

                    val adapter = adapter(this@MainActivity, watches)
                    smartList.adapter = adapter

                }
            }

            override fun onFailure(call: Call<List<SmartWatch>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Erreur: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}