package com.example.roomtutorial

import com.example.roomtutorial.NoteDatabase.Companion.getInstance
import android.app.Application
import androidx.lifecycle.LiveData

class NoteRepository(application: Application?) {
    private val noteDao: NoteDao
    val allNotes: LiveData<List<Note?>?>?

    init {
        val noteDatabase = getInstance(application!!)
        noteDao = noteDatabase!!.noteDao()
        allNotes = noteDao.allNotes
    }

    suspend fun insert(note: Note?) {
        noteDao.insert(note)
    }

    suspend fun update(note: Note?) {
        noteDao.update(note)
    }

    suspend fun delete(note: Note?) {
        noteDao.delete(note)
    }

    suspend fun deleteAllNotes() {
        noteDao.deleteAllNotes()
    }

}