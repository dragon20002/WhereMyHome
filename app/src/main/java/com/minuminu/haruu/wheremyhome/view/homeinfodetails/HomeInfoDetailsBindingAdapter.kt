package com.minuminu.haruu.wheremyhome.view.homeinfodetails

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableList
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.minuminu.haruu.wheremyhome.R
import com.minuminu.haruu.wheremyhome.databinding.ItemPictureBinding
import com.minuminu.haruu.wheremyhome.databinding.ItemQandaBinding
import com.minuminu.haruu.wheremyhome.db.data.Picture
import com.minuminu.haruu.wheremyhome.db.data.QandaViewData
import com.minuminu.haruu.wheremyhome.utils.AppUtils
import com.minuminu.haruu.wheremyhome.view.picturefullscreen.PictureFullscreenActivity
import com.minuminu.haruu.wheremyhome.view.homeinfodetails.components.QandaRemarkDialog

object HomeInfoDetailsBindingAdapter {

    @BindingAdapter("pictures", "layout")
    @JvmStatic
    fun setPictureList(viewGroup: ViewGroup, pictures: List<Picture>, layout: Int) {
        Log.d(HomeInfoDetailsViewModel::class.simpleName, "pictures - observe : ${pictures.size}")

        val pictureViewCnt = viewGroup.childCount - 1
        if (pictureViewCnt >= pictures.size)
            return

        val inflater =
            viewGroup.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        for (i in pictures.indices) {
            val picture = pictures[i]

            if (i < pictureViewCnt) {
                val binding =
                    DataBindingUtil.getBinding<ItemPictureBinding>(viewGroup.getChildAt(i + 1))
                if (binding != null) {
                    // binding.picture = picture
                    continue
                }
            }

            DataBindingUtil.inflate<ItemPictureBinding>(
                inflater,
                layout,
                viewGroup,
                true
            )?.let {
                it.picture = picture
                it.root.findViewById<ImageView>(R.id.ivPicture)?.setOnClickListener {
                    // 전체화면
                    inflater.context.startActivity(
                        Intent(
                            inflater.context,
                            PictureFullscreenActivity::class.java
                        ).apply {
                            putExtra("pictureName", picture.name)
                        })
                }
            }
        }
    }

    @BindingAdapter("pictureName")
    @JvmStatic
    fun setImageBitmap(iv: ImageView, pictureName: String) {
        var imageFile = AppUtils.loadSnapshotFile(iv.context, pictureName)
        if (imageFile == null) {
            Log.d(HomeInfoDetailsBindingAdapter::class.simpleName, "loadSnapshotFile is failed")

            imageFile = AppUtils.loadImageFile(iv.context, pictureName).let {
                AppUtils.resizeBitmap(it, iv.width.toFloat(), iv.height.toFloat())
            }
            AppUtils.createSnapshotFile(iv.context, pictureName, imageFile)
        }
        iv.setImageBitmap(imageFile)
    }

    @BindingAdapter("layout", "viewModel")
    @JvmStatic
    fun setQandaList(
        viewGroup: ViewGroup,
        layout: Int,
        viewModel: HomeInfoDetailsViewModel,
    ) {
        viewModel.qandaList.addOnListChangedCallback(object :
            ObservableList.OnListChangedCallback<ObservableList<QandaViewData>>() {
            override fun onChanged(qandas: ObservableList<QandaViewData>?) {
            }

            override fun onItemRangeChanged(
                sender: ObservableList<QandaViewData>?,
                positionStart: Int,
                itemCount: Int
            ) {
            }

            override fun onItemRangeInserted(
                qandas: ObservableList<QandaViewData>?,
                positionStart: Int,
                itemCount: Int
            ) {
                updateQandaList(viewGroup, layout, viewModel, qandas)
            }

            override fun onItemRangeMoved(
                sender: ObservableList<QandaViewData>?,
                fromPosition: Int,
                toPosition: Int,
                itemCount: Int
            ) {
            }

            override fun onItemRangeRemoved(
                sender: ObservableList<QandaViewData>?,
                positionStart: Int,
                itemCount: Int
            ) {
            }
        })

        updateQandaList(viewGroup, layout, viewModel, viewModel.qandaList)
    }

    private fun updateQandaList(
        viewGroup: ViewGroup,
        layout: Int,
        viewModel: HomeInfoDetailsViewModel,
        qandas: ObservableList<QandaViewData>?
    ) {
        if (qandas == null) return
        Log.d(
            HomeInfoDetailsViewModel::class.simpleName,
            "qandas - observe : ${qandas.size}"
        )

        if (viewGroup.childCount >= qandas.size)
            return

        val inflater =
            viewGroup.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        for (i in qandas.indices) {
            val qanda = qandas[i]

            if (i < viewGroup.childCount) {
                val binding =
                    DataBindingUtil.getBinding<ItemQandaBinding>(viewGroup.getChildAt(i + 1))
                if (binding != null) {
                    binding.qanda = qanda
                    binding.viewModel = viewModel
                    continue
                }
            }

            DataBindingUtil.inflate<ItemQandaBinding>(
                inflater,
                layout,
                viewGroup,
                true
            )?.let { binding ->
                binding.qanda = qanda
                binding.viewModel = viewModel
                binding.root.findViewById<CheckBox>(R.id.cbx_answer)
                    ?.setOnClickListener { view ->
                        val cbx = view as CheckBox
                        cbx.text = when (cbx.isChecked) {
                            true -> "예"
                            else -> "아니오"
                        }
                    }
                binding.root.findViewById<Button>(R.id.btn_remark)
                    ?.setOnClickListener { view ->
                        QandaRemarkDialog().also { dialog ->
                            dialog.caller = binding.root.findViewById(R.id.qanda_remark)
                            dialog.listener =
                                object : QandaRemarkDialog.RemarkDialogListener {
                                    override fun onDialogPositiveClick(
                                        dialog: DialogFragment,
                                        remark: String,
                                        caller: View?
                                    ) {
                                        if (viewModel.isEditing.get() != false) {
                                            qanda.remark = remark
                                        }
                                    }

                                    override fun onDialogNegativeClick(dialog: DialogFragment) {
                                    }
                                }
                            dialog.remark = qanda.remark
                            dialog.viewModel = viewModel
                        }.show(
                            (view.context as FragmentActivity).supportFragmentManager,
                            "RemarkDialogFragment"
                        )
                    }
            }
        }
    }
}