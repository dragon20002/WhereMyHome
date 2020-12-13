package com.minuminu.haruu.wheremyhome.dao

import androidx.room.*
import com.minuminu.haruu.wheremyhome.data.HomeInfo
import com.minuminu.haruu.wheremyhome.data.HomeInfoWithQandas

@Dao
interface HomeInfoDao {
    @Transaction
    @Query("SELECT * FROM HomeInfo")
    fun getAll(): List<HomeInfoWithQandas>

    @Transaction
    @Query("SELECT * FROM HomeInfo WHERE id IN (:ids)")
    fun loadAllByIds(ids: Array<String>): List<HomeInfoWithQandas>

    @Insert
    fun insertAll(vararg homeInfos: HomeInfo): List<Long>

    @Update
    fun updateAll(vararg homeInfos: HomeInfo): Int

    @Delete
    fun delete(homeInfo: HomeInfo)
}