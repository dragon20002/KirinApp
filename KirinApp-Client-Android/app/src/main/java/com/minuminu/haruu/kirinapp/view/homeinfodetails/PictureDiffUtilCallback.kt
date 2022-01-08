package com.minuminu.haruu.kirinapp.view.homeinfodetails

import androidx.recyclerview.widget.DiffUtil
import com.minuminu.haruu.kirinapp.db.data.PictureViewData

class PictureDiffUtilCallback : DiffUtil.ItemCallback<PictureViewData>() {
    override fun areItemsTheSame(oldItem: PictureViewData, newItem: PictureViewData) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: PictureViewData, newItem: PictureViewData) =
        oldItem.toString() == newItem.toString()

}