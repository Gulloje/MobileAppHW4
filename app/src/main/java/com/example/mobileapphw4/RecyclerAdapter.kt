package com.example.mobileapphw4

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date

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

        var stringDate = curItem.dates.start.localDate
        var date = SimpleDateFormat("yyyy-MM-dd").parse(stringDate)
        stringDate = SimpleDateFormat("MM/dd/yyyy").format(date)
        holder.date.text = "$stringDate"

        holder.eventLocation.text = "${curItem._embedded.venues[0].name}"
        holder.address.text = "${curItem._embedded.venues[0].address}" //have to concatenate this mess with res of data
        holder.priceRange.text = "${curItem.priceRanges}"

    }
    override fun getItemCount(): Int {
        return eventList.size
    }
}