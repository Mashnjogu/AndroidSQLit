package com.globomed.learn

import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*
import com.globomed.learn.GlobalMedDBContract.EmployeeEntry as EmployeeEntry

class AddEmployeeActivity : AppCompatActivity() {

//    private val etEmpName = findViewById<EditText>(R.id.etEmpName)
//    val etDOB = findViewById<EditText>(R.id.etDOB)
//    val bSave = findViewById<Button>(R.id.bSave)
//    val bCancel = findViewById<Button>(R.id.bCancel)
//    val etDesignation = findViewById<EditText>(R.id.etDesignation)
    lateinit var etEmpName: EditText
    lateinit var etDOB: EditText
    lateinit var bSave: Button
    lateinit var bCancel: Button
    lateinit var etDesignation: EditText
    lateinit var sSurgeon: Switch

    private var myCalendar = Calendar.getInstance()
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        etEmpName = findViewById(R.id.etEmpName)
        etDOB = findViewById(R.id.etDOB)
        bSave = findViewById(R.id.bSave)
        bCancel = findViewById(R.id.bCancel)
        etDesignation = findViewById(R.id.etDesignation)
        sSurgeon = findViewById(R.id.sSurgeon)

        databaseHelper = DatabaseHelper(this)

        //on clicking ok on the calendar dialog
        val date = DatePickerDialog.OnDateSetListener{ view, year, month, dayOfMonth ->  
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            etDOB.setText(getFormattedDate(myCalendar.timeInMillis))
        }

        etDOB.setOnClickListener {
            setUpCalendar(date)
        }

        bSave.setOnClickListener {
            saveEmployee()
        }

        bCancel.setOnClickListener {
            finish()
        }

    }

    private fun saveEmployee() {
        var isValid = true

        etEmpName.error = if(etEmpName?.text.toString().isEmpty()){
            isValid = false
            "Required Field"
        } else null

        etDesignation.error = if (etDesignation?.text.toString().isEmpty()){
            isValid = false
            "Required Field"
        }else null

        if (isValid){
            val name: String = etEmpName?.text.toString()
            val designation: String = etDesignation?.text.toString()
            val dob: Long = myCalendar.timeInMillis
            val isSurgeon: Int = if (sSurgeon.isChecked) 1 else 0

            val db = databaseHelper.writableDatabase
            val values = ContentValues() //in SQLite values are saved in ContentValue class
            values.put(EmployeeEntry.COLUMN_NAME, name)
            values.put(EmployeeEntry.COLUMN_DOB, dob)
            values.put(EmployeeEntry.COLUMN_DESIGNATION, designation)
            values.put(EmployeeEntry.COLUMN_SURGEON, isSurgeon)

            //insert values into the database
            val result = db.insert(EmployeeEntry.TABLE_NAME, null, values)

            setResult(Activity.RESULT_OK, Intent())

            Toast.makeText(applicationContext, "Employee Added", Toast.LENGTH_SHORT).show()
        }

        finish()
    }

    private fun setUpCalendar(date: DatePickerDialog.OnDateSetListener){
        DatePickerDialog(
            this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun getFormattedDate(dobInMillis: Long?): String{
        return dobInMillis?.let {
            val sdf = SimpleDateFormat("dd MMM yyyy",Locale.getDefault())
            sdf.format(dobInMillis)
        } ?: "Not found"
    }
}