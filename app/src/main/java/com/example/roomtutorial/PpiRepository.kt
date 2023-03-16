package com.example.roomtutorial

import android.app.Application

class PpiRepository(application: Application?) {
    private val ppiDao: PpiDao


    init {
        val noteDatabase = NoteDatabase.getInstance(application!!)
        ppiDao=noteDatabase!!.ppiDao()

    }

    suspend fun insert(ppi: Ppi?) {
        ppiDao.insert(ppi)
    }

    suspend fun update(ppi: Ppi?) {
        ppiDao.update(ppi)
    }

    suspend fun delete(ppi: Ppi?) {
        ppiDao.delete(ppi)
    }

    suspend fun deleteAllPpi() {
        ppiDao.deleteAllPpi()
    }

}