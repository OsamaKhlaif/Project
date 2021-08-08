package com.example.projects;

import android.content.pm.PackageInstaller;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.projects.APITodo.Todos;

import java.util.ArrayList;

public class TodoChildAttributes implements Parcelable {

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

    protected TodoChildAttributes(Parcel in) {
        todosChildNameList = in.createStringArrayList();
        positionProjects = in.readInt();
    }

    public static final Creator<TodoChildAttributes> CREATOR = new Creator<TodoChildAttributes>() {
        @Override
        public TodoChildAttributes createFromParcel(Parcel in) {
            return new TodoChildAttributes(in);
        }

        @Override
        public TodoChildAttributes[] newArray(int size) {
            return new TodoChildAttributes[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(todosChildNameList);
        dest.writeInt(positionProjects);
    }
}
