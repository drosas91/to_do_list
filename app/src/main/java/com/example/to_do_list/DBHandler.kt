package com.example.to_do_list

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.to_do_list.DTO.ToDo

class DBHandler(val context: Context) : SQLiteOpenHelper(context,
    DB_NAME, null,
    DB_VERSION
){
    override fun onCreate(db: SQLiteDatabase) { // Create table TABLE_TODO
        val createToDoTable = "CREATE TABLE $TABLE_TODO (" +
                "$COL_ID integer PRIMARY KEY AUTOINCREMENT," +
                "$COL_NAME varchar," +
                "$COL_DESCRIPTION varchar," +
                "$COL_IS_COMPLETED boolean" +
                ");"

        db.execSQL(createToDoTable)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
    }

    fun addToDo(toDo: ToDo):Boolean{ // Create new task
        val result = writableDatabase.insert(TABLE_TODO, null, getContentValuesFromToDo(toDo))
        return result != (-1).toLong()
    }

    fun updateToDo(toDo: ToDo){ // Update existing task
        writableDatabase.update(TABLE_TODO, getContentValuesFromToDo(toDo), "$COL_ID=?", arrayOf(toDo.id.toString()))
    }

    private fun getContentValuesFromToDo(toDo: ToDo): ContentValues { // obtain COL_NAME, COL_DESCRIPTION and COL_IS_COMPLETED values
        val cv = ContentValues()
        cv.put(COL_NAME, toDo.name)
        cv.put(COL_DESCRIPTION, toDo.description)
        cv.put(COL_IS_COMPLETED, toDo.isCompleted)
        return cv
    }

    fun deleteToDo(toDoId: Long){ // Delete task by id
        val db = writableDatabase
        db.delete(TABLE_TODO,"$COL_ID=?", arrayOf(toDoId.toString()))
    }

    fun getToDoElements(): MutableList<ToDo>{ // Get existing tasks
        val result: MutableList<ToDo> = ArrayList()
        val db = readableDatabase
        val queryResult = db.rawQuery("SELECT * FROM $TABLE_TODO", null)
        if (queryResult.moveToFirst()){
            do{
                result.add(getToDoFromResult(queryResult))
            }while(queryResult.moveToNext())
        }
        queryResult.close()
        return result
    }

    private fun getToDoFromResult(queryResult: Cursor): ToDo { // store data (from select query) into a ToDo element
        val todo = ToDo()
        todo.id = queryResult.getLong(queryResult.getColumnIndex(COL_ID))
        todo.name = queryResult.getString(queryResult.getColumnIndex(COL_NAME))
        todo.description = queryResult.getString(queryResult.getColumnIndex(COL_DESCRIPTION))
        todo.isCompleted = queryResult.getInt(queryResult.getColumnIndex(COL_IS_COMPLETED)) > 0
        return todo
    }

    fun getToDoById(toDoId: Long): ToDo? { // Obtain data from a task by id
        var toDo: ToDo? = null
        val db = readableDatabase
        val queryResult = db.rawQuery("SELECT * FROM $TABLE_TODO WHERE $COL_ID=?", arrayOf(toDoId.toString()))
        if (queryResult.moveToFirst()){
            toDo = getToDoFromResult(queryResult)
        }
        queryResult.close()
        return toDo
    }

}