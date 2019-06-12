package com.example.todolist.DTO

class ToDo{
    var id: Long = -1
    var nombre = ""
    var descripcion = ""
    var nuevo = true
    var items: MutableList<ToDo> = ArrayList()
}