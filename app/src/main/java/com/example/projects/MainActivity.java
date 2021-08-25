package com.example.projects;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.projects.APIProjects.Project;
import com.example.projects.ProjectsRecyclerView.ProjectAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView projectsRecyclerView;
    private ProjectAdapter.RecyclerViewClickListener listener;
    private ProgressDialog loadingProgressDialog;
    private List<Project> projectsList;
    private Project project;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private int projectPosition;

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

        projectPosition = 0;
        projectsList = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("projects");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                while(dataSnapshot.hasChild(String.valueOf(projectPosition))) {
                    String idProject = String.valueOf(dataSnapshot.child(String.valueOf(projectPosition)).child("id").getValue());
                    String nameProjects = String.valueOf(dataSnapshot.child(String.valueOf(projectPosition)).child("name").getValue());
                   project = new Project(idProject, nameProjects);
                   projectsList.add(project);
                    projectPosition++;
                }
                setOnClickListener(projectsList);
                ProjectAdapter adapter = new ProjectAdapter(MainActivity.this, projectsList, listener);
                projectsRecyclerView.setAdapter(adapter);
                loadingProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                loadingProgressDialog.dismiss();
                boolean connected;
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                //we are connected to a network
                connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                        .getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                                .getState() == NetworkInfo.State.CONNECTED;

                if (!connected) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.error)
                            + R.string.internetConnectionMessage, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.error) + error.getMessage(), Toast.LENGTH_LONG).show();
                }
                Log.d(TAG, Constants.ON_ERROR + error.getMessage());
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