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


import com.example.projects.API.APIClient;
import com.example.projects.API.APIInterface;
import com.example.projects.APIProjects.Project;
import com.example.projects.ProjectsRecyclerView.ProjectAdapter;


import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView projectsRecyclerView;
    private APIInterface apiInterface;
    private ProjectAdapter.RecyclerViewClickListener listener;
    private ProgressDialog loadingProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {

            boolean isVisible = savedInstanceState.getBoolean("reply_visible");

            if (isVisible) {
                projectsRecyclerView.setVisibility(View.VISIBLE);
            }
        }

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
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Observable<List<Project>> apiCall = apiInterface.getProjects(Constants.ID_PROJECT_LINK)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        apiCall.subscribe(new Observer<List<Project>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.d(TAG, Constants.ON_SUBSCRIBE);
            }

            @Override
            public void onNext(@NonNull List<Project> projects) {
                Log.d(TAG, Constants.ON_NEXT);
                loadingProgressDialog.dismiss();
                setOnClickListener(projects);
                ProjectAdapter adapter = new ProjectAdapter(MainActivity.this, projects, listener);
                projectsRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onError(@NonNull Throwable e) {
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
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.error) + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                Log.d(TAG, Constants.ON_ERROR + e);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, Constants.ON_COMPLETE);
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

    @Override
    protected void onSaveInstanceState(@androidx.annotation.NonNull @NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (projectsRecyclerView.getVisibility() == View.VISIBLE) {
            outState.putBoolean("reply_visible", true);
        }
    }
}