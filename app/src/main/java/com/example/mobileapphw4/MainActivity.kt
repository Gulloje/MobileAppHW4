package com.example.mobileapphw4

import android.app.ActionBar
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
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

    //navbar
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Set the title of the default navbar
        getSupportActionBar()?.setTitle("Some Cool Name");



        //for the hamburger menu
        var drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.menu_open, R.string.menu_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true) //toggle button changes to back arrow
        findViewById<NavigationView>(R.id.navView).setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.signout -> MenuButtonClicks(this).signOut()

            }
            true
        }
        initRecyclerView()
    }

    private var seeMoreCounter = 0;
    // wanted to prevent changing the text field and then hitting seeMore causing the new loaded events to be that of the changed text field
    private var previousCityName =""
    private var previousKeyword = ""
    fun search(view: View) {
        eventList.clear()
        previousCityName = findViewById<EditText>(R.id.textCity).text.toString()
        previousKeyword = findViewById<EditText>(R.id.textKeyword).text.toString()
        seeMoreCounter = 0
        loadTickets(previousCityName, previousKeyword)
        view.hideKeyboard()
    }
    fun seeMore(view:View) {
        seeMoreCounter++
        loadTickets(previousCityName, previousKeyword)

    }
    private fun loadTickets(cityName: String, keyword: String) {


        if (keyword =="") {
            createDialog("Missing Keyword", "Enter a keyword to search for.")
        } else if (cityName =="") {
            createDialog("Missing City Name", "Please enter a city.")
        } else {

            eventAPI.getEventNameByCity(cityName, keyword, seeMoreCounter.toString(), apiKey).enqueue(object : Callback<TicketData?> {
                override fun onResponse(call: Call<TicketData?>, response: Response<TicketData?>) {
                    if (response.body()?._embedded == null) {
                        //Toast.makeText(this@MainActivity, "No Events Found", Toast.LENGTH_SHORT).show()
                        findViewById<TextView>(R.id.noResultsTextView).visibility = View.VISIBLE

                    } else {
                        Log.d(TAG, "onResponse: ${response.body()}")
                        Log.d(TAG, "Name ${response.body()!!._embedded.events[0]}")
                        Log.d(TAG, "Body: ${response.body()}")

                        findViewById<TextView>(R.id.noResultsTextView).visibility = View.GONE

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
        adapter = RecyclerAdapter(this, eventList);
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)

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

    override fun onResume() {
        super.onResume()
        //want to hide the keyboard when they go back to it
        //view = findViewById(android.R.id.content).getRootView().getWindowToken();  https://rmirabelle.medium.com/close-hide-the-soft-keyboard-in-android-db1da22b09d2
        findViewById<View>(android.R.id.content).hideKeyboard()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)

    }






}