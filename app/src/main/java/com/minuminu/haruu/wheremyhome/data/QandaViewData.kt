package com.minuminu.haruu.wheremyhome.data

data class QandaViewData(
    val id: Long?,
    var group: String = "", // 구분
    var num: String = "", // 번호
    var question: String = "", // 항목명
    val type: String = "", // 답변형식
    var strAnswer: String = "", // 답변
    var remark: String = "", // 비고
) {
    var blnAnswer: Boolean
        get() {
            return strAnswer.toBoolean()
        }
        set(b) {
            strAnswer = b.toString()
        }
}