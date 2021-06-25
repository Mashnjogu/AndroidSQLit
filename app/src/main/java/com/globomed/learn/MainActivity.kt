package com.globomed.learn

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private val employeeListAdapter = EmployeeListAdapter(this)
    lateinit var recyclerView: RecyclerView
    lateinit var databaseHelper: DatabaseHelper



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        databaseHelper = DatabaseHelper(this)

        recyclerView.adapter = employeeListAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        employeeListAdapter.setEmployees(DataManager.fetchAllEmployees(databaseHelper))
        val getDatabaseContents = registerForActivityResult(StartActivityForResult()){ result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK){
                employeeListAdapter.setEmployees(DataManager.fetchAllEmployees(databaseHelper))
            }

        }

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val addEmployee = Intent(this, AddEmployeeActivity::class.java)
            getDatabaseContents.launch(addEmployee)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_deleteAll -> {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(R.string.confirm_sure)
                    .setPositiveButton(R.string.yes){dialog, eId ->
                        val result: Int = DataManager.deleteAllEmployee(databaseHelper)

                        Toast.makeText(applicationContext, "$result record deleted", Toast.LENGTH_SHORT)
                            .show()

                        setResult(Activity.RESULT_OK, Intent())
                    }
                    .setNegativeButton(R.string.no){dialog, id ->
                        dialog.dismiss()
                    }

                val dialog: AlertDialog = builder.create()
                dialog.setTitle("Are you sure")
                dialog.show()

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}