package com.minuminu.haruu.wheremyhome.view.homeinfodetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.minuminu.haruu.wheremyhome.R
import com.minuminu.haruu.wheremyhome.databinding.ItemEvalInfoBinding
import com.minuminu.haruu.wheremyhome.db.data.EvalInfoViewData
import com.minuminu.haruu.wheremyhome.view.homeinfodetails.components.EvalInfoRemarkDialog

class EvalInfoRecyclerViewAdapter(val viewModel: HomeInfoDetailsViewModel) :
    ListAdapter<EvalInfoViewData, EvalInfoRecyclerViewAdapter.ViewHolder>(EvalInfoDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_eval_info,
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        with(holder) {
            binding.evalInfo = item
            binding.viewModel = viewModel
            cbxResult.setOnClickListener { v ->
                val cbx = v as CheckBox
                cbx.text = when (cbx.isChecked) {
                    true -> "예"
                    else -> "아니오"
                }
            }
            btnRemark.setOnClickListener { v ->
                EvalInfoRemarkDialog().also { dialog ->
                    dialog.caller = btnRemark
                    dialog.listener = object : EvalInfoRemarkDialog.RemarkDialogListener {
                        override fun onDialogPositiveClick(
                            dialog: DialogFragment,
                            remark: String,
                            caller: View?
                        ) {
                            if (viewModel.isEditing.get() != false) {
                                item.remark = remark
                            }
                        }

                        override fun onDialogNegativeClick(dialog: DialogFragment) {
                        }
                    }
                    dialog.remark = item.remark
                    dialog.viewModel = viewModel
                }.show(
                    (v.context as FragmentActivity).supportFragmentManager,
                    "RemarkDialogFragment"
                )
            }
        }
    }

    inner class ViewHolder(val binding: ItemEvalInfoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val cbxResult: CheckBox = binding.root.findViewById(R.id.cbx_result)
        val btnRemark: Button = binding.root.findViewById(R.id.btn_remark)
    }

}