package com.example.roomtutorial

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase

import kotlin.jvm.Synchronized
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Note::class,Ppi::class], version = 2)
internal abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun ppiDao():PpiDao
    companion object{
        private var instance: NoteDatabase? = null
        @JvmStatic
        @Synchronized
        fun getInstance(context: Context): NoteDatabase? {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java, "note_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance
        }


    }
}