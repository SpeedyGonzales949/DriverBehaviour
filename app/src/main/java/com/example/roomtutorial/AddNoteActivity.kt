package com.example.roomtutorial

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.widget.Toolbar

class AddNoteActivity : AppCompatActivity() {
    companion object{
        public val EXTRA_TITLE = "com.example.roomtutorial.EXTRA_TITLE"
        public val EXTRA_DESCRIPTION = "com.example.roomtutorial.EXTRA_DESCRIPTION"
        public val EXTRA_PRIORITY = "com.example.roomtutorial.EXTRA_PRIORITY"
    }

    private lateinit var editTextTitle: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var numberPickerPriority: NumberPicker
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)
        editTextTitle = findViewById(R.id.edit_text_title)
        editTextDescription = findViewById(R.id.edit_text_description)
        numberPickerPriority = findViewById(R.id.number_picker_priority)

        numberPickerPriority.minValue = 1
        numberPickerPriority.maxValue = 10


        val toolbar=findViewById<Toolbar>(R.id.add_note_toolbar)
        setSupportActionBar(toolbar)

        //supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)

    }

    fun saveNote() {
        val title = editTextTitle.text.toString()
        val description = editTextDescription.text.toString()
        val priority = numberPickerPriority.value

        if (title.trim().isEmpty() || description.trim().isEmpty()) {
            Toast.makeText(this, "Please insert a title and description", Toast.LENGTH_SHORT).show()
            return
        }

        val data=Intent()
        data.putExtra(EXTRA_DESCRIPTION,title)
        data.putExtra(EXTRA_TITLE,description)
        data.putExtra(EXTRA_PRIORITY,priority)
        setResult(RESULT_OK,data)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_note_menu, menu)
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save_note -> {
                saveNote()
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }

        }

    }


}