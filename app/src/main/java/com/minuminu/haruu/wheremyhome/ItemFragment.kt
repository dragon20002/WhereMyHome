package com.minuminu.haruu.wheremyhome

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.minuminu.haruu.wheremyhome.db.AppDatabase
import com.minuminu.haruu.wheremyhome.dummy.DummyContent

/**
 * A fragment representing a list of Items.
 */
class ItemFragment : Fragment() {

    companion object {

        const val ARG_COLUMN_COUNT = "column-count"

        @JvmStatic
        fun newInstance(columnCount: Int) =
            ItemFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }

    private var columnCount = 1
    private lateinit var list: RecyclerView
    private lateinit var adapter: MyItemRecyclerViewAdapter

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
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)

        list = view.findViewById(R.id.list)
        val fab = view.findViewById<FloatingActionButton>(R.id.fab)

        // Set the adapter
        list.layoutManager = when {
            columnCount <= 1 -> LinearLayoutManager(context)
            else -> GridLayoutManager(context, columnCount)
        }
        Thread {
            val homeInfos = AppDatabase.getDatabase(requireContext()).homeInfoDao().getAll()
            adapter = MyItemRecyclerViewAdapter(
                this@ItemFragment,
                ArrayList(homeInfos)
            )
            Handler(Looper.getMainLooper()).post {
                list.adapter = adapter
            }
        }.start()

        fab.setOnClickListener {
            findNavController().navigate(R.id.action_ItemFragment_to_ItemDetailsFragment)
        }

        findNavController().addOnDestinationChangedListener { _, dest, args ->
            when (dest.id) {
                R.id.ItemDetailsFragment -> {
                    args?.get("item")?.let {
                        Log.d(javaClass.name, "homeInfo - observe $")
                    }

                    Thread {
                        val homeInfos = AppDatabase.getDatabase(requireContext()).homeInfoDao().getAll()
                        adapter.values = ArrayList(homeInfos)
                        Handler(Looper.getMainLooper()).post {
                            adapter.notifyDataSetChanged()
                        }
                    }.start()
                }
            }
        }

        return view
    }
}