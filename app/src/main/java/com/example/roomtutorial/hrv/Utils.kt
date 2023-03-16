package com.example.roomtutorial.hrv

import com.example.roomtutorial.Ppi
import org.apache.commons.math3.transform.DftNormalization
import org.apache.commons.math3.transform.FastFourierTransformer
import org.apache.commons.math3.transform.TransformType
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class Utils {
    companion object {
        val TAG = "UTILITY"
    }

    /**
     * Standard deviation of the average NN intervals for each 5min segment of a 24h HRV recording
     */
    fun sdann(rr: List<Ppi>): Double {

        val listOfAverages: List<Double> = rr.groupBy { it.timestamp / 5 * 60 * 1000 }
            .mapValues { it.value.map { elem -> elem.ppi }.average() }
            .map { it.value }
        val mean = listOfAverages.average()

        return sqrt(
            listOfAverages.sumOf { (it - mean).pow(2) }
        )

    }

    /**
     * Mean of the standard deviations of all the NN intervals for each 5min segment of a 24h HRV recording
     */
    fun sdnni(rr: List<Ppi>): Double {
        return rr.groupBy { it.timestamp / 5 * 60 * 1000 }
            .mapValues {
                val mean = it.value.map { elem -> elem.ppi }.average()
                sqrt(
                    it.value.sumOf { elem -> (elem.ppi - mean).pow(2) }
                )
            }.map { it.value }
            .average()
    }

    /**
     * @param intervals rr_stream
     * @return Standard deviation of RR intervals
     */
    fun sdrr(intervals: List<Int>): Double {
        var sum = 0.0
        var standardDeviation = 0.0
        val length = intervals.size
        for (num in intervals) {
            sum += num
        }
        val mean = sum / length
        for (num in intervals) {
            standardDeviation += Math.pow(num - mean, 2.0)
        }
        return sqrt(standardDeviation / length)
    }

    /**
     * @param intervals rr_stream
     * @return Percentage of successive RR intervals that differ by more than 50ms
     */
    fun pnn50(intervals: List<Int>): Double {
        var ct = 0
        for (i in 0 until intervals.size - 1) {
            if (abs(intervals[i] - intervals[i + 1]) >= 50) {
                ct++
            }
        }
        return ct.toDouble() / (intervals.size - 1) * 100
    }

    /**
     * @param intervals rr_stream
     * @return Root mean square of successive RR interval differences
     */
    fun rmssd(intervals: List<Int>): Double {
        val differences: MutableList<Int> = ArrayList()
        for (i in 0 until intervals.size - 1) {
            differences.add(abs(intervals[i] - intervals[i + 1]))
        }
        val sum = differences.stream().mapToInt { elem: Int -> elem * elem }.sum()
        val size = differences.size


        return sqrt(sum.toDouble() / size)
    }



}