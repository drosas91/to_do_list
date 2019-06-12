package com.example.todolist

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.todolist.DTO.ToDo
import com.example.todolist.DTO.ToDoItem

class DBHandler(val context: Context) : SQLiteOpenHelper(context,
    DB_NAME, null,
    DB_VERSION
){
    override fun onCreate(db: SQLiteDatabase) {
        val createToDoTable = "CREATE TABLE $TABLE_TODO (" +
                "$COL_ID integer PRIMARY KEY AUTOINCREMENT," +
                "$COL_NOMBRE varchar," +
                "$COL_DESCRIPCION varchar," +
                "$COL_NUEVO boolean);"

        db.execSQL(createToDoTable)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun addToDo(toDo: ToDo):Boolean{
        val db = writableDatabase
        val cv = ContentValues()
        cv.put(COL_NOMBRE, toDo.nombre)
        cv.put(COL_DESCRIPCION, toDo.descripcion)
        cv.put(COL_NUEVO, toDo.nuevo)
        val result = db.insert(TABLE_TODO, null, cv)
        return result != (-1).toLong()
    }

    fun updateToDo(toDo: ToDo){
        val db = writableDatabase
        val cv = ContentValues()
        cv.put(COL_NOMBRE, toDo.nombre)
        cv.put(COL_DESCRIPCION, toDo.descripcion)
        cv.put(COL_NUEVO, toDo.nuevo)
        db.update(TABLE_TODO, cv, "$COL_ID=?", arrayOf(toDo.id.toString()))
    }

    fun deleteToDo(toDoId: Long){
        val db = writableDatabase
        db.delete(TABLE_TODO,"$COL_ID=?", arrayOf(toDoId.toString()))
    }

    fun updateToDoCompletedStatus(toDoId: Long, isCompleted: Boolean){
        val db = writableDatabase
        val queryResult = db.rawQuery("SELECT * FROM $TABLE_TODO WHERE $COL_ID = $toDoId", null)

        if(queryResult.moveToFirst()){
            do{
                val item = ToDo()
                item.id = queryResult.getLong(queryResult.getColumnIndex(COL_ID))
                item.nombre = queryResult.getString(queryResult.getColumnIndex(COL_NOMBRE))
                item.descripcion = queryResult.getString(queryResult.getColumnIndex(COL_DESCRIPCION))
                item.nuevo = queryResult.getInt(queryResult.getColumnIndex(COL_NUEVO))>0
            }while (queryResult.moveToNext())
        }
    }

    fun getToDoElement(): MutableList<ToDo>{
        val result: MutableList<ToDo> = ArrayList()
        val db = readableDatabase
        val queryResult = db.rawQuery("SELECT * FROM $TABLE_TODO", null)
        if (queryResult.moveToFirst()){
            do{
                val todo = ToDo()
                todo.id = queryResult.getLong(queryResult.getColumnIndex(COL_ID))
                todo.nombre = queryResult.getString(queryResult.getColumnIndex(COL_NOMBRE))
                todo.descripcion = queryResult.getString(queryResult.getColumnIndex(COL_DESCRIPCION))
                todo.nuevo = queryResult.getInt(queryResult.getColumnIndex(COL_NUEVO))>0
                result.add(todo)
                println("TODO.TOSTRING: ")
                println(todo.id)
                println(todo.nombre)
                println(todo.descripcion)
                println(todo.nuevo)
            }while(queryResult.moveToNext())
        }
        queryResult.close()
        return result
    }

}