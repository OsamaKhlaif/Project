package com.example.projects.ProjectsRecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projects.APIProjects.Project;
import com.example.projects.R;

import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectViewHeaven> {

    private final Context context;
    private final List<Project> projects;
    private final RecyclerViewClickListener listener;

    public ProjectAdapter(Context context, List<Project> projects, RecyclerViewClickListener listener) {
        this.context = context;
        this.projects = projects;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProjectViewHeaven onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.project_item_view, parent, false);
        return new ProjectViewHeaven(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHeaven holder, int position) {
        holder.projectNameTextView.setText(projects.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    public interface RecyclerViewClickListener {
        void onClick(View v, int position);
    }
}
