package com.example.projects;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projects.APIProjects.Project;
import com.example.projects.APIProjects.Todo;
import com.example.projects.TodosRecyclerView.TodoAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    private FloatingActionButton addTodoFabButton;
    private AddTaskDialog addTaskDialog;
    private String id;

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
            showDialog(view.getContext());
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
        setOnClickListener(todosParentData);
        TodoAdapter adapter = new TodoAdapter(getContext(), todosParentData, listener);
        todosRecyclerView.setAdapter(adapter);

    }

    private void getToDosChild(List<Todo> todosParentData, int position) {
        id = todosParentData.get(position).getId();
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
                        todoList = todosData.getChildTodos(todosParentData.get(position));

                        todosAttributes = new TodosAttributes(project, todoList, todosData, id);

                        Intent intent = new Intent(getActivity(), TodosChildActivity.class);
                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                                .makeSceneTransitionAnimation(getActivity(), todosRecyclerView,
                                        Constants.TODOS_RECYCLER_VIEW);
                        intent.putExtra(Constants.TODOS_ACTIVITY, todosAttributes);
                        startActivity(intent, optionsCompat.toBundle());


                    } else {
                        Log.w(TAG, Constants.ERROR, task.getException());
                    }
                });
    }

    private void setOnClickListener(List<Todo> todosData) {
        listener = (v, position) -> getToDosChild(todosData, position);

    }

    private void showDialog(Context context) {
        addTaskDialog = new AddTaskDialog(getActivity());
        addTaskDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        addTaskDialog.setCancelable(false);
        addTaskDialog.show();
    }

    public void addNewTodo() {
        //Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put(Constants.ID, addTaskDialog.idTodoEditText.getText().toString());
        user.put(Constants.NAME, addTaskDialog.nameTodoEditText.getText().toString());
        user.put(Constants.PARENT_TODO, idParentTodo);
        user.put(Constants.PROJECT_ID, project.getId());
        user.put(Constants.START_DATE, addTaskDialog.startDateEditText.getText().toString());
        user.put(Constants.DUE_DATE, addTaskDialog.dueDateEditText.getText().toString());
        user.put(Constants.STATUS, addTaskDialog.statusEditText.getText().toString());

        //Add a new document with a generated ID
        db.collection("todo")
                .add(user)
                .addOnSuccessListener(documentReference -> Toast.makeText(getContext(), "Add Successful", Toast.LENGTH_SHORT).show())
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
                            todoList = todosData.refresh(idParentTodo);
                            todosAttributes = new TodosAttributes(project, todoList, todosData, id);
                            setAdapter(todosData.refresh(idParentTodo));

                            Bundle bundle = new Bundle();
                            bundle.putBoolean(Constants.BOOLEAN, true);
                            bundle.putSerializable(Constants.TODOS_ATTRIBUTES, todosAttributes);
                            Fragment fragment = new InfoFragment();
                            fragment.setArguments(bundle);
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.info_fragment, fragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();

                        } else {
                            Log.w(TAG, Constants.ERROR, task.getException());
                        }
                    });
        }

    }

    //AddTaskDialog class
    public class AddTaskDialog extends Dialog implements View.OnClickListener {

        public EditText idTodoEditText, nameTodoEditText, startDateEditText, dueDateEditText, statusEditText;
        public Button add, cancel;
        private Activity activity;
        private TextView idTodoTextView, nameTodoTextView, startDateTextView, dueDateTextView, statusTextView;


        public AddTaskDialog(@NonNull Activity activity) {
            super(activity);
            this.activity = activity;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.activity_add_task_dialog);

            idTodoTextView = findViewById(R.id.todo_id_text_view);
            idTodoEditText = findViewById(R.id.todo_id_edit_text);
            nameTodoTextView = findViewById(R.id.todo_name_text_view);
            nameTodoEditText = findViewById(R.id.todo_name_edit_text);
            startDateTextView = findViewById(R.id.start_date_text_view);
            startDateEditText = findViewById(R.id.start_date_edit_text);
            dueDateTextView = findViewById(R.id.due_date_text_view);
            dueDateEditText = findViewById(R.id.due_date_edit_text);
            statusTextView = findViewById(R.id.status_text_view);
            statusEditText = findViewById(R.id.status_edit_text);
            add = findViewById(R.id.add_button);
            cancel = findViewById(R.id.cancel_button);
            add.setOnClickListener(this);
            cancel.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.add_button:
                    addNewTodo();
                    dismiss();
                    break;

                case R.id.cancel_button:
                    dismiss();
                    break;

                default:
                    break;
            }
            dismiss();

        }

    }
}
