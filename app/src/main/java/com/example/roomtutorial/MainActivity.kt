package com.example.roomtutorial

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.roomtutorial.hrv.HRV
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.math.pow


class MainActivity() : AppCompatActivity() {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1
        private const val TAG = "MainActivity"
    }

    private lateinit var noteViewModel: NoteViewModel
    private val deviceId = "A70E2C2B"

    private val bluetoothOnActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode != Activity.RESULT_OK) {
                Log.w(TAG, "Bluetooth off")
            }
        }

    private val addNoteActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val title: String = intent!!.getStringExtra(AddNoteActivity.EXTRA_TITLE).toString()
                val description: String =
                    intent!!.getStringExtra(AddNoteActivity.EXTRA_DESCRIPTION).toString()
                val priority = intent.getIntExtra(AddNoteActivity.EXTRA_PRIORITY, 1)

                val note = Note(title, description, priority)
                noteViewModel.insert(note)
                showToast("Note saved")
            } else {
                showToast("Note not saved")
            }

        }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {

        val numberOfSamples = 2.0.pow(16)
        val hrv = HRV()
        val rr1 = assets.open("rr/000.txt")
            .readAllBytes()
            .map { it.toDouble() / 1000 }
            .take(numberOfSamples.toInt())
//        val rr2 = assets.open("rr/002.txt")
//            .readAllBytes()
//            .map { it.toDouble()/1000 }
//            .take(65536)


        val points = hrv.frequencyDomain(rr1, 128)
        points.forEach {
            Log.d(
                TAG, it.toString()
            )
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val buttonAddNote = findViewById<FloatingActionButton>(R.id.button_add_note)
        buttonAddNote.setOnClickListener {
            val intent = Intent(this@MainActivity, AddNoteActivity::class.java)
            addNoteActivityResultLauncher.launch(intent)
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onClickOpenMenu()
        }
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        val adapter = NoteAdapter()
        recyclerView.adapter = adapter


        noteViewModel =
            ViewModelProvider(
                this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            )[NoteViewModel::class.java]
        noteViewModel.allNotes?.observe(
            this
        ) { notes -> //update RecyclerView
            showToast("Data Changed")
            adapter.setNotes(notes)
        }


        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: ViewHolder,
                target: ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
                noteViewModel.delete(adapter.getNoteAt(viewHolder.adapterPosition))
                showToast("Note deleted")
            }
        }).attachToRecyclerView(recyclerView)

        checkBT()


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    private fun checkBT() {
        val btManager = applicationContext.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter: BluetoothAdapter? = btManager.adapter
        if (bluetoothAdapter == null) {
            showToast("Device doesn't support Bluetooth")
            return
        }

        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            bluetoothOnActivityResultLauncher.launch(enableBtIntent)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    requestPermissions(
                        arrayOf(
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ), PERMISSION_REQUEST_CODE
                    )
                } else {
                    requestPermissions(
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        PERMISSION_REQUEST_CODE
                    )
                }
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    private fun showToast(message: String) {
        val toast = Toast.makeText(applicationContext, message, Toast.LENGTH_LONG)
        toast.show()
    }

    private fun onClickConnectPolar() {
        checkBT()
        showToast("Connecting to device" + " " + deviceId)
        val intent = Intent(this, PolarActivity::class.java)
        intent.putExtra("id", deviceId)
        startActivity(intent)
    }

    private fun onClickOpenMenu() {
        val intent = Intent(this, HrvDataActivity::class.java)
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.connect_polar -> {
                onClickConnectPolar()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}