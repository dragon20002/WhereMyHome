package com.minuminu.haruu.wheremyhome.view.evalformgroupdetails

import androidx.recyclerview.widget.DiffUtil
import com.minuminu.haruu.wheremyhome.db.data.EvalFormViewData

class EvalFormDiffUtilCallback : DiffUtil.ItemCallback<EvalFormViewData>() {
    override fun areItemsTheSame(oldItem: EvalFormViewData, newItem: EvalFormViewData) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: EvalFormViewData, newItem: EvalFormViewData) =
        oldItem.toString() == newItem.toString()
}