package com.example.roomtutorial

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ppi_table")
class Ppi(val ppi:Int, val timestamp:Long) {
    @PrimaryKey(autoGenerate = true)
    var id = 0
}