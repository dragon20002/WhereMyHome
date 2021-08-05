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
import com.minuminu.haruu.wheremyhome.databinding.ItemQandaBinding
import com.minuminu.haruu.wheremyhome.db.data.QandaViewData
import com.minuminu.haruu.wheremyhome.view.homeinfodetails.components.QandaRemarkDialog

class QandaItemRecyclerViewAdapter(val viewModel: HomeInfoDetailsViewModel) :
    ListAdapter<QandaViewData, QandaItemRecyclerViewAdapter.ViewHolder>(QandaDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_qanda,
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        with(holder) {
            binding.qanda = item
            binding.viewModel = viewModel
            cbxAnswer.setOnClickListener { v ->
                val cbx = v as CheckBox
                cbx.text = when (cbx.isChecked) {
                    true -> "예"
                    else -> "아니오"
                }
            }
            btnRemark.setOnClickListener { v ->
                QandaRemarkDialog().also { dialog ->
                    dialog.caller = btnRemark
                    dialog.listener = object : QandaRemarkDialog.RemarkDialogListener {
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

    inner class ViewHolder(val binding: ItemQandaBinding) : RecyclerView.ViewHolder(binding.root) {
        val cbxAnswer = binding.root.findViewById<CheckBox>(R.id.cbx_answer)
        val btnRemark = binding.root.findViewById<Button>(R.id.btn_remark)
    }

}