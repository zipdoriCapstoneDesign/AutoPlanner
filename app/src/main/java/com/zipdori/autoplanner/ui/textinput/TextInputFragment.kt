package com.zipdori.autoplanner.ui.textinput

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.zipdori.autoplanner.Consts
import com.zipdori.autoplanner.R
import com.zipdori.autoplanner.databinding.FragmentTextInputBinding
import com.zipdori.autoplanner.modules.calendarprovider.EventsVO
import com.zipdori.autoplanner.modules.common.CommonModule
import com.zipdori.autoplanner.modules.common.NameEntity
import com.zipdori.autoplanner.schedulegenerator.ListupSchedulecellActivity
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

        getResultSetSchedule = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Consts.RESULT_SCHEDULELIST_REG) {
                view!!.findNavController().navigate(R.id.action_nav_text_input_to_nav_calendar)
            }
        }

        btnOK.setOnClickListener {
            val commonModule: CommonModule = CommonModule(context!!)
            val text = etInput.text.toString()

            Thread {
                // TODO: 2022-05-27 정규표현식 사용하여 실제 일정등록
                val nameEntities : ArrayList<NameEntity> = commonModule.callNerApi(text)

                val imgList: ArrayList<Uri> = arrayListOf<Uri>()
                val eventList: ArrayList<EventsVO> = arrayListOf<EventsVO>()

                val intent = Intent(context, ListupSchedulecellActivity::class.java)
                intent.putParcelableArrayListExtra("imgURIs", imgList)
                intent.putParcelableArrayListExtra("events", eventList)
                getResultSetSchedule.launch(intent)


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