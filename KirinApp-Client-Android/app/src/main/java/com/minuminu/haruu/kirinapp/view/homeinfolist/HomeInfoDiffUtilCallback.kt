package com.minuminu.haruu.kirinapp.view.homeinfolist

import androidx.recyclerview.widget.DiffUtil
import com.minuminu.haruu.kirinapp.db.data.HomeInfo

class HomeInfoDiffUtilCallback : DiffUtil.ItemCallback<HomeInfo>() {
    override fun areItemsTheSame(oldItem: HomeInfo, newItem: HomeInfo) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: HomeInfo, newItem: HomeInfo) =
        oldItem.toString() == newItem.toString()

}