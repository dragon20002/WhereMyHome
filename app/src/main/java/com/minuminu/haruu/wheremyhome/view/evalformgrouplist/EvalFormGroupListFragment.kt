package com.minuminu.haruu.wheremyhome.view.evalformgrouplist

import android.content.Context
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
import com.minuminu.haruu.wheremyhome.databinding.FragmentEvalFormGroupListBinding
import com.minuminu.haruu.wheremyhome.db.AppDatabase

class EvalFormGroupListFragment : Fragment() {

    companion object {
        fun newInstance() = EvalFormGroupListFragment()
    }

    private var viewModel: EvalFormGroupListViewModel? = null
    private var binding: FragmentEvalFormGroupListBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[EvalFormGroupListViewModel::class.java].apply {
            init(AppDatabase.getDatabase(requireContext()))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_eval_form_group_list, container, false
        )
        binding?.viewModel = viewModel
        val view = binding?.root

        view?.findViewById<RecyclerView>(R.id.eval_form_group_list)?.apply {
            this.layoutManager = LinearLayoutManager(context)
            this.adapter =
                EvalFormGroupRecyclerViewAdapter(this@EvalFormGroupListFragment, viewModel!!)
        }

        view?.findViewById<FloatingActionButton>(R.id.eval_form_group_fab)?.setOnClickListener {
            findNavController().navigate(
                R.id.action_EvalFormGroupListFragment_to_EvalFormGroupDetailsFragment
            )
        }

        viewModel?.run {
            evalFormGroupListLiveData.observe(viewLifecycleOwner) { _evalFormGroupList ->
                evalFormGroupList.clear()
                evalFormGroupList.addAll(_evalFormGroupList)

                activity?.getSharedPreferences("evalFormGroup", Context.MODE_PRIVATE)
                    ?.getLong("checkedId", -1L)?.let { checkedId ->
                        checked.set(checkedId)
                    }
            }

            loadEvalFormGroupList()
        }

        return view
    }

}