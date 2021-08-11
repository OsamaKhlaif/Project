package com.example.projects.API;



import com.example.projects.APIProjects.Project;
import com.example.projects.APIProjects.Todo;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;


public interface APIInterface {

    @GET("/{id}")
    Observable<List<Project>> getProjects(@Path("id") String id);

    @GET("/{id}")
    Observable<List<Todo>> getTodos(@Path("id") String id);



}
