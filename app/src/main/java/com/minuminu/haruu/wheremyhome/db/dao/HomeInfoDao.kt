package com.minuminu.haruu.wheremyhome.db.dao

import androidx.room.*
import com.minuminu.haruu.wheremyhome.db.data.HomeInfo

@Dao
interface HomeInfoDao {
    @Transaction
    @Query("SELECT * FROM HomeInfo")
    fun getAll(): List<HomeInfo>

//    @Transaction
//    @Query("SELECT * FROM HomeInfo WHERE id IN (:ids)")
//    fun loadAllByIds(ids: Array<String>): List<HomeInfoWithQandas>

    @Transaction
    @Query("SELECT * FROM HomeInfo WHERE id = :id")
    fun getOneById(id: Long): HomeInfo

    @Insert
    fun insertAll(vararg homeInfos: HomeInfo): List<Long>

    @Update
    fun updateAll(vararg homeInfos: HomeInfo): Int

    @Delete
    fun delete(homeInfo: HomeInfo)
}