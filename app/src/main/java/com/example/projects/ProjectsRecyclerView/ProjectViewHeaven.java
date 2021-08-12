package com.example.projects.ProjectsRecyclerView;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projects.ProjectsRecyclerView.ProjectAdapter;
import com.example.projects.R;

public class ProjectViewHeaven extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView projectNameTextView;
    private ProjectAdapter.RecyclerViewClickListener listener;

    public ProjectViewHeaven(@NonNull View itemView, ProjectAdapter.RecyclerViewClickListener listener) {
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
