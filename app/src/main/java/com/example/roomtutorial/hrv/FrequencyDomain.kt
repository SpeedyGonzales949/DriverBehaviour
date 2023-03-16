package com.example.roomtutorial.hrv

import android.util.Log
import org.apache.commons.math3.complex.Complex
import org.apache.commons.math3.transform.DftNormalization
import org.apache.commons.math3.transform.FastFourierTransformer
import org.apache.commons.math3.transform.TransformType
import kotlin.math.pow

class FrequencyDomain {


    fun compute(
        intervals: List<Double>,
        sampleRate: Int,
        bands: List<FrequencyBand>
    ): List<FrequencyDomainDataPoint> {
        val fft = fft(intervals)
        val frequencyResolution = sampleRate / fft.size.toDouble()
        var totalPower = 0.0
        val dataPoints: ArrayList<FrequencyDomainDataPoint> = ArrayList()
        for (band in bands) {
            val currentDataPoint = FrequencyDomainDataPoint(
                absolutePower = absolutePower(fft, band, frequencyResolution),
                peak = peak(fft, band, frequencyResolution),
                frequencyBand = band
            )
            totalPower += currentDataPoint.absolutePower
            dataPoints.add(currentDataPoint)
        }
        for (dataPoint in dataPoints) {
            dataPoint.relativePower = dataPoint.absolutePower / totalPower
        }
        val ratioLFHF: Double? =
            dataPoints
                .find { it.frequencyBand == FrequencyBand.LF }
                ?.absolutePower
                ?.div(dataPoints.find { it.frequencyBand == FrequencyBand.HF }?.absolutePower!!)

        val relativePowerHFnu: Double? =
            dataPoints
                .find { it.frequencyBand == FrequencyBand.HF }
                ?.absolutePower
                ?.div(
                    dataPoints.filter { it.frequencyBand == FrequencyBand.HF || it.frequencyBand == FrequencyBand.LF }
                        .sumOf { it.absolutePower }
                )

        val relativePowerLFnu: Double? =
            dataPoints
                .find { it.frequencyBand == FrequencyBand.LF }
                ?.absolutePower
                ?.div(
                    dataPoints.filter { it.frequencyBand == FrequencyBand.HF || it.frequencyBand == FrequencyBand.LF }
                        .sumOf { it.absolutePower }
                )


        Log.d(Utils.TAG,"relativePowerHFnu=${relativePowerHFnu}")
        Log.d(Utils.TAG,"relativePowerLFnu=${relativePowerLFnu}")
        Log.d(Utils.TAG,"ratioLFHF=${ratioLFHF}")

        return dataPoints
    }

    fun fft(intervals: List<Double>): List<Complex> {
        return FastFourierTransformer(DftNormalization.STANDARD)
            .transform(
                intervals.stream().mapToDouble { it.toDouble() }.toArray(),
                TransformType.FORWARD
            )
            .toList()
    }


    fun absolutePower(
        fft: List<Complex>,
        frequencyBand: FrequencyBand,
        frequencyResolution: Double
    ): Double {
        return fft
            .filterIndexed { index, _ -> index * frequencyResolution < frequencyBand.max && index * frequencyResolution > frequencyBand.min }
            .sumOf { it.abs().pow(2) }
    }

    fun peak(
        fft: List<Complex>,
        frequencyBand: FrequencyBand,
        frequencyResolution: Double
    ): Double {
        return fft.mapIndexed { index, _ ->  index * frequencyResolution }
            .filter { it > frequencyBand.min && it < frequencyBand.max }
            .max()

    }

}