package com.example.roomtutorial.ui.nonlinear

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.roomtutorial.hrv.HRV
import kotlin.math.pow

class NonLinearDomainViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is slideshow Fragment"
    }
    private val _poincareData = MutableLiveData<String>().apply {

        value = "This"

    }
    val text: LiveData<String> = _text
}