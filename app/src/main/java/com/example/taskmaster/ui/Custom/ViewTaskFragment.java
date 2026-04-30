package com.example.taskmaster.ui.Custom;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.taskmaster.Adapter.TODOAdapter;
import com.example.taskmaster.Model.TasksData;
import com.example.taskmaster.R;
import com.example.taskmaster.ViewModel.TaskViewModel;

import java.util.List;

public class ViewTaskFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private TextView emptyStateText;
    private TaskViewModel viewModel;
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
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        observeViewModel();
        
        // Initial load based on arguments or default
        if (getArguments() != null) {
            String filterType = getArguments().getString("filter_type");
            if (filterType != null) {
                currentFilter = filterType;
            }
        }
        applyCurrentFilter();
    }

    private void initView(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        recyclerView = view.findViewById(R.id.tasksRecyclerView);
        emptyStateText = view.findViewById(R.id.emptyStateText);

        filterAll = view.findViewById(R.id.filterAll);
        filterCompleted = view.findViewById(R.id.filterCompleted);
        filterPending = view.findViewById(R.id.filterPending);
        filterOverdue = view.findViewById(R.id.filterOverdue);

        filterAll.setOnClickListener(v -> {
            currentFilter = "ALL";
            applyCurrentFilter();
        });

        filterCompleted.setOnClickListener(v -> {
            currentFilter = "COMPLETED";
            applyCurrentFilter();
        });

        filterPending.setOnClickListener(v -> {
            currentFilter = "PENDING";
            applyCurrentFilter();
        });

        filterOverdue.setOnClickListener(v -> {
            currentFilter = "OVERDUE";
            applyCurrentFilter();
        });
    }

    private void observeViewModel() {
        viewModel.getAllTasks().observe(getViewLifecycleOwner(), tasks -> {
            if ("ALL".equals(currentFilter)) {
                updateUI(tasks);
            }
        });

        viewModel.getCompletedTasks().observe(getViewLifecycleOwner(), tasks -> {
            if ("COMPLETED".equals(currentFilter)) {
                updateUI(tasks);
            }
        });

        viewModel.getPendingTasks().observe(getViewLifecycleOwner(), tasks -> {
            if ("PENDING".equals(currentFilter)) {
                updateUI(tasks);
            }
        });

        viewModel.getOverdueTasks().observe(getViewLifecycleOwner(), tasks -> {
            if ("OVERDUE".equals(currentFilter)) {
                updateUI(tasks);
            }
        });
    }

    private void applyCurrentFilter() {
        switch (currentFilter) {
            case "COMPLETED":
                updateSelectedFilter(filterCompleted);
                break;
            case "PENDING":
                updateSelectedFilter(filterPending);
                break;
            case "OVERDUE":
                updateSelectedFilter(filterOverdue);
                break;
            default:
                currentFilter = "ALL";
                updateSelectedFilter(filterAll);
                break;
        }
        viewModel.refreshTasks();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        swipeRefreshLayout.setOnRefreshListener(() -> {
            viewModel.refreshTasks();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void updateSelectedFilter(TextView selectedView) {
        filterAll.setBackgroundResource(R.drawable.bg_surface_card);
        filterCompleted.setBackgroundResource(R.drawable.bg_surface_card);
        filterPending.setBackgroundResource(R.drawable.bg_surface_card);
        filterOverdue.setBackgroundResource(R.drawable.bg_surface_card);

        filterAll.setTextColor(requireContext().getColor(R.color.text_primary));
        filterCompleted.setTextColor(requireContext().getColor(R.color.text_primary));
        filterPending.setTextColor(requireContext().getColor(R.color.text_primary));
        filterOverdue.setTextColor(requireContext().getColor(R.color.text_primary));

        selectedView.setBackgroundResource(R.drawable.bg_chip_blue);
        selectedView.setTextColor(requireContext().getColor(R.color.colorPrimarydark));
    }

    private void updateUI(List<TasksData> data) {
        todoAdapter = new TODOAdapter(requireContext(), data, () -> viewModel.refreshTasks());
        recyclerView.setAdapter(todoAdapter);

        emptyStateText.setVisibility(data.isEmpty() ? View.VISIBLE : View.GONE);
        swipeRefreshLayout.setVisibility(data.isEmpty() ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) {
            viewModel.refreshTasks();
        }
    }
}
