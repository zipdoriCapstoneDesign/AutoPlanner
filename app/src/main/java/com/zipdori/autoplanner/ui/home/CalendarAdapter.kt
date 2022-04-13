package com.zipdori.autoplanner.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.recyclerview.widget.RecyclerView
import com.toyproject.testproject3_zipdori.ui.home.MonthAdapter
import com.zipdori.autoplanner.R

class CalendarAdapter(private val context: Context, val monthAdapterArrayList: ArrayList<MonthAdapter>)
    : RecyclerView.Adapter<CalendarAdapter.PagerViewHolder>() {

    inner class PagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val gvMonth: GridView = itemView.findViewById(R.id.gv_month)

        fun bind(monthAdapter: MonthAdapter) {
            gvMonth.adapter = monthAdapter
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.calendar, parent, false)

        return PagerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        holder.bind(monthAdapterArrayList.get(position))
    }

    override fun getItemCount(): Int {
        return monthAdapterArrayList.size
    }
}

