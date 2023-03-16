package com.example.roomtutorial

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NoteDao {
    @Insert
    suspend fun insert(note: Note?)

    @Update
    suspend fun update(note: Note?)

    @Delete
    suspend fun delete(note: Note?)

    @Query("delete from note_table")
    suspend fun deleteAllNotes()

    @get:Query("Select * from note_table order by priority desc")
    val allNotes: LiveData<List<Note?>?>?
}