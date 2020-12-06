package com.minuminu.haruu.wheremyhome.dummy

import java.util.*

/**
 * Helper class for providing sample content
 */
object DummyContent {

    val ITEMS: MutableList<HomeInfo> = ArrayList()
    val ITEM_MAP: MutableMap<String, HomeInfo> = HashMap() //A map of sample (dummy) items, by ID.
    private val COUNT = 25

    init {
        // Add some sample items.
        for (i in 1..COUNT) {
            addItem(createDummyItem(i))
        }
    }

    private fun addItem(item: HomeInfo) {
        ITEMS.add(item)
        ITEM_MAP[item.id] = item
    }

    private fun createDummyItem(position: Int): HomeInfo {
        return HomeInfo(
            position.toString(), "Item $position", "서울시 $position",
            qandas = createQandaTemplate()
        )
    }

    public fun createQandaTemplate(): List<Qanda> {
        return arrayListOf(
            Qanda("id-1",	"내부",	1,	"햇빛 채광 점수",	"Int"),
            Qanda("id-2",	"내부",	2,	"물 샌 흔적 갯수(-)",	"Int"),
            Qanda("id-3",	"내부",	3,	"천장/벽/장판 곰팡이 갯수(-)",	"Int"),
            Qanda("id-4",	"내부",	4,	"환기구 갯수",	"Int"),
            Qanda("id-5",	"내부",	5,	"외풍차단 여부",	"Boolean"),
            Qanda("id-6",	"내부",	6,	"발코니 여부",	"Boolean"),
            Qanda("id-7",	"내부",	7,	"빨래 건조 공간 여부",	"Boolean"),
            Qanda("id-8",	"내부",	8,	"방 공간 점수",	"Int"),
            Qanda("id-9",	"시설물",	9,	"전등 갯수",	"Int"),
            Qanda("id-10",	"시설물",	10,	"화장실",	"Int"),
            Qanda("id-11",	"시설물",	11,	"- 수도 상태",	"Boolean"),
            Qanda("id-12",	"시설물",	12,	"- 배수 상태",	"Boolean"),
            Qanda("id-13",	"시설물",	13,	"- 청결 점수",	"Int"),
            Qanda("id-14",	"시설물",	14,	"싱크대 여부",	"Boolean"),
            Qanda("id-15",	"시설물",	15,	"- 수도 상태",	"Boolean"),
            Qanda("id-16",	"시설물",	16,	"- 배수 상태",	"Boolean"),
            Qanda("id-17",	"시설물",	17,	"- 수납장 갯수",	"Int"),
            Qanda("id-18",	"시설물",	18,	"세탁기 여부",	"Boolean"),
            Qanda("id-19",	"시설물",	19,	"- 수도 상태",	"Boolean"),
            Qanda("id-20",	"시설물",	20,	"- 배수 상태",	"Boolean"),
            Qanda("id-21",	"시설물",	21,	"콘센트 갯수",	"Int"),
            Qanda("id-22",	"시설물",	22,	"난방 방식(도시가스/전기)",	"Boolean"),
            Qanda("id-23",	"입지환경",	23,	"방충망 설치 여부",	"Boolean"),
            Qanda("id-24",	"입지환경",	24,	"복도(계단X) 여부",	"Boolean"),
            Qanda("id-25",	"입지환경",	25,	"냄새 점수",	"Int"),
            Qanda("id-26",	"입지환경",	26,	"근처 가로등 여부",	"Boolean"),
        )
    }

    data class HomeInfo(
        var id: String = "",
        var name: String = "",
        var address: String = "", // 주소
        var deposit: Int = 0, // 보증금
        var rental: Int = 0, // 월세
        var expense: Int = 0, // 관리비
        var startDate: String = "", // 계약가능일(시작)
        var endDate: String = "", // 계약가능일(종료)
        var score: Int = 0, // 점수
        var qandas: List<Qanda> = ArrayList(), // 질문/답변
        var picDir: String = "", // 사진파일 경로
    ) {
        override fun toString(): String {
            return "HomeInfo(id='$id', name='$name', address='$address', deposit=$deposit, rental=$rental, expense=$expense, startDate='$startDate', endDate='$endDate', score=$score, qandas=$qandas, picDir='$picDir')"
        }
    }

    data class Qanda(
        var id: String = "",
        var group: String = "", // 구분
        var num: Int = 0, // 번호
        var question: String = "", // 항목명
        var type: String = "", // 답변형식
        var answer: String = "", // 답변
        var remark: String = "", // 비고
    )
}