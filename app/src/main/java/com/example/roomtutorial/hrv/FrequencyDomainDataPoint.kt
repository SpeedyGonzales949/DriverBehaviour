package com.example.roomtutorial.hrv

class FrequencyDomainDataPoint(
    var absolutePower: Double,
    var relativePower: Double = 0.0,
    var peak: Double,
    var frequencyBand: FrequencyBand
) {
    override fun toString(): String {
        return "FrequencyDomainDataPoint(absolutePower=$absolutePower, relativePower=$relativePower, peak=$peak, frequencyBand=$frequencyBand)"
    }
}