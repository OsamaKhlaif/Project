package com.example.projects.API;

import com.example.projects.APIProjects.APIProject;
import com.example.projects.APIProjects.APIProject;
import com.example.projects.APITodo.Todos;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;


public interface APIInterface {

    @GET("/api/{type}")
    Observable<APIProject> getProjects(@Path("type") String type);

    @GET("/api/{type}")
    Observable<Todos> getToDos(@Path("type") String type);
}
