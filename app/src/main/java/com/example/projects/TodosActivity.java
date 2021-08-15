package com.example.projects;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projects.API.APIClient;
import com.example.projects.API.APIInterface;
import com.example.projects.APIProjects.Project;
import com.example.projects.APIProjects.Todo;
import com.example.projects.TodosRecyclerView.TodoAdapter;

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
    private RecyclerView todosRecyclerView;
    private TextView titleTodosActivityTextView;
    private APIInterface apiInterface;
    private Project project;
    private TodoAdapter.RecyclerViewClickListener listener;
    private ProgressDialog loadingProgressDialog;
    private List<Todo> todoList;
    private TodosData todosData;
    private Intent intent;
    private TodosAttributes todosAttributes;

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
        todoList = new ArrayList<>();

        intent = getIntent();
        project = (Project) intent.getSerializableExtra(Constants.PROJECT);
        todosRecyclerView = findViewById(R.id.to_dos_recycler_view);
        todosRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        titleTodosActivityTextView = findViewById(R.id.title_todos_text_view);

        if (intent.getSerializableExtra(Constants.TODOS_ACTIVITY) == null) {
            getToDos();
        } else {
            todosRecyclerView.removeAllViews();
            todosAttributes = (TodosAttributes) intent.getSerializableExtra(Constants.TODOS_ACTIVITY);
            todosData = todosAttributes.getTodosData();
            setAdapter(todosAttributes.getTodosParentData());
            project = todosAttributes.getProject();
        }
        titleTodosActivityTextView.setText(project.getName());

    }

    private void getToDos() {
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Observable<List<Todo>> apiCall = apiInterface.getTodos(Constants.ID_TO_DOS_ALL_LINK)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        apiCall.subscribe(new Observer<List<Todo>>() {

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.d(TAG, Constants.ON_SUBSCRIBE);
            }

            @Override
            public void onNext(@NonNull List<Todo> todos) {
                Log.d(TAG, Constants.ON_NEXT);
                //disable the progress bar.
                loadingProgressDialog.dismiss();
                todosData = new TodosData(todos);
                todoList = todosData.getParentTodos(project);
                setAdapter(todoList);
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
                    Toast.makeText(TodosActivity.this, getResources().getString(R.string.error) + R.string.internetConnectionMessage, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TodosActivity.this, getResources().getString(R.string.error) + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                Log.d(TAG, Constants.ON_ERROR + e);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, Constants.ON_COMPLETE);
            }
        });
    }

    public void setAdapter(List<Todo> todosParentData) {
        loadingProgressDialog.dismiss();

        if (!todosParentData.isEmpty()) {
            setOnClickListener(todosParentData);
            TodoAdapter adapter = new TodoAdapter(TodosActivity.this, todosParentData, listener);
            todosRecyclerView.setAdapter(adapter);
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(TodosActivity.this, todosRecyclerView, Constants.TODOS_RECYCLER_VIEW);
            startActivity(intent, optionsCompat.toBundle());
            Toast.makeText(this, R.string.projectNonTodos, Toast.LENGTH_LONG).show();
        }

    }

    private void getToDosChild(List<Todo> todosParentData, int position) {
        todosParentData = todosData.getChildTodos(todosParentData.get(position));

        if (!todosParentData.isEmpty()) {
            Intent intent = new Intent(TodosActivity.this, TodosActivity.class);
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(TodosActivity.this, todosRecyclerView, Constants.TODOS_RECYCLER_VIEW);
            todosAttributes = new TodosAttributes(project, todosParentData, todosData);
            intent.putExtra(Constants.TODOS_ACTIVITY, todosAttributes);
            startActivity(intent, optionsCompat.toBundle());
        } else {
            Toast.makeText(this, getResources().getString(R.string.todoNonChildTodos), Toast.LENGTH_SHORT).show();
        }

    }

    private void setOnClickListener(List<Todo> todosData) {

        listener = (v, position) -> getToDosChild(todosData, position);

    }

}