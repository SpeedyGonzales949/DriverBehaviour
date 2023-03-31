package com.example.roomtutorial;

import com.example.roomtutorial.hrv.TimeDomain
import org.junit.Assert
import org.junit.Test;
import java.lang.Double.NaN
import kotlin.math.sqrt

class TimeDomainTest {
     val td = TimeDomain()
     @Test
     fun addition_isCorrect() {
         Assert.assertEquals(4, 2 + 2)
     }

     @Test
     fun pnn50_bvaTest(){
         // Test case 1: Minimum value
         val input1 = listOf(0)
         val expectedOutput1 = 0.0
         assert(td.pnn50(input1)==expectedOutput1)

         // Test case 2: Maximum value
         val input2 = listOf(3000)
         val expectedOutput2 = 0.0
         assert(td.pnn50(input2)==expectedOutput2)

         // Test case 3: Single R-R interval above the maximum value
         val input3 = listOf(3500)
         val expectedOutput3 = 0.0
         assert(td.pnn50(input3)==expectedOutput3)

         // Test case 4: Two R-R intervals above the maximum value
         val input4 = listOf(4500, 3500)
         val expectedOutput4 = 100.0

         assert(td.pnn50(input4)==expectedOutput4)

         // Test case 5: Single R-R interval below the minimum value
         val input5 = listOf(-50)
         val expectedOutput5 = 0.0
         assert(td.pnn50(input5)==expectedOutput5)

         // Test case 6: Two R-R intervals below the minimum value
         val input6 = listOf(-50, 1500)
         val expectedOutput6 = 100.0
         assert(td.pnn50(input6)==expectedOutput6)
     }

    @Test
    fun rmssd_ecpTest(){
        val class1 = listOf(-128, -125,-129) // R-R Intervals less than 0 ms
        val class2 = listOf(128,125,129) // R-R Intervals between 0 ms and 3000 ms
        val class3 = listOf(3008,3005,3009) // R-R Intervals greater than 3000ms
        assert(td.rmssd(class1)== sqrt(25.0/2))
        assert(td.rmssd(class2)==sqrt(25.0/2))
        assert(td.rmssd(class3)==sqrt(25.0/2))
    }
}
