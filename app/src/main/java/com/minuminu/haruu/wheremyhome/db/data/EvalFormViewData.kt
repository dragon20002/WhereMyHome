package com.minuminu.haruu.wheremyhome.db.data

data class EvalFormViewData(
    val id: Long?,
    var category: String = "", // 구분
    var num: String = "", // 번호
    var content: String = "", // 평가내용
    var method: String = "", // 평가방식 ('Int', 'Boolean')
    var weight: String = "1.0", // 가중치
    var evalFormGroupId: Long? = null, // join
    var deleted: Boolean = false
) {
    var typeDescription: String
        get() {
            return when (method) {
                EvalInfoMethodType.Boolean.name -> EvalInfoMethodType.Boolean.description
                EvalInfoMethodType.Int.name -> EvalInfoMethodType.Int.description
                else -> ""
            }
        }
        set(td) {
            method = when (td) {
                EvalInfoMethodType.Boolean.description -> EvalInfoMethodType.Boolean.name
                EvalInfoMethodType.Int.description -> EvalInfoMethodType.Int.name
                else -> ""
            }
        }

    override fun toString(): String {
        return "EvalFormViewData(id=$id, category='$category', num='$num', content='$content', method='$method', weight='$weight', evalFormGroupId=$evalFormGroupId, deleted=$deleted, typeDescription='$typeDescription')"
    }

}