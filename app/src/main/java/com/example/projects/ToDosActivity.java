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
import com.example.projects.APITodo.Todos;

import java.util.ArrayList;
import java.util.List;


import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ToDosActivity extends AppCompatActivity {

    private RecyclerView toDosRecyclerView;
    private APIInterface apiInterface;
    private ArrayList<String> toDosNameParent;
    private List<Integer> toDosNameParentIndex;
    private ArrayList<String> toDosNameChild;
    private ArrayList<Integer> toDosNameChildIndex;
    private String id;
    private static final String TAG = ToDosActivity.class.getSimpleName();
    private RecyclerAdapter.RecyclerViewClickListener listener;
    private List<List<Integer>> parent_childIndex;
    private List<List<Integer>> parent_childBackIndex;
    private Todos todo;
    private int positionProjectClick = 0;
    private ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_dos);

        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait To Dos loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        Intent intent = getIntent();
        id = intent.getStringExtra("ID");
        toDosRecyclerView = findViewById(R.id.to_dos_recycler_view);
        toDosRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        toDosNameParentIndex = new ArrayList<>();
        parent_childIndex = new ArrayList<>();
        parent_childBackIndex = new ArrayList<>();

        if (intent.getSerializableExtra("ChildToDo") == null) {

            findToDos();
        } else {
            toDosNameParent = (ArrayList<String>) (intent.getSerializableExtra("ChildToDo"));
            toDosNameParentIndex = (ArrayList<Integer>) (intent.getSerializableExtra("ChildToDoIndex"));
            todo = (Todos) intent.getSerializableExtra("Todo");
            positionProjectClick = intent.getIntExtra("Position Project", 0);
            setAdapter();
        }
    }

    private void findToDos() {
        toDosNameParent = new ArrayList<>();
        apiInterface = APIClient.getClient().create(APIInterface.class);

        Observable<Todos> apiCall = apiInterface.getToDos("to_dos_all")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        apiCall.subscribe(new Observer<Todos>() {

            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "Start the subscribe");
            }

            @Override
            public void onNext(Todos todos) {
                //disable the progress bar.
                progress.dismiss();
                todo = todos;
                Log.d(TAG, "--Success--");
                for (int positionProject = 0; positionProject < todos.getToDosAll().size(); positionProject++) {

                    for (int position = 0; position < todos.getToDosAll().get(positionProject).getToDo().size(); position++) {
                        if (todos.getToDosAll().get(positionProject).getId().equals(id)
                                && todos.getToDosAll().get(positionProject).getToDo().get(position).getParent().equals("0")) {
                            toDosNameParent.add(todos.getToDosAll().get(positionProject).getToDo().get(position).getName());
                            toDosNameParentIndex.add(position);
                            positionProjectClick = positionProject;
                        }
                    }
                }


                setAdapter();
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
                    Toast.makeText(ToDosActivity.this, "The Internet connection failed, Check the Internet", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ToDosActivity.this, "--> Close the application and Open it time other", Toast.LENGTH_LONG).show();
                }
                Log.d(TAG, "--ERROR-->>: " + e);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "Finish the Process");
            }
        });
    }

    public void setAdapter() {
        progress.dismiss();

        setOnClickListener(todo, positionProjectClick, toDosNameParentIndex);
        RecyclerAdapter adapter = new RecyclerAdapter(ToDosActivity.this, toDosNameParent, listener);
        toDosRecyclerView.setAdapter(adapter);
    }

    private void findToDosChild(Todos todos, int positionProject, int positionToDoParentClick, List<Integer> toDosNameParentIndex) {
        toDosNameChild = new ArrayList<>();
        toDosNameChildIndex = new ArrayList<>();
        String idChild = todos.getToDosAll().get(positionProject).getToDo().get(toDosNameParentIndex.get(positionToDoParentClick)).getId();

        for (int position = 0; position < todos.getToDosAll().get(positionProject).getToDo().size(); position++) {

            if (todos.getToDosAll().get(positionProject).getToDo().get(position).getParent().equals(idChild)) {
                toDosNameChild.add(todos.getToDosAll().get(positionProject).getToDo().get(position).getName());
                toDosNameChildIndex.add(position);
            }

        }

        if (!toDosNameChild.isEmpty()) {
            Intent intent = new Intent(ToDosActivity.this, ToDosActivity.class);
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(ToDosActivity.this, toDosRecyclerView, "toDosRecyclerView");

            intent.putExtra("ChildToDo", toDosNameChild);
            intent.putExtra("ChildToDoIndex", toDosNameChildIndex);
            intent.putExtra("Todo", todos);
            intent.putExtra("Position Project", positionProject);
            startActivity(intent, optionsCompat.toBundle());
        } else {

            Toast.makeText(this, "This to-do not have any child to-dos", Toast.LENGTH_SHORT).show();
        }
    }

    private void setOnClickListener(Todos todos, int positionProject, List<Integer> toDosNameParentIndex) {

        listener = (v, position) -> {
            findToDosChild(todos, positionProject, position, toDosNameParentIndex);
        };

    }


}