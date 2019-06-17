package com.example.to_do_list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.to_do_list.DTO.ToDo
import com.example.todolist01.R
import com.example.todolist01.R.*
import kotlinx.android.synthetic.main.activity_dashboard.*


class DashboardActivity : AppCompatActivity() {

    lateinit var dbHandler: DBHandler

    // Initial setup
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_dashboard)
        setSupportActionBar(dashboard_toolbar)
        title = getString(R.string.app_title)
        dbHandler = DBHandler(this)
        rv_dashboard.layoutManager = LinearLayoutManager(this)
        fab_dashboard.setOnClickListener {
            buttonClick()
        }
    }

    fun updateToDo(toDo: ToDo) {
        buttonClick(toDo)
    }

    // Edit task
    fun buttonClick(toDo: ToDo) {
        val startNewActivityIntent = Intent(this@DashboardActivity, DashboardActivityNewEdit::class.java)
        val id = toDo.id
        startNewActivityIntent.putExtra("id", id)
        startActivity(startNewActivityIntent)
    }

    // Create task
    fun buttonClick() {
        val intent = Intent(this@DashboardActivity, DashboardActivityNewEdit::class.java)
        // Activity Transition
        startActivity(intent)
    }


    override fun onResume() {
        showList(dbHandler.getToDoElements())
        super.onResume()
    }

    private fun showList(elementList: MutableList<ToDo>) {
        if (elementList.isEmpty()) {
            // if list is empty show message
            showEmptyListMessage()
        } else {
            refreshList(elementList)
        }
    }

    private fun showEmptyListMessage() {
        Toast.makeText(applicationContext, getString(string.empty_state_label), Toast.LENGTH_LONG).show()
    }

    private fun refreshList(elementList: MutableList<ToDo>) {
        rv_dashboard.adapter = DashboardAdapter(this, elementList)
    }

    // List initialization
    class DashboardAdapter(val activity: DashboardActivity, val list: MutableList<ToDo>) :
        RecyclerView.Adapter<DashboardAdapter.ViewHolder>() {
        override fun onCreateViewHolder(viewGroup: ViewGroup, currentPosition: Int): ViewHolder {
            return if (list.isEmpty()) {
                ViewHolder(View(activity.applicationContext))
            }else {
                // Inflating child items
                ViewHolder(
                    LayoutInflater.from(activity).inflate(
                        layout.rv_child_dashboard,
                        viewGroup,
                        false
                    )
                )
            }
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
            super.onBindViewHolder(holder, position, payloads)
            //set visibility to GONE on check icon when task is done
            holder.taskFinished.visibility = if (list[position].isCompleted) View.VISIBLE else View.GONE
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, elementPosition: Int) {
            holder.toDoName.text = list[elementPosition].name
            holder.taskFinished
            var elementList = activity.dbHandler.getToDoElements()
            holder.menu.setOnClickListener {
                val popup = PopupMenu(activity, holder.menu)

                //Inflates layout with reset button if task is completed
                popup.inflate(if (list[elementPosition].isCompleted) menu.dashboard_child_reset else menu.dashboard_child)

                popup.setOnMenuItemClickListener {
                    // Menu actions switching
                    when (it.itemId) {
                        id.menu_edit -> {
                            activity.updateToDo(list[elementPosition])
                            activity.showList(elementList)
                        }
                        id.menu_delete -> {
                            activity.dbHandler.deleteToDo(list[elementPosition].id)
                            elementList = activity.dbHandler.getToDoElements()
                            activity.refreshList(elementList)
                            activity.showList(elementList)
                        }
                        id.menu_finish -> {
                            holder.taskFinished.visibility = View.VISIBLE
                            list[elementPosition].isCompleted = true
                            activity.dbHandler.updateToDo(list[elementPosition])
                        }
                        id.menu_reset -> {
                            holder.taskFinished.visibility = View.GONE
                            list[elementPosition].isCompleted = false
                            activity.dbHandler.updateToDo(list[elementPosition])
                        }
                    }
                    true
                }
                popup.show()
            }
            // React to click event when tapping on text to edit task
            holder.toDoName.setOnClickListener {
                activity.updateToDo(list[elementPosition])
                activity.showList(elementList)
            }
        }

        // Elements to show on task list
        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val taskFinished: ImageView = v.findViewById(id.set_completed)
            val toDoName: TextView = v.findViewById(id.tv_todo_name)
            val menu: ImageView = v.findViewById(id.iv_menu)
        }
    }
}
