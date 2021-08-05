package com.minuminu.haruu.wheremyhome.db.dao

import androidx.room.*
import com.minuminu.haruu.wheremyhome.db.data.Qanda

@Dao
interface QandaDao {
    @Transaction
    @Query("SELECT * FROM Qanda WHERE home_info_id = :homeInfoId")
    fun getAllByHomeInfoId(homeInfoId: Long): List<Qanda>

    @Insert
    fun insertAll(vararg qandas: Qanda): List<Long>

    @Update
    fun updateAll(vararg qandas: Qanda): Int

    @Delete
    fun delete(qanda: Qanda)
}