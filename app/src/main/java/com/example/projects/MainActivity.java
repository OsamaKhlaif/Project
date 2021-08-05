package com.example.projects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.projects.API.APIClient;
import com.example.projects.API.APIInterface;
import com.example.projects.APIProjects.APIProject;
import com.example.projects.APIProjects.APIProject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private RecyclerView projectsRecyclerView;
    private APIInterface apiInterface;
    private List<String> projectsName;
    private RecyclerAdapter.RecyclerViewClickListener listener;
    private static final String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait Projects loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        projectsRecyclerView = findViewById(R.id.projects_recycler_view);
        projectsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        projectsName = new ArrayList<>();
        findProjectName();
    }

    private void findProjectName() {

        apiInterface = APIClient.getClient().create(APIInterface.class);
        Observable<APIProject> apiCall = apiInterface.getProjects("projects")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        apiCall.subscribe(new Observer<APIProject>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "Start the subscribe");
            }

            @Override
            public void onNext(APIProject apiProjects) {
                progress.dismiss();
                Log.d(TAG, "--Success--");
                for (int position = 0; position < apiProjects.getProjects().size(); position++){
                    projectsName.add(apiProjects.getProjects().get(position).getName());
                }
                setOnClickListener(apiProjects);
                RecyclerAdapter adapter = new RecyclerAdapter(MainActivity.this,projectsName, listener);
                projectsRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onError(Throwable e) {
                progress.dismiss();
                boolean connected;
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network
                    connected = true;
                } else {
                    connected = false;
                }

                if (connected == false) {
                    Toast.makeText(MainActivity.this, "The Internet connection failed, Check the Internet", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "--> Close the application and Open it time other", Toast.LENGTH_LONG).show();
                }
                Log.d(TAG, "--ERROR-->>"+e);
            }

            @Override
            public void onComplete() {
                Log.d("Main Activity", "Finish the Process");
            }
        });

    }

    private void setOnClickListener(APIProject apiProject) {
        listener = (v, position) -> {
            Intent intent = new Intent(MainActivity.this, ToDosActivity.class);
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, projectsRecyclerView, "projectsRecyclerView");
            intent.putExtra("ID",apiProject.getProjects().get(position).getId());
            MainActivity.this.startActivity(intent, optionsCompat.toBundle());
        };
    }
}