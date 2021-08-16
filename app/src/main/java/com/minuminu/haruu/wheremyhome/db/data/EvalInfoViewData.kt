package com.minuminu.haruu.wheremyhome.db.data

data class EvalInfoViewData(
    val evalInfoId: Long?,
    var category: String = "", // 구분
    var num: String = "", // 번호
    var content: String = "", // 항목명
    val method: String = "", // 답변형식
    var result: String = "", // 답변
    var remark: String = "", // 비고
) {
    var blnResult: Boolean
        get() {
            return result.toBoolean()
        }
        set(b) {
            result = b.toString()
        }

    override fun toString(): String {
        return "EvalInfoViewData(evalInfoId=$evalInfoId, category='$category', num='$num', content='$content', method='$method', result='$result', remark='$remark', blnResult=$blnResult)"
    }

}