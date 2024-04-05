package com.example.mobileapphw4

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.ceil
import kotlin.math.floor

class RecyclerAdapter(private val context: Context, private val eventList: ArrayList<EventData>): RecyclerView.Adapter<RecyclerAdapter.ViewHolder>()
{
    //code for adding the see more button: https://stackoverflow.com/questions/29106484/how-to-add-a-button-at-the-end-of-recyclerview
    val TAG = "Recycler Adapter"
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val eventName = itemView.findViewById<TextView>(R.id.textEventName)
        val eventLocation = itemView.findViewById<TextView>(R.id.textLocation)
        val address = itemView.findViewById<TextView>(R.id.textAddress)
        val date = itemView.findViewById<TextView>(R.id.textDate)
        val priceRange = itemView.findViewById<TextView>(R.id.textPriceRange)
        val image = itemView.findViewById<ImageView>(R.id.imageView)
        val btnSeeMore = itemView.findViewById<Button>(R.id.btnSeeMore)
        val btnSeeTickets = itemView.findViewById<Button>(R.id.btnSeeTickets)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = when (viewType) {
            //if the item is a row, give the row_item xml, else its the button
            R.layout.row_item -> LayoutInflater.from(parent.context).inflate(R.layout.row_item, parent, false)
            else -> LayoutInflater.from(parent.context).inflate(R.layout.see_more_btn, parent, false)
        }
        return ViewHolder(itemView)
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: $position")
        //COMEBACK
        //if(position == eventList.size && eventList.size > 0) {

        //if(position == eventList.size && eventList.size > 0) {
            //holder.btnSeeMore.visibility = View.VISIBLE

        if (position == eventList.size) {
            // This is the "See more" button item
            if (eventList.size % 20 == 0 && eventList.size != 0) {
                holder.btnSeeMore.visibility = View.VISIBLE
            } else {
                holder.btnSeeMore.visibility = View.GONE
            }
        } else if (eventList.size > 0){
            val curItem = eventList[position]
            holder.eventName.text = "${curItem.name}"

            //date  https://www.datetimeformatter.com/how-to-format-date-time-in-kotlin/
            var stringDate = curItem.dates.start.localDate
            var date = SimpleDateFormat("yyyy-MM-dd").parse(stringDate)
            stringDate = SimpleDateFormat("MM/dd/yyyy").format(date)

            //time
            try {
                var time24 = curItem.dates.start.localTime
                var time12 = SimpleDateFormat("H:mm:ss").parse(time24)
                var realTime = SimpleDateFormat("h:mm a").format(time12)
                holder.date.text = "$stringDate at $realTime"
            } catch (e: Exception) {
                holder.date.text = "Date: N/A"
            }


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

            val highestQualityImage = curItem.images.maxByOrNull {
                it.width.toInt() * it.height.toInt()

            }
            val context = holder.itemView.context
            Glide.with(context).load(highestQualityImage?.url).into(holder.image)
            holder.btnSeeTickets.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW)
                browserIntent.data = Uri.parse(eventList[position].url)
                context.startActivity( browserIntent)
            }
        }




    }

    override fun getItemViewType(position: Int): Int {
        return if (position == eventList.size) {
            R.layout.see_more_btn
        } else {
            R.layout.row_item
        }
    }


    override fun getItemCount(): Int {
        return eventList.size + 1
    }
}