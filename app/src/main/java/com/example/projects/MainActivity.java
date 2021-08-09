package com.example.projects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.projects.API.APIClient;
import com.example.projects.API.APIInterface;
import com.example.projects.APIProjects.APIProject;
import com.example.projects.APIProjects.Project;

import java.util.ArrayList;
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
    private List<String> projectsNameList;
    private RecyclerAdapter.RecyclerViewClickListener listener;
    private ProgressDialog loadingProgressDialog;

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
        findProjectName();
    }

    private void findProjectName() {

        projectsNameList = new ArrayList<>();
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Observable<APIProject> apiCall = apiInterface.getProjects("projects")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        apiCall.subscribe(new Observer<APIProject>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.d(TAG, "Start the subscribe");
            }

            @Override
            public void onNext(@NonNull APIProject apiProjects) {
                loadingProgressDialog.dismiss();
                Log.d(TAG, "--Success--");
                for (Project project : apiProjects.getProjects()) {
                    projectsNameList.add(project.getName());
                }

                setOnClickListener(apiProjects);
                RecyclerAdapter adapter = new RecyclerAdapter(MainActivity.this, projectsNameList, listener);
                projectsRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                loadingProgressDialog.dismiss();
                boolean connected;
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                //we are connected to a network
                connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;

                if (!connected) {
                    Toast.makeText(MainActivity.this, R.string.internetConnectionMessage, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, " "+e, Toast.LENGTH_LONG).show();
                }
                Log.d(TAG, "--ERROR-->>" + e);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "Finish the Process");
            }
        });

    }

    private void setOnClickListener(APIProject apiProject) {

        listener = (v, position) -> {
            Intent intent = new Intent(MainActivity.this, TodosActivity.class);
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, projectsRecyclerView, "projectsRecyclerView");
            intent.putExtra("ID", apiProject.getProjects().get(position).getId());
            MainActivity.this.startActivity(intent, optionsCompat.toBundle());
        };
    }

    public void refreshProjects(View view) {

        loadingProgressDialog = new ProgressDialog(this);
        loadingProgressDialog.setTitle(R.string.Loading);
        loadingProgressDialog.setMessage(getResources().getString(R.string.textProjectLoading));
        loadingProgressDialog.setCancelable(false); // disable dismiss by tapping outside of the dialog
        loadingProgressDialog.show();
        findProjectName();
    }

}