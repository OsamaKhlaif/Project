package com.example.projects;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projects.APIProjects.Project;
import com.example.projects.APIProjects.Todo;
import com.example.projects.TodosRecyclerView.TodoAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TodoListFragment extends Fragment {

    private static final String TAG = TodosActivity.class.getSimpleName();
    private FirebaseFirestore db;
    private RecyclerView todosRecyclerView;
    private Project project;
    private TodoAdapter.RecyclerViewClickListener listener;
    private ProgressDialog loadingProgressDialog;
    private List<Todo> todoList;
    private TodosData todosData;
    private TodosAttributes todosAttributes;
    private String positionCome;
    private Todo todo;
    private String idParentTodo;
    private EditText idTodoEditText, nameTodoEditText, startDateEditText, dueDateEditText, statusEditText;
    private TextView idTodoTextView, nameTodoTextView, startDateTextView, dueDateTextView, statusTextView;
    private FloatingActionButton addTodoFabButton;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.todo_list_fragment, container, false);

        context = getContext();
        idTodoEditText = new EditText(context);
        idTodoTextView = new TextView(context);
        idTodoTextView.setText("\n\n"+context.getResources().getString(R.string.idTodo));
        nameTodoEditText = new EditText(context);
        nameTodoTextView = new TextView(context);
        nameTodoTextView.setText(context.getResources().getString(R.string.todosName));
        startDateEditText = new EditText(context);
        startDateTextView = new TextView(context);
        startDateTextView.setText(context.getResources().getString(R.string.startDateTodo));
        dueDateEditText = new EditText(context);
        dueDateTextView = new TextView(context);
        dueDateTextView.setText(context.getResources().getString(R.string.dueDateTodo));
        statusEditText = new EditText(context);
        statusTextView = new TextView(context);
        statusTextView.setText(context.getResources().getString(R.string.statusTodo));

        loadingProgressDialog = new ProgressDialog(getContext());
        loadingProgressDialog.setTitle(R.string.Loading);
        loadingProgressDialog.setMessage(getResources().getString(R.string.textTodosLoading));
        // disable dismiss by tapping outside of the dialog
        loadingProgressDialog.setCancelable(false);
        loadingProgressDialog.show();

        todoList = new ArrayList<>();
        positionCome = getArguments().getString(Constants.CLASS);
        todosRecyclerView = view.findViewById(R.id.todos_recycler_view);
        addTodoFabButton = view.findViewById(R.id.add_todo_fab_button);
        todosRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.VERTICAL, false));
        db = FirebaseFirestore.getInstance();

        if (positionCome.equals(Constants.TODOS_ACTIVITY)) {
            project = (Project) getArguments().getSerializable(Constants.PROJECT);
            idParentTodo = "0";
            getToDos();
        } else {
            positionCome = Constants.TODOS_CHILD_ACTIVITY;
            todosRecyclerView.removeAllViews();
            todosAttributes = (TodosAttributes) getArguments().getSerializable(Constants.TODOS_CHILD_ACTIVITY);
            todosData = todosAttributes.getTodosData();
            todoList = todosAttributes.getTodosParentData();
            idParentTodo = todosAttributes.getId();
            setAdapter(todosAttributes.getTodosParentData());
            project = todosAttributes.getProject();
        }

        addTodoFabButton.setOnClickListener(v -> {
            showDialog();
        });

        return view;
    }

    private void getToDos() {
        todoList = new ArrayList<>();

        db.collection(Constants.TODO_REFERENCE)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            todo = new Todo(document);
                            todoList.add(todo);
                            todosData = new TodosData(todoList);
                        }
                        todoList = todosData.getParentTodos(project);
                        setOnClickListener(todoList);
                        setAdapter(todoList);

                    } else {
                        Log.w(TAG, Constants.ERROR, task.getException());
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
        String id = todosParentData.get(position).getId();

        todosParentData = todosData.getChildTodos(todosParentData.get(position));
        if (!todosParentData.isEmpty()) {
            todosAttributes = new TodosAttributes(project, todosParentData, todosData, id);

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

    private void showDialog() {
        LinearLayout dialogLinearLayout = new LinearLayout(context);
        dialogLinearLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLinearLayout.setPadding(20,0,0,0);
        dialogLinearLayout.addView(idTodoTextView);
        dialogLinearLayout.addView(idTodoEditText);
        dialogLinearLayout.addView(nameTodoTextView);
        dialogLinearLayout.addView(nameTodoEditText);
        dialogLinearLayout.addView(startDateTextView);
        dialogLinearLayout.addView(startDateEditText);
        dialogLinearLayout.addView(dueDateTextView);
        dialogLinearLayout.addView(dueDateEditText);
        dialogLinearLayout.addView(statusTextView);
        dialogLinearLayout.addView(statusEditText);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Add a new task")
                .setView(dialogLinearLayout)
                .setIcon(R.drawable.ic_baseline_add_circle_24)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addNewTodo();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    public void addNewTodo(){
        //Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put(Constants.ID, idTodoEditText.getText().toString());
        user.put(Constants.NAME, nameTodoEditText.getText().toString());
        user.put(Constants.PARENT_TODO, idParentTodo);
        user.put(Constants.PROJECT_ID, project.getId());
        user.put(Constants.START_DATE,startDateEditText.getText().toString());
        user.put(Constants.DUE_DATE,dueDateEditText.getText().toString());
        user.put(Constants.STATUS,dueDateEditText.getText().toString());

        //Add a new document with a generated ID
        db.collection("todo")
                .add(user)
                .addOnSuccessListener(documentReference -> Toast.makeText(context, "Add Successful", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Log.w(TAG, e.getMessage()));

        if (positionCome.equals(Constants.TODOS_ACTIVITY)) {
            getToDos();
        } else {
            todoList = new ArrayList<>();
            todosRecyclerView.removeAllViews();
            db.collection(Constants.TODO_REFERENCE)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                todo = new Todo(document);
                                todoList.add(todo);
                                todosData = new TodosData(todoList);
                            }
                            setAdapter(todosData.refresh(idParentTodo));
                        } else {
                            Log.w(TAG, Constants.ERROR, task.getException());
                        }
                    });
        }
    }
}
