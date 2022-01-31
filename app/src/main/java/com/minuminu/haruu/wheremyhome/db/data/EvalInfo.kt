package com.minuminu.haruu.wheremyhome.db.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class EvalInfo(
    @PrimaryKey(autoGenerate = true)
    val id: Long?,
    var category: String = "", // 구분
    var num: Int = 0, // 번호
    var content: String = "", // 평가항목,
    val method: String = "", // 평가방식 ('Int', 'Boolean')
    val weight: Float = 1f, // 가중치
    val result: String = "", // 평가결과,
    val remark: String = "", // 비고
    @ColumnInfo(name = "home_info_id")
    var homeInfoId: Long? = null, // join
)