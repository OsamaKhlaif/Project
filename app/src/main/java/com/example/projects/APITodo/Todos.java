
package com.example.projects.APITodo;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Todos implements Serializable{

    @SerializedName("id")
    private String mId;
    @SerializedName("to_dos_all")
    private List<ToDosAll> mToDosAll;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public List<ToDosAll> getToDosAll() {
        return mToDosAll;
    }

    public void setToDosAll(List<ToDosAll> toDosAll) {
        mToDosAll = toDosAll;
    }


}
