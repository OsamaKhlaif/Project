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
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class TodoListFragment extends Fragment {

    private static final String TAG = TodosActivity.class.getSimpleName();
    private RecyclerView todosRecyclerView;
    private APIInterface apiInterface;
    private Project project;
    private TodoAdapter.RecyclerViewClickListener listener;
    private ProgressDialog loadingProgressDialog;
    private List<Todo> todoList;
    private TodosData todosData;
    private TodosAttributes todosAttributes;
    private String positionCome;

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
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Observable<List<Todo>> apiCall = apiInterface.getTodos(Constants.ID_TO_DOS_ALL_LINK)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        apiCall.subscribe(new Observer<List<Todo>>() {

            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                Log.d(TAG, Constants.ON_SUBSCRIBE);
            }

            @Override
            public void onNext(@io.reactivex.annotations.NonNull List<Todo> todos) {
                Log.d(TAG, Constants.ON_NEXT);
                //disable the progress bar.
                loadingProgressDialog.dismiss();
                todosData = new TodosData(todos);
                todoList = todosData.getParentTodos(project);
                setAdapter(todoList);
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                loadingProgressDialog.dismiss();
                boolean connected;
                ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                //we are connected to a network
                connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;

                if (!connected) {
                    Toast.makeText(getContext(), getResources().getString(R.string.error) + R.string.internetConnectionMessage, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), getResources().getString(R.string.error) + e.getMessage(), Toast.LENGTH_LONG).show();
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
            TodoAdapter adapter = new TodoAdapter(getContext(), todosParentData, listener);
            todosRecyclerView.setAdapter(adapter);
        } else {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), todosRecyclerView, Constants.TODOS_RECYCLER_VIEW);
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
