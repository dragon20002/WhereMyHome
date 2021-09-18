package com.minuminu.haruu.wheremyhome.view.homeinfodetails

import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.minuminu.haruu.wheremyhome.db.data.EvalInfoViewData
import com.minuminu.haruu.wheremyhome.db.data.PictureSize
import com.minuminu.haruu.wheremyhome.db.data.PictureViewData
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

    @BindingAdapter("pictureName", "deleted", "size")
    @JvmStatic
    fun setImageBitmap(
        iv: ImageView,
        pictureName: String,
        deleted: Boolean,
        size: PictureSize = PictureSize.Small
    ) {
        val imageFile = when(size) {
            PictureSize.Full -> {
                AppUtils.loadImageFile(iv.context, pictureName)
            }
            else -> {
                var snapshotFile = AppUtils.loadSnapshotFile(iv.context, "${pictureName}_${size}")
                if (snapshotFile == null) {
                    Log.d(HomeInfoDetailsBindingAdapter::class.simpleName, "loadSnapshotFile is failed")

                    snapshotFile = AppUtils.loadImageFile(iv.context, pictureName).let {
                        var width = iv.layoutParams.width
                        var height = iv.layoutParams.height
                        if (width.compareTo(ViewGroup.LayoutParams.MATCH_PARENT) == 0) {
                            width =
                                iv.context.resources.displayMetrics.widthPixels
                        }
                        if (height.compareTo(ViewGroup.LayoutParams.MATCH_PARENT) == 0) {
                            height =
                                iv.context.resources.displayMetrics.heightPixels
                        }
                        AppUtils.resizeBitmap(it, width.toFloat(), height.toFloat())
                    }

                    AppUtils.createSnapshotFile(iv.context, "${pictureName}_${size}", snapshotFile)
                }
                snapshotFile
            }
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