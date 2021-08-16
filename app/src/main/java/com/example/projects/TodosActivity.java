package com.example.projects;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projects.APIProjects.Project;

public class TodosActivity extends AppCompatActivity {

    private Project project;
    private TextView titleTodosActivityTextView;
    private Bundle bundle;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_dos);

        intent = getIntent();
        titleTodosActivityTextView = findViewById(R.id.title_todos_text_view);
        project = (Project) intent.getSerializableExtra(Constants.PROJECT);
        titleTodosActivityTextView.setText(project.getName());
        bundle = new Bundle();
        bundle.putSerializable(Constants.PROJECT, project);
        bundle.putString(Constants.CLASS, Constants.TODOS_ACTIVITY);

        TodoListFragment todoListFragment = new TodoListFragment();
        todoListFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.frame_layout, todoListFragment).commit();

    }


}