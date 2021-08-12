package com.example.projects;

import android.content.pm.PackageInstaller;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.projects.APIProjects.Todo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TodoChildAttributes implements Serializable {

    private List<String> todosChildNameList;
    private List<Integer> todosChildIndexList;
    private List<Todo> todos;

    public TodoChildAttributes(List<String> todosChildNameList, List<Integer> todosChildIndexList, List<Todo> todos) {
        this.todosChildNameList = todosChildNameList;
        this.todosChildIndexList = todosChildIndexList;
        this.todos = todos;
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

    public List<Todo> getTodos() {
        return todos;
    }

    public void setTodos(List<Todo> todos) {
        this.todos = todos;
    }

}
