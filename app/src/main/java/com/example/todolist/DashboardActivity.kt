package com.example.todolist

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.DTO.ToDo
import com.example.todolist01.R
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : AppCompatActivity() {

    lateinit var dbHandler: DBHandler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        setSupportActionBar(dashboard_toolbar)
        title = getString(R.string.app_title)
        dbHandler = DBHandler(this)
        rv_dashboard.layoutManager = LinearLayoutManager(this)
        fab_dashboard.setOnClickListener{
            val dialog = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.dialog_dashboard, null)
            val toDoName = view.findViewById<EditText>(R.id.ev_todo)
            dialog.setView(view)
            dialog.setTitle(getString(R.string.add_new_task))
            dialog.setPositiveButton(getString(R.string.add_task_button_label)) { _: DialogInterface, _: Int ->
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

    fun updateToDo(toDo: ToDo){
        val dialog = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_dashboard, null)
        val toDoName = view.findViewById<EditText>(R.id.ev_todo)
        toDoName.setText(toDo.nombre)
        dialog.setView(view)
        dialog.setTitle(getString(R.string.update_task))
        dialog.setPositiveButton(getString(R.string.update_task_button_label)) { _: DialogInterface, _: Int ->
            if (toDoName.text.isNotEmpty()){
                val toDo = ToDo()
                toDo.nombre = toDoName.text.toString()
                toDo.descripcion = toDoName.text.toString()
                toDo.nuevo = false
                try {
                    dbHandler.updateToDo(toDo)
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

    /*fun buttonClick(view: View) {
        val mintent = Intent(this@DashboardActivity, DashboardActivityNewEdit::class.java)
        mintent.putExtra("element", (view as TextView).text.split(": ")[0])
        startActivity(mintent)
    }*/

    override fun onResume() {
        refreshList()
        super.onResume()
    }

    private fun refreshList(){
        rv_dashboard.adapter = DashboardAdapter(this, dbHandler.getToDoElement())
    }

    class DashboardAdapter(val activity: DashboardActivity, val list: MutableList<ToDo>) :
        RecyclerView.Adapter<DashboardAdapter.ViewHolder>(){
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(activity).inflate(
                    R.layout.rv_child_dashboard,
                    p0,
                    false
                )
            )
        }

        override fun getItemCount(): Int {
            return list.size
        }


        override fun onBindViewHolder(holder: ViewHolder, p1: Int) {
            holder.toDoName.text = ""+list[p1].id+": "+list[p1].nombre
            /*holder.toDoName.text = list[p1].descripcion
            holder.toDoName.text = list[p1].nuevo.toString()
            holder.toDoName.text = list[p1].id.toString()*/

            /*holder.toDoName.setOnClickListener {
                val intent = Intent(context,ItemActivity::class.java)
                intent.putExtra(INTENT_TODO_ID, list[p1].id)
                intent.putExtra(INTENT_TODO_NAME, list[p1].name)
                context.startActivity(intent)
            }*/

            holder.menu.setOnClickListener{

                val popup = PopupMenu(activity, holder.menu)
                popup.inflate(R.menu.dashboard_child)
                popup.setOnMenuItemClickListener {
                    when(it.itemId){
                        R.id.menu_edit->{
                            activity.updateToDo(list[p1])
                        }
                        R.id.menu_delete->{
                            activity.dbHandler.deleteToDo(list[p1].id)
                            activity.refreshList()
                        }
                        R.id.menu_finish->{
                            activity.dbHandler.updateToDoCompletedStatus(list[p1].id, true)
                        }
                        R.id.menu_reset-> {
                            activity.dbHandler.updateToDoCompletedStatus(list[p1].id, false)
                        }
                    }
                    true
                }
                popup.show()
            }

        }

        class ViewHolder(v: View) : RecyclerView.ViewHolder(v){
            val toDoName : TextView = v.findViewById(R.id.tv_todo_name)
            val menu : ImageView = v.findViewById(R.id.iv_menu)
        }
    }

}
