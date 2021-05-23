package com.minuminu.haruu.wheremyhome.db.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Picture(
    @PrimaryKey(autoGenerate = true)
    val id: Long?,
    val name: String = "", // 파일명
    @ColumnInfo(name = "home_info_id")
    var homeInfoId: Long? = null, // join
)