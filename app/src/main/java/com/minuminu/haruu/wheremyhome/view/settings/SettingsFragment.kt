package com.minuminu.haruu.wheremyhome.view.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.minuminu.haruu.wheremyhome.R

class SettingsFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = SettingsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        view.findViewById<Button>(R.id.btn_setting_qna)?.setOnClickListener { _ ->
            findNavController().navigate(
                R.id.action_SettingsFragment_to_QnaListFragment
            )
        }

        return view
    }

}