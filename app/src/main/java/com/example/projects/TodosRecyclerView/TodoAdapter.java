package com.example.projects.TodosRecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projects.APIProjects.Todo;
import com.example.projects.R;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoViewHeaven> {

    private final Context context;
    private final List<Todo> todosList;
    private final RecyclerViewClickListener listener;

    public TodoAdapter(Context context, List<Todo> todosList, RecyclerViewClickListener listener) {
        this.context = context;
        this.todosList = todosList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TodoViewHeaven onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.todo_item_view, parent, false);
        return new TodoViewHeaven(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHeaven holder, int position) {
        holder.todoNameTextView.setText(todosList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return todosList.size();
    }

    public interface RecyclerViewClickListener {
        void onClick(View v, int position);
    }
}
