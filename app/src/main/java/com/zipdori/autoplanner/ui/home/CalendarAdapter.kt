package com.toyproject.testproject3_zipdori.ui.home

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import com.zipdori.autoplanner.R
import java.text.SimpleDateFormat
import java.util.*

class CalendarAdapter(context: Context, monthFrom1902: Int) : BaseAdapter() {
    private var dateArray: ArrayList<Date>
    private val context: Context
    private val dateManager: DateManager
    private val layoutInflater: LayoutInflater

    init {
        this.context = context
        layoutInflater = LayoutInflater.from(this.context)
        dateManager = DateManager()
        dateArray = dateManager.getDays(monthFrom1902)
    }

    private class ViewHolder {
        lateinit var dateText: TextView
    }

    override fun getCount(): Int {
        return dateArray.size
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View? {
        var viewHolder: ViewHolder
        var convertView: View? = view

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.calendar_cell, null)
            viewHolder = ViewHolder()
            viewHolder.dateText = convertView.findViewById(R.id.tv_date)!!
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        val dp: Float = context.resources.displayMetrics.density
        val params: AbsListView.LayoutParams = AbsListView.LayoutParams(parent!!.width / 7 - dp.toInt(), (parent.height - dp.toInt() * dateManager.getWeeks()) / dateManager.getWeeks())
        convertView?.layoutParams = params

        val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("d", Locale.US)
        viewHolder.dateText.text = simpleDateFormat.format(dateArray.get(position))

        /*
        // 해당달이 아닌 날짜들은 회색
        if (dateManager.isCurrentMonth(dateArray.get(position))) {
            convertView?.setBackgroundColor(Color.WHITE)
        } else {
            convertView?.setBackgroundColor(Color.LTGRAY)
        }

         */
        convertView?.setBackgroundColor(Color.WHITE)

        var colorId: Int
        when (dateManager.getDayOfWeek(dateArray.get(position))) {
            1 -> {
                if (dateManager.isCurrentMonth(dateArray.get(position))) {
                    colorId = context.getColor(R.color.material_800_red)
                } else {
                    colorId = context.getColor(R.color.material_800_red_50pct)
                }
            }
            7 -> {
                if (dateManager.isCurrentMonth(dateArray.get(position))) {
                    colorId = context.getColor(R.color.material_800_blue)
                } else {
                    colorId = context.getColor(R.color.material_800_blue_50pct)
                }
            }
            else -> {
                if (dateManager.isCurrentMonth(dateArray.get(position))) {
                    colorId = context.getColor(R.color.black)
                } else {
                    colorId = context.getColor(R.color.black_50pct)
                }
            }
        }
        viewHolder.dateText.setTextColor(colorId)


        if(SimpleDateFormat("yyyy.MM.dd", Locale.US).format(dateArray.get(position)) == SimpleDateFormat("yyyy.MM.dd", Locale.US).format(Calendar.getInstance().time)) {
            viewHolder.dateText.setTextColor(Color.WHITE)
            viewHolder.dateText.setBackgroundColor(Color.BLACK)
        }

        convertView?.setOnClickListener(View.OnClickListener {
            Toast.makeText(context, SimpleDateFormat("yyyy.MM.dd", Locale.US).format(dateArray.get(position)).toString(), Toast.LENGTH_SHORT).show()
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
        return simpleDateFormat.format(dateManager.calendar.time)
    }

    // TODO: 2022-03-16 delete 
    fun nextMonth() {
        dateManager.nextMonth()
        dateArray = dateManager.getDays()
        this.notifyDataSetChanged()
    }

    fun prevMonth() {
        dateManager.prevMonth()
        dateArray = dateManager.getDays()
        this.notifyDataSetChanged()
    }
}

