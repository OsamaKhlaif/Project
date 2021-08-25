package com.example.projects;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.projects.APIProjects.Project;
import com.example.projects.APIProjects.Todo;
import com.example.projects.TodosRecyclerView.TodoAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class TodoListFragment extends Fragment {

    private static final String TAG = TodosActivity.class.getSimpleName();
    private RecyclerView todosRecyclerView;
    private Project project;
    private TodoAdapter.RecyclerViewClickListener listener;
    private ProgressDialog loadingProgressDialog;
    private List<Todo> todoList;
    private TodosData todosData;
    private TodosAttributes todosAttributes;
    private String positionCome;
    private Todo todo;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private int projectPosition;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.todo_list_fragment, container, false);

        loadingProgressDialog = new ProgressDialog(getContext());
        loadingProgressDialog.setTitle(R.string.Loading);
        loadingProgressDialog.setMessage(getResources().getString(R.string.textTodosLoading));
        // disable dismiss by tapping outside of the dialog
        loadingProgressDialog.setCancelable(false);
        loadingProgressDialog.show();

        todoList = new ArrayList<>();
        positionCome = getArguments().getString(Constants.CLASS);
        todosRecyclerView = view.findViewById(R.id.todos_recycler_view);
        todosRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.VERTICAL, false));

        if (positionCome.equals(Constants.TODOS_ACTIVITY)) {
            project = (Project) getArguments().getSerializable(Constants.PROJECT);
            getToDos();
        } else {
            positionCome = Constants.TODOS_CHILD_ACTIVITY;
            todosRecyclerView.removeAllViews();
            todosAttributes = (TodosAttributes) getArguments().getSerializable(Constants.TODOS_CHILD_ACTIVITY);
            todosData = todosAttributes.getTodosData();
            setAdapter(todosAttributes.getTodosParentData());
            project = todosAttributes.getProject();
        }

        return view;
    }

    private void getToDos() {

        projectPosition = 0;
        todoList = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("todo");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                while(dataSnapshot.hasChild(String.valueOf(projectPosition))) {
                    todo = new Todo(dataSnapshot.child(String.valueOf(projectPosition)));
                    todoList.add(todo);
                    todosData = new TodosData(todoList);
                    projectPosition++;
                }
                todoList = todosData.getParentTodos(project);
                setOnClickListener(todoList);
                setAdapter(todoList);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                loadingProgressDialog.dismiss();
                boolean connected;
                ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                //we are connected to a network
                connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                        .getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                                .getState() == NetworkInfo.State.CONNECTED;
                if (!connected) {
                    Toast.makeText(getContext(), getResources().getString(R.string.error)
                            + R.string.internetConnectionMessage, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), getResources().getString(R.string.error) + error.getMessage(), Toast.LENGTH_LONG).show();
                }
                Log.d(TAG, Constants.ON_ERROR + error.getMessage());
            }
        });
    }

    public void setAdapter(List<Todo> todosParentData) {
        loadingProgressDialog.dismiss();

        if (!todosParentData.isEmpty()) {
            setOnClickListener(todosParentData);
            TodoAdapter adapter = new TodoAdapter(getContext(), todosParentData, listener);
            todosRecyclerView.setAdapter(adapter);
        } else {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(getActivity(), todosRecyclerView, Constants.TODOS_RECYCLER_VIEW);
            startActivity(intent, optionsCompat.toBundle());
            Toast.makeText(getContext(), R.string.projectNonTodos, Toast.LENGTH_LONG).show();
        }

    }

    private void getToDosChild(List<Todo> todosParentData, int position) {
        todosParentData = todosData.getChildTodos(todosParentData.get(position));

        if (!todosParentData.isEmpty()) {
            todosAttributes = new TodosAttributes(project, todosParentData, todosData);

            Intent intent = new Intent(getActivity(), TodosChildActivity.class);
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(getActivity(), todosRecyclerView,
                            Constants.TODOS_RECYCLER_VIEW);
            intent.putExtra(Constants.TODOS_ACTIVITY, todosAttributes);
            startActivity(intent, optionsCompat.toBundle());

        } else {
            Toast.makeText(getContext(), getResources().getString(R.string.todoNonChildTodos), Toast.LENGTH_SHORT).show();
        }

    }

    private void setOnClickListener(List<Todo> todosData) {

        listener = (v, position) -> getToDosChild(todosData, position);

    }

}
