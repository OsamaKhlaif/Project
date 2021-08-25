package com.example.projects.APIProjects;


import com.google.firebase.database.DataSnapshot;

import java.io.Serializable;

public class Todo implements Serializable {

    private String id;
    private String name;
    private String parent;
    private String projectId;
    private String startDate;
    private String dueDate;
    private String status;

    public Todo(DataSnapshot dataSnapshot) {
        this.id = String.valueOf(dataSnapshot.child("id").getValue());
        this.name = String.valueOf(dataSnapshot.child("name").getValue());
        this.parent = String.valueOf(dataSnapshot.child("parent").getValue());
        this.projectId = String.valueOf(dataSnapshot.child("project_id").getValue());
        this.startDate = String.valueOf(dataSnapshot.child("start_date").getValue());
        this.dueDate = String.valueOf(dataSnapshot.child("due_date").getValue());
        this.status = String.valueOf(dataSnapshot.child("status").getValue());
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String parentId) {
        projectId = parentId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
