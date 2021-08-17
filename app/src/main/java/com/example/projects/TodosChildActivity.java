package com.example.projects;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class TodosChildActivity extends AppCompatActivity {

    private TextView titleTodosActivityTextView;
    private Bundle bundle;
    private Intent intent;
    private TodosAttributes todosAttributes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todos_child);

        intent = getIntent();
        titleTodosActivityTextView = findViewById(R.id.title_todos_text_view);
        todosAttributes = (TodosAttributes) intent.getSerializableExtra(Constants.TODOS_ACTIVITY);
        bundle = new Bundle();
        bundle.putSerializable(Constants.TODOS_CHILD_ACTIVITY, todosAttributes);
        bundle.putString(Constants.CLASS, Constants.TODOS_CHILD_ACTIVITY);

        SectionPagerAdapter sectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager(),
                this, bundle);
        ViewPager viewPager = findViewById(R.id.activity_todos_child_view_pager);
        viewPager.setAdapter(sectionPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.activity_todos_child_tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        titleTodosActivityTextView.setText(todosAttributes.getProject().getName());

    }
}