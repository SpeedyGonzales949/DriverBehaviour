package com.example.roomtutorial.hrv

class HRV {

    val fd = FrequencyDomain()
    val nld = NonLinearDomain()


    fun frequencyDomain(
        intervals: List<Double>,
        sampleRate: Int
    ): List<FrequencyDomainDataPoint> {
        return fd.compute(
            intervals,
            sampleRate,
            arrayListOf(FrequencyBand.ULF, FrequencyBand.LF, FrequencyBand.VLF, FrequencyBand.HF)
        )
    }

    fun nonlinearDomain(intervals: List<Double>): List<Double> {
        return nld.rrDifferences(intervals)
    }
}