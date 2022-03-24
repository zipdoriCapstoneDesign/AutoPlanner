package com.zipdori.autoplanner.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.recyclerview.widget.RecyclerView
import com.toyproject.testproject3_zipdori.ui.home.CalendarAdapter
import com.zipdori.autoplanner.R

class ViewPager2Adapter(private val context: Context, val calendarAdapterArrayList: ArrayList<CalendarAdapter>) :
    RecyclerView.Adapter<ViewPager2Adapter.PagerViewHolder>() {

    inner class PagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val gridView: GridView = itemView.findViewById(R.id.gv_calendar)

        fun bind(calendarAdapter: CalendarAdapter) {
            gridView.adapter = calendarAdapter
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.calendar, parent, false)

        return PagerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        holder.bind(calendarAdapterArrayList.get(position))
    }

    override fun getItemCount(): Int {
        return calendarAdapterArrayList.size
    }
}

