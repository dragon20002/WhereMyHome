package com.minuminu.haruu.wheremyhome.view.evalformgrouplist

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.minuminu.haruu.wheremyhome.db.data.EvalFormGroup

object EvalFormGroupListBindingAdapter {

    @BindingAdapter("evalFormGroups")
    @JvmStatic
    fun setEvalFormGroupList(recyclerView: RecyclerView, evalFormGroups: List<EvalFormGroup>) {
        val adapter = recyclerView.adapter as EvalFormGroupRecyclerViewAdapter
        adapter.submitList(ArrayList<EvalFormGroup>().apply {
            addAll(evalFormGroups) // diffUtil이 동작하도록 새로운 Array에 넣어줘야 한다.
        })
    }
}