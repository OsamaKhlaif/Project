package com.example.projects.APIProjects;


import com.example.projects.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;

public class Todo implements Serializable {

    private String id;
    private String name;
    private String parent;
    private String projectId;
    private String startDate;
    private String dueDate;
    private String status;

    public Todo(QueryDocumentSnapshot document) {
        this.id = String.valueOf(document.get(Constants.ID));
        this.name = String.valueOf(document.get(Constants.NAME));
        this.parent = String.valueOf(document.get(Constants.PARENT_TODO));
        this.projectId = String.valueOf(document.get(Constants.PROJECT_ID));
        this.startDate = String.valueOf(document.get(Constants.START_DATE));
        this.dueDate = String.valueOf(document.get(Constants.DUE_DATE));
        this.status = String.valueOf(document.get(Constants.STATUS));
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
