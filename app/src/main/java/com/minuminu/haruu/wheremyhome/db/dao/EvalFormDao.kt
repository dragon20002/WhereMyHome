package com.minuminu.haruu.wheremyhome.db.dao

import androidx.room.*
import com.minuminu.haruu.wheremyhome.db.data.EvalForm

@Dao
interface EvalFormDao {
    @Transaction
    @Query("SELECT * FROM EvalForm")
    fun getAll(): List<EvalForm>

    @Transaction
    @Query("SELECT * FROM EvalForm WHERE eval_form_group_id = :evalFormGroupId")
    fun getAllByEvalFormGroupId(evalFormGroupId: Long): List<EvalForm>

    @Transaction
    @Query("SELECT * FROM EvalForm WHERE id = :id")
    fun getOneById(id: Long): EvalForm

    @Insert
    fun insertAll(vararg evalForms: EvalForm): List<Long>

    @Update
    fun updateAll(vararg evalForms: EvalForm): Int

    @Delete
    fun delete(vararg evalForms: EvalForm)
}