package com.minuminu.haruu.wheremyhome.view.evalformgrouplist

import androidx.recyclerview.widget.DiffUtil
import com.minuminu.haruu.wheremyhome.db.data.EvalFormGroup

class EvalFormGroupDiffUtilCallback : DiffUtil.ItemCallback<EvalFormGroup>() {
    override fun areItemsTheSame(oldItem: EvalFormGroup, newItem: EvalFormGroup) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: EvalFormGroup, newItem: EvalFormGroup) =
        oldItem.toString() == newItem.toString()
}