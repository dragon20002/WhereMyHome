package com.minuminu.haruu.wheremyhome.view.homeinfodetails

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.minuminu.haruu.wheremyhome.R
import com.minuminu.haruu.wheremyhome.databinding.ItemPictureBinding
import com.minuminu.haruu.wheremyhome.db.data.PictureViewData
import com.minuminu.haruu.wheremyhome.view.picturefullscreen.PictureFullScreenActivity

class PictureRecyclerViewAdapter(
    val context: Context,
    val viewModel: HomeInfoDetailsViewModel
) :
    ListAdapter<PictureViewData, PictureRecyclerViewAdapter.ViewHolder>(PictureDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_picture,
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        with(holder) {
            binding.picture = item
            binding.viewModel = viewModel
            pictureView.setOnClickListener {
                // 전체화면
                context.startActivity(
                    Intent(
                        context,
                        PictureFullScreenActivity::class.java
                    ).apply {
                        putExtra("pictureName", item.name)
                    })
            }
            btnRemove.setOnClickListener { v ->
                MaterialAlertDialogBuilder(v.context)
                    .setMessage("선택한 사진을 삭제하시겠습니까?")
                    .setNegativeButton(v.resources.getText(R.string.cancel), null)
                    .setPositiveButton(v.resources.getText(R.string.ok)) { _, _ ->
                        if (viewModel.thumbnail.get()!! == item.name) {
                            viewModel.thumbnail.set("")
                        }
                        item.deleted = true
                        binding.picture = item
                    }
                    .show()
            }
            btnRestore.setOnClickListener { _ ->
                if (viewModel.thumbnail.get()!! == item.name) {
                    viewModel.thumbnail.set("")
                }
                item.deleted = false
                binding.picture = item
            }
            btnStarBorder.setOnClickListener {
                viewModel.thumbnail.set(item.name)
            }
            btnStar.setOnClickListener {
                viewModel.thumbnail.set(null)
            }
        }
    }

    inner class ViewHolder(val binding: ItemPictureBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val pictureView: ImageView = binding.root.findViewById(R.id.iv_picture)
        val btnRemove: Button = binding.root.findViewById(R.id.btn_remove)
        val btnRestore: Button = binding.root.findViewById(R.id.btn_restore)
        val btnStarBorder: Button = binding.root.findViewById(R.id.btn_star_border)
        val btnStar: Button = binding.root.findViewById(R.id.btn_star)
    }
}