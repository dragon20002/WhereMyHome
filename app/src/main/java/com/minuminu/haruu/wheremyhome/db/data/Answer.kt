package com.minuminu.haruu.wheremyhome.db.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Answer(
    @PrimaryKey(autoGenerate = true)
    val id: Long?,
    val content: String = "", // 답변내용,
    val remark: String = "", // 비고
    @ColumnInfo(name = "question_id")
    var questionId: Long? = null, // join
    @ColumnInfo(name = "home_info_id")
    var homeInfoId: Long? = null, // join
)