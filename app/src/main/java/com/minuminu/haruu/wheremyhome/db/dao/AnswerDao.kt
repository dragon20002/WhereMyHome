package com.minuminu.haruu.wheremyhome.db.dao

import androidx.room.*
import com.minuminu.haruu.wheremyhome.db.data.Answer

@Dao
interface AnswerDao {
    @Transaction
    @Query("SELECT * FROM Answer WHERE home_info_id = :homeInfoId")
    fun getAllByHomeInfoId(homeInfoId: Long): List<Answer>

    @Insert
    fun insertAll(vararg answers: Answer): List<Long>

    @Update
    fun updateAll(vararg answers: Answer): Int

    @Delete
    fun delete(answers: Answer)
}