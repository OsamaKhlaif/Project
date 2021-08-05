
package com.example.projects.APITodo;

import java.io.Serializable;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class ToDosAll implements Serializable {

    @SerializedName("id")
    private String mId;
    @SerializedName("to_do")
    private List<ToDo> mToDo;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public List<ToDo> getToDo() {
        return mToDo;
    }

    public void setToDo(List<ToDo> toDo) {
        mToDo = toDo;
    }

}
