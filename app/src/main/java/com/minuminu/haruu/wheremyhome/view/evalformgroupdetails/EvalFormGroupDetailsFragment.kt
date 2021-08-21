package com.minuminu.haruu.wheremyhome.view.evalformgroupdetails

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.minuminu.haruu.wheremyhome.R
import com.minuminu.haruu.wheremyhome.databinding.FragmentEvalFormGroupDetailsBinding
import com.minuminu.haruu.wheremyhome.db.AppDatabase
import com.minuminu.haruu.wheremyhome.db.data.EvalFormGroup
import com.minuminu.haruu.wheremyhome.db.data.EvalFormViewData
import kotlinx.coroutines.launch

class EvalFormGroupDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = EvalFormGroupDetailsFragment()
    }

    private var viewModel: EvalFormGroupDetailsViewModel? = null
    private var binding: FragmentEvalFormGroupDetailsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(EvalFormGroupDetailsViewModel::class.java).apply {
            init(AppDatabase.getDatabase(requireContext()))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_eval_form_group_details, container, false
        )
        binding?.viewModel = viewModel
        val view = binding?.root

        view?.findViewById<Button>(R.id.btn_go_to_list)?.setOnClickListener {
            findNavController().popBackStack()
        }
        view?.findViewById<Button>(R.id.btn_edit)?.setOnClickListener {
            viewModel?.isEditing?.set(true)
        }
        view?.findViewById<Button>(R.id.btn_cancel)?.setOnClickListener {
            val evalFormGroupId = arguments?.getLong("evalFormGroupId")
            Log.d(javaClass.name, "evalFormGroupId $evalFormGroupId")

            when (evalFormGroupId) {
                null -> {
                    Snackbar.make(view, "존재하지 않는 평가 템플릿입니다.", Snackbar.LENGTH_SHORT).show()
                }
                -1L -> {
                    findNavController().popBackStack()
                }
                else -> {
                    viewModel?.loadEvalFormGroup(evalFormGroupId)
                    viewModel?.isEditing?.set(false)
                }
            }
        }
        view?.findViewById<Button>(R.id.btn_done)?.setOnClickListener {
            viewModel?.viewModelScope?.launch {
                val evalFormGroup = viewModel?.saveItem()
                Log.d(javaClass.name, "evalFormGroup $evalFormGroup")

                findNavController().popBackStack()
            }
        }
        view?.findViewById<RecyclerView>(R.id.eval_form_list)?.apply {
            this.layoutManager = LinearLayoutManager(context)
            this.adapter = viewModel?.let { EvalFormRecyclerViewAdapter(it) }
        }
        view?.findViewById<Button>(R.id.btn_add)?.setOnClickListener {
            viewModel?.evalFormList?.let { evalFormList ->
                evalFormList.add(
                    EvalFormViewData(
                        null, "", (evalFormList.size + 1).toString(), "", "", "1.0", null
                    )
                )
            }
        }

        viewModel?.run {
            evalFormGroupLiveData.observe(viewLifecycleOwner, { evalFormGroup ->
                name.set(evalFormGroup.name)
                description.set(evalFormGroup.description)

                evalFormGroup.id?.let { evalFormGroupId ->
                    loadEvalFormList(evalFormGroupId)
                }
            })

            evalFormListLiveData.observe(viewLifecycleOwner, { _evalFormList ->
                evalFormList.clear()
                evalFormList.addAll(_evalFormList)
            })
        }

        val evalFormGroupId = arguments?.getLong("evalFormGroupId")
        Log.d(javaClass.name, "evalFormGroupId $evalFormGroupId")

        when (evalFormGroupId) {
            null -> {
                view?.let { _view ->
                    Snackbar.make(_view, "존재하지 않는 평가 템플릿입니다.", Snackbar.LENGTH_SHORT).show()
                }
            }
            -1L -> {
                Log.d(javaClass.name, "add mode")

                // Create temporary data
                viewModel?.isEditing?.set(true)
                viewModel?.evalFormGroupLiveData?.postValue(EvalFormGroup(null, "새 평가 템플릿", ""))
                viewModel?.evalFormListLiveData?.postValue(ArrayList())
            }
            else -> {
                Log.d(javaClass.name, "modify mode")

                (evalFormGroupId as Long?)?.let { _evalFormGroupId ->
                    viewModel?.loadEvalFormGroup(_evalFormGroupId)
                }
            }
        }

        return view
    }

}