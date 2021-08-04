package com.minuminu.haruu.wheremyhome.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.minuminu.haruu.wheremyhome.R
import com.minuminu.haruu.wheremyhome.db.AppDatabase
import com.minuminu.haruu.wheremyhome.viewmodel.HomeInfoItemRecyclerViewAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A fragment representing a list of Items.
 */
class HomeInfoListFragment : Fragment() {

    companion object {

        const val ARG_COLUMN_COUNT = "column-count"

        @JvmStatic
        fun newInstance(columnCount: Int) =
            HomeInfoListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }

    private var columnCount = 1
    private var list: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home_info_list, container, false)

        list = view.findViewById<RecyclerView>(R.id.list).apply {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
            this@apply.adapter = adapter
        }

        view.findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            findNavController().navigate(R.id.action_HomeInfoListFragment_to_HomeInfoDetailsFragment)
        }

        CoroutineScope(Dispatchers.IO).launch {
            val homeInfos = AppDatabase.getDatabase(requireContext()).homeInfoDao().getAll()
            val adapter = HomeInfoItemRecyclerViewAdapter(
                this@HomeInfoListFragment,
                ArrayList(homeInfos)
            )
            launch(Dispatchers.Main) {
                list?.adapter = adapter
            }
        }

        return view
    }
}