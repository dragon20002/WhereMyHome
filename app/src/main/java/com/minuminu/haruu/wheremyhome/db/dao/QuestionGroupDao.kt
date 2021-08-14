package com.minuminu.haruu.wheremyhome.db.dao

import androidx.room.*
import com.minuminu.haruu.wheremyhome.db.data.QuestionGroup

@Dao
interface QuestionGroupDao {
    @Transaction
    @Query("SELECT * FROM QuestionGroup")
    fun getAll(): List<QuestionGroup>

    @Insert
    fun insertAll(vararg questionGroups: QuestionGroup): List<Long>

    @Update
    fun updateAll(vararg questionGroups: QuestionGroup): Int

    @Delete
    fun delete(questionGroups: QuestionGroup)
}