package com.zipdori.autoplanner.ui.home

import android.os.Bundle
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.toyproject.testproject3_zipdori.ui.home.CalendarAdapter
import com.zipdori.autoplanner.MainActivity
import com.zipdori.autoplanner.R
import com.zipdori.autoplanner.database.AutoPlannerModule
import com.zipdori.autoplanner.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment(), View.OnClickListener {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewPager2: ViewPager2
    private lateinit var tvYYYYMM: TextView
    private lateinit var fabAI: FloatingActionButton
    private lateinit var fabPhoto: FloatingActionButton
    private lateinit var fabGallery: FloatingActionButton
    private lateinit var fabText: FloatingActionButton
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation

    private var isFabOpen = false

    private lateinit var autoPlannerModule: AutoPlannerModule

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

        viewPager2 = binding.vpCalendar
        tvYYYYMM = binding.tvYyyymm
        fabAI = binding.fabAi
        fabPhoto = binding.fabPhoto
        fabGallery = binding.fabGallery
        fabText = binding.fabText
        fabAdd = binding.fabAdd

        fabOpen = AnimationUtils.loadAnimation(context, R.anim.fab_open)
        fabClose = AnimationUtils.loadAnimation(context, R.anim.fab_close)

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

        fabAI.setOnClickListener(this)
        fabPhoto.setOnClickListener(this)
        fabGallery.setOnClickListener(this)
        fabText.setOnClickListener(this)
        fabAdd.setOnClickListener(this)

        autoPlannerModule = AutoPlannerModule(context)

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_home, menu)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        (activity as MainActivity).supportActionBar?.title = ""
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

    override fun onClick(p0: View?) {
        when(p0?.id) {
            R.id.fab_ai -> toggleFab()
            R.id.fab_photo -> {
                toggleFab()
                Toast.makeText(context, "fab photo clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.fab_gallery -> {
                toggleFab()
                Toast.makeText(context, "fab gallery clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.fab_text -> {
                toggleFab()
                Toast.makeText(context, "fab text clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.fab_add -> {
                Toast.makeText(context, "fab add clicked", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun toggleFab() {
        if (isFabOpen) {
            fabAI.setImageResource(R.drawable.ic_baseline_add_24_black)
            fabPhoto.startAnimation(fabClose)
            fabGallery.startAnimation(fabClose)
            fabText.startAnimation(fabClose)
            fabPhoto.setClickable(false)
            fabGallery.setClickable(false)
            fabText.setClickable(false)
            isFabOpen = false
        } else {
            fabAI.setImageResource(R.drawable.ic_baseline_close_24_black)
            fabPhoto.startAnimation(fabOpen)
            fabGallery.startAnimation(fabOpen)
            fabText.startAnimation(fabOpen)
            fabPhoto.setClickable(true)
            fabGallery.setClickable(true)
            fabText.setClickable(true)
            isFabOpen = true
        }
    }
}