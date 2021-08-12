package com.example.projects;

import com.example.projects.APIProjects.Project;
import com.example.projects.APIProjects.Todo;

import java.io.Serializable;
import java.util.List;

public class TodosAttributes implements Serializable {

    private Project project;
    private List<Todo> todosParentData;
    private TodosData todosData;

    public TodosAttributes(Project project, List<Todo> todosParentData, TodosData todosData) {
        this.project = project;
        this.todosParentData = todosParentData;
        this.todosData = todosData;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<Todo> getTodosParentData() {
        return todosParentData;
    }

    public void setTodosParentData(List<Todo> todosParentData) {
        this.todosParentData = todosParentData;
    }

    public TodosData getTodosData() {
        return todosData;
    }

    public void setTodosData(TodosData todosData) {
        this.todosData = todosData;
    }
}
