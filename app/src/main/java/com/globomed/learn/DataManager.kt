package com.globomed.learn

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.globomed.learn.GlobalMedDBContract.EmployeeEntry

object DataManager {

    fun fetchAllEmployees(databaseHelper: DatabaseHelper): ArrayList<Employee>{
        val employees = ArrayList<Employee>()

        //fetch data from database
        val db: SQLiteDatabase = databaseHelper.readableDatabase

        val columns = arrayOf(
            EmployeeEntry.COLUMN_ID,
            EmployeeEntry.COLUMN_NAME,
            EmployeeEntry.COLUMN_DOB,
            EmployeeEntry.COLUMN_DESIGNATION,
            EmployeeEntry.COLUMN_SURGEON
        )

        val cursor: Cursor = db.query(EmployeeEntry.TABLE_NAME, columns, null, null,
            null,null,null)

        val idPos: Int = cursor.getColumnIndex(EmployeeEntry.COLUMN_ID)
        val namePos: Int = cursor.getColumnIndex(EmployeeEntry.COLUMN_NAME)
        val dobPos: Int = cursor.getColumnIndex(EmployeeEntry.COLUMN_DOB)
        val designationPos: Int = cursor.getColumnIndex(EmployeeEntry.COLUMN_DESIGNATION)
        val surgeonPos: Int = cursor.getColumnIndex(EmployeeEntry.COLUMN_SURGEON)

       while (cursor.moveToNext()){
           val id: String = cursor.getString(idPos)
           val name: String = cursor.getString(namePos)
           val dob: Long = cursor.getLong(dobPos)
           val designation: String = cursor.getString(designationPos)
           val surgeon: Int = cursor.getInt(surgeonPos)

           employees.add(Employee(id, name, dob, designation, surgeon))
       }

        cursor.close()
        return employees
    }

    fun fetchEmployee(databaseHelper: DatabaseHelper, empId: String): Employee? {
        val db: SQLiteDatabase = databaseHelper.readableDatabase
        var employee: Employee? = null

        val columns = arrayOf(
            EmployeeEntry.COLUMN_NAME,
            EmployeeEntry.COLUMN_DOB,
            EmployeeEntry.COLUMN_DESIGNATION,
            EmployeeEntry.COLUMN_SURGEON
        )

        val selection = EmployeeEntry.COLUMN_ID + " LIKE ? "
        val selectionArgs: Array<String> = arrayOf(empId)

        val cursor: Cursor = db.query(EmployeeEntry.TABLE_NAME, columns, selection, selectionArgs,
            null,null,null)

        //fetch index of the columns
        val namePos: Int = cursor.getColumnIndex(EmployeeEntry.COLUMN_NAME)
        val dobPos: Int = cursor.getColumnIndex(EmployeeEntry.COLUMN_DOB)
        val designationPos: Int = cursor.getColumnIndex(EmployeeEntry.COLUMN_DESIGNATION)
        val surgeonPos: Int = cursor.getColumnIndex(EmployeeEntry.COLUMN_SURGEON)

        while (cursor.moveToNext()){
            val name: String = cursor.getString(namePos)
            val dob: Long = cursor.getLong(dobPos)
            val designation: String = cursor.getString(designationPos)
            val surgeon: Int = cursor.getInt(surgeonPos)

            employee = Employee(empId, name, dob, designation, surgeon)
        }

        cursor.close()

        return employee
    }

    fun updateEmployee(databaseHelper: DatabaseHelper, employee: Employee){

        val db: SQLiteDatabase = databaseHelper.writableDatabase

        val values = ContentValues() //in SQLite values are saved in ContentValue class
        values.put(EmployeeEntry.COLUMN_NAME, employee.name)
        values.put(EmployeeEntry.COLUMN_DOB, employee.dob)
        values.put(EmployeeEntry.COLUMN_DESIGNATION, employee.designation)
        values.put(EmployeeEntry.COLUMN_SURGEON, employee.isSurgeon)

        //selection argument
        val selection = EmployeeEntry.COLUMN_ID + " LIKE ? "
        val selectionArgs: Array<String> = arrayOf(employee.id)

        db.update(EmployeeEntry.TABLE_NAME, values, selection, selectionArgs)
    }

    //Int since it returns the number of rows affected due to deletion
    fun deleteEmployee(databaseHelper: DatabaseHelper, empId: String): Int{

        val db: SQLiteDatabase = databaseHelper.writableDatabase

        val selection = EmployeeEntry.COLUMN_ID + " LIKE ? "
        val selectionArgs: Array<String> = arrayOf(empId)

        return db.delete(EmployeeEntry.TABLE_NAME, selection, selectionArgs)
    }

    fun deleteAllEmployee(databaseHelper: DatabaseHelper): Int {

        val db: SQLiteDatabase = databaseHelper.writableDatabase

        return db.delete(EmployeeEntry.TABLE_NAME, "1", null)
    }

}