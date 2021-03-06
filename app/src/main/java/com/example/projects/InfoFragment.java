package com.example.projects;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projects.InfoRecyclerView.InfoAdapter;

public class InfoFragment extends Fragment {

    Boolean bool = false;
    private TodosAttributes todosAttributes;
    private RecyclerView infoRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.info_fragment, container, false);
        infoRecyclerView = view.findViewById(R.id.info_recycler_view);
        infoRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.VERTICAL, false));
        bool = getArguments().getBoolean(Constants.BOOLEAN);

        if (!bool) {
            todosAttributes = (TodosAttributes) getArguments().getSerializable(Constants.TODOS_CHILD_ACTIVITY);
        } else {
            todosAttributes = (TodosAttributes) getArguments().getSerializable(Constants.TODOS_ATTRIBUTES);
        }
        InfoAdapter infoAdapter = new InfoAdapter(getContext(), todosAttributes.getTodosParentData());
        infoRecyclerView.setAdapter(infoAdapter);

        return view;
    }

}
