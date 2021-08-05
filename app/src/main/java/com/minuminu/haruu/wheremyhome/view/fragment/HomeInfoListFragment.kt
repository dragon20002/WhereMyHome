package com.minuminu.haruu.wheremyhome.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.minuminu.haruu.wheremyhome.R
import com.minuminu.haruu.wheremyhome.databinding.FragmentHomeInfoListBinding
import com.minuminu.haruu.wheremyhome.db.AppDatabase
import com.minuminu.haruu.wheremyhome.viewmodel.HomeInfoListViewModel

/**
 * A fragment representing a list of Items.
 */
class HomeInfoListFragment : Fragment() {

    companion object {
        fun newInstance(columnCount: Int) = HomeInfoListFragment()
    }

    private var list: RecyclerView? = null
    private var viewModel: HomeInfoListViewModel? = null
    private var binding: FragmentHomeInfoListBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(HomeInfoListViewModel::class.java).apply {
            init(AppDatabase.getDatabase((requireContext())))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_home_info_list, container, false
        )
        binding?.viewModel = viewModel
        val view = binding?.root

        list = view?.findViewById<RecyclerView>(R.id.list)?.apply {
            this.layoutManager = LinearLayoutManager(context)
            // this@apply.adapter = adapter
        }

        view?.findViewById<FloatingActionButton>(R.id.fab)?.setOnClickListener {
            findNavController().navigate(
                R.id.action_HomeInfoListFragment_to_HomeInfoDetailsFragment,
                Bundle().apply {
                    putLong("homeInfoId", -1L)
                })
        }

        viewModel?.run {
            homeInfoListLiveData.observe(viewLifecycleOwner, {
                homeInfoList.clear()
                homeInfoList.addAll(it)
            })

            loadHomeInfoList()
        }

        return view
    }

}