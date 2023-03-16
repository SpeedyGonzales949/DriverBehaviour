package com.example.roomtutorial.hrv

class NonLinearDomain {

    fun rrDifferences(rrIntervals:List<Double>):List<Double>{
        return rrIntervals.zipWithNext { a, b -> b-a }
    }
}