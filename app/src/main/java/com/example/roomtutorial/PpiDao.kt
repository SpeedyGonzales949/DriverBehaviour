package com.example.roomtutorial

import androidx.room.*


@Dao
interface PpiDao {
    @Insert
    suspend fun insert(ppi: Ppi?)

    @Update
    suspend fun update(ppi: Ppi?)

    @Delete
    suspend fun delete(ppi: Ppi?)

    @Query("delete from ppi_table")
    suspend fun deleteAllPpi()

}