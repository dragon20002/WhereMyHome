package com.minuminu.haruu.wheremyhome.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import com.minuminu.haruu.wheremyhome.dummy.DummyContent

@Dao
interface PictureDao {
    @Insert
    fun insertAll(vararg qandas: DummyContent.Picture): List<Long>

    @Update
    fun updateAll(vararg qandas: DummyContent.Picture): Int

    @Delete
    fun delete(qanda: DummyContent.Picture)
}