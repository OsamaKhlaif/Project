package com.example.projects.InfoRecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projects.APIProjects.Todo;
import com.example.projects.R;

import java.util.List;

public class InfoAdapter extends RecyclerView.Adapter<InfoViewHeaven> {

    private final Context context;
    private final List<Todo> todosList;

    public InfoAdapter(Context context, List<Todo> todosList) {
        this.context = context;
        this.todosList = todosList;
    }

    @NonNull
    @Override
    public InfoViewHeaven onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.info_item_view, parent, false);
        return new InfoViewHeaven(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InfoViewHeaven holder, int position) {
        holder.infoTodoTextView.setText(context.getString(R.string.todosNameInfo)
                + todosList.get(position).getName() + context.getString(R.string.startDateTodo)
                + todosList.get(position).getStartDate() + context.getString(R.string.dueDate)
                + todosList.get(position).getDueDate() + context.getString(R.string.statusTodo)
                + todosList.get(position).getStatus());
    }

    @Override
    public int getItemCount() {
        return todosList.size();
    }

}
