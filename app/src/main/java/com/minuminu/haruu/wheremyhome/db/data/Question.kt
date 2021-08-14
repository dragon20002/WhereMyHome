package com.minuminu.haruu.wheremyhome.db.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Question(
    @PrimaryKey(autoGenerate = true)
    val id: Long?,
    var category: String = "", // 구분
    var num: Int = 0, // 번호
    var content: String = "", // 질문내용
    val type: String = "", // 답변형식 ('Int', 'Boolean')
    @ColumnInfo(name = "question_group_id")
    var questionGroupId: Long? = null, // join
)