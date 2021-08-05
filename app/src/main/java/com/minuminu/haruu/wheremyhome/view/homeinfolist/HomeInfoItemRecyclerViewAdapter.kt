package com.minuminu.haruu.wheremyhome.view.homeinfolist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.minuminu.haruu.wheremyhome.R
import com.minuminu.haruu.wheremyhome.db.data.HomeInfo
import com.minuminu.haruu.wheremyhome.utils.AppUtils

/**
 * [RecyclerView.Adapter] that can display a [HomeInfo].
 */
class HomeInfoItemRecyclerViewAdapter(
    val fragment: Fragment,
) : ListAdapter<HomeInfo, HomeInfoItemRecyclerViewAdapter.ViewHolder>(HomeInfoDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_info, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.layout.setOnClickListener { _ ->
            fragment.findNavController()
                .navigate(
                    R.id.action_HomeInfoListFragment_to_HomeInfoDetailsFragment,
                    Bundle().apply {
                        item.id?.let { homeInfoId ->
                            putLong("homeInfoId", homeInfoId)
                        }
                    })
        }
        item.thumbnail?.takeIf { it.isNotEmpty() }?.let { thumbnail ->
            var imageFile = AppUtils.loadSnapshotFile(holder.thumbnailView.context, thumbnail)
            if (imageFile == null) {
                Log.d(
                    HomeInfoItemRecyclerViewAdapter::class.simpleName,
                    "loadSanpshotFile is failed"
                )

                imageFile = AppUtils.loadImageFile(holder.thumbnailView.context, thumbnail).let {
                    AppUtils.resizeBitmap(
                        it,
                        holder.thumbnailView.width.toFloat(),
                        holder.thumbnailView.height.toFloat()
                    )
                }
                AppUtils.createSnapshotFile(holder.thumbnailView.context, thumbnail, imageFile)
            }
            holder.thumbnailView.setImageBitmap(imageFile)
        }
        holder.nameView.text = item.name
        holder.depositView.text = item.deposit.toString()
        holder.rentalView.text = item.rental.toString()
        holder.expenseView.text = item.expense.toString()
        holder.scoreView.text = item.score.toString()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val layout: LinearLayout = view.findViewById(R.id.item_layout)
        val thumbnailView: ImageView = view.findViewById(R.id.item_thumbnail)
        val nameView: TextView = view.findViewById(R.id.item_name)
        val depositView: Chip = view.findViewById(R.id.item_deposit)
        val rentalView: Chip = view.findViewById(R.id.item_rental)
        val expenseView: Chip = view.findViewById(R.id.item_expense)
        val scoreView: TextView = view.findViewById(R.id.item_score)
    }
}