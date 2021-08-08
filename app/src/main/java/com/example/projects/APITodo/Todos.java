
package com.example.projects.APITodo;

import java.io.Serializable;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Todos implements Serializable{

    @SerializedName("id")
    private String id;
    @SerializedName("to_dos_all")
    private List<TodosAll> todosAll;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<TodosAll> getToDosAll() {
        return todosAll;
    }

    public void setToDosAll(List<TodosAll> toDosAll) {
        todosAll = toDosAll;
    }


}
