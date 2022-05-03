package com.zipdori.autoplanner.ui.home

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.RecyclerView
import com.zipdori.autoplanner.R
import com.zipdori.autoplanner.modules.CommonModule
import com.zipdori.autoplanner.modules.calendarprovider.CalendarProviderModule
import com.zipdori.autoplanner.modules.calendarprovider.EventsVO
import com.zipdori.autoplanner.schedulegenerator.SetScheduleActivity
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ScheduleListAdapter(
    private val context : Context,
    private var eventsVOArrayList: ArrayList<EventsVO>,
    private val date: Date,
    val getResultSetSchedule: ActivityResultLauncher<Intent>
    )
    : RecyclerView.Adapter<ScheduleListAdapter.ViewHolder>() {
    interface OnEventsChangeListener {
        fun onEventsChange(calendar: Calendar)
    }

    private var onEventsChangeListener: OnEventsChangeListener?
    private val commonModule: CommonModule = CommonModule(context)

    init {
        onEventsChangeListener = null
    }

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

            itemView.setOnLongClickListener {
                val builderScheduleControl: AlertDialog.Builder = AlertDialog.Builder(context)
                builderScheduleControl.setTitle(SimpleDateFormat("d일", Locale.US).format(date) + " " + tvScheduleTitle.text)
                    .setItems(R.array.schedule_long_click_array, DialogInterface.OnClickListener { dialogInterface, i ->
                        when (i) {
                            // 수정
                            0 -> {
                                val intent = Intent(context, SetScheduleActivity::class.java)
                                intent.putExtra("SingleScheduleData", eventsVO)

                                getResultSetSchedule.launch(intent)
                            }
                            // 삭제
                            1 -> {
                                val builderDeleteSchedule: AlertDialog.Builder = AlertDialog.Builder(context)
                                builderDeleteSchedule.setMessage("일정을 휴지통으로 이동할까요?")
                                    .setNegativeButton("취소", DialogInterface.OnClickListener { dialogInterface, i ->

                                    })
                                    .setPositiveButton("휴지통으로 이동", DialogInterface.OnClickListener { dialogInterface, i ->
                                        val calendarProviderModule: CalendarProviderModule = CalendarProviderModule(context)
                                        calendarProviderModule.setEventDeleted(eventsVO.id)
                                        val tempEventsVOArrayList : ArrayList<EventsVO>? = commonModule.getEventsVOArrayList(SimpleDateFormat("yyyy.MM.dd", Locale.US).format(date))
                                        if (tempEventsVOArrayList == null) {
                                            eventsVOArrayList = ArrayList()
                                        } else {
                                            eventsVOArrayList = tempEventsVOArrayList
                                        }
                                        notifyDataSetChanged()

                                        val calendar: Calendar = Calendar.getInstance()
                                        calendar.time = date
                                        onEventsChangeListener!!.onEventsChange(calendar)
                                    })

                                val alertDialogDeleteSchedule: AlertDialog = builderDeleteSchedule.create()
                                alertDialogDeleteSchedule.show()
                            }
                        }
                    })
                val alertDialogScheduleControl: AlertDialog = builderScheduleControl.create()
                alertDialogScheduleControl.show()

                true
            }
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

    fun setOnEventsChangeListener(onEventsChangeListener: OnEventsChangeListener) {
        this.onEventsChangeListener = onEventsChangeListener
    }

    fun getDate() : Date {
        return this.date
    }

    fun setEventsVOArrayList(eventsVOArrayList: ArrayList<EventsVO>) {
        this.eventsVOArrayList = eventsVOArrayList
    }
}