package com.example.todolist01

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist01.DTO.ToDo
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : AppCompatActivity() {

    lateinit var dbHandler: DBHandler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        setSupportActionBar(dashboard_toolbar)
        title = "Lista de Tareas"
        dbHandler = DBHandler(this)
        rv_dashboard.layoutManager = LinearLayoutManager(this)
        fab_dashboard.setOnClickListener{
            val dialog = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.dialog_dashboard, null)
            val toDoName = view.findViewById<EditText>(R.id.ev_todo)
            dialog.setView(view)
            dialog.setTitle("Agregar nueva tarea")
            dialog.setPositiveButton("Agregar") { _: DialogInterface, _: Int ->
                if (toDoName.text.isNotEmpty()){
                    val toDo = ToDo()
                    toDo.nombre = toDoName.text.toString()
                    toDo.descripcion = toDoName.text.toString()
                    toDo.nuevo = false
                    try {
                        dbHandler.addToDo(toDo)
                    }catch (e: FileSystemException) {
                        e.printStackTrace()
                    }
                    try {
                        refreshList()
                    }catch(e: FileSystemException){
                        e.printStackTrace()
                    }

                }
            }
            dialog.setNegativeButton("Cancelar"){ _: DialogInterface, _: Int ->

            }
            dialog.show()
        }
    }

    fun buttonClick(view: View) {
        val mintent = Intent(this@DashboardActivity, DashboardActivityNewEdit::class.java)
        startActivity(mintent)
    }

    override fun onResume() {
        refreshList()
        super.onResume()
    }

    private fun refreshList(){
        rv_dashboard.adapter = DashboardAdapter(this, dbHandler.getToDoElement())
    }

    class DashboardAdapter(val context: Context, val list: MutableList<ToDo>) :
        RecyclerView.Adapter<DashboardAdapter.ViewHolder>(){
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(context).inflate(R.layout.rv_child_dashboard,p0,false))
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, p1: Int) {

            holder.toDoName.text = "["+list[p1].id+"] | "+list[p1].nombre
            /*holder.toDoName.text = list[p1].descripcion
            holder.toDoName.text = list[p1].nuevo.toString()
            holder.toDoName.text = list[p1].id.toString()*/

        }

        class ViewHolder(v: View) : RecyclerView.ViewHolder(v){
            val toDoName : TextView = v.findViewById(R.id.tv_todo_name)
        }
    }

}
