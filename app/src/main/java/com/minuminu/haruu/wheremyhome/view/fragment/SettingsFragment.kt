package com.minuminu.haruu.wheremyhome.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.minuminu.haruu.wheremyhome.R

class SettingsFragment : Fragment() {

    private var list: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        list = view.findViewById<RecyclerView>(R.id.list).apply {
            layoutManager = LinearLayoutManager(context)
            this@apply.adapter = adapter
        }

//        CoroutineScope(Dispatchers.IO).launch {
//            val settings = AppDatabase.getDatabase(requireContext()).settingDao().getAll()
//            val adapter = ...
//        }

        return view
    }
}