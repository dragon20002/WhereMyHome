package com.minuminu.haruu.wheremyhome.db.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class EvalForm(
    @PrimaryKey(autoGenerate = true)
    val id: Long?,
    var category: String = "", // 구분
    var num: Int = 0, // 번호
    var content: String = "", // 평가내용
    val method: String = "", // 평가방식 ('Count', 'YesOrNo')
    @ColumnInfo(name = "eval_form_group_id")
    var evalFormGroupId: Long? = null, // join
)