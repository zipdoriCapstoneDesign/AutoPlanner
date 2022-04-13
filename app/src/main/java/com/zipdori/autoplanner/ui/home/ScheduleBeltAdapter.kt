package com.zipdori.autoplanner.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zipdori.autoplanner.R
import com.zipdori.autoplanner.modules.calendarprovider.EventsVO

class ScheduleBeltAdapter(private val context: Context, private val eventsVOArrayList: ArrayList<EventsVO>)
    : RecyclerView.Adapter<ScheduleBeltAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvScheduleTitleSb: TextView = itemView.findViewById(R.id.tv_schedule_title_sb)

        fun bind(context: Context, eventsVO: EventsVO) {
            if (eventsVO.title.equals("") || eventsVO.title == null) {
                tvScheduleTitleSb.text = "(제목 없음)"
            } else {
                tvScheduleTitleSb.text = eventsVO.title
            }
            itemView.setBackgroundColor(eventsVO.displayColor)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_rv_schedule_belt, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position < 9) {
            holder.bind(context, eventsVOArrayList.get(position))
        }
    }

    override fun getItemCount(): Int {
        return eventsVOArrayList.size
    }
}