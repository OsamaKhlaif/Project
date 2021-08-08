
package com.example.projects.APITodo;

import java.io.Serializable;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class TodosAll implements Serializable {

    @SerializedName("id")
    private String id;
    @SerializedName("to_do")
    private List<Todo> todo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Todo> getToDo() {
        return todo;
    }

    public void setToDo(List<Todo> toDo) {
        todo = toDo;
    }

}
