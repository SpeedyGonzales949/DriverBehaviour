package com.example.roomtutorial

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val noteRepository: NoteRepository
    var allNotes: LiveData<List<Note?>?>?
        get() = noteRepository.allNotes

    init {
        noteRepository = NoteRepository(application)
        allNotes = noteRepository.allNotes
    }

    fun insert(note: Note?) {
        viewModelScope.launch { noteRepository.insert(note) }
    }

    fun update(note: Note?) {
        viewModelScope.launch {
            noteRepository.update(note)
        }

    }

    fun delete(note: Note?) {
        viewModelScope.launch { noteRepository.delete(note) }

    }

    fun deleteAllNotes() {
        viewModelScope.launch { noteRepository.deleteAllNotes() }

    }
}