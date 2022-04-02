package com.zipdori.autoplanner.ui.home

import android.Manifest
import android.content.ContentValues
import android.content.Intent
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
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.toyproject.testproject3_zipdori.ui.home.CalendarAdapter
import com.zipdori.autoplanner.Consts
import com.zipdori.autoplanner.Consts.Companion.FLAG_PERM_CAMERA
import com.zipdori.autoplanner.Consts.Companion.FLAG_PERM_STORAGE
import com.zipdori.autoplanner.R
import com.zipdori.autoplanner.databinding.FragmentHomeBinding
import com.zipdori.autoplanner.modules.calendarprovider.EventsVO
import com.zipdori.autoplanner.modules.database.AutoPlannerDBModule
import com.zipdori.autoplanner.schedulegenerator.SetScheduleActivity
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

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

    private lateinit var autoPlannerDBModule: AutoPlannerDBModule

    val schedules: HashMap<String, ArrayList<EventsVO>> = HashMap()

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
        val calendar: Calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, 1902)
        calendar.set(Calendar.MONTH, 0)
        calendar.set(Calendar.DATE, 1)
        for (i in 0 until 2400) {
            calendarAdapterArrayList.add(CalendarAdapter(context!!, calendar, schedules))
            calendar.add(Calendar.MONTH, 1)
        }

        // ViewPager2 어댑터 및 초기 설정
        viewPager2.adapter = ViewPager2Adapter(context!!, calendarAdapterArrayList)
        viewPager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tvYYYYMM.text = (viewPager2.adapter as ViewPager2Adapter).calendarAdapterArrayList.get(position).getTitle()

                super.onPageSelected(position)
            }
        })
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
        viewPager2.setCurrentItem(monthDiff, smoothScroll)
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
                //pManager.openCamera()
            }
            R.id.fab_gallery -> {
                toggleFab()
                Toast.makeText(context, "fab gallery clicked", Toast.LENGTH_SHORT).show()

                val NEED_PERMISSIONS = arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )

                requestPermissions(NEED_PERMISSIONS, FLAG_PERM_STORAGE)
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

                intent.putExtra("FromDate", fromCal.timeInMillis)
                intent.putExtra("ToDate", toCal.timeInMillis)
                startActivity(intent)
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
                Toast.makeText(
                    context,
                    "아직 넘길 액티비티가 없어요. (카메라)",
                    Toast.LENGTH_SHORT
                ).show()
            }
            Consts.GET_GALLERY_IMAGE ->{
                Toast.makeText(
                    context,
                    "아직 넘길 액티비티가 없어요. (갤러리)",
                    Toast.LENGTH_SHORT
                ).show()
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
                val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                val uri: Uri? = createImageUri("JPEG_${timeStamp}_", "image/jpeg")
                val selectedImageUri = uri

                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImageUri)
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
        }
    }

    private fun createImageUri(filename: String, mimeType: String): Uri? {
        var values = ContentValues()
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType)
        return requireActivity().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )
    }
}