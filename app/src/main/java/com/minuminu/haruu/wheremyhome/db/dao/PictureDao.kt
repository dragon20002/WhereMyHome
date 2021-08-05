package com.minuminu.haruu.wheremyhome.db.dao

import androidx.room.*
import com.minuminu.haruu.wheremyhome.db.data.Picture

@Dao
interface PictureDao {
    @Transaction
    @Query("SELECT * FROM Picture WHERE home_info_id = :homeInfoId")
    fun getAllByHomeInfoId(homeInfoId: Long): List<Picture>

    @Insert
    fun insertAll(vararg qandas: Picture): List<Long>

    @Update
    fun updateAll(vararg qandas: Picture): Int

    @Delete
    fun delete(qanda: Picture)
}