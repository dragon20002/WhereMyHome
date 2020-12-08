package com.minuminu.haruu.wheremyhome.dummy

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import java.util.*

/**
 * Helper class for providing sample content
 */
object DummyContent {
    public fun createDummyItem(): HomeInfoWithQandas {
        return HomeInfoWithQandas(
            HomeInfo(
                null, "", "",
            ),
            createQandaTemplate(),
            ArrayList<Picture>(),
        )
    }

    public fun createQandaTemplate(): List<Qanda> {
        return arrayListOf(
            Qanda(null, "내부", 1, "햇빛 채광 점수", "Int"),
            Qanda(null, "내부", 2, "물 샌 흔적 갯수(-)", "Int"),
            Qanda(null, "내부", 3, "천장/벽/장판 곰팡이 갯수(-)", "Int"),
            Qanda(null, "내부", 4, "환기구 갯수", "Int"),
            Qanda(null, "내부", 5, "외풍차단 여부", "Boolean"),
            Qanda(null, "내부", 6, "발코니 여부", "Boolean"),
            Qanda(null, "내부", 7, "빨래 건조 공간 여부", "Boolean"),
            Qanda(null, "내부", 8, "방 공간 점수", "Int"),
            Qanda(null, "시설물", 9, "전등 갯수", "Int"),
            Qanda(null, "시설물", 10, "화장실", "Int"),
            Qanda(null, "시설물", 11, "- 수도 상태", "Boolean"),
            Qanda(null, "시설물", 12, "- 배수 상태", "Boolean"),
            Qanda(null, "시설물", 13, "- 청결 점수", "Int"),
            Qanda(null, "시설물", 14, "싱크대 여부", "Boolean"),
            Qanda(null, "시설물", 15, "- 수도 상태", "Boolean"),
            Qanda(null, "시설물", 16, "- 배수 상태", "Boolean"),
            Qanda(null, "시설물", 17, "- 수납장 갯수", "Int"),
            Qanda(null, "시설물", 18, "세탁기 여부", "Boolean"),
            Qanda(null, "시설물", 19, "- 수도 상태", "Boolean"),
            Qanda(null, "시설물", 20, "- 배수 상태", "Boolean"),
            Qanda(null, "시설물", 21, "콘센트 갯수", "Int"),
            Qanda(null, "시설물", 22, "난방 방식(도시가스/전기)", "Boolean"),
            Qanda(null, "입지환경", 23, "방충망 설치 여부", "Boolean"),
            Qanda(null, "입지환경", 24, "복도(계단X) 여부", "Boolean"),
            Qanda(null, "입지환경", 25, "냄새 점수", "Int"),
            Qanda(null, "입지환경", 26, "근처 가로등 여부", "Boolean"),
        )
    }

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
    ) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readFloat(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt()
        )

        override fun toString(): String {
            return "HomeInfo(id='$id', name='$name', address='$address', deposit=$deposit, rental=$rental, expense=$expense, startDate='$startDate', endDate='$endDate', score=$score')"
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeValue(id)
            parcel.writeString(name)
            parcel.writeString(address)
            parcel.writeInt(deposit)
            parcel.writeInt(rental)
            parcel.writeFloat(expense)
            parcel.writeString(startDate)
            parcel.writeString(endDate)
            parcel.writeInt(score)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<HomeInfo> {
            override fun createFromParcel(parcel: Parcel): HomeInfo {
                return HomeInfo(parcel)
            }

            override fun newArray(size: Int): Array<HomeInfo?> {
                return arrayOfNulls(size)
            }
        }
    }

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

    @Entity
    data class Picture(
        @PrimaryKey(autoGenerate = true)
        val id: Long?,
        val name: String = "", // 파일명
        @ColumnInfo(name = "home_info_id")
        var homeInfoId: Long? = null, // join
    )

    data class HomeInfoWithQandas(
        @Embedded
        val homeInfo: HomeInfo,
        @Relation(
            parentColumn = "id",
            entityColumn = "home_info_id",
        )
        val qandas: List<Qanda>,
        @Relation(
            parentColumn = "id",
            entityColumn = "home_info_id",
        )
        val pictures: List<Picture>,
    )
}