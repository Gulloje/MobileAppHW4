package com.example.mobileapphw4

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private val BASE_URL = "https://app.ticketmaster.com/"
    private val apiKey = "yL6rMKTtCDSqaZBhQ1FCUHf4z6mO3htG"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val eventAPI = retrofit.create(EventDataService::class.java)

        findViewById<Button>(R.id.btnSearch).setOnClickListener {
            val cityName = findViewById<EditText>(R.id.textCity).text.toString()
            eventAPI.getEventNameByCity(cityName, apiKey).enqueue(object : Callback<TicketData?> {
                override fun onResponse(call: Call<TicketData?>, response: Response<TicketData?>) {
                    Toast.makeText(this@MainActivity, "$response", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "onResponse: $response")
                    Log.d(TAG, "Name ${response.body()!!._embedded.events[0]}")


                }

                override fun onFailure(call: Call<TicketData?>, t: Throwable) {
                    Log.d(TAG, "onFailure: $t")
                }
            })
        }
    }




}