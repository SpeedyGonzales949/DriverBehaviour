package com.example.roomtutorial.hrv

class HRV {

    val fd = FrequencyDomain()
    val nld = NonLinearDomain()
    val td = TimeDomain()


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
    fun timeDomain(intervals: List<Int>):String{
        return "rmssd:${td.rmssd(intervals)}"+
                "pnn50:${td.pnn50(intervals)}"+
                "sdrr:${td.sdrr(intervals)}"
    }


}