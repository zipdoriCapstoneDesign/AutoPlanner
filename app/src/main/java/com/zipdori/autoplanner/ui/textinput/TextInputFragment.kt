package com.zipdori.autoplanner.ui.textinput

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.zipdori.autoplanner.Consts
import com.zipdori.autoplanner.R
import com.zipdori.autoplanner.databinding.FragmentTextInputBinding
import com.zipdori.autoplanner.modules.calendarprovider.CalendarProviderModule
import com.zipdori.autoplanner.modules.calendarprovider.EventsVO
import com.zipdori.autoplanner.modules.common.CommonModule
import com.zipdori.autoplanner.modules.common.NameEntity
import com.zipdori.autoplanner.modules.database.AutoPlannerDBModule
import com.zipdori.autoplanner.modules.database.EventExtraInfoVO
import com.zipdori.autoplanner.schedulegenerator.ListupSchedulecellActivity
import com.zipdori.autoplanner.schedulegenerator.dateparser.DateParser
import java.util.*
import kotlin.collections.ArrayList

class TextInputFragment : Fragment() {

    private lateinit var textInputViewModel: TextInputViewModel
    private var _binding: FragmentTextInputBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var etInput: EditText
    private lateinit var btnOK: Button

    private lateinit var getResultSetSchedule: ActivityResultLauncher<Intent>

    private lateinit var autoPlannerDBModule: AutoPlannerDBModule

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        textInputViewModel =
            ViewModelProvider(this).get(TextInputViewModel::class.java)

        _binding = FragmentTextInputBinding.inflate(inflater, container, false)
        val root: View = binding.root

        etInput = binding.etInputTextInput
        btnOK = binding.btOkTextInput

        autoPlannerDBModule = AutoPlannerDBModule(context)

        getResultSetSchedule = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Consts.RESULT_SCHEDULELIST_REG) {
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
                    // 체크했던 이벤트를 캘린더에 넣는 작업
                    // 일단 체크한 일정은 모두 넣지만, 나중에 일정 중복 감지 기능을 고려할 여지가 있음
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

                    Log.e("들어온 인덱스",id.toString())
                    Log.e("들어온 사진uri",tempEventExtra.photo.toString())
                    autoPlannerDBModule.insertExtraInfo(tempEventExtra.event_id,tempEventExtra.photo.toString())
                }

                // 확인용 : DB 확인하고 초기화
                autoPlannerDBModule.selectAllExtraInfo()
                autoPlannerDBModule.initExtraInfo()

                view!!.findNavController().navigate(R.id.action_nav_text_input_to_nav_calendar)
            }
        }

        btnOK.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setView(R.layout.dialog_progress)
                .setCancelable(false)
            val alertDialog = builder.create()
            alertDialog.show()

            val commonModule: CommonModule = CommonModule(context!!)
            val text = etInput.text.toString()

            Thread {
                val nameEntities : ArrayList<NameEntity> = commonModule.callNerApi(text)

                nameEntities.forEach {
                    Log.i("NameEntities", "text : " + it.text + ", type : " + it.type)
                }

                val colors: ArrayList<String> = ArrayList()

                colors.add("#5EAEFF")
                colors.add("#82B926")
                colors.add("#a276eb")
                colors.add("#6a3ab2")
                colors.add("#666666")
                colors.add("#FFFF00")
                colors.add("#3C8D2F")
                colors.add("#FA9F00")
                colors.add("#FF0000")

                val uri: Uri = Uri.parse("")
                val parser = DateParser(context!!)
                parser.setSource(nameEntities)
                parser.setUri(uri)
                parser.extractAsDate(colors[(0 until colors.size).random()])

                alertDialog.dismiss()
                if (parser.events.isEmpty()) {
                    val handler: Handler = Handler(Looper.getMainLooper())
                    handler.postDelayed(kotlinx.coroutines.Runnable {
                        Toast.makeText(context, "감지된 일정이 없습니다.", Toast.LENGTH_SHORT).show()
                    }, 0)
                } else {
                    val intent = Intent(context, ListupSchedulecellActivity::class.java)
                    intent.putParcelableArrayListExtra("imgURIs", parser.imgUris)
                    intent.putParcelableArrayListExtra("events", parser.events)
                    getResultSetSchedule.launch(intent)
                }
            }.start()
        }
        val calendar: Calendar = Calendar.getInstance()
        calendar.get(Calendar.HOUR)
        calendar.set(Calendar.HOUR, 23)

        calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + 2)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}