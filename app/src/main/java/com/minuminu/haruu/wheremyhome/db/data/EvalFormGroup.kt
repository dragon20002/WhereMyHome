package com.minuminu.haruu.wheremyhome.db.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class EvalFormGroup(
    @PrimaryKey(autoGenerate = true)
    var id: Long?,
    var name: String? = "", // 그룹명
    var description: String? = "", // 그룹설명
) {
    override fun toString(): String {
        return "EvalFormGroup(id=$id, name='$name', description='$description')"
    }
}