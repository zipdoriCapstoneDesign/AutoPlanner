package com.zipdori.autoplanner.ui.calendar

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.functions.FirebaseFunctions
import com.google.gson.*
import com.toyproject.testproject3_zipdori.ui.home.MonthAdapter
import com.zipdori.autoplanner.Consts
import com.zipdori.autoplanner.Consts.Companion.FLAG_PERM_CAMERA
import com.zipdori.autoplanner.Consts.Companion.FLAG_PERM_STORAGE_MULTIPICK
import com.zipdori.autoplanner.R
import com.zipdori.autoplanner.databinding.FragmentCalendarBinding
import com.zipdori.autoplanner.modules.calendarprovider.CalendarProviderModule
import com.zipdori.autoplanner.modules.calendarprovider.EventsVO
import com.zipdori.autoplanner.modules.common.App
import com.zipdori.autoplanner.modules.common.CommonModule
import com.zipdori.autoplanner.modules.common.NameEntity
import com.zipdori.autoplanner.modules.database.AutoPlannerDBModule
import com.zipdori.autoplanner.modules.database.EventExtraInfoVO
import com.zipdori.autoplanner.schedulegenerator.ListupSchedulecellActivity
import com.zipdori.autoplanner.schedulegenerator.SetScheduleActivity
import com.zipdori.autoplanner.schedulegenerator.dateparser.DateParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CalendarFragment : Fragment(), View.OnClickListener {

    private lateinit var calendarViewModel: CalendarViewModel
    private var _binding: FragmentCalendarBinding? = null

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

    var singleUri:Uri? = null //????????? ????????? ????????? ?????? ?????? ????????? uri ??????????????? ??????, ?????? ???????????? ??????

    private lateinit var commonModule: CommonModule

    private lateinit var getResultSetSchedule: ActivityResultLauncher<Intent>
    val monthAdapterArrayList: ArrayList<MonthAdapter> = ArrayList()

    private lateinit var functions: FirebaseFunctions

    private lateinit var onBackPressedCallback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        calendarViewModel =
            ViewModelProvider(this).get(CalendarViewModel::class.java)

        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
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
                val tempEventsVO: EventsVO = result.data?.getParcelableExtra("scheduleItem")!!
                val tempEventExtraVO: EventExtraInfoVO = result.data?.getParcelableExtra("scheduleItemExtra")!!
                val sharedPreferences: SharedPreferences =
                    requireActivity().getSharedPreferences(
                        getString(R.string.preference_file_key),
                        Context.MODE_PRIVATE
                    )
                val calendarId =
                    sharedPreferences.getLong(getString(R.string.calendar_index), 0)
                val id = tempEventsVO.id
                val title = tempEventsVO.title
                val eventLocation = tempEventsVO.eventLocation
                val description = tempEventsVO.description
                val eventColor = tempEventsVO.eventColor
                val dtStart = tempEventsVO.dtStart
                val dtEnd = tempEventsVO.dtEnd!!
                val duration = tempEventsVO.duration
                val allDay = tempEventsVO.allDay
                val rRule = tempEventsVO.rRule
                val rDate = tempEventsVO.rDate
                val eventTimeZone = "UTC"

                // id??? -1?????? ????????? Event??? ???????????? ????????? ??????
                val calendarProviderModule =
                    CalendarProviderModule(requireActivity().applicationContext)
                if (id.equals((-1).toLong())) {
                    val eventId = calendarProviderModule.insertEvent(
                        calendarId,
                        title,
                        eventLocation,
                        description,
                        eventColor,
                        dtStart,
                        dtEnd,
                        eventTimeZone,
                        duration,
                        allDay,
                        rRule,
                        rDate
                    )
                    tempEventExtraVO.event_id = eventId
                    autoPlannerDBModule.insertExtraInfo(tempEventExtraVO.event_id,tempEventExtraVO.photo.toString())
                } else {
                    calendarProviderModule.updateEvent(
                        id,
                        title,
                        eventLocation,
                        description,
                        eventColor,
                        dtStart,
                        dtEnd,
                        eventTimeZone,
                        duration,
                        allDay,
                        rRule,
                        rDate
                    )
                    autoPlannerDBModule.updateExtraInfo(tempEventExtraVO._id, tempEventExtraVO.event_id, tempEventExtraVO.photo.toString())
                }

                val schedules: HashMap<String, ArrayList<EventsVO>> = commonModule.getAllEventsAsHashmap()
                for (monthAdapter in monthAdapterArrayList) {
                    monthAdapter.schedules = schedules
                    if (monthAdapter.scheduleListAdapter != null) {
                        val date: Date = monthAdapter.scheduleListAdapter!!.getDate()
                        var tempEventsVOArrayList: ArrayList<EventsVO>? = schedules.get(SimpleDateFormat("yyyy.MM.dd", Locale.US).format(date))
                        if (tempEventsVOArrayList == null) {
                            tempEventsVOArrayList = ArrayList()
                        }
                        monthAdapter.scheduleListAdapter!!.setEventsVOArrayList(tempEventsVOArrayList)
                        monthAdapter.scheduleListAdapter!!.notifyDataSetChanged()
                    }
                }

                // TODO: 2022-04-06 ??? ???????????? ?????? ???????????????
                val calendar: Calendar = Calendar.getInstance()
                calendar.timeInMillis = tempEventsVO.dtStart
                drawCalendar()
                setViewPager2Position(calendar, false)
            }
            else if (result.resultCode == Consts.RESULT_SCHEDULELIST_REG) { // ?????????????????? ?????????????????? ????????? ???????????? ???????????? ???
                val tempEventList: ArrayList<EventsVO> =
                    result.data?.getParcelableArrayListExtra("checkedList")!!
                val tempEventListExtraVO: ArrayList<EventExtraInfoVO> =
                    result.data?.getParcelableArrayListExtra("checkedListExtra")!!

                val sharedPreferences: SharedPreferences =
                    requireActivity().getSharedPreferences(
                        getString(R.string.preference_file_key),
                        Context.MODE_PRIVATE
                    )
                val calendarId =
                    sharedPreferences.getLong(getString(R.string.calendar_index), 0)
                val calendarProviderModule =
                    CalendarProviderModule(requireActivity().applicationContext)

                val it = tempEventList.iterator()
                val itExtra = tempEventListExtraVO.iterator()
                while (it.hasNext()) {
                    // ???????????? ???????????? ???????????? ?????? ??????
                    // ?????? ????????? ????????? ?????? ?????????, ????????? ?????? ?????? ?????? ????????? ????????? ????????? ??????
                    val tempEvent = it.next()
                    val tempEventExtra = itExtra.next()
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

                    val eventId = calendarProviderModule.insertEvent(
                        calendarId,
                        title,
                        eventLocation,
                        description,
                        eventColor,
                        dtStart,
                        dtEnd,
                        eventTimeZone,
                        duration,
                        allDay,
                        rRule,
                        rDate
                    )
                    tempEventExtra.event_id = eventId

                    Log.e("????????? ?????????",id.toString())
                    Log.e("????????? ??????uri",tempEventExtra.photo.toString())
                    autoPlannerDBModule.insertExtraInfo(tempEventExtra.event_id,tempEventExtra.photo.toString())
                }

                // ????????? : DB ???????????? ?????????
                autoPlannerDBModule.selectAllExtraInfo()
                autoPlannerDBModule.initExtraInfo()

            }
        }

        fabAI.setOnClickListener(this)
        fabPhoto.setOnClickListener(this)
        fabGallery.setOnClickListener(this)
        fabText.setOnClickListener(this)
        fabAdd.setOnClickListener(this)

        autoPlannerDBModule = AutoPlannerDBModule(context)

        functions = FirebaseFunctions.getInstance()

        onBackPressedCallback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    finishAffinity(activity as Activity)
                    // System.runFinalization()
                    // System.exit(0)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_home, menu)
    }

    override fun onResume() {
        super.onResume()

        drawCalendar()
        setViewPager2CurMonth(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
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
                Log.e("??????????????????",Manifest.permission.READ_EXTERNAL_STORAGE.toString() + " / " + Manifest.permission.WRITE_EXTERNAL_STORAGE.toString())
                val NEED_PERMISSIONS = arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )

                requestPermissions(NEED_PERMISSIONS, FLAG_PERM_CAMERA)

            }
            R.id.fab_gallery -> {
                toggleFab()

                val NEED_PERMISSIONS = arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )

                requestPermissions(NEED_PERMISSIONS, FLAG_PERM_STORAGE_MULTIPICK)

            }
            R.id.fab_text -> {
                toggleFab()
                view.findNavController().navigate(R.id.action_nav_calendar_to_nav_text_input)
            }
            R.id.fab_add -> {
                val intent = Intent(context, SetScheduleActivity::class.java)

                //SetSchedule ???????????? ?????? ?????? ???????????? ?????? ???????????? ?????? ?????????
                val fromCal = Calendar.getInstance()
                val toCal = Calendar.getInstance()

                // ?????? ?????? ?????? ????????? ??????????????? +1???????????? ?????? ??????
                fromCal.add(Calendar.HOUR, 1)
                fromCal.set(Calendar.MINUTE, 0)
                toCal.add(Calendar.HOUR, 2)
                toCal.set(Calendar.MINUTE, 0)

                val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                val calendarId = sharedPreferences.getLong(getString(R.string.calendar_index), 0)
                val tempEvent = EventsVO(-1, calendarId,null,null,null,null,-10572033,-10572033,fromCal.timeInMillis,toCal.timeInMillis,"UTC",null,null,null,null,null,null,null)
                val tempEventExtra = EventExtraInfoVO(0,0,null)
                intent.putExtra("SingleScheduleData", tempEvent)
                intent.putExtra("SingleScheduleDataExtra", tempEventExtra)

                getResultSetSchedule.launch(intent)
            }
        }
    }

    private fun toggleFab() {
        if (isFabOpen) {
            //fabAI.setImageResource(R.drawable.ic_baseline_add_24_black)
            fabPhoto.startAnimation(fabClose)
            fabGallery.startAnimation(fabClose)
            fabText.startAnimation(fabClose)
            fabPhoto.setClickable(false)
            fabGallery.setClickable(false)
            fabText.setClickable(false)
            isFabOpen = false
        } else {
            //fabAI.setImageResource(R.drawable.ic_baseline_close_24_black)
            fabPhoto.startAnimation(fabOpen)
            fabGallery.startAnimation(fabOpen)
            fabText.startAnimation(fabOpen)
            fabPhoto.setClickable(true)
            fabGallery.setClickable(true)
            fabText.setClickable(true)
            isFabOpen = true
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            Consts.FLAG_REQ_CAMERA ->{
                if(resultCode == Activity.RESULT_OK) {
                    Log.i("Image uri", singleUri.toString())

                    var imgTextMap = mutableMapOf<Uri, String>()
                    imgTextMap.clear()

                    imgTextMap[singleUri!!] = ""

                    autoPlanning(imgTextMap)
                }
                else
                    requireActivity().contentResolver.delete(singleUri!!,null,null)
            }
            Consts.GET_GALLERY_IMAGE_MULTI->{
                var imgTextMap = mutableMapOf<Uri, String>()
                imgTextMap.clear()
                if(data?.clipData != null){
                    val count = data.clipData!!.itemCount
                    Log.e("????????? ?????? ???",count.toString())
                    if (count > 5) {
                        Toast.makeText(App.context(), "????????? 5????????? ?????? ???????????????.", Toast.LENGTH_LONG).show()
                        return
                    }
                    for (i in 0 until count) {
                        val imageUri = data.clipData!!.getItemAt(i).uri
                        imgTextMap[imageUri] = ""
                        Log.e("imgList??? ?????? ??????", imageUri.toString())
                    }
                } else { // ?????? ??????
                    data?.data?.let { uri ->
                        val imageUri : Uri? = data?.data
                        if (imageUri != null) {
                            imgTextMap[imageUri] = ""
                        }
                    }
                }

                autoPlanning(imgTextMap)
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
                Log.e("????????????","?????????")
                for (grant in grantResults) {
                    Log.e("????????????",grant.toString())
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(
                            context,
                            "????????? ????????? ?????????????????? ???????????? ????????? ??? ????????????. ??? ???????????? ??????????????????.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }
                }
                Log.e("????????????","????????? ?????? ??????")
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
                        //????????? ???????????? ???????????? return ??? ???????????? ???????????? ???????????? ?????????
                        Toast.makeText(
                            context,
                            "????????? ????????? ?????????????????? ?????? ????????? ??? ????????????..",
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
                        //????????? ???????????? ???????????? return ??? ???????????? ???????????? ???????????? ?????????
                        Toast.makeText(
                            context,
                            "????????? ????????? ?????????????????? ?????? ????????? ??? ????????????..",
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

    override fun onDetach() {
        super.onDetach()
        onBackPressedCallback.remove()
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
        // GridView ??? ?????? CalendarAdapter
        monthAdapterArrayList.clear()
        val calendar: Calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, 1902)
        calendar.set(Calendar.MONTH, 0)
        calendar.set(Calendar.DATE, 1)
        val schedules: HashMap<String, ArrayList<EventsVO>> = commonModule.getAllEventsAsHashmap()
        for (i in 0 until 2400) {
            val monthAdapter: MonthAdapter = MonthAdapter(context!!, calendar, schedules, getResultSetSchedule)
            monthAdapter.setOnEventsChangeListener(object : ScheduleListAdapter.OnEventsChangeListener {
                override fun onEventsChange(calendar: Calendar) {
                    drawCalendar()
                    setViewPager2Position(calendar, false)
                }
            })
            monthAdapterArrayList.add(monthAdapter)
            calendar.add(Calendar.MONTH, 1)
        }

        // ViewPager2 ????????? ??? ?????? ??????
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun encodeImgToBase64(imgUri: Uri): String {
        var bitmap: Bitmap = MediaStore.Images.Media.getBitmap(context!!.contentResolver, imgUri)

        val byteArrayOutputStream = ByteArrayOutputStream()
        // TODO: 2022-06-01 ????????? ?????? ??????
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
        val imageBytes: ByteArray = byteArrayOutputStream.toByteArray()
        val base64encoded = Base64.getEncoder().encodeToString(imageBytes)

        return base64encoded
    }

    private fun makeJsonRequest(base64encoded: String) : JsonObject {
        // Create json request to cloud vision
        val request = JsonObject()
        // Add image to request
        val image = JsonObject()
        image.add("content", JsonPrimitive(base64encoded))
        request.add("image", image)
        //Add features to the request
        val feature = JsonObject()
        feature.add("type", JsonPrimitive("TEXT_DETECTION"))
        // Alternatively, for DOCUMENT_TEXT_DETECTION:
        // feature.add("type", JsonPrimitive("DOCUMENT_TEXT_DETECTION"))
        val features = JsonArray()
        features.add(feature)
        request.add("features", features)

        // Add language hints
        val imageContext = JsonObject()
        val languageHints = JsonArray()
        languageHints.add("ko")
        imageContext.add("languageHints", languageHints)
        request.add("imageContext", imageContext)

        return request
    }

    private fun annotateImage(requestJson: String): Task<JsonElement> {
        return functions
            .getHttpsCallable("annotateImage")
            .call(requestJson)
            .continueWith { task ->
                // This continuation runs on either success or failure, but if the task
                // has failed then result will throw an Exception which will be
                // propagated down.
                val result = task.result?.data
                JsonParser.parseString(Gson().toJson(result))
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun autoPlanning(imgTextMap: MutableMap<Uri, String>) {
        if(imgTextMap.isNotEmpty()) {
            val builder = AlertDialog.Builder(context)
            builder.setView(R.layout.dialog_progress)
                .setCancelable(false)
            val alertDialog = builder.create()
            alertDialog.show()

            CoroutineScope(Dispatchers.IO).launch {
                for (img in imgTextMap) {
                    val base64encoded = encodeImgToBase64(img.key)
                    val request = makeJsonRequest(base64encoded)

                    val tempTask = annotateImage(request.toString())
                        .addOnCompleteListener { task ->
                            if (!task.isSuccessful) {
                                Log.e("Text recognition", "failed")
                            } else {
                                Log.i("Text recognition", "success")

                                try {
                                    val annotation = task.result!!.asJsonArray[0].asJsonObject["fullTextAnnotation"].asJsonObject
                                    System.out.format("%nComplete annotation:")
                                    System.out.format("%n%s", annotation["text"].asString)
                                    imgTextMap[img.key] = annotation["text"].asString

                                    /*
                                    // word, paragraph, block ?????????
                                    for (page in annotation["pages"].asJsonArray) {
                                        var pageText = ""
                                        for (block in page.asJsonObject["blocks"].asJsonArray) {
                                            var blockText = ""
                                            for (para in block.asJsonObject["paragraphs"].asJsonArray) {
                                                var paraText = ""
                                                for (word in para.asJsonObject["words"].asJsonArray) {
                                                    var wordText = ""
                                                    for (symbol in word.asJsonObject["symbols"].asJsonArray) {
                                                        wordText += symbol.asJsonObject["text"].asString
                                                        // System.out.format("Symbol text: %s (confidence: %f)%n",
                                                        //     symbol.asJsonObject["text"].asString, symbol.asJsonObject["confidence"].asFloat)
                                                    }
                                                    System.out.format("Word text: %s (confidence: %f)%n%n", wordText,
                                                        word.asJsonObject["confidence"].asFloat)
                                                    System.out.format("Word bounding box: %s%n", word.asJsonObject["boundingBox"])
                                                    paraText = String.format("%s%s ", paraText, wordText)
                                                }
                                                System.out.format("%nParagraph: %n%s%n", paraText)
                                                System.out.format("Paragraph bounding box: %s%n", para.asJsonObject["boundingBox"])
                                                System.out.format("Paragraph Confidence: %f%n", para.asJsonObject["confidence"].asFloat)
                                                blockText += paraText
                                            }
                                            System.out.format("%nBlock: %n%s%n", blockText)
                                            System.out.format("Block bounding box: %s%n", block.asJsonObject["boundingBox"])
                                            System.out.format("Block Confidence: %f%n", block.asJsonObject["confidence"].asFloat)
                                            pageText += blockText
                                        }
                                        System.out.format("%nPage: %n%s%n", pageText)
                                        System.out.format("Page bounding box: %s%n", page.asJsonObject["boundingBox"])
                                        System.out.format("Page Confidence: %f%n", page.asJsonObject["confidence"].asFloat)
                                    }
                                     */
                                } catch (e: NullPointerException) {
                                    e.printStackTrace()
                                    Log.e("Text recognition", "The Image has no text.")
                                }
                            }
                        }.await()
                }

                /*
                imgTextMap.forEach {
                    Log.i("URI", it.key.toString())
                    Log.i("STRING", it.value)
                }

                 */

                Thread {
                    val imgEntitiesMap: MutableMap<Uri, ArrayList<NameEntity>> = mutableMapOf<Uri, ArrayList<NameEntity>>()
                    imgTextMap.forEach {
                        val nameEntity: ArrayList<NameEntity> = commonModule.callNerApi(it.value)
                        if (nameEntity.isNotEmpty()) {
                            imgEntitiesMap[it.key] = nameEntity

                            Log.i("Uri", it.key.toString())
                            nameEntity.forEach {
                                Log.i("NameEntity", "TEXT : " + it.text + ", TYPE : " + it.type)
                            }
                        }
                    }

                    alertDialog.dismiss()

                    if (imgEntitiesMap.isEmpty()) {
                        val handler: Handler = Handler(Looper.getMainLooper())
                        handler.postDelayed(kotlinx.coroutines.Runnable {
                            Toast.makeText(context, "????????? ????????? ????????????.", Toast.LENGTH_SHORT).show()
                        }, 0)
                    } else {
                        val colors: ArrayList<String> = ArrayList()

                        colors.add("#5EAEFF")
                        colors.add("#82B926")
                        colors.add("#a276eb")
                        // colors.add("#FFFF00")
                        colors.add("#FA9F00")
                        colors.add("#FF0000")

                        colors.add("#5EAEFF")
                        colors.add("#82B926")
                        colors.add("#a276eb")
                        // colors.add("#FFFF00")
                        colors.add("#FA9F00")
                        colors.add("#FF0000")

                        colors.add("#5EAEFF")
                        colors.add("#82B926")
                        colors.add("#a276eb")
                        // colors.add("#FFFF00")
                        colors.add("#FA9F00")
                        colors.add("#FF0000")

                        colors.add("#5EAEFF")
                        colors.add("#82B926")
                        colors.add("#a276eb")
                        // colors.add("#FFFF00")
                        colors.add("#FA9F00")
                        colors.add("#FF0000")

                        val parser = DateParser(context!!)
                        var colorIdx = 0
                        imgEntitiesMap.forEach {
                            parser.setSource(it.value)
                            parser.setUri(it.key)
                            parser.extractAsDate(colors[colorIdx])
                            colorIdx += 1
                        }

                        val calendar = Calendar.getInstance()
                        parser.events.forEach {
                            calendar.timeInMillis = it.dtStart
                            Log.i("Event Title", SimpleDateFormat("yyyy.MM.dd").format(calendar.time))
                        }

                        if (parser.events.isEmpty()) {
                            val handler: Handler = Handler(Looper.getMainLooper())
                            handler.postDelayed(kotlinx.coroutines.Runnable {
                                Toast.makeText(context, "????????? ????????? ????????????.", Toast.LENGTH_SHORT).show()
                            }, 0)
                        } else {
                            val intent = Intent(context, ListupSchedulecellActivity::class.java)
                            intent.putParcelableArrayListExtra("imgURIs", parser.imgUris)
                            intent.putParcelableArrayListExtra("events", parser.events)
                            getResultSetSchedule.launch(intent)
                        }
                    }
                }.start()
            }
        }

    }
}