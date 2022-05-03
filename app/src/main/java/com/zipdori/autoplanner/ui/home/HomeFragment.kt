package com.zipdori.autoplanner.ui.home

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.toyproject.testproject3_zipdori.ui.home.MonthAdapter
import com.zipdori.autoplanner.Consts
import com.zipdori.autoplanner.Consts.Companion.FLAG_PERM_CAMERA
import com.zipdori.autoplanner.Consts.Companion.FLAG_PERM_STORAGE_MULTIPICK
import com.zipdori.autoplanner.R
import com.zipdori.autoplanner.databinding.FragmentHomeBinding
import com.zipdori.autoplanner.modules.App
import com.zipdori.autoplanner.modules.CommonModule
import com.zipdori.autoplanner.modules.calendarprovider.CalendarProviderModule
import com.zipdori.autoplanner.modules.calendarprovider.EventsVO
import com.zipdori.autoplanner.modules.database.AutoPlannerDBModule
import com.zipdori.autoplanner.schedulegenerator.ListupSchedulecellActivity
import com.zipdori.autoplanner.schedulegenerator.SetScheduleActivity
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment(), View.OnClickListener {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var vpCalendar: ViewPager2
    private lateinit var tvYYYYMM: TextView
    private lateinit var fabAI: FloatingActionButton
    private lateinit var fabPhoto: FloatingActionButton
    private lateinit var fabGallery: FloatingActionButton
    private lateinit var fabText: FloatingActionButton
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation

    private var isFabOpen = false

    private lateinit var autoPlannerDBModule: AutoPlannerDBModule

    var imgList = ArrayList<Uri>() //사진 다중 선택할 때 사진 Uri 담는 리스트
    var singleUri:Uri? = null //카메라 사용시 카메라 키기 전에 만드는 uri 전역변수로 저장, 다른 함수에서 활용

    private lateinit var commonModule: CommonModule

    private lateinit var getResultSetSchedule: ActivityResultLauncher<Intent>
    val monthAdapterArrayList: ArrayList<MonthAdapter> = ArrayList()

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

        vpCalendar = binding.vpCalendar
        tvYYYYMM = binding.tvYyyymm
        fabAI = binding.fabAi
        fabPhoto = binding.fabPhoto
        fabGallery = binding.fabGallery
        fabText = binding.fabText
        fabAdd = binding.fabAdd

        fabOpen = AnimationUtils.loadAnimation(context, R.anim.fab_open)
        fabClose = AnimationUtils.loadAnimation(context, R.anim.fab_close)

        commonModule = CommonModule(context!!)

        getResultSetSchedule = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val tempEvent:EventsVO = result.data?.getParcelableExtra("scheduleItem")!!

                val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                val calendarId = sharedPreferences.getLong(getString(R.string.calendar_index), 0)
                val id = tempEvent.id
                val title = tempEvent.title
                val eventLocation = tempEvent.eventLocation
                val description = tempEvent.description
                val eventColor = tempEvent.eventColor
                val dtStart = tempEvent.dtStart
                val dtEnd = tempEvent.dtEnd!!
                val duration = tempEvent.duration
                val allDay = tempEvent.allDay
                val rRule = tempEvent.rRule
                val rDate = tempEvent.rDate
                val eventTimeZone = "UTC"

                // id가 -1이면 새로운 Event를 추가하는 것으로 간주
                val calendarProviderModule = CalendarProviderModule(requireActivity().applicationContext)
                if (id.equals((-1).toLong())) {
                    calendarProviderModule.insertEvent(calendarId, title, eventLocation, description, eventColor, dtStart, dtEnd, eventTimeZone, duration, allDay, rRule, rDate)
                } else {
                    calendarProviderModule.updateEvent(id, title, eventLocation, description, eventColor, dtStart, dtEnd, eventTimeZone, duration, allDay, rRule, rDate)

                    for (monthAdapter in monthAdapterArrayList) {
                        if (monthAdapter.scheduleListAdapter != null) {
                            val date: Date = monthAdapter.scheduleListAdapter!!.getDate()
                            var tempEventsVOArrayList : ArrayList<EventsVO>? = commonModule.getEventsVOArrayList(SimpleDateFormat("yyyy.MM.dd", Locale.US).format(date))
                            if (tempEventsVOArrayList == null) {
                                tempEventsVOArrayList = ArrayList()
                            }
                            monthAdapter.scheduleListAdapter!!.setEventsVOArrayList(tempEventsVOArrayList)
                            monthAdapter.scheduleListAdapter!!.notifyDataSetChanged()
                        }
                    }
                }

                // TODO: 2022-04-06 더 효율적인 방법 구상해보기
                val calendar: Calendar = Calendar.getInstance()
                calendar.timeInMillis = tempEvent.dtStart
                drawCalendar()
                setViewPager2Position(calendar, false)
            }
        }

        drawCalendar()
        setViewPager2CurMonth(false)

        fabAI.setOnClickListener(this)
        fabPhoto.setOnClickListener(this)
        fabGallery.setOnClickListener(this)
        fabText.setOnClickListener(this)
        fabAdd.setOnClickListener(this)

        autoPlannerDBModule = AutoPlannerDBModule(context)

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
        vpCalendar.setCurrentItem(monthDiff, smoothScroll)
    }

    private fun setViewPager2Position(calendar: Calendar, smoothScroll: Boolean) {
        val monthDiff = (calendar.get(Calendar.YEAR) - 1902) * 12 + calendar.get(Calendar.MONTH)
        vpCalendar.setCurrentItem(monthDiff, smoothScroll)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.fab_ai -> toggleFab()
            R.id.fab_photo -> {
                toggleFab()
                Toast.makeText(context, "fab photo clicked", Toast.LENGTH_SHORT).show()
                Log.e("홈카메라버튼",Manifest.permission.READ_EXTERNAL_STORAGE.toString() + " / " + Manifest.permission.WRITE_EXTERNAL_STORAGE.toString())
                val NEED_PERMISSIONS = arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )

                requestPermissions(NEED_PERMISSIONS, FLAG_PERM_CAMERA)

            }
            R.id.fab_gallery -> {
                toggleFab()
                Toast.makeText(context, "fab gallery clicked", Toast.LENGTH_SHORT).show()

                val NEED_PERMISSIONS = arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )

                requestPermissions(NEED_PERMISSIONS, FLAG_PERM_STORAGE_MULTIPICK)

            }
            R.id.fab_text -> {
                toggleFab()
                view.findNavController().navigate(R.id.action_nav_home_to_nav_text_input)
            }
            R.id.fab_add -> {
                val intent = Intent(context, SetScheduleActivity::class.java)

                //SetSchedule 액티비티 실행 전에 날짜시간 인자 보내주는 걸로 일반화
                val fromCal = Calendar.getInstance()
                val toCal = Calendar.getInstance()

                // 기본 일정 추가 버튼은 현재시간과 +1시간으로 범위 설정
                toCal.add(Calendar.HOUR,1)

                val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                val calendarId = sharedPreferences.getLong(getString(R.string.calendar_index), 0)
                val tempEvent = EventsVO(-1, calendarId,null,null,null,null,-10572033,-10572033,fromCal.timeInMillis,toCal.timeInMillis,"UTC",null,null,null,null,null,null,null)
                intent.putExtra("SingleScheduleData", tempEvent)

                val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                val uri: Uri? = createImageUri("JPEG_${timeStamp}_", "image/jpeg")
                Log.e("Uri",uri.toString())

                getResultSetSchedule.launch(intent)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            Consts.FLAG_REQ_CAMERA ->{
                if(resultCode == Activity.RESULT_OK) {
                    imgList.add(singleUri!!)
                    val intent = Intent(context, ListupSchedulecellActivity::class.java)
                    startActivity(intent)
                }
                else
                    requireActivity().contentResolver.delete(singleUri!!,null,null)
            }
            Consts.GET_GALLERY_IMAGE_MULTI->{
                imgList.clear()
                if(data?.clipData != null){
                    val count = data.clipData!!.itemCount
                    Log.e("선택한 사진 수",count.toString())
                    if (count > 5) {
                        Toast.makeText(App.context(), "사진은 5장까지 선택 가능합니다.", Toast.LENGTH_LONG).show()
                        return
                    }
                    for (i in 0 until count) {
                        val imageUri = data.clipData!!.getItemAt(i).uri
                        imgList.add(imageUri)
                        Log.e("imgList에 넣는 주소", imageUri.toString())
                    }
                } else { // 단일 선택
                    data?.data?.let { uri ->
                        val imageUri : Uri? = data?.data
                        if (imageUri != null) {
                            imgList.add(imageUri)
                        }
                    }

                }

                if(imgList.isNotEmpty()) {
                    val intent = Intent(context, ListupSchedulecellActivity::class.java)
                    intent.putParcelableArrayListExtra("imgURIs", imgList)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            Consts.FLAG_PERM_CAMERA -> {
                Log.e("승인절차","카메라")
                for (grant in grantResults) {
                    Log.e("승인절차",grant.toString())
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(
                            context,
                            "카메라 권한을 승인해야지만 카메라를 사용할 수 있습니다. 앱 정보에서 승인해주세요.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }
                }
                Log.e("승인절차","카메라 승인 확인")
                imgList.clear()
                val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                val uri: Uri? = createImageUri("JPEG_${timeStamp}_", "image/jpeg")

                singleUri = uri

                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                startActivityForResult(takePictureIntent, Consts.FLAG_REQ_CAMERA)
            }
            Consts.FLAG_PERM_STORAGE -> {
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        //권한이 승인되지 않았다면 return 을 사용하여 메소드를 종료시켜 줍니다
                        Toast.makeText(
                            context,
                            "저장소 권한을 승인해야지만 앱을 사용할 수 있습니다..",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }
                }
                val intent = Intent(Intent.ACTION_PICK)
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                startActivityForResult(intent, Consts.GET_GALLERY_IMAGE)
            }
            Consts.FLAG_PERM_STORAGE_MULTIPICK -> {
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        //권한이 승인되지 않았다면 return 을 사용하여 메소드를 종료시켜 줍니다
                        Toast.makeText(
                            context,
                            "저장소 권한을 승인해야지만 앱을 사용할 수 있습니다..",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }
                }
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

                startActivityForResult(intent, Consts.GET_GALLERY_IMAGE_MULTI)
            }

        }
    }

    private fun createImageUri(filename: String, mimeType: String): Uri? {
        var values = ContentValues()
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType)
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/AutoPlanner")
        return requireActivity().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )
    }

    private fun drawCalendar() {
        // GridView 를 위한 CalendarAdapter
        monthAdapterArrayList.clear()
        val calendar: Calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, 1902)
        calendar.set(Calendar.MONTH, 0)
        calendar.set(Calendar.DATE, 1)
        for (i in 0 until 2400) {
            val monthAdapter: MonthAdapter = MonthAdapter(context!!, calendar, getResultSetSchedule)
            monthAdapter.setOnEventsChangeListener(object : ScheduleListAdapter.OnEventsChangeListener {
                override fun onEventsChange(calendar: Calendar) {
                    drawCalendar()
                    setViewPager2Position(calendar, false)
                }
            })
            monthAdapterArrayList.add(monthAdapter)
            calendar.add(Calendar.MONTH, 1)
        }

        // ViewPager2 어댑터 및 초기 설정
        val calendarAdapter: CalendarAdapter = CalendarAdapter(context!!, monthAdapterArrayList)
        vpCalendar.adapter = calendarAdapter
        vpCalendar.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        vpCalendar.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tvYYYYMM.text = (vpCalendar.adapter as CalendarAdapter).monthAdapterArrayList.get(position).getTitle()

                super.onPageSelected(position)
            }
        })
    }
}