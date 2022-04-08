package com.zipdori.autoplanner.schedulegenerator

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zipdori.autoplanner.R
import com.zipdori.autoplanner.modules.calendarprovider.EventsVO
import java.util.*

class ScheduleCellAdapter (val context: Context) :
    RecyclerView.Adapter<ScheduleCellAdapter.ViewHolder>() {
        var scheduleList= mutableListOf<EventsVO>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ScheduleCellAdapter.ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.schedule_simplecell, null)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {
        holder.bind(scheduleList[position])
    }

    override fun getItemCount(): Int {
        return scheduleList.size
    }

    inner class ViewHolder(view:View) :RecyclerView.ViewHolder(view){
        val eventColor = view.findViewById<ImageView>(R.id.schedule_cell_color)
        val eventTitle = view.findViewById<TextView>(R.id.schedule_cell_title)
        val eventPeriod = view.findViewById<TextView>(R.id.schedule_cell_period)
        val eventRegCheck = view.findViewById<View>(R.id.schedule_cell_reg_check)

        fun bind(item: EventsVO) {
            eventTitle.text = item.title
            eventPeriod.text = DateForm.integratedForm.format(Date(item.dtStart)) + "~" + DateForm.integratedForm.format(Date(item.dtEnd!!))
        }
    }
}