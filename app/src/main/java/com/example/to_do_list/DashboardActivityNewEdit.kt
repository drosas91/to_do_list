package com.example.to_do_list

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.to_do_list.DTO.ToDo
import com.example.todolist01.R
import kotlinx.android.synthetic.main.activity_dashboard_new_edit.*

class DashboardActivityNewEdit : AppCompatActivity() {

    lateinit var dbHandler: DBHandler

    // Initial setup
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_new_edit)
        setSupportActionBar(dashboard_toolbar)

        dbHandler = DBHandler(this)

        // If receive extras means is a task edition
        if (intent.extras != null) {
            title = getString(R.string.edit_title)
            val toDo = dbHandler.getToDoById(intent.getLongExtra("id", -1))
            if (toDo != null) {
                input_task_name.setText(toDo.name)
                input_task_description.setText(toDo.description)
            }
            // Hide add icon
            agregar.hide()
            // Show save icon
            editar.show()
            editar.setOnClickListener {
                val toDo = ToDo()
                toDo.id = intent.getLongExtra("id", -1)
                toDo.name = input_task_name.text.toString()
                toDo.description = input_task_description.text.toString()
                // Input name and input description must be not empty
                if (toDo.name.isNotEmpty() && toDo.description.isNotEmpty()) {
                    try {
                        dbHandler.updateToDo(toDo)
                    } catch (e: FileSystemException) {
                        e.printStackTrace()
                    }
                    goToDashboard()
                } else {
                    showErrorMessage(toDo)
                }
            }
        } else {
            // If not receive extras means is a task creation
            title = getString(R.string.create_title)
            // Hide save icon
            editar.hide()
            // Show add icon
            agregar.show()
            agregar.setOnClickListener {
                val toDo = ToDo()
                toDo.name = input_task_name.text.toString()
                toDo.description = input_task_description.text.toString()
                if (toDo.name.isNotEmpty() && toDo.description.isNotEmpty()) {
                    try {
                        dbHandler.addToDo(toDo)
                    } catch (e: FileSystemException) {
                        e.printStackTrace()
                    }
                    goToDashboard()
                } else {
                    showErrorMessage(toDo)
                }
            }
        }

    }

    private fun showErrorMessage(toDo: ToDo) {
        var toastErrorMessage = getString(R.string.error_input_fields_empty)
        if (toDo.name.isEmpty() && toDo.description.isNotEmpty()) {
            toastErrorMessage = getString(R.string.error_input_name_empty)
        } else if (toDo.name.isNotEmpty() && toDo.description.isEmpty()) {
            toastErrorMessage = getString(R.string.error_input_description_empty)
        }

        Toast.makeText(
            applicationContext,
            toastErrorMessage,
            Toast.LENGTH_SHORT
        ).show()
    }

    // back to DashboardActivity
    private fun goToDashboard() {
        val intent = Intent(this@DashboardActivityNewEdit, DashboardActivity::class.java)
        startActivity(intent)
    }
}
