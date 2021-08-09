package com.example.projects;

import android.content.pm.PackageInstaller;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.projects.APITodo.Todos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TodoChildAttributes implements Serializable {

    private List<String> todosChildNameList;
    private List<Integer> todosChildIndexList;
    private Todos todos;
    private int positionProjects;

    public TodoChildAttributes(List<String> todosChildNameList, List<Integer> todosChildIndexList, Todos todos, int positionProjects) {
        this.todosChildNameList = todosChildNameList;
        this.todosChildIndexList = todosChildIndexList;
        this.todos = todos;
        this.positionProjects = positionProjects;
    }

    public List<String> getTodosChildNameList() {
        return todosChildNameList;
    }

    public void setTodosChildNameList(ArrayList<String> todosChildNameList) {
        this.todosChildNameList = todosChildNameList;
    }

    public List<Integer> getTodosChildIndexList() {
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
