package com.minuminu.haruu.wheremyhome.view.homeinfodetails

import androidx.recyclerview.widget.DiffUtil
import com.minuminu.haruu.wheremyhome.db.data.QandaViewData

class QandaDiffUtilCallback : DiffUtil.ItemCallback<QandaViewData>() {
    override fun areItemsTheSame(oldItem: QandaViewData, newItem: QandaViewData) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: QandaViewData, newItem: QandaViewData) =
        oldItem.toString() == newItem.toString()

}