package com.minuminu.haruu.wheremyhome.view.evalformgrouplist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import com.minuminu.haruu.wheremyhome.R
import com.minuminu.haruu.wheremyhome.databinding.ItemEvalFormGroupBinding
import com.minuminu.haruu.wheremyhome.db.data.EvalFormGroup

class EvalFormGroupRecyclerViewAdapter(
    val fragment: Fragment,
    val viewModel: EvalFormGroupListViewModel
) :
    ListAdapter<EvalFormGroup, EvalFormGroupRecyclerViewAdapter.ViewHolder>(
        EvalFormGroupDiffUtilCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_eval_form_group,
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        with(holder) {
            binding.evalFormGroup = item
            binding.viewModel = viewModel
            layout.setOnClickListener {
                fragment.findNavController().navigate(
                    R.id.action_EvalFormGroupListFragment_to_EvalFormGroupDetailsFragment,
                    Bundle().apply {
                        item.id?.let { evalFormGroupId ->
                            putLong("evalFormGroupId", evalFormGroupId)
                        }
                    }
                )
            }
            chkSetDefault.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    viewModel.checked.set(item.id)

                    buttonView.context.getSharedPreferences("evalFormGroup", Context.MODE_PRIVATE)
                        .edit().apply {
                            item.id?.let { checkedId ->
                                this.putLong("checkedId", checkedId)
                            }
                            this.apply()
                        }
                }
            }
        }
    }

    inner class ViewHolder(val binding: ItemEvalFormGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val layout: LinearLayout = binding.root.findViewById(R.id.eval_form_group_layout)
        val chkSetDefault: MaterialCheckBox =
            binding.root.findViewById(R.id.chk_set_default_eval_form_group)
    }

}