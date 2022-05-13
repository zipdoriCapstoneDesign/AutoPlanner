package com.toyproject.testproject3_zipdori.ui.home

import EventExtraInfoVO
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.zipdori.autoplanner.R
import com.zipdori.autoplanner.modules.calendarprovider.EventsVO
import com.zipdori.autoplanner.schedulegenerator.SetScheduleActivity
import com.zipdori.autoplanner.ui.calendar.ScheduleBeltAdapter
import com.zipdori.autoplanner.ui.calendar.ScheduleListAdapter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MonthAdapter(
    val context: Context,
    calendar: Calendar,
    var schedules: HashMap<String, ArrayList<EventsVO>>,
    val getResultSetSchedule: ActivityResultLauncher<Intent>
    ) : BaseAdapter() {
    private val layoutInflater: LayoutInflater
    private val calendar: Calendar = Calendar.getInstance()
    private val dateManager: DateManager
    private var dateArray: ArrayList<Date>
    private var onEventsChangeListener: ScheduleListAdapter.OnEventsChangeListener?
    var scheduleListAdapter: ScheduleListAdapter?

    init {
        layoutInflater = LayoutInflater.from(context)
        this.calendar.time = calendar.time
        dateManager = DateManager(this.calendar)
        dateArray = dateManager.getDays()
        this.onEventsChangeListener = null
        this.scheduleListAdapter = null
    }

    private class ViewHolder {
        lateinit var tvDate: TextView
        lateinit var rvScheduleBelt: RecyclerView
    }

    override fun getCount(): Int {
        return dateArray.size
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View? {
        var viewHolder: ViewHolder
        var convertView: View? = view

        val date: Date = dateArray.get(position)

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.calendar_cell, null)
            viewHolder = ViewHolder()
            viewHolder.tvDate = convertView.findViewById(R.id.tv_date)!!
            viewHolder.rvScheduleBelt = convertView.findViewById(R.id.rv_schedule_belt)!!
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        val dp: Float = context.resources.displayMetrics.density
        val params: AbsListView.LayoutParams = AbsListView.LayoutParams(parent!!.width / 7 - dp.toInt(), (parent.height - dp.toInt() * dateManager.getWeeks()) / dateManager.getWeeks())
        convertView?.layoutParams = params

        val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("d", Locale.US)
        viewHolder.tvDate.text = simpleDateFormat.format(date)

        // TODO: 2022-03-17 Delete 
        /*
        if (dateManager.isCurrentMonth(dateArray.get(position))) {
            convertView?.setBackgroundColor(Color.WHITE)
        } else {
            convertView?.setBackgroundColor(Color.LTGRAY)
        }

         */
        convertView?.setBackgroundColor(Color.WHITE)

        var colorId: Int
        when (dateManager.getDayOfWeek(date)) {
            1 -> {
                if (dateManager.isCurrentMonth(date)) {
                    colorId = context.getColor(R.color.material_800_red)
                } else {
                    colorId = context.getColor(R.color.material_800_red_50pct)
                }
            }
            7 -> {
                if (dateManager.isCurrentMonth(date)) {
                    colorId = context.getColor(R.color.material_800_blue)
                } else {
                    colorId = context.getColor(R.color.material_800_blue_50pct)
                }
            }
            else -> {
                if (dateManager.isCurrentMonth(date)) {
                    colorId = context.getColor(R.color.black)
                } else {
                    colorId = context.getColor(R.color.black_50pct)
                }
            }
        }
        viewHolder.tvDate.setTextColor(colorId)


        if(SimpleDateFormat("yyyy.MM.dd", Locale.US).format(dateArray.get(position)) == SimpleDateFormat("yyyy.MM.dd", Locale.US).format(Calendar.getInstance().time)) {
            viewHolder.tvDate.setTextColor(Color.WHITE)
            viewHolder.tvDate.setBackgroundColor(Color.BLACK)
        }

        var eventsVOArrayList: ArrayList<EventsVO>? = schedules.get(SimpleDateFormat("yyyy.MM.dd", Locale.US).format(dateArray.get(position)))
        if (eventsVOArrayList == null) {
            eventsVOArrayList = ArrayList<EventsVO>()
        }
        viewHolder.rvScheduleBelt.layoutManager = LinearLayoutManager(context)
        val scheduleBeltAdapter: ScheduleBeltAdapter = ScheduleBeltAdapter(context, eventsVOArrayList)
        viewHolder.rvScheduleBelt.adapter = scheduleBeltAdapter
        viewHolder.rvScheduleBelt.suppressLayout(true)

        convertView?.setOnClickListener(View.OnClickListener {
            val layoutInflater: LayoutInflater = (context as FragmentActivity).layoutInflater
            val constraintLayout: ConstraintLayout = layoutInflater.inflate(R.layout.dialog_show_schedule, null) as ConstraintLayout

            val tvDateDss: TextView = constraintLayout.findViewById(R.id.tv_date_dss)
            val tvDayDss: TextView = constraintLayout.findViewById(R.id.tv_day_dss)
            val tvLunarCalendar: TextView = constraintLayout.findViewById(R.id.tv_lunar_calendar_dss)
            val rvDss: RecyclerView = constraintLayout.findViewById(R.id.rv_dss)
            val fabDss: FloatingActionButton = constraintLayout.findViewById(R.id.fab_add_dss)

            tvDateDss.text = SimpleDateFormat("d", Locale.US).format(date)
            tvDayDss.text = SimpleDateFormat("E", Locale.KOREA).format(date) + "요일"
            // TODO: 2022-03-18 음력 날짜 구하기
            tvLunarCalendar.text = "음력 날짜"

            tvDateDss.setTextColor(colorId)
            tvDayDss.setTextColor(colorId)

            scheduleListAdapter = ScheduleListAdapter(context, eventsVOArrayList, date, getResultSetSchedule)
            scheduleListAdapter!!.setOnEventsChangeListener(onEventsChangeListener!!)
            rvDss.layoutManager = LinearLayoutManager(context)
            rvDss.adapter = scheduleListAdapter

            fabDss.setOnClickListener {
                val intent = Intent(context, SetScheduleActivity::class.java)

                val fromCal = Calendar.getInstance()
                val toCal = Calendar.getInstance()

                fromCal.time = date
                toCal.time = date

                fromCal.add(Calendar.HOUR, 1)
                fromCal.set(Calendar.MINUTE, 0)
                toCal.add(Calendar.HOUR, 2)
                toCal.set(Calendar.MINUTE, 0)

                val sharedPreferences: SharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                val calendarId = sharedPreferences.getLong(context.getString(R.string.calendar_index), 0)
                val tempEvent = EventsVO(-1, calendarId,null,null,null,null,-10572033,-10572033,fromCal.timeInMillis,toCal.timeInMillis,"UTC",null,null,null,null,null,null,null)
                val tempEventExtra = EventExtraInfoVO(0,0,null)
                intent.putExtra("SingleScheduleData", tempEvent)
                intent.putExtra("SingleScheduleDataExtra", tempEventExtra)

                getResultSetSchedule.launch(intent)
            }

            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setView(constraintLayout)
            val alertDialog: AlertDialog = builder.create()
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            alertDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
            alertDialog.show()

        })

        return convertView
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getItem(p0: Int): Any? {
        return null
    }

    fun getTitle(): String {
        val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("yyyy.MM", Locale.US)
        return simpleDateFormat.format(calendar.time)
    }

    // TODO: 2022-03-16 Delete
    fun nextMonth() {
        dateManager.nextMonth()
        dateArray = dateManager.getDays()
        this.notifyDataSetChanged()
    }

    // TODO: 2022-03-17 Delete 
    fun prevMonth() {
        dateManager.prevMonth()
        dateArray = dateManager.getDays()
        this.notifyDataSetChanged()
    }

    fun setOnEventsChangeListener(onEventsChangeListener: ScheduleListAdapter.OnEventsChangeListener) {
        this.onEventsChangeListener = onEventsChangeListener
    }
}

