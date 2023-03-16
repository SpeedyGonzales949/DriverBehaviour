package com.example.roomtutorial

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PpiViewModel(application: Application) : AndroidViewModel(application) {
    private val ppiRepository: PpiRepository


    init {
        ppiRepository = PpiRepository(application)

    }

    fun insert(ppi: Ppi?) {
        viewModelScope.launch { ppiRepository.insert(ppi) }
    }

    fun update(ppi: Ppi?) {
        viewModelScope.launch {
            ppiRepository.update(ppi)
        }

    }

    fun delete(ppi: Ppi?) {
        viewModelScope.launch { ppiRepository.delete(ppi) }

    }

    fun deleteAllPpi() {
        viewModelScope.launch { ppiRepository.deleteAllPpi() }

    }
}