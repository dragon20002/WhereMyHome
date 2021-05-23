package com.minuminu.haruu.wheremyhome.viewmodel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.minuminu.haruu.wheremyhome.R
import com.minuminu.haruu.wheremyhome.db.data.HomeInfo
import com.minuminu.haruu.wheremyhome.db.data.HomeInfoWithQandas

/**
 * [RecyclerView.Adapter] that can display a [HomeInfo].
 */
class HomeInfoItemRecyclerViewAdapter(
    val fragment: Fragment,
    var values: ArrayList<HomeInfoWithQandas>
) : RecyclerView.Adapter<HomeInfoItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_info, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position].homeInfo
        holder.idView.text = (position + 1).toString()
        holder.nameView.text = item.name
        holder.descView.text = "${item.deposit}만 / ${item.rental}만 / ${item.expense}만 / ${item.score}점"
        holder.btnEdit.setOnClickListener {
            fragment.findNavController()
                .navigate(R.id.action_HomeInfoListFragment_to_HomeInfoDetailsFragment, Bundle().apply {
                    putString("itemId", item.id?.toString())
                })
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val idView: TextView = view.findViewById(R.id.item_number)
        val nameView: TextView = view.findViewById(R.id.item_name)
        val descView: TextView = view.findViewById(R.id.item_desc)
        val btnEdit: ImageButton = view.findViewById(R.id.btn_edit)
    }
}