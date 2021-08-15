package com.minuminu.haruu.wheremyhome.view.homeinfolist

import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.minuminu.haruu.wheremyhome.utils.AppUtils

object HomeInfoItemBindingAdapter {

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