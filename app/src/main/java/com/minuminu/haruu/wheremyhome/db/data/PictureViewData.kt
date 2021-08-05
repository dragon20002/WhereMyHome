package com.minuminu.haruu.wheremyhome.db.data

data class PictureViewData(
    val id: Long?,
    val name: String = "", // 파일명
    var homeInfoId: Long? = null,
    var deleted: Boolean = false
) {
    override fun toString(): String {
        return "PictureViewData(id=$id, name='$name', homeInfoId=$homeInfoId, deleted=$deleted)"
    }
}