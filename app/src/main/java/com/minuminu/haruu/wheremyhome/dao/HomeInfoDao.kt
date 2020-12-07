package com.minuminu.haruu.wheremyhome.dao

import androidx.room.*
import com.minuminu.haruu.wheremyhome.dummy.DummyContent

@Dao
interface HomeInfoDao {
    @Transaction
    @Query("SELECT * FROM HomeInfo")
    fun getAll(): List<DummyContent.HomeInfoWithQandas>

    @Transaction
    @Query("SELECT * FROM HomeInfo WHERE id IN (:ids)")
    fun loadAllByIds(ids: Array<String>): List<DummyContent.HomeInfoWithQandas>

    @Insert
    fun insertAll(vararg homeInfos: DummyContent.HomeInfo): List<Long>

    @Update
    fun updateAll(vararg homeInfos: DummyContent.HomeInfo): Int

    @Delete
    fun delete(homeInfo: DummyContent.HomeInfo)
}