package com.minuminu.haruu.kirinapp.db.dao

import androidx.room.*
import com.minuminu.haruu.kirinapp.db.data.HomeInfo

@Dao
interface HomeInfoDao {
    @Transaction
    @Query("SELECT * FROM HomeInfo")
    fun getAll(): List<HomeInfo>

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