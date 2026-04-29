package com.example.taskmaster.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmaster.Adapter.TaskAdapter;
import com.example.taskmaster.Model.TasksData;
import com.example.taskmaster.R;
import com.example.taskmaster.Utils.database;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private TextView taskCountView;
    private TextView nextTimeView;
    private TextView focusTaskView;
    private TextView focusDetailView;
    private database databasehelper;
    private RecyclerView completedTaskRecycler;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initView(view);
        initClick(view);
        bindTaskSummary();
        setData();

        return view;
    }

    private void setData() {

        ArrayList<TasksData> completedTasks =
                databasehelper.getAllCompletedTasks();

        completedTaskRecycler.setLayoutManager(
                new LinearLayoutManager(getContext())
        );

        completedTaskRecycler.setAdapter(
                new TaskAdapter(this.getContext(),completedTasks,this::bindTaskSummary));
    }

    private void initClick(View view) {
        Button addTaskButton = view.findViewById(R.id.homeAddTaskButton);
        Button viewTasksButton = view.findViewById(R.id.homeViewTasksButton);

        addTaskButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.add_Task));

        viewTasksButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.View_tasks));
    }

    private void initView(View view) {
        taskCountView = view.findViewById(R.id.homeTaskCount);
        nextTimeView = view.findViewById(R.id.homeNextTime);
        focusTaskView = view.findViewById(R.id.homeFocusTask);
        focusDetailView = view.findViewById(R.id.homeFocusDetail);
        databasehelper=new database(getContext());
        completedTaskRecycler=view.findViewById(R.id.completedTaskRecycler);
    }

    private void bindTaskSummary() {
        if (getContext() == null) {
            return;
        }

        List<TasksData> tasks = new database(getContext()).getData();
        int taskCount = tasks.size();
        taskCountView.setText(String.valueOf(taskCount));

        if (taskCount == 0) {
            nextTimeView.setText(getString(R.string.home_empty_time));
            focusTaskView.setText(getString(R.string.home_empty_focus));
            focusDetailView.setText(getString(R.string.home_section_message));
            return;
        }

        TasksData latestTask = tasks.get(taskCount - 1);
        String taskTitle = safeValue(latestTask.task);
        String taskNumber = safeValue(latestTask.taskno);
        String taskTime = safeValue(latestTask.time);

        focusTaskView.setText(taskTitle);
        nextTimeView.setText(taskTime.isEmpty() ? getString(R.string.home_empty_time) : taskTime);

        String detail;
        if (taskNumber.isEmpty() && taskTime.isEmpty()) {
            detail = getString(R.string.home_section_message);
        } else if (taskTime.isEmpty()) {
            detail = "Task " + taskNumber;
        } else if (taskNumber.isEmpty()) {
            detail = "Scheduled at " + taskTime;
        } else {
            detail = "Task " + taskNumber + " scheduled at " + taskTime;
        }
        focusDetailView.setText(detail);
    }

    private String safeValue(String value) {
        return value == null ? "" : value.trim();
    }

    @Override
    public void onResume() {
        super.onResume();
        bindTaskSummary();
    }
}
