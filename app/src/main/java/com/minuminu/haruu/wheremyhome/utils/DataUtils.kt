package com.minuminu.haruu.wheremyhome.utils

import com.minuminu.haruu.wheremyhome.db.data.EvalForm
import com.minuminu.haruu.wheremyhome.db.data.EvalFormGroup

object DataUtils {

    fun createEvalFormGroupTemplate(): EvalFormGroup {
        return EvalFormGroup(-1L, "기본", "기본으로 제공되는 폼은 수정 불가합니다.")
    }

    fun createEvalFormListTemplate(): List<EvalForm> {
        return arrayListOf(
            EvalForm(null, "내부", 1, "햇빛 채광 점수", "Int", 1f, -1L),
            EvalForm(null, "내부", 2, "물 샌 흔적 갯수(-)", "Int", 1f, -1L),
            EvalForm(null, "내부", 3, "천장/벽/장판 곰팡이 갯수(-)", "Int", 1f, -1L),
            EvalForm(null, "내부", 4, "환기구 갯수", "Int", 1f, -1L),
            EvalForm(null, "내부", 5, "외풍차단 여부", "Boolean", 1f, -1L),
            EvalForm(null, "내부", 6, "발코니 여부", "Boolean", 1f, -1L),
            EvalForm(null, "내부", 7, "빨래 건조 공간 여부", "Boolean", 1f, -1L),
            EvalForm(null, "내부", 8, "방 공간 점수", "Int", 1f, -1L),
            EvalForm(null, "시설물", 9, "전등 갯수", "Int", 1f, -1L),
            EvalForm(null, "시설물", 10, "화장실", "Int", 1f, -1L),
            EvalForm(null, "시설물", 11, "- 수도 상태", "Boolean", 1f, -1L),
            EvalForm(null, "시설물", 12, "- 배수 상태", "Boolean", 1f, -1L),
            EvalForm(null, "시설물", 13, "- 청결 점수", "Int", 1f, -1L),
            EvalForm(null, "시설물", 14, "싱크대 여부", "Boolean", 1f, -1L),
            EvalForm(null, "시설물", 15, "- 수도 상태", "Boolean", 1f, -1L),
            EvalForm(null, "시설물", 16, "- 배수 상태", "Boolean", 1f, -1L),
            EvalForm(null, "시설물", 17, "- 수납장 갯수", "Int", 1f, -1L),
            EvalForm(null, "시설물", 18, "세탁기 여부", "Boolean", 1f, -1L),
            EvalForm(null, "시설물", 19, "- 수도 상태", "Boolean", 1f, -1L),
            EvalForm(null, "시설물", 20, "- 배수 상태", "Boolean", 1f, -1L),
            EvalForm(null, "시설물", 21, "콘센트 갯수", "Int", 1f, -1L),
            EvalForm(null, "시설물", 22, "난방 방식(도시가스/전기)", "Boolean", 1f, -1L),
            EvalForm(null, "입지환경", 23, "방충망 설치 여부", "Boolean", 1f, -1L),
            EvalForm(null, "입지환경", 24, "복도(계단X) 여부", "Boolean", 1f, -1L),
            EvalForm(null, "입지환경", 25, "냄새 점수", "Int", 1f, -1L),
            EvalForm(null, "입지환경", 26, "근처 가로등 여부", "Boolean", 1f, -1L),
        )
    }
}