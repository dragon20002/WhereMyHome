package com.minuminu.haruu.wheremyhome.view.homeinfolist

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.minuminu.haruu.wheremyhome.db.data.HomeInfo

object HomeInfoListBindingAdapter {

    @BindingAdapter("homeInfos")
    @JvmStatic
    fun setHomeInfoList(recyclerView: RecyclerView, homeInfos: List<HomeInfo>) {
        val adapter = recyclerView.adapter as HomeInfoItemRecyclerViewAdapter
        adapter.submitList(ArrayList<HomeInfo>().apply {
            addAll(homeInfos) // diffUtil이 동작하도록 새로운 Array에 넣어줘야 한다.
        })
    }
}