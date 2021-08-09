package com.example.projects;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerViewHeaven> {

    private Context context;
    private List<String> projectsNameList;
    private RecyclerViewClickListener listener;

    public RecyclerAdapter(Context context, List<String> projectsNameList, RecyclerViewClickListener listener) {
        this.context = context;
        this.projectsNameList = projectsNameList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerViewHeaven onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_view, parent, false);
        return new RecyclerViewHeaven(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHeaven holder, int position) {
        holder.projectNameTextView.setText(projectsNameList.get(position));
    }

    @Override
    public int getItemCount() {
        return projectsNameList.size();
    }

    public interface RecyclerViewClickListener {
        void onClick(View v, int position);
    }
}
