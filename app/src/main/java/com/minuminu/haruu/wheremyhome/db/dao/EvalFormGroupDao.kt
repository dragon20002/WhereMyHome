package com.minuminu.haruu.wheremyhome.db.dao

import androidx.room.*
import com.minuminu.haruu.wheremyhome.db.data.EvalFormGroup

@Dao
interface EvalFormGroupDao {
    @Transaction
    @Query("SELECT * FROM EvalFormGroup")
    fun getAll(): List<EvalFormGroup>

    @Transaction
    @Query("SELECT * FROM EvalFormGroup WHERE id = :id")
    fun getOneById(id: Long): EvalFormGroup

    @Insert
    fun insertAll(vararg evalFormGroups: EvalFormGroup): List<Long>

    @Update
    fun updateAll(vararg evalFormGroups: EvalFormGroup): Int

    @Delete
    fun delete(evalFormGroups: EvalFormGroup)
}