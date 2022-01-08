package com.minuminu.haruu.kirinapp.view.homeinfodetails

import androidx.recyclerview.widget.DiffUtil
import com.minuminu.haruu.kirinapp.db.data.EvalInfoViewData

class EvalInfoDiffUtilCallback : DiffUtil.ItemCallback<EvalInfoViewData>() {
    override fun areItemsTheSame(oldItem: EvalInfoViewData, newItem: EvalInfoViewData) =
        oldItem.evalInfoId == newItem.evalInfoId

    override fun areContentsTheSame(oldItem: EvalInfoViewData, newItem: EvalInfoViewData) =
        oldItem.toString() == newItem.toString()

}