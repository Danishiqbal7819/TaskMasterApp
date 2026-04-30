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
import com.example.taskmaster.Utils.MyDbHelper;

import java.util.List;

public class ViewTaskFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private TextView emptyStateText;
    private MyDbHelper myDbHelperHelper;
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
        myDbHelperHelper = new MyDbHelper(requireContext());

        currentFilter = "ALL";
        updateSelectedFilter(filterAll);
        loadTasks(myDbHelperHelper.getData());

        filterAll.setOnClickListener(v -> {
            currentFilter = "ALL";
            updateSelectedFilter(filterAll);
            loadTasks(myDbHelperHelper.getData());
        });

        filterCompleted.setOnClickListener(v -> {
            currentFilter = "COMPLETED";
            updateSelectedFilter(filterCompleted);
            loadTasks(myDbHelperHelper.getAllCompletedTasks());
        });

        filterPending.setOnClickListener(v -> {
            currentFilter = "PENDING";
            updateSelectedFilter(filterPending);
            loadTasks(myDbHelperHelper.getPendingTasks());
        });

        filterOverdue.setOnClickListener(v -> {
            currentFilter = "OVERDUE";
            updateSelectedFilter(filterOverdue);
            loadTasks(myDbHelperHelper.getOverdueTasks());
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
                loadTasks(myDbHelperHelper.getAllCompletedTasks());

            } else if ("PENDING".equals(filterType)) {

                currentFilter = "PENDING";
                updateSelectedFilter(filterPending);
                loadTasks(myDbHelperHelper.getPendingTasks());

            } else if ("OVERDUE".equals(filterType)) {

                currentFilter = "OVERDUE";
                updateSelectedFilter(filterOverdue);
                loadTasks(myDbHelperHelper.getOverdueTasks());

            } else {

                currentFilter = "ALL";
                updateSelectedFilter(filterAll);
                loadTasks(myDbHelperHelper.getData());
            }

        } else {

            currentFilter = "ALL";
            updateSelectedFilter(filterAll);
            loadTasks(myDbHelperHelper.getData());
        }

        swipeRefreshLayout.setOnRefreshListener(() -> {

            switch (currentFilter) {

                case "COMPLETED":
                    loadTasks(myDbHelperHelper.getAllCompletedTasks());
                    break;

                case "PENDING":
                    loadTasks(myDbHelperHelper.getPendingTasks());
                    break;

                case "OVERDUE":
                    loadTasks(myDbHelperHelper.getOverdueTasks());
                    break;

                default:
                    loadTasks(myDbHelperHelper.getData());
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
                loadTasks(myDbHelperHelper.getAllCompletedTasks());
                updateSelectedFilter(filterCompleted);
                break;

            case "PENDING":
                loadTasks(myDbHelperHelper.getPendingTasks());
                updateSelectedFilter(filterPending);
                break;

            case "OVERDUE":
                loadTasks(myDbHelperHelper.getOverdueTasks());
                updateSelectedFilter(filterOverdue);
                break;

            default:
                loadTasks(myDbHelperHelper.getData());
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
                loadTasks(myDbHelperHelper.getAllCompletedTasks());
                break;

            case "PENDING":
                loadTasks(myDbHelperHelper.getPendingTasks());
                break;

            case "OVERDUE":
                loadTasks(myDbHelperHelper.getOverdueTasks());
                break;

            default:
                loadTasks(myDbHelperHelper.getData());
                break;
        }
    }
}
