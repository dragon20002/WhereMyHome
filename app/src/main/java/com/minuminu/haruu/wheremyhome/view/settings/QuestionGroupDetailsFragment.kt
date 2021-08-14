package com.minuminu.haruu.wheremyhome.view.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.minuminu.haruu.wheremyhome.R

class QuestionGroupDetailsFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = QuestionGroupDetailsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_question_group_details, container, false)
    }

}