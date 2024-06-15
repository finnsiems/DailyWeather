package com.example.dailyweather

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class Hourly_Recycler constructor(
    context: Context?,
    time: List<String>,
    temp: List<String>,
    desc: List<String>,
    chooseView: Int
    //activity: AppCompatActivity
) :
    RecyclerView.Adapter<Hourly_Recycler.ViewHolder>() {
    //private var context: AppCompatActivity
    private val inflater: LayoutInflater

    var viewChoice: Int
    var tempList: List<String>
    var timeList: List<String>
    var descList: List<String>
    //var context: Context


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view: View = inflater.inflate(R.layout.weather_hour, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val time = timeList[position]
        holder.hourlyTime.setText(time)

        val temp = tempList[position]
        holder.hourlyTemp.setText(temp)

        val desc = descList[position]
        holder.hourlyDesc.setText(desc)


    }

    override fun getItemCount(): Int {
        return timeList.size
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
    }


    //assigns the values
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var cv: CardView
        var hourlyTime: TextView
        var hourlyTemp: TextView
        var hourlyDesc: TextView

        //var relative: RelativeLayout
        //var linear: LinearLayout

        init {
            cv = itemView.findViewById<View>(R.id.cardview) as CardView
            hourlyTime = itemView.findViewById<View>(R.id.textview_hourly_time) as TextView
            hourlyTemp = itemView.findViewById<View>(R.id.textview_hourly_temp) as TextView
            hourlyDesc = itemView.findViewById<View>(R.id.textview_hourly_desc) as TextView
            //relative = itemView.findViewById<View>(R.id.relativelayout) as RelativeLayout
            //linear = itemView.findViewById<View>(R.id.linear) as LinearLayout

        }
    }


    init {
        inflater = LayoutInflater.from(context)
        timeList = time
        tempList = temp
        descList = desc
        viewChoice = chooseView
        //this.context = activity
    }

}