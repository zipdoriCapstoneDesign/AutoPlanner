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
import com.zipdori.autoplanner.modules.calendarprovider.EventsVO
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ScheduleListAdapter(private val context : Context, private val eventsVOArrayList: ArrayList<EventsVO>)
    : RecyclerView.Adapter<ScheduleListAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val linearLayout: LinearLayout = itemView.findViewById(R.id.ll_schedule_color)
        private val tvScheduleTitle: TextView = itemView.findViewById(R.id.tv_schedule_title)
        private val tvScheduleTime: TextView = itemView.findViewById(R.id.tv_schedule_time)

        fun bind(context: Context, eventsVO: EventsVO) {
            val gradientDrawable: GradientDrawable = GradientDrawable()
            gradientDrawable.cornerRadius = (10 as Int).toFloat()
            gradientDrawable.setColor(eventsVO.displayColor)
            linearLayout.background = gradientDrawable

            if (eventsVO.title.equals("") || eventsVO.title == null) {
                tvScheduleTitle.text = "(제목 없음)"
            } else {
                tvScheduleTitle.text = eventsVO.title
            }

            var scheduleTime: String = ""
            val start = Calendar.getInstance()
            var simpleDateFormat: SimpleDateFormat = SimpleDateFormat("a hh:mm", Locale.KOREA)

            start.timeInMillis = eventsVO.dtStart
            if (eventsVO.allDay == 1) {
                scheduleTime = "하루 종일"
            } else if (eventsVO.dtEnd != null) {
                val end = Calendar.getInstance()
                end.timeInMillis = eventsVO.dtEnd!!
                if (!SimpleDateFormat("yyyy.MM.dd", Locale.US).format(start.time).equals(SimpleDateFormat("yyyy.MM.dd", Locale.US).format(end.time))) {
                    simpleDateFormat = SimpleDateFormat("M월 dd일 a hh:mm", Locale.KOREA)
                }
                scheduleTime = simpleDateFormat.format(start.time)
                scheduleTime += " ~ "
                scheduleTime += simpleDateFormat.format(end.time)
            } else {
                scheduleTime = simpleDateFormat.format(start.time)
                scheduleTime += " ~ "
            }
            tvScheduleTime.text = scheduleTime
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_rv_dss, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(context, eventsVOArrayList.get(position))
    }

    override fun getItemCount(): Int {
        return eventsVOArrayList.size
    }
}