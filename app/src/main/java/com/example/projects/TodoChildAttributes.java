package com.example.projects;

import android.content.pm.PackageInstaller;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.projects.APITodo.Todos;

import java.io.Serializable;
import java.util.ArrayList;

public class TodoChildAttributes implements Serializable {

    private ArrayList<String> todosChildNameList;
    private ArrayList<Integer> todosChildIndexList;
    private Todos todos;
    private int positionProjects;

    public TodoChildAttributes(ArrayList<String> todosChildNameList, ArrayList<Integer> todosChildIndexList, Todos todos, int positionProjects) {
        this.todosChildNameList = todosChildNameList;
        this.todosChildIndexList = todosChildIndexList;
        this.todos = todos;
        this.positionProjects = positionProjects;
    }

    public ArrayList<String> getTodosChildNameList() {
        return todosChildNameList;
    }

    public void setTodosChildNameList(ArrayList<String> todosChildNameList) {
        this.todosChildNameList = todosChildNameList;
    }

    public ArrayList<Integer> getTodosChildIndexList() {
        return todosChildIndexList;
    }

    public void setTodosChildIndexList(ArrayList<Integer> todosChildIndexList) {
        this.todosChildIndexList = todosChildIndexList;
    }

    public Todos getTodos() {
        return todos;
    }

    public void setTodos(Todos todos) {
        this.todos = todos;
    }

    public int getPositionProjects() {
        return positionProjects;
    }

    public void setPositionProjects(int positionProjects) {
        this.positionProjects = positionProjects;
    }

}
