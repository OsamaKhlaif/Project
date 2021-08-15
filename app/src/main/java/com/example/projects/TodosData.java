package com.example.projects;

import com.example.projects.APIProjects.Project;
import com.example.projects.APIProjects.Todo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TodosData implements Serializable {

    private final List<Todo> allTodos;
    private List<Todo> todoList;

    public TodosData(List<Todo> allTodos) {
        this.allTodos = allTodos;
    }

    public List<Todo> getParentTodos(Project project) {
        todoList = new ArrayList<>();

        for (Todo todo : allTodos) {
            if (todo.getProjectId().equals(project.getId()) && todo.getParent().equals(Constants.TODO_PARENT)) {
                todoList.add(todo);
            }
        }
        return todoList;
    }

    public List<Todo> getChildTodos(Todo todo) {
        todoList = new ArrayList<>();
        for (Todo todoChild : allTodos) {
            if (todoChild.getParent().equals(todo.getId())) {
                todoList.add(todoChild);
            }
        }
        return todoList;
    }

}
