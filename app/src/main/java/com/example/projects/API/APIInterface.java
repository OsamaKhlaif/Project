package com.example.projects.API;

import com.example.projects.APIProjects.APIProject;
import com.example.projects.APIProjects.APIProject;
import com.example.projects.APITodo.Todos;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;


public interface APIInterface {

    @GET("/api/{id}")
    Observable<APIProject> getProjects(@Path("id") String id);

    @GET("/api/{id}")
    Observable<Todos> getToDos(@Path("id") String id);
}
