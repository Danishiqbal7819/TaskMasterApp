package com.example.taskmaster.ui.Custom;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.taskmaster.Adapter.TODOAdapter;
import com.example.taskmaster.Model.TasksData;
import com.example.taskmaster.R;
import com.example.taskmaster.Utils.database;

import java.util.List;

public class ViewTaskFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private TextView emptyStateText;
    private database databaseHelper;
    private TODOAdapter todoAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.viewtaskfragment, container, false);
        initView(view);
        setupRecyclerView();
        loadTasks();
        return view;
    }

    private void initView(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        recyclerView = view.findViewById(R.id.tasksRecyclerView);
        emptyStateText = view.findViewById(R.id.emptyStateText);
        databaseHelper = new database(requireContext());
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadTasks();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void loadTasks() {
        List<TasksData> data = databaseHelper.getData();
        todoAdapter = new TODOAdapter(requireContext(), data, this::loadTasks);
        recyclerView.setAdapter(todoAdapter);
        emptyStateText.setVisibility(data.isEmpty() ? View.VISIBLE : View.GONE);
        swipeRefreshLayout.setVisibility(data.isEmpty() ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTasks();
    }
}
