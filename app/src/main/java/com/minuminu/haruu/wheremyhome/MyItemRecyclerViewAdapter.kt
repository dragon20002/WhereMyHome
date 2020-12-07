package com.minuminu.haruu.wheremyhome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.minuminu.haruu.wheremyhome.dummy.DummyContent
import com.minuminu.haruu.wheremyhome.dummy.DummyContent.HomeInfo

/**
 * [RecyclerView.Adapter] that can display a [HomeInfo].
 */
class MyItemRecyclerViewAdapter(
    val fragment: Fragment,
    var values: ArrayList<DummyContent.HomeInfoWithQandas>
) : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position].homeInfo
        holder.idView.text = (position + 1).toString()
        holder.nameView.text = item.name
        holder.descView.text = "${item.deposit} / ${item.rental} / ${item.expense} / ${item.score}"
        holder.btnEdit.setOnClickListener {
            fragment.findNavController()
                .navigate(R.id.action_ItemFragment_to_ItemDetailsFragment, Bundle().apply {
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