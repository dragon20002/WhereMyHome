package com.minuminu.haruu.wheremyhome.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import com.minuminu.haruu.wheremyhome.db.data.Qanda

@Dao
interface QandaDao {
    @Insert
    fun insertAll(vararg qandas: Qanda): List<Long>

    @Update
    fun updateAll(vararg qandas: Qanda): Int

    @Delete
    fun delete(qanda: Qanda)
}