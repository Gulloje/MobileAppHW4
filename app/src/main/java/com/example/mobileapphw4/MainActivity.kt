package com.example.mobileapphw4

import android.app.AlertDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecyclerAdapter
    private  val eventList = ArrayList<EventData>()
    private val eventAPI = initRetrofit().create(EventDataService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initRecyclerView()
    }
    private var seeMoreCounter = 0;
    //tied to search button
    fun search(view: View) {
        eventList.clear()
        seeMoreCounter = 0
        loadTickets()
        view.hideKeyboard()
    }
    fun seeMore(view:View) {
        seeMoreCounter++
        loadTickets()

    }
    private fun loadTickets() {

        val cityName = findViewById<EditText>(R.id.textCity).text.toString()
        val keyword = findViewById<EditText>(R.id.textKeyword).text.toString()
        if (keyword =="" || cityName == "") { //COMEBACK
            createDialog("Something is missing!", "Fill out all fields")
        } else {
            //COMEBACK TO FIGURE OUT PAGING
            eventAPI.getEventNameByCity(cityName, keyword, seeMoreCounter.toString(), apiKey).enqueue(object : Callback<TicketData?> {
                override fun onResponse(call: Call<TicketData?>, response: Response<TicketData?>) {
                    if (response.body()?._embedded == null) {
                        Toast.makeText(this@MainActivity, "No Events Found", Toast.LENGTH_SHORT).show()

                    } else {
                        Log.d(TAG, "onResponse: ${response.body()}")
                        Log.d(TAG, "Name ${response.body()!!._embedded.events[0]}")
                        Log.d(TAG, "Body: ${response.body()}")

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
    private fun createDialog(title: String, message: String ) {
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(true)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.show()
    }
    private fun initRecyclerView() {
        recyclerView = findViewById(R.id.recycleView)
        adapter = RecyclerAdapter(eventList);
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        //recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }
    private fun initRetrofit() : Retrofit {
        val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit
    }

    private fun View.hideKeyboard() {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }






}