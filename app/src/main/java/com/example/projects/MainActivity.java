package com.example.projects;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projects.APIProjects.Project;
import com.example.projects.ProjectsRecyclerView.ProjectAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView projectsRecyclerView;
    private ProjectAdapter.RecyclerViewClickListener listener;
    private ProgressDialog loadingProgressDialog;
    private List<Project> projectsList;
    private Project project;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadingProgressDialog = new ProgressDialog(this);
        loadingProgressDialog.setTitle(R.string.Loading);
        loadingProgressDialog.setMessage(getResources().getString(R.string.textProjectLoading));
        // disable dismiss by tapping outside of the dialog
        loadingProgressDialog.setCancelable(false);
        loadingProgressDialog.show();
        projectsRecyclerView = findViewById(R.id.projects_recycler_view);
        projectsRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));

        getProjectName();
    }

    private void getProjectName() {

        db = FirebaseFirestore.getInstance();
        projectsList = new ArrayList<>();

        db.collection(Constants.PROJECT_REFERENCE)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String idProject = String.valueOf(document.get(Constants.ID));
                            String nameProjects = String.valueOf(document.get(Constants.NAME));
                            project = new Project(idProject, nameProjects);
                            projectsList.add(project);
                        }
                        setOnClickListener(projectsList);
                        ProjectAdapter adapter = new ProjectAdapter(MainActivity.this, projectsList, listener);
                        projectsRecyclerView.setAdapter(adapter);
                        loadingProgressDialog.dismiss();

                    } else {
                        Log.w(TAG, Constants.ERROR, task.getException());
                    }
                });
    }

    private void setOnClickListener(List<Project> projects) {

        listener = (v, position) -> {
            Intent intent = new Intent(MainActivity.this, TodosActivity.class);
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(MainActivity.this, projectsRecyclerView,
                            Constants.PROJECTS_RECYCLER_VIEW);
            intent.putExtra(Constants.PROJECT, projects.get(position));
            MainActivity.this.startActivity(intent, optionsCompat.toBundle());
        };
    }

    public void refreshProjects(View view) {
        loadingProgressDialog = new ProgressDialog(this);
        loadingProgressDialog.setTitle(R.string.Loading);
        loadingProgressDialog.setMessage(getResources().getString(R.string.textProjectLoading));
        // disable dismiss by tapping outside of the dialog
        loadingProgressDialog.setCancelable(false);
        loadingProgressDialog.show();
        getProjectName();
    }

}