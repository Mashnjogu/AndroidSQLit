package com.globomed.learn

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class UpdateEmployeeActicity: AppCompatActivity(){

    lateinit var etEmpName: EditText
    lateinit var etDOB: EditText
    lateinit var bSave: Button
    lateinit var bCancel: Button
    lateinit var etDesignation: EditText
    lateinit var sSurgeon: Switch
//    lateinit var actionDelete: Menu
//    lateinit var actionDeleteAll: Menu

    lateinit var databaseHelper: DatabaseHelper
    private val myCalendar = Calendar.getInstance()

    var empId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        etEmpName = findViewById(R.id.etEmpName)
        etDOB = findViewById(R.id.etDOB)
        bSave = findViewById(R.id.bSave)
        bCancel = findViewById(R.id.bCancel)
        etDesignation = findViewById(R.id.etDesignation)
        sSurgeon = findViewById(R.id.sSurgeon)

//        actionDelete = findViewById(R.id.action_delete)
//        actionDeleteAll = findViewById(R.id.action_deleteAll)

        databaseHelper = DatabaseHelper(this)

        val bundle: Bundle? = intent.extras
        bundle?.let {
            empId = bundle.getString(GlobalMedDBContract.EmployeeEntry.COLUMN_ID)

            val employee = DataManager.fetchEmployee(databaseHelper, empId!!)

            employee?.let {
                etEmpName.setText(employee.name)
                etDesignation.setText(employee.designation)
                etDOB.setText(getFormattedDate(employee.dob))

                sSurgeon.isChecked = (1 == employee.isSurgeon)
            }
        }

        //on clicking 0k on the calendar dialog
        val date = DatePickerDialog.OnDateSetListener{view, year, month, dayOfMonth ->
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

        etEmpName.error = if(etEmpName.text.toString().isEmpty()){
            isValid = false
            "Required Field"
        } else null

        etDesignation.error = if (etDesignation.text.toString().isEmpty()){
            isValid = false
            "Required Field"
        }else null

        if (isValid){
            val updatedName: String = etEmpName.text.toString()
            val updatedDOB: Long = myCalendar.timeInMillis
            val updatedDesignation: String = etDesignation.text.toString()

            val updatedIsSurgeon: Int = if (sSurgeon.isChecked) 1 else 0

            val updatedEmployee = Employee(empId!!, updatedName, updatedDOB, updatedDesignation, updatedIsSurgeon)
            DataManager.updateEmployee(databaseHelper, updatedEmployee)

            setResult(Activity.RESULT_OK, Intent())

            Toast.makeText(applicationContext, "Employee Updated", Toast.LENGTH_SHORT).show()

            finish()
        }
    }

    private fun setUpCalendar(date: DatePickerDialog.OnDateSetListener){
        DatePickerDialog(
            this, date, myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun getFormattedDate(dobInMillis: Long?): String{
        return dobInMillis?.let {
            val sdf = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
            sdf.format(dobInMillis)
        } ?: "Not Found"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_delete -> {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(R.string.confirm_sure)
                    .setPositiveButton(R.string.yes){dialog, eId ->
                        val result: Int = DataManager.deleteEmployee(databaseHelper, empId.toString())

                        Toast.makeText(applicationContext, "$result record deleted", Toast.LENGTH_SHORT)
                            .show()

                        setResult(Activity.RESULT_OK, Intent())
                        finish()
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
