package com.minuminu.haruu.kirinapp.view.evalformgroupdetails

import androidx.recyclerview.widget.DiffUtil
import com.minuminu.haruu.kirinapp.db.data.EvalFormViewData

class EvalFormDiffUtilCallback : DiffUtil.ItemCallback<EvalFormViewData>() {
    override fun areItemsTheSame(oldItem: EvalFormViewData, newItem: EvalFormViewData) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: EvalFormViewData, newItem: EvalFormViewData) =
        oldItem.toString() == newItem.toString()
}