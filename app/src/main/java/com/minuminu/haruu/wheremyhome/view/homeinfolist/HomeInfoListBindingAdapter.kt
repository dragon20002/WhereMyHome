package com.minuminu.haruu.wheremyhome.view.homeinfolist

import androidx.databinding.BindingAdapter
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.RecyclerView
import com.minuminu.haruu.wheremyhome.db.data.HomeInfo

object HomeInfoListBindingAdapter {

    @BindingAdapter("homeInfos")
    @JvmStatic
    fun setHomeInfoList(recyclerView: RecyclerView, homeInfos: List<HomeInfo>) {
        recyclerView.adapter = HomeInfoItemRecyclerViewAdapter(
            recyclerView.findFragment(),
            homeInfos
        )
    }
}