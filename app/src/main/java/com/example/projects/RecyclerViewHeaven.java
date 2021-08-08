package com.example.projects;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewHeaven extends RecyclerView.ViewHolder implements View.OnClickListener{

    TextView projectNameTextView;
    private RecyclerAdapter.RecyclerViewClickListener listener;

    public RecyclerViewHeaven(@NonNull  View itemView, RecyclerAdapter.RecyclerViewClickListener listener) {
        super(itemView);
        projectNameTextView = itemView.findViewById(R.id.project_name_text_view);
        itemView.setOnClickListener(this);
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        listener.onClick(v, getAdapterPosition());
    }
}
