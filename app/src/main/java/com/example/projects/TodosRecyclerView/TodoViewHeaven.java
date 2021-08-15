package com.example.projects.TodosRecyclerView;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projects.R;

public class TodoViewHeaven extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView todoNameTextView;
    private final TodoAdapter.RecyclerViewClickListener listener;

    public TodoViewHeaven(@NonNull View itemView, TodoAdapter.RecyclerViewClickListener listener) {
        super(itemView);
        todoNameTextView = itemView.findViewById(R.id.todo_name_text_view);
        itemView.setOnClickListener(this);
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        listener.onClick(v, getAdapterPosition());
    }
}
