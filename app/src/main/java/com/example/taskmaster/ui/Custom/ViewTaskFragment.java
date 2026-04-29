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
    private TextView filterAll;
    private TextView filterCompleted;
    private TextView filterPending;
    private TextView filterOverdue;
    private String currentFilter = "ALL";


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.viewtaskfragment, container, false);
        initView(view);
        setupRecyclerView();
//        loadTasks();
        // Click listeners for filters
        return view;
    }

    private void initView(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        recyclerView = view.findViewById(R.id.tasksRecyclerView);
        emptyStateText = view.findViewById(R.id.emptyStateText);

        filterAll = view.findViewById(R.id.filterAll);
        filterCompleted = view.findViewById(R.id.filterCompleted);
        filterPending = view.findViewById(R.id.filterPending);
        filterOverdue = view.findViewById(R.id.filterOverdue);
        databaseHelper = new database(requireContext());

        currentFilter = "ALL";
        updateSelectedFilter(filterAll);
        loadTasks(databaseHelper.getData());

        filterAll.setOnClickListener(v -> {
            currentFilter = "ALL";
            updateSelectedFilter(filterAll);
            loadTasks(databaseHelper.getData());
        });

        filterCompleted.setOnClickListener(v -> {
            currentFilter = "COMPLETED";
            updateSelectedFilter(filterCompleted);
            loadTasks(databaseHelper.getAllCompletedTasks());
        });

        filterPending.setOnClickListener(v -> {
            currentFilter = "PENDING";
            updateSelectedFilter(filterPending);
            loadTasks(databaseHelper.getPendingTasks());
        });

        filterOverdue.setOnClickListener(v -> {
            currentFilter = "OVERDUE";
            updateSelectedFilter(filterOverdue);
            loadTasks(databaseHelper.getOverdueTasks());
        });
    }

    private void setupRecyclerView() {

        recyclerView.setLayoutManager(
                new LinearLayoutManager(
                        getContext(),
                        LinearLayoutManager.VERTICAL,
                        false
                )
        );

        if (getArguments() != null) {

            String filterType =
                    getArguments().getString("filter_type");

            if ("COMPLETED".equals(filterType)) {

                currentFilter = "COMPLETED";
                updateSelectedFilter(filterCompleted);
                loadTasks(databaseHelper.getAllCompletedTasks());

            } else if ("PENDING".equals(filterType)) {

                currentFilter = "PENDING";
                updateSelectedFilter(filterPending);
                loadTasks(databaseHelper.getPendingTasks());

            } else if ("OVERDUE".equals(filterType)) {

                currentFilter = "OVERDUE";
                updateSelectedFilter(filterOverdue);
                loadTasks(databaseHelper.getOverdueTasks());

            } else {

                currentFilter = "ALL";
                updateSelectedFilter(filterAll);
                loadTasks(databaseHelper.getData());
            }

        } else {

            currentFilter = "ALL";
            updateSelectedFilter(filterAll);
            loadTasks(databaseHelper.getData());
        }

        swipeRefreshLayout.setOnRefreshListener(() -> {

            switch (currentFilter) {

                case "COMPLETED":
                    loadTasks(databaseHelper.getAllCompletedTasks());
                    break;

                case "PENDING":
                    loadTasks(databaseHelper.getPendingTasks());
                    break;

                case "OVERDUE":
                    loadTasks(databaseHelper.getOverdueTasks());
                    break;

                default:
                    loadTasks(databaseHelper.getData());
                    break;
            }

            swipeRefreshLayout.setRefreshing(false);
        });
    }

//    private void loadTasks() {
//        List<TasksData> data = databaseHelper.getData();
//        todoAdapter = new TODOAdapter(requireContext(), data, this::loadTasks);
//        recyclerView.setAdapter(todoAdapter);
//        emptyStateText.setVisibility(data.isEmpty() ? View.VISIBLE : View.GONE);
//        swipeRefreshLayout.setVisibility(data.isEmpty() ? View.GONE : View.VISIBLE);
//    }
private void updateSelectedFilter(TextView selectedView) {

    // Reset all filters to default style
    filterAll.setBackgroundResource(R.drawable.bg_surface_card);
    filterCompleted.setBackgroundResource(R.drawable.bg_surface_card);
    filterPending.setBackgroundResource(R.drawable.bg_surface_card);
    filterOverdue.setBackgroundResource(R.drawable.bg_surface_card);

    filterAll.setTextColor(requireContext().getColor(R.color.text_primary));
    filterCompleted.setTextColor(requireContext().getColor(R.color.text_primary));
    filterPending.setTextColor(requireContext().getColor(R.color.text_primary));
    filterOverdue.setTextColor(requireContext().getColor(R.color.text_primary));

    // Highlight selected filter
    selectedView.setBackgroundResource(R.drawable.bg_chip_blue);
    selectedView.setTextColor(
            requireContext().getColor(R.color.colorPrimarydark)
    );
}
    private void reloadCurrentFilter() {

        switch (currentFilter) {

            case "COMPLETED":
                loadTasks(databaseHelper.getAllCompletedTasks());
                updateSelectedFilter(filterCompleted);
                break;

            case "PENDING":
                loadTasks(databaseHelper.getPendingTasks());
                updateSelectedFilter(filterPending);
                break;

            case "OVERDUE":
                loadTasks(databaseHelper.getOverdueTasks());
                updateSelectedFilter(filterOverdue);
                break;

            default:
                loadTasks(databaseHelper.getData());
                updateSelectedFilter(filterAll);
                break;
        }
    }
    private void loadTasks(List<TasksData> data) {

        todoAdapter = new TODOAdapter(
                requireContext(),
                data,
                this::reloadCurrentFilter
        );

        recyclerView.setAdapter(todoAdapter);

        emptyStateText.setVisibility(
                data.isEmpty() ? View.VISIBLE : View.GONE
        );

        swipeRefreshLayout.setVisibility(
                data.isEmpty() ? View.GONE : View.VISIBLE
        );
    }

    @Override
    public void onResume() {
        super.onResume();

        switch (currentFilter) {

            case "COMPLETED":
                loadTasks(databaseHelper.getAllCompletedTasks());
                break;

            case "PENDING":
                loadTasks(databaseHelper.getPendingTasks());
                break;

            case "OVERDUE":
                loadTasks(databaseHelper.getOverdueTasks());
                break;

            default:
                loadTasks(databaseHelper.getData());
                break;
        }
    }
}
