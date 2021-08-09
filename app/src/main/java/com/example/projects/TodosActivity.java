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
import android.widget.Toast;

import com.example.projects.API.APIClient;
import com.example.projects.API.APIInterface;
import com.example.projects.APITodo.Todo;
import com.example.projects.APITodo.Todos;
import com.example.projects.APITodo.TodosAll;

import java.util.ArrayList;
import java.util.List;


import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class TodosActivity extends AppCompatActivity {

    private static final String TAG = TodosActivity.class.getSimpleName();
    private int positionProjectClicked = 0;
    private RecyclerView todosRecyclerView;
    private APIInterface apiInterface;
    private ArrayList<String> todosParentNameList;
    private List<Integer> todosParentIndexList;
    private ArrayList<String> todosChildNameList;
    private ArrayList<Integer> todosChildIndexList;
    private String idProject;
    private RecyclerAdapter.RecyclerViewClickListener listener;
    private Todos todo;
    private ProgressDialog loadingProgressDialog;
    private TodoChildAttributes todoChildAttributes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_dos);

        loadingProgressDialog = new ProgressDialog(this);
        loadingProgressDialog.setTitle(R.string.Loading);
        loadingProgressDialog.setMessage(getResources().getString(R.string.textTodosLoading));
        // disable dismiss by tapping outside of the dialog
        loadingProgressDialog.setCancelable(false);
        loadingProgressDialog.show();

        Intent intent = getIntent();
        idProject = intent.getStringExtra("ID");
        todosRecyclerView = findViewById(R.id.to_dos_recycler_view);
        todosRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        todosParentIndexList = new ArrayList<>();

        if (intent.getSerializableExtra("Todo Child Attributes") == null) {
            findToDos();

        } else {
            todoChildAttributes = (TodoChildAttributes) intent.getSerializableExtra("Todo Child Attributes");
            todosParentNameList = todoChildAttributes.getTodosChildNameList();
            todosParentIndexList = todoChildAttributes.getTodosChildIndexList();
            todo = todoChildAttributes.getTodos();
            positionProjectClicked = todoChildAttributes.getPositionProjects();
            setAdapter();

        }
    }

    private void findToDos() {

        todosParentNameList = new ArrayList<>();
        apiInterface = APIClient.getClient().create(APIInterface.class);

        Observable<Todos> apiCall = apiInterface.getToDos("to_dos_all")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        apiCall.subscribe(new Observer<Todos>() {

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.d(TAG, "Start the subscribe");
            }

            @Override
            public void onNext(@NonNull Todos todos) {
                Log.d(TAG, "--Success--");
                //disable the progress bar.
                loadingProgressDialog.dismiss();
                todo = todos;
                int positionProject = 0;
                for (TodosAll todosAll : todos.getToDosAll()) {
                    int position = 0;
                    for (Todo todo : todosAll.getToDo()) {
                        if (todosAll.getId().equals(idProject)
                                && todo.getParent().equals("0")) {
                            todosParentNameList.add(todo.getName());
                            todosParentIndexList.add(position);
                            positionProjectClicked = positionProject;
                        }
                        position++;
                    }
                    positionProject++;
                }
                setAdapter();
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
                    Toast.makeText(TodosActivity.this, R.string.internetConnectionMessage, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TodosActivity.this, " " + e, Toast.LENGTH_LONG).show();
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
        loadingProgressDialog.dismiss();

        if (!todosParentNameList.isEmpty()) {
            setOnClickListener(todo, positionProjectClicked, todosParentIndexList);
            RecyclerAdapter adapter = new RecyclerAdapter(TodosActivity.this, todosParentNameList, listener);
            todosRecyclerView.setAdapter(adapter);
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(TodosActivity.this, todosRecyclerView, "toDosRecyclerView");
            startActivity(intent, optionsCompat.toBundle());
            Toast.makeText(this, R.string.nonTodos, Toast.LENGTH_LONG).show();
        }

    }

    private void findToDosChild(Todos todos, int positionProject, int positionToDoParentClick, List<Integer> toDosNameParentIndex) {
        todosChildNameList = new ArrayList<>();
        todosChildIndexList = new ArrayList<>();
        String idChild = todos.getToDosAll().get(positionProject).getToDo().get(toDosNameParentIndex.get(positionToDoParentClick)).getId();
        int position = 0;
        for (Todo todo : todos.getToDosAll().get(positionProject).getToDo()) {

            if (todo.getParent().equals(idChild)) {
                todosChildNameList.add(todo.getName());
                todosChildIndexList.add(position);
            }
            position++;
        }

        if (!todosChildNameList.isEmpty()) {
            Intent intent = new Intent(TodosActivity.this, TodosActivity.class);
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(TodosActivity.this, todosRecyclerView, "toDosRecyclerView");
            todoChildAttributes = new TodoChildAttributes(todosChildNameList, todosChildIndexList, todo, positionProject);
            intent.putExtra("Todo Child Attributes", todoChildAttributes);
            startActivity(intent, optionsCompat.toBundle());
        } else {

            Toast.makeText(this, "This to-do not have any child to-dos", Toast.LENGTH_SHORT).show();
        }
    }

    private void setOnClickListener(Todos todos, int positionProject, List<Integer> toDosNameParentIndex) {

        listener = (v, position) -> findToDosChild(todos, positionProject, position, toDosNameParentIndex);

    }

}