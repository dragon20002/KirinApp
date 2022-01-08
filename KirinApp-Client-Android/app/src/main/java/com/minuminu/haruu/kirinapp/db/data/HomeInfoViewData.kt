package com.minuminu.haruu.kirinapp.db.data

import androidx.room.Embedded
import androidx.room.Relation

data class HomeInfoViewData(
    @Embedded
    val homeInfo: HomeInfo,
    @Relation(
        parentColumn = "id",
        entityColumn = "home_info_id",
    )
    val evalInfos: List<EvalInfo>,
    @Relation(
        parentColumn = "id",
        entityColumn = "home_info_id",
    )
    val pictures: List<Picture>,
)