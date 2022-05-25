package com.zipdori.autoplanner.schedulegenerator

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zipdori.autoplanner.R
import com.zipdori.autoplanner.modules.calendarprovider.EventsVO
import java.util.*

import android.widget.LinearLayout


import com.zipdori.autoplanner.modules.database.EventExtraInfoVO

class ScheduleCellAdapter(val context: Context, val saveIntent: ActivityResultLauncher<Intent>) :
    RecyclerView.Adapter<ScheduleCellAdapter.ViewHolder>() {
        var scheduleList= mutableListOf<EventsVO>()
        var scheduleListExtra = mutableListOf<EventExtraInfoVO>()
        var scheduleListBool:BooleanArray = BooleanArray(0)
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
        val eventColor = itemView.findViewById<ImageView>(R.id.schedule_cell_color)
        val eventTitle = itemView.findViewById<TextView>(R.id.schedule_cell_title)
        val eventPeriod = itemView.findViewById<TextView>(R.id.schedule_cell_period)
        val eventRegCheck: ImageView = itemView.findViewById(R.id.schedule_cell_reg_check)
        val modifyArea = itemView.findViewById<LinearLayout>(R.id.schedule_cell_detail)

        fun bind(item: EventsVO) {
            eventTitle.text = item.title
            eventPeriod.text = DateForm.integratedForm.format(Date(item.dtStart)) + "\n~" + DateForm.integratedForm.format(Date(item.dtEnd!!))

            val drawable = ContextCompat.getDrawable(
                context,
                R.drawable.ic_colorpickerbutton
            ) as GradientDrawable?
            if(item.eventColor != null) {
                drawable?.setColor(item.eventColor!!)
                eventColor.setImageDrawable(drawable)
            }
            if(scheduleListBool[adapterPosition])
                eventRegCheck.imageTintList = ColorStateList.valueOf(Color.GREEN)
            else
                eventRegCheck.imageTintList = ColorStateList.valueOf(Color.GRAY)

            // 등록할 일정 리사이클러뷰 클릭 이벤트
            modifyArea.setOnClickListener{
                val intent = Intent(context, SetScheduleActivity::class.java)
                intent.putExtra("SingleScheduleData", item)
                intent.putExtra("SingleScheduleDataExtra", scheduleListExtra[adapterPosition])
                saveIntent.launch(intent)
            }
            eventRegCheck.setOnClickListener{
                if(scheduleListBool[adapterPosition]) {
                    scheduleListBool[adapterPosition] = false
                    eventRegCheck.imageTintList = ColorStateList.valueOf(Color.GRAY)
                }
                else {
                    scheduleListBool[adapterPosition] = true
                    eventRegCheck.imageTintList = ColorStateList.valueOf(Color.GREEN)
                }
            }
        }

    }
}