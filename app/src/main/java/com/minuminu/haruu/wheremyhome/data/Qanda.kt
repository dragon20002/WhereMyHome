package com.minuminu.haruu.wheremyhome.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Qanda(
    @PrimaryKey(autoGenerate = true)
    val id: Long?,
    var group: String = "", // 구분
    var num: Int = 0, // 번호
    var question: String = "", // 항목명
    val type: String = "", // 답변형식
    var answer: String = "", // 답변
    var remark: String = "", // 비고
    @ColumnInfo(name = "home_info_id")
    var homeInfoId: Long? = null, // join
) {
    override fun toString(): String {
        return "Qanda(id=$id, group='$group', num=$num, question='$question', type='$type', answer='$answer', remark='$remark', homeInfoId=$homeInfoId)"
    }
}