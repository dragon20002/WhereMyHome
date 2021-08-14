package com.minuminu.haruu.wheremyhome.view.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.minuminu.haruu.wheremyhome.R

class QuestionGroupListFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = QuestionGroupListFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_question_group_list, container, false)
    }

}