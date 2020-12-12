package com.minuminu.haruu.wheremyhome

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import com.minuminu.haruu.wheremyhome.databinding.FragmentQandaBinding
import com.minuminu.haruu.wheremyhome.databinding.ItemPictureBinding
import com.minuminu.haruu.wheremyhome.dummy.DummyContent
import com.minuminu.haruu.wheremyhome.utils.Utils

object ItemDetailsBindingAdapter {

    @BindingAdapter("pictures", "layout")
    @JvmStatic
    fun setPictureList(viewGroup: ViewGroup, pictures: List<DummyContent.Picture>, layout: Int) {
        Log.d(ItemDetailsViewModel::class.simpleName, "pictures - observe : ${pictures.size}")

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
                    viewGroup.findNavController().navigate(
                        R.id.action_ItemDetailsFragment_to_PictureFullScreenFragment,
                        Bundle().apply {
                            putString("pictureName", picture.name)
                        })
                }
            }
        }
    }

    @BindingAdapter("pictureName")
    @JvmStatic
    fun setImageBitmap(iv: ImageView, pictureName: String) {
        var imageFile = Utils.loadSnapshotFile(iv.context, pictureName)
        if (imageFile == null) {
            Log.d(ItemDetailsBindingAdapter::class.simpleName, "loadSnapshotFile is failed")

            imageFile = Utils.loadImageFile(iv.context, pictureName).let {
                Utils.resizeBitmap(it, iv.width.toFloat(), iv.height.toFloat())
            }
            Utils.createSnapshotFile(iv.context, pictureName, imageFile)
        }
        iv.setImageBitmap(imageFile)
    }

    @BindingAdapter("qandas", "layout")
    @JvmStatic
    fun setQandaList(viewGroup: ViewGroup, qandas: List<DummyContent.QandaViewData>, layout: Int) {
        Log.d(ItemDetailsViewModel::class.simpleName, "qandas - observe : ${qandas.size}")

        if (viewGroup.childCount >= qandas.size)
            return

        val inflater =
            viewGroup.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        for (i in qandas.indices) {
            val qanda = qandas[i]

            if (i < viewGroup.childCount) {
                val binding =
                    DataBindingUtil.getBinding<FragmentQandaBinding>(viewGroup.getChildAt(i + 1))
                if (binding != null) {
                    binding.qanda = qanda
                    continue
                }
            }

            DataBindingUtil.inflate<FragmentQandaBinding>(
                inflater,
                layout,
                viewGroup,
                true
            )?.let { binding ->
                binding.qanda = qanda
                binding.root.findViewById<CheckBox>(R.id.cbx_answer)?.setOnClickListener { view ->
                    val cbx = view as CheckBox
                    cbx.text = when (cbx.isChecked) {
                        true -> "예"
                        else -> "아니오"
                    }
                }
                binding.root.findViewById<Button>(R.id.btn_remark)?.setOnClickListener { view ->
                    RemarkDialogFragment().also { dialog ->
                        dialog.caller = binding.root.findViewById(R.id.qanda_remark)
                        dialog.listener = object : RemarkDialogFragment.RemarkDialogListener {
                            override fun onDialogPositiveClick(
                                dialog: DialogFragment, remark: String, caller: View?
                            ) {
                                qanda.remark = remark
                            }

                            override fun onDialogNegativeClick(dialog: DialogFragment) {
                            }
                        }
                        dialog.remark = qanda.remark
                    }.show(
                        (view.context as FragmentActivity).supportFragmentManager,
                        "RemarkDialogFragment"
                    )
                }
            }
        }

    }
}