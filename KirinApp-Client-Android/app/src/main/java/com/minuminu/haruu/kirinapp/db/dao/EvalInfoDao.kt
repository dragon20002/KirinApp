package com.minuminu.haruu.kirinapp.db.dao

import androidx.room.*
import com.minuminu.haruu.kirinapp.db.data.EvalInfo

@Dao
interface EvalInfoDao {
    @Transaction
    @Query("SELECT * FROM EvalInfo WHERE home_info_id = :homeInfoId")
    fun getAllByHomeInfoId(homeInfoId: Long): List<EvalInfo>

    @Insert
    fun insertAll(vararg evalInfos: EvalInfo): List<Long>

    @Update
    fun updateAll(vararg evalInfos: EvalInfo): Int

    @Delete
    fun delete(evalInfos: EvalInfo)
}