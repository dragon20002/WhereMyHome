package com.minuminu.haruu.wheremyhome.view.evalformgroupdetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.minuminu.haruu.wheremyhome.R
import com.minuminu.haruu.wheremyhome.databinding.ItemEvalFormBinding
import com.minuminu.haruu.wheremyhome.db.data.EvalFormViewData
import com.minuminu.haruu.wheremyhome.view.evalformgroupdetails.components.EvalFormDialog

class EvalFormRecyclerViewAdapter(val viewModel: EvalFormGroupDetailsViewModel) :
    ListAdapter<EvalFormViewData, EvalFormRecyclerViewAdapter.ViewHolder>(
        EvalFormDiffUtilCallback()
    ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_eval_form,
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        with(holder) {
            binding.evalForm = item
            binding.viewModel = viewModel
            layout.setOnClickListener { v ->
                if (viewModel.isEditing.get() != true || item.deleted) return@setOnClickListener

                EvalFormDialog().also { dialog ->
                    dialog.caller = layout
                    dialog.listener = object : EvalFormDialog.EvalFormDialogListener {
                        override fun onDialogPositiveClick(
                            dialog: DialogFragment,
                            evalForm: EvalFormViewData?,
                            caller: View?
                        ) {
                            evalForm?.let { _evalForm ->
                                item.category = _evalForm.category
                                item.content = _evalForm.content
                                item.method = _evalForm.method
                            }

                            binding.evalForm = item
                        }

                        override fun onDialogNegativeClick(dialog: DialogFragment) {
                        }
                    }
                    dialog.evalForm = item
                }.show(
                    (v.context as FragmentActivity).supportFragmentManager,
                    "EvalFormDialogFragment"
                )
            }
            btnRemove.setOnClickListener { v ->
                MaterialAlertDialogBuilder(v.context)
                    .setMessage("선택한 평가항목을 삭제하시겠습니까?")
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.ok) { _, _ ->
                        item.deleted = true
                        binding.evalForm = item
                    }
                    .show()
            }
            btnRestore.setOnClickListener { _ ->
                item.deleted = false
                binding.evalForm = item
            }
        }
    }

    inner class ViewHolder(val binding: ItemEvalFormBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val layout: LinearLayout = binding.root.findViewById(R.id.eval_form_layout)
        val btnRemove: Button = binding.root.findViewById(R.id.btn_remove)
        val btnRestore: Button = binding.root.findViewById(R.id.btn_restore)
    }
}