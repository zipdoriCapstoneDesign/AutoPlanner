package com.zipdori.autoplanner.ui.home

import android.os.Bundle
import android.view.*
import android.view.animation.Animation
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.toyproject.testproject3_zipdori.ui.home.CalendarAdapter
import com.zipdori.autoplanner.R
import com.zipdori.autoplanner.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewPager2: ViewPager2
    private lateinit var tvYYYYMM: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setHasOptionsMenu(true)

        // View Binding
        viewPager2 = binding.vpCalendar
        tvYYYYMM = binding.tvYyyymm

        // GridView 를 위한 CalendarAdapter
        val calendarAdapterArrayList: ArrayList<CalendarAdapter> = ArrayList()
        for (i in 0 until 2400) {
            calendarAdapterArrayList.add(CalendarAdapter(context!!, i))
        }

        // ViewPager2 어댑터 및 초기 설정
        viewPager2.adapter = ViewPager2Adapter(context!!, calendarAdapterArrayList)
        viewPager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val calendar:Calendar = Calendar.getInstance()
                calendar.set(Calendar.YEAR, 1902)
                calendar.set(Calendar.MONTH, 0)
                calendar.set(Calendar.DATE, 1)
                calendar.add(Calendar.MONTH, position)

                val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("yyyy.MM", Locale.US)
                tvYYYYMM.text = simpleDateFormat.format(calendar.time)

                super.onPageSelected(position)
            }
        })
        setViewPager2CurMonth(false)

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_home, menu)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> {
                // TODO: 2022-03-17 Item Search 클릭 시 action 구현
            }
            R.id.action_today -> {
                setViewPager2CurMonth(true)
            }
        }
        
        return super.onOptionsItemSelected(item)
    }
    
    private fun setViewPager2CurMonth(smoothScroll: Boolean) {
        val calendar: Calendar = Calendar.getInstance()
        val monthDiff = (calendar.get(Calendar.YEAR) - 1902) * 12 + calendar.get(Calendar.MONTH)
        viewPager2.setCurrentItem(monthDiff, smoothScroll)
    }
}