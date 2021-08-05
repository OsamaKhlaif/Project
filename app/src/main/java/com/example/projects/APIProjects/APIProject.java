
package com.example.projects.APIProjects;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class APIProject {

    @SerializedName("id")
    private String mId;
    @SerializedName("projects")
    private List<Project> mProjects;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public List<Project> getProjects() {
        return mProjects;
    }

    public void setProjects(List<Project> projects) {
        mProjects = projects;
    }

}
