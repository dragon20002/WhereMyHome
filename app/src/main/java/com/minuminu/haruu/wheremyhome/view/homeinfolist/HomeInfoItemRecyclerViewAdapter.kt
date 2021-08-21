package com.minuminu.haruu.wheremyhome.view.homeinfolist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.minuminu.haruu.wheremyhome.R
import com.minuminu.haruu.wheremyhome.databinding.ItemHomeInfoBinding
import com.minuminu.haruu.wheremyhome.db.data.HomeInfo

class HomeInfoItemRecyclerViewAdapter(
    val fragment: Fragment,
) : ListAdapter<HomeInfo, HomeInfoItemRecyclerViewAdapter.ViewHolder>(HomeInfoDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_home_info,
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        with(holder) {
            binding.homeInfo = item
            layout.setOnClickListener {
                fragment.findNavController()
                    .navigate(
                        R.id.action_HomeInfoListFragment_to_HomeInfoDetailsFragment,
                        Bundle().apply {
                            item.id?.let { homeInfoId ->
                                putLong("homeInfoId", homeInfoId)
                            }
                        })
            }
        }
    }

    inner class ViewHolder(val binding: ItemHomeInfoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val layout: LinearLayout = binding.root.findViewById(R.id.item_layout)
    }
}