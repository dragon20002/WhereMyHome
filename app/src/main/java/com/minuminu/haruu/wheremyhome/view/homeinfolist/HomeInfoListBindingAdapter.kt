package com.minuminu.haruu.wheremyhome.view.homeinfolist

import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.minuminu.haruu.wheremyhome.db.data.HomeInfo
import com.minuminu.haruu.wheremyhome.utils.AppUtils

object HomeInfoListBindingAdapter {

    @BindingAdapter("homeInfos")
    @JvmStatic
    fun setHomeInfoList(recyclerView: RecyclerView, homeInfos: List<HomeInfo>) {
        val adapter = recyclerView.adapter as HomeInfoItemRecyclerViewAdapter
        adapter.submitList(ArrayList<HomeInfo>().apply {
            addAll(homeInfos) // diffUtil이 동작하도록 새로운 Array에 넣어줘야 한다.
        })
    }

    @BindingAdapter("thumbnail")
    @JvmStatic
    fun setThumbnail(iv: ImageView, thumbnail: String?) {
        thumbnail?.takeIf { _thumbnail -> _thumbnail.isNotEmpty() }?.let { _thumbnail ->
            var imageFile = AppUtils.loadSnapshotFile(iv.context, _thumbnail)
            if (imageFile == null) {
                Log.d(javaClass.simpleName, "loadSnapshotFile is failed")

                imageFile = AppUtils.loadImageFile(iv.context, _thumbnail).let {
                    AppUtils.resizeBitmap(it, iv.width.toFloat(), iv.height.toFloat())
                }
                AppUtils.createSnapshotFile(iv.context, _thumbnail, imageFile)
            }
            iv.setImageBitmap(imageFile)
        }
    }
}