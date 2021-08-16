package com.minuminu.haruu.wheremyhome.view.evalformgroupdetails

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.minuminu.haruu.wheremyhome.db.data.EvalFormViewData

object EvalFormGroupDetailsBindingAdapter {

    @BindingAdapter("evalForms")
    @JvmStatic
    fun setEvalFormList(recyclerView: RecyclerView, evalForms: List<EvalFormViewData>) {
        val adapter = recyclerView.adapter as EvalFormRecyclerViewAdapter
        adapter.submitList(ArrayList<EvalFormViewData>().apply {
            addAll(evalForms) // diffUtil이 동작하도록 새로운 Array에 넣어줘야 한다.
        })
    }

    @BindingAdapter("deleted")
    @JvmStatic
    fun setDeleted(view: View, deleted: Boolean) {
        view.alpha = when (deleted) {
            true -> 0.3f
            else -> 1f
        }
    }
}