package com.minuminu.haruu.wheremyhome.db.dao

import androidx.room.*
import com.minuminu.haruu.wheremyhome.db.data.Question

@Dao
interface QuestionDao {
    @Transaction
    @Query("SELECT * FROM Question")
    fun getAll(): List<Question>

    @Transaction
    @Query("SELECT * FROM Question WHERE question_group_id = :questionGroupId")
    fun getAllByQuestionInfoId(questionGroupId: Long): List<Question>

    @Transaction
    @Query("SELECT * FROM Question WHERE id = :id")
    fun getOneById(id: Long): Question

    @Insert
    fun insertAll(vararg questions: Question): List<Long>

    @Update
    fun updateAll(vararg questions: Question): Int

    @Delete
    fun delete(questions: Question)
}