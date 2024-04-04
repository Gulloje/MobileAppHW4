package com.example.mobileapphw4

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.ceil
import kotlin.math.floor

class RecyclerAdapter(private val eventList: ArrayList<EventData>): RecyclerView.Adapter<RecyclerAdapter.ViewHolder>()
{
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val eventName = itemView.findViewById<TextView>(R.id.textEventName)
        val eventLocation = itemView.findViewById<TextView>(R.id.textLocation)
        val address = itemView.findViewById<TextView>(R.id.textAddress)
        val date = itemView.findViewById<TextView>(R.id.textDate)
        val priceRange = itemView.findViewById<TextView>(R.id.textPriceRange)
        //still have to do image
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_item, parent, false);
        return ViewHolder(view);
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val curItem = eventList[position]
        holder.eventName.text = "${curItem.name}"

        //date  https://www.datetimeformatter.com/how-to-format-date-time-in-kotlin/
        var stringDate = curItem.dates.start.localDate
        var date = SimpleDateFormat("yyyy-MM-dd").parse(stringDate)
        stringDate = SimpleDateFormat("MM/dd/yyyy").format(date)

        //time
        var time24 = curItem.dates.start.localTime
        var time12 = SimpleDateFormat("H:mm:ss").parse(time24)
        var realTime = SimpleDateFormat("h:mm a").format(time12)
        holder.date.text = "$stringDate at $realTime"

        //address
        holder.eventLocation.text = "${curItem._embedded.venues[0].name}"
        holder.address.text = "${curItem._embedded.venues[0].address.line1}, " +
                "${curItem._embedded.venues[0].city.name}, ${curItem._embedded.venues[0].state.stateCode}"

        //wanted to get rid of .0 and .5 and handle null
        try {
            holder.priceRange.text = "Price Range: $${ceil(curItem.priceRanges[0].min).toInt()} - $${ceil(curItem.priceRanges[0].max).toInt()}"
        } catch(e: Exception) {
            holder.priceRange.text = "PriceRange: N/A"
        }


    }

    override fun getItemCount(): Int {
        return eventList.size
    }
}