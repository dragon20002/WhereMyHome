package com.minuminu.haruu.wheremyhome.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import com.minuminu.haruu.wheremyhome.db.data.Picture

@Dao
interface PictureDao {
    @Insert
    fun insertAll(vararg qandas: Picture): List<Long>

    @Update
    fun updateAll(vararg qandas: Picture): Int

    @Delete
    fun delete(qanda: Picture)
}