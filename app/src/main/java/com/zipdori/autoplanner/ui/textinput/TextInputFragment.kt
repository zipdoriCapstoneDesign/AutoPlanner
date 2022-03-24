package com.zipdori.autoplanner.ui.textinput

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.zipdori.autoplanner.databinding.FragmentTextInputBinding

class TextInputFragment : Fragment() {

    private lateinit var textInputViewModel: TextInputViewModel
    private var _binding: FragmentTextInputBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        textInputViewModel =
            ViewModelProvider(this).get(TextInputViewModel::class.java)

        _binding = FragmentTextInputBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}