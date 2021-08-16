package com.minuminu.haruu.wheremyhome.view.homeinfodetails

import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.minuminu.haruu.wheremyhome.db.data.PictureViewData
import com.minuminu.haruu.wheremyhome.db.data.EvalInfoViewData
import com.minuminu.haruu.wheremyhome.utils.AppUtils

object HomeInfoDetailsBindingAdapter {

    @BindingAdapter("pictures")
    @JvmStatic
    fun setPictureList(
        recyclerView: RecyclerView,
        pictures: List<PictureViewData>,
    ) {
        val adapter = recyclerView.adapter as PictureRecyclerViewAdapter
        adapter.submitList(ArrayList<PictureViewData>().apply {
            addAll(pictures)
        })
    }

    @BindingAdapter("pictureName", "deleted")
    @JvmStatic
    fun setImageBitmap(iv: ImageView, pictureName: String, deleted: Boolean) {
        var imageFile = AppUtils.loadSnapshotFile(iv.context, pictureName)
        if (imageFile == null) {
            Log.d(HomeInfoDetailsBindingAdapter::class.simpleName, "loadSnapshotFile is failed")

            imageFile = AppUtils.loadImageFile(iv.context, pictureName).let {
                AppUtils.resizeBitmap(it, iv.width.toFloat(), iv.height.toFloat())
            }
            AppUtils.createSnapshotFile(iv.context, pictureName, imageFile)
        }
        iv.setImageBitmap(imageFile)
        iv.alpha = when (deleted) {
            true -> 0.3f
            else -> 1f
        }
    }

    @BindingAdapter("evalInfos")
    @JvmStatic
    fun setEvalInfoList(recyclerView: RecyclerView, evalInfos: List<EvalInfoViewData>) {
        val adapter = recyclerView.adapter as EvalInfoRecyclerViewAdapter
        adapter.submitList(ArrayList<EvalInfoViewData>().apply {
            addAll(evalInfos)
        })
    }

}