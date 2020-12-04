package com.minuminu.haruu.wheremyhome.dummy

import java.util.*

/**
 * Helper class for providing sample content
 */
object DummyContent {

    val ITEMS: MutableList<DummyItem> = ArrayList()
    val ITEM_MAP: MutableMap<String, DummyItem> = HashMap() //A map of sample (dummy) items, by ID.
    private val COUNT = 25

    init {
        // Add some sample items.
        for (i in 1..COUNT) {
            addItem(createDummyItem(i))
        }
    }

    private fun addItem(item: DummyItem) {
        ITEMS.add(item)
        ITEM_MAP[item.id] = item
    }

    private fun createDummyItem(position: Int): DummyItem {
        return DummyItem(
            position.toString(), "Item $position", "서울시 $position",
            0, 0, 0, 0
        )
    }

    private fun makeDetails(position: Int): String {
        val builder = StringBuilder()
        builder.append("Details about Item: ").append(position)
        for (i in 0 until position) {
            builder.append("\nMore details information here.")
        }
        return builder.toString()
    }

    data class DummyItem(
        val id: String,
        val name: String,
        val address: String, // 주소
        val deposit: Int, // 보증금
        val rental: Int, // 월세
        val expense: Int, // 관리비
        val score: Int, // 점수
    )
}