package com.example.mobileapphw4

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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



        var eventList = ArrayList<EventData>()
        val adapter = RecyclerAdapter(eventList);
        val recyclerView = findViewById<RecyclerView>(R.id.recycleView);
        recyclerView.adapter = adapter
        //recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        //recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val eventAPI = retrofit.create(EventDataService::class.java)

        findViewById<Button>(R.id.btnSearch).setOnClickListener {
            val cityName = findViewById<EditText>(R.id.textCity).text.toString()
            val keyword = findViewById<EditText>(R.id.textKeyword).text.toString()
            if (keyword =="" || cityName == "") {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("My Dialog")
                builder.setMessage("Welcome to my app!")

                // create the dialog and show it
                val dialog = builder.create()
                dialog.show()
            } else {
                eventList.clear(); //if you hit the button a second time, clear the list
                eventAPI.getEventNameByCity(cityName, keyword, apiKey).enqueue(object : Callback<TicketData?> {
                    override fun onResponse(call: Call<TicketData?>, response: Response<TicketData?>) {
                        Toast.makeText(this@MainActivity, "$response", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "onResponse: $response")
                        Log.d(TAG, "Name ${response.body()!!._embedded.events[0]}")
                        Log.d(TAG, "Body: ${response.body()}")
                        if (response.body() == null) {
                            Toast.makeText(this@MainActivity, "No Events Found", Toast.LENGTH_SHORT).show()
                        } else {
                            eventList.addAll(response.body()!!._embedded.events)
                        }
                        adapter.notifyDataSetChanged()



                    }

                    override fun onFailure(call: Call<TicketData?>, t: Throwable) {
                        Log.d(TAG, "onFailure: $t")
                    }
                })
            }

        }
    }




}