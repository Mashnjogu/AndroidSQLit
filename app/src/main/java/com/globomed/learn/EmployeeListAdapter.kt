package com.globomed.learn

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text
import java.util.ArrayList


class EmployeeListAdapter(private val context: Context):
    RecyclerView.Adapter<EmployeeListAdapter.EmployeeViewHolder>(){

    lateinit var employeeList: ArrayList<Employee>

    inner class EmployeeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val tvEmpName: TextView
        val tvEmpDesignation: TextView
        val tvIsSurgeonConfirm: TextView
        var pos = 0

        init {
            tvEmpName = itemView.findViewById(R.id.tvEmpName)
            tvEmpDesignation = itemView.findViewById(R.id.tvEmpDesignation)
            tvIsSurgeonConfirm = itemView.findViewById(R.id.tvIsSurgeonConfirm)
        }

        fun setData(name: String, designation: String, isSurgeon: Int, pos: Int) {
            tvEmpName.text = name
            tvEmpDesignation.text = designation
            tvIsSurgeonConfirm.text = if (1 == isSurgeon){
                "YES"
            }else{"NO"}
            this.pos = pos
        }

        fun setListener() {
            itemView.setOnClickListener{
                val intent = Intent(context, UpdateEmployeeActicity::class.java)
                intent.putExtra(GlobalMedDBContract.EmployeeEntry.COLUMN_ID, employeeList[pos].id)
                (context as Activity).startActivity(intent)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        return EmployeeViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        val employee: Employee = employeeList[position]
        holder.setData(employee.name, employee.designation, employee.isSurgeon, position)
        holder.setListener()
    }

    override fun getItemCount(): Int = employeeList.size

    fun setEmployees(employees: ArrayList<Employee>) {
        employeeList = employees
        notifyDataSetChanged()
    }
}

