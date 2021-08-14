package com.minuminu.haruu.wheremyhome.db.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HomeInfo(
    @PrimaryKey(autoGenerate = true)
    var id: Long?,
    var name: String? = "",
    var address: String? = "", // 주소
    var deposit: Int = 0, // 보증금
    var rental: Int = 0, // 월세
    var expense: Float = 0f, // 관리비
    @ColumnInfo(name = "start_date")
    var startDate: String? = "", // 계약가능일(시작)
    @ColumnInfo(name = "end_date")
    var endDate: String? = "", // 계약가능일(종료)
    var score: Int = 0, // 점수
    var thumbnail: String? = "", // 썸네일 (사진목록 중 첫번째 사진)
) {
    override fun toString(): String {
        return "HomeInfo(id=$id, name=$name, address=$address, deposit=$deposit, rental=$rental, expense=$expense, startDate=$startDate, endDate=$endDate, score=$score, thumbnail=$thumbnail)"
    }
}