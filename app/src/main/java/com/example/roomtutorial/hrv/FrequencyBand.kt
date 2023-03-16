package com.example.roomtutorial.hrv

enum class FrequencyBand(val min:Double, val max: Double) {
    ULF(0.0,0.003),
    VLF(0.0033,0.04),
    LF(0.04,0.15),
    HF(0.15,0.4)
}