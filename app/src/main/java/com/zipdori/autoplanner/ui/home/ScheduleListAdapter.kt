package com.zipdori.autoplanner.ui.home

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zipdori.autoplanner.R

class ScheduleListAdapter(private val context : Context, private val itemScheduleArrayList: ArrayList<ItemSchedule>) : RecyclerView.Adapter<ScheduleListAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val linearLayout: LinearLayout = itemView.findViewById(R.id.ll_schedule_color)
        private val tvScheduleTitle: TextView = itemView.findViewById(R.id.tv_schedule_title)
        private val tvScheduleTime: TextView = itemView.findViewById(R.id.tv_schedule_time)

        fun bind(context: Context, itemSchedule: ItemSchedule) {
            val gradientDrawable: GradientDrawable = GradientDrawable()
            gradientDrawable.cornerRadius = (10 as Int).toFloat()
            gradientDrawable.setColor(itemSchedule.color)
            linearLayout.background = gradientDrawable
            tvScheduleTitle.text = itemSchedule.tvScheduleTitle
            tvScheduleTime.text = itemSchedule.tvScheduleTime
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_schedule, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(context, itemScheduleArrayList.get(position))
    }

    override fun getItemCount(): Int {
        return itemScheduleArrayList.size
    }
}