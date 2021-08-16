package com.example.projects.InfoRecyclerView;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projects.R;

public class InfoViewHeaven extends RecyclerView.ViewHolder {

    TextView infoTodoTextView;
    public InfoViewHeaven(@NonNull View itemView) {
        super(itemView);
        infoTodoTextView = itemView.findViewById(R.id.info_todo_text_view);
    }
}
