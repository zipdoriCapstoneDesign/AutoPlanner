package com.zipdori.autoplanner.ui.textinput

import android.content.Intent
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
import com.zipdori.autoplanner.modules.CommonModule
import com.zipdori.autoplanner.schedulegenerator.ListupSchedulecellActivity

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
                commonModule.callNerApi(text)

                // TODO: 2022-05-25 정규표현식 적용 후 일정감지 
                val intent = Intent(context, ListupSchedulecellActivity::class.java)
                // intent.putParcelableArrayListExtra("imgURIs", imgList)
                getResultSetSchedule.launch(intent)


            }.start()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}