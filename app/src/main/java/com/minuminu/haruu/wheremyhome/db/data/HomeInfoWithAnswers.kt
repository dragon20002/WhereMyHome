package com.minuminu.haruu.wheremyhome.db.data

import androidx.room.Embedded
import androidx.room.Relation

data class HomeInfoWithAnswers(
    @Embedded
    val homeInfo: HomeInfo,
    @Relation(
        parentColumn = "id",
        entityColumn = "home_info_id",
    )
    val answers: List<Answer>,
    @Relation(
        parentColumn = "id",
        entityColumn = "home_info_id",
    )
    val pictures: List<Picture>,
)